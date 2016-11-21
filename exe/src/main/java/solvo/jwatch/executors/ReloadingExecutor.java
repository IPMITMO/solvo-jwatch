/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 11, 2015
 */

package solvo.jwatch.executors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.ProcessContext;
import solvo.jwatch.bo.SystemModel;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.config.ConfigurationModel;
import solvo.jwatch.config.WatchProcessDescription;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.exceptions.ConfigurationNotFoundException;
import solvo.jwatch.exceptions.InaccessibleOperationException;
import solvo.jwatch.exceptions.JwatchConfigurationException;

/**
 *
 * @author user_akrikheli
 */
public class ReloadingExecutor extends AbstractSystemExecutor
{
    private static enum ReloadingProcessLabel
    {
        NEW,
        OVERLOADED
    }

    private static class ReloadingProcessContainer
    {
        private ReloadingProcessLabel       label;
        private WatchProcessDescription     currentProcessDescription;
        private WatchProcessDescription     newProcessDescription;
        private ProcessContext              processContext;

        public ReloadingProcessLabel getLabel()
        {
            return label;
        }

        public void setLabel(ReloadingProcessLabel label)
        {
            this.label = label;
        }

        public WatchProcessDescription getCurrentProcessDescription() 
        {
            return currentProcessDescription;
        }

        public void setCurrentProcessDescription(WatchProcessDescription currentProcessDescription)
        {
            this.currentProcessDescription = currentProcessDescription;
        }

        public WatchProcessDescription getNewProcessDescription()
        {
            return newProcessDescription;
        }

        public void setNewProcessDescription(WatchProcessDescription newProcessDescription)
        {
            this.newProcessDescription = newProcessDescription;
        }

        public ProcessContext getProcessContext()
        {
            return processContext;
        }

        public void setProcessContext(ProcessContext processContext)
        {
            this.processContext = processContext;
        }
    }


    public ReloadingExecutor()
    {
        super();
    }

    @Override
    public void validateSystemState() throws InaccessibleOperationException
    {
        super.validateSystemState();
        if ( !systemManager.canSystemReload() )
        {
            SystemModel system = systemManager.getSystem();
            Lib.log.warn("System can not reload now, current system state: " +
                                system.getSystemState());
            throw new InaccessibleOperationException();
        }
    }

    @Override
    public String getOperationName()
    {
        return "Reload system";
    }

    private OperationResult reloadOnly() throws JwatchConfigurationException
    {
        Lib.log.debug("Restart mode is disabled, reload configuration only...");
        systemManager.reloadConfiguration();
        systemManager.configureSystem();

        return OperationResult.createCompleted(SystemState.STOPPED);
    }

    private OperationResult reloadAndRestart() throws JwatchConfigurationException
    {
        Lib.log.debug("Restart mode is enabled, reload configuration and restart...");
        ConfigurationModel configModel;
        try
        {
            configModel = systemManager.reloadConfiguration();

            // We need to apply system changes manually, because we don't call
            //'configureSystem' method in this case
            systemManager.getSystem().setEnvironment(configModel.getEnvironment());
        }
        catch (ConfigurationNotFoundException ex)
        {
            Lib.log.error("Can not reload because of invalid configuration files");
            throw ex;
        }

        // Make reloading processes map
        Map<String, ReloadingProcessContainer> reloadingProcesses =
                                                makeReloadingProcessesMap(configModel);

        final Map<String, ProcessContext> processes = systemManager.getSystem().getProcesses();

        // Shut down old processes
        for (Entry<String, ProcessContext> entry : processes.entrySet())
        {
            if ( !reloadingProcesses.containsKey(entry.getKey()) )
            {
                ProcessContext processContext = entry.getValue();
                systemManager.shutDownProcess(processContext);

                //Mark halting process as "incidental"
                if ( processContext.isFailedToShutDown() )
                {
                    processContext.markAsIncidental();
                }
            }
        }

        //
        // Now, we can put into processes map actual processes
        //
        processes.clear();

        // Add halting processes
        if ( systemManager.hasHaltingProcesses() )
        {
            final Map<String, ProcessContext> incidentalProcesses = systemManager.
                                                getSystem().getIncidentalProcesses();

            for (Entry<String, ProcessContext> entry : systemManager.getSystem().
                                        getHaltingProcesses().entrySet())
            {
                ProcessContext processContext   = entry.getValue();
                if ( processContext.isIncidental() )
                {
                    String processName              = entry.getKey();
                    incidentalProcesses.put(processName, processContext);
                }
            }
        }

        // Add new processes
        for (Entry<String, ReloadingProcessContainer> entry : reloadingProcesses.entrySet())
        {
            String processName                          = entry.getKey();
            ReloadingProcessContainer reloadingProcess  = entry.getValue();

            processes.put(processName, reloadingProcess.getProcessContext());
        }

        boolean startFailOccured = false;

        for (Entry<String, ProcessContext> entry : processes.entrySet())
        {
            String processName              = entry.getKey();
            ProcessContext processContext   = entry.getValue();
            ReloadingProcessContainer reloadingProcess = reloadingProcesses.get(processName);

            WatchProcessDescription newProcessDescription       =
                            reloadingProcess.getNewProcessDescription();
            switch (reloadingProcess.getLabel())
            {
                case OVERLOADED:
                    WatchProcessDescription currentProcessDescription   =
                            reloadingProcess.getCurrentProcessDescription();

                    RestartResolver resolver = new RestartResolver( currentProcessDescription,
                                                                    newProcessDescription);
                    boolean needRestart = resolver.resolveRestart();

                    if (needRestart)
                    {
                        if (Lib.log.isInfoEnabled())
                        {
                            Lib.log.info("Restart mode is enabled for '" + processName + "'");
                        }
                        systemManager.restartProcess(processContext, newProcessDescription);
                    }
                    else
                    {
                        if (Lib.log.isInfoEnabled())
                        {
                            Lib.log.info("Restart mode is disabled for '" + processName
                                    + "', reload configuration only");
                        }
                        processContext.applyConfiguration(newProcessDescription);
                    }
                    break;

                case NEW:
                    systemManager.startProcess(processContext);
                    break;
            }

            if ( processContext.isFailedToStart() )
            {
                startFailOccured = true;
            }
        }

        if (startFailOccured)
        {
            Lib.log.warn("Some processes weren't started correctly");
        }


        SystemState newState = systemManager.hasHaltingProcesses() ?
                                            SystemState.HALTING :
                                            SystemState.RUNNING;
        if (newState == SystemState.HALTING)
        {
            Lib.log.error("System was not reloaded correctly");
        }
        else
        {
            Lib.log.info("System was reloaded successfully");
        }

        return OperationResult.createCompleted(newState);
    }


    private Map<String, ReloadingProcessContainer> makeReloadingProcessesMap
                                                    (final ConfigurationModel configModel)
    {
        final Map<String, ReloadingProcessContainer> reloadingProcesses = new LinkedHashMap<>();

        final Map<String, ProcessContext> processes = systemManager.getSystem().getProcesses();
        final List<WatchProcessDescription> wpdList = configModel.getProcessDescriptionList();

        for (WatchProcessDescription wpd : wpdList)
        {
            String processName = wpd.getProcessName();
            ReloadingProcessContainer rpc = new ReloadingProcessContainer();
            if ( processes.containsKey(processName) ) //Overloaded process
            {
                ProcessContext processContext = processes.get(processName);

                rpc.setLabel(ReloadingProcessLabel.OVERLOADED);
                rpc.setNewProcessDescription(wpd);
                rpc.setCurrentProcessDescription(processContext.getProcessDescription());
                rpc.setProcessContext(processContext);
            }
            else //New process
            {
                ProcessContext processContext = ProcessContext.create(wpd);

                rpc.setLabel(ReloadingProcessLabel.NEW);
                rpc.setProcessContext(processContext);
            }
            reloadingProcesses.put(processName, rpc);
        }

        return reloadingProcesses;
    }

    @Override
    protected OperationResult perform(AbstractOperationEvent se) throws JwatchConfigurationException
    {
        Lib.log.info("Trying to reload system...");
        boolean restartMode = (originalState == SystemState.RUNNING);

        OperationResult operationResult;
        if ( !restartMode )
        {
            operationResult = reloadOnly();
        }
        else
        {
            operationResult = reloadAndRestart();
        }

        return operationResult;
    }

    @Override
    public SystemState getIntermediateState()
    {
        return SystemState.RELOADING;
    }

    @Override
    public AbstractExecutor newInstance()
    {
        return new ReloadingExecutor();
    }
}