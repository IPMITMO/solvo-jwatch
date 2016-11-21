/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Jul 22, 2015
 */

package solvo.jwatch;

import solvo.jwatch.bo.ProcessContext;
import solvo.jwatch.bo.SystemModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.AbstractEvent;
import solvo.jwatch.config.ConfigurationModel;
import solvo.jwatch.config.WatchProcessDescription;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.ProcessEvent;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.exceptions.ConfigurationNotFoundException;
import solvo.jwatch.exceptions.InvalidConfigurationException;
import solvo.jwatch.executors.AbstractExecutor;
import solvo.jwatch.handlers.EventHandlerRegistry;
import solvo.jwatch.handlers.IEventHandler;

/**
 * This class is a mechanism of system/processes management.
 *
 * Never invoke controller's method directly in your executors!
 * Use methods provided by this class.
 *
 * @author user_akrikheli
 */

public class SystemManager
{
    private SystemModel         system;
    private volatile boolean    aborted = false;
    private AbstractExecutor    executor;

    private final CommunicatorHelper communicator = CommunicatorHelperInstance.get();

    /**
     * Initializes system by configuration manager
     * @throws solvo.jwatch.exceptions.InvalidConfigurationException
     */
    public void initialize() throws InvalidConfigurationException
    {
        try
        {
            File spoolDirectory = getSpoolDirectory();
            this.system = new SystemModel();
            this.system.setSpoolDirectory(spoolDirectory);
            configureSystem();
        }
        catch (InvalidConfigurationException ex)
        {
            Lib.log.fatal("System manager can not be initialized, "
                                            + "invalid configuration", ex);
            throw ex;
        }
    }

    /**
     * Configures system settings by current configuration model
     */
    public void configureSystem()
    {
        ConfigurationModel configModel = ConfigManager.get().getConfig();

        system.setEnvironment(configModel.getEnvironment());        
        
        List<WatchProcessDescription> wpdList   = configModel.getProcessDescriptionList();
        Map<String, ProcessContext> processes = system.getProcesses();
        if ( !processes.isEmpty() )
        {
            processes.clear();
        }

        for (WatchProcessDescription wpd : wpdList)
        {
            /* Generate context */
            ProcessContext processContext = ProcessContext.create(wpd);
            processes.put(wpd.getProcessName(), processContext);
        }
    }

     /**
     * Reloads jwatch configuration
     * @return configuration model
     * @throws ConfigurationNotFoundException if configuration files not found
     */
    public ConfigurationModel reloadConfiguration()
                                    throws ConfigurationNotFoundException
    {
        Lib.log.debug("Reloading watch configuration..");
        try
        {
            ConfigManager configManager = ConfigManager.get();

            AbstractConfigLoader loader = configManager.loadConfig();
            configManager.setConfigLoader(loader);
            configManager.logConfiguration();

            return configManager.getConfig();
        }
        catch (ConfigurationNotFoundException ex)
        {
            Lib.log.error("There aren't configuration files that can be read");
            throw ex;
        }
    }

    public boolean hasHaltingProcesses()
    {
        return !system.getHaltingProcesses().isEmpty();
    }

    /**
     * Returns system model object
     * @return system
     */
    public SystemModel getSystem()
    {
        return system;
    }

    /**
     * Returns true if system can start, otherwise - false
     * @return can system start
     */
    public boolean canSystemStart()
    {
        return system.isStopped();
    }

    /**
    * Returns true if system can stop, otherwise - false
    * @return can system stop
    */
    public boolean canSystemStop()
    {
        return system.isRunning();
    }

    /**
    * Returns true if system can reload, otherwise - false
    * @return can system stop
    */
    public boolean canSystemReload()
    {
        return  system.isRunning() || system.isStopped();
    }

    /**
    * Returns true if system can save configuration, otherwise - false
    * @return can system save
    */
    public boolean canSystemSave()
    {
        return system.isStopped();
    }

    public void assignExecutor(AbstractExecutor executor)
    {
        this.executor = executor;
    }

    public void unassignExecutor()
    {
        this.executor = null;
    }

    public void registerSystemChanges(AbstractOperationEvent operationEvent)
    {
        OperationResult operationResult     = operationEvent.getOperationResult();
        OperationResult.ResultEnum result   = operationResult.getResult();

        if (system.getSystemState() != operationResult.getNewSystemState())
        {
            system.setSystemState( operationResult.getNewSystemState() );
        }

        if (Lib.log.isDebugEnabled())
        {
            Lib.log.debug(String.format(
            "System changes registered after %s operation: result - %s, system state: %s",
                operationEvent.getEventName(), result, operationResult.getNewSystemState()));
        }
    }


    /**
     * Shuts down process and waits operation completed.
     *
     * If process shutdown is not required this operation will be skipped
     * @param processContext process context
     */
    public void shutDownProcess(ProcessContext processContext)
    {
        final String processName = processContext.getProcessDescription().getProcessName();
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info("Shutting down process '" + processName + "'...");
        }
        boolean required = isShutdownRequired(processContext);

        if ( !required )
        {
            return;
        }

        ProcessController controller = processContext.getController();
        controller.shutDown();
        waitShutdown(processContext);
    }

     /**
     * Starts process and wait operation completed.
     *
     * If process is disabled it will be skipped for starting.
     * @param processContext process context
     */
    public void startProcess(ProcessContext processContext)
    {
        String processName = processContext.getProcessDescription().getProcessName();
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info("Starting process '" + processName + "'...");
        }

        boolean required = isStartRequired(processContext);

        if ( !required )
        {
            return;
        }

        ProcessController controller = processContext.getController();
        controller.startProcess();
        waitStart(processContext);
    }

    /**
     * Resets runtime properties for process context.
     * @param processContext process context
     * @throws IllegalStateException if process starter is not closed
     */
    public void resetProcess(ProcessContext processContext)
    {
        if (Lib.log.isDebugEnabled())
        {
            Lib.log.debug("Reseting '" + processContext.getProcessDescription().getProcessName()
                                    + "' settings...");
        }
        if ( !processContext.isStarterClosed() )
        {
            final String processName = processContext.getProcessDescription().getProcessName();
            throw new IllegalStateException
                        ("Process starter '" + processName + "' is not closed");
        }

        processContext.getController().reset();
    }


    /**
     * Restarts process using new configuration
     * @param processContext process context
     * @param newConfiguration new configuration
     */
    public void restartProcess( ProcessContext processContext,
                                WatchProcessDescription newConfiguration)
    {
        String processName = processContext.getProcessDescription().getProcessName();

        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info("Restarting '" + processName + "'...");
        }

        //First of all, we need to shut down old process
        shutDownProcess(processContext);

        if ( !processContext.isStarterClosed() )
        {
            Lib.log.warn("Process '" + processName + "' was failed to shut down, "
                    + "applying of new configuration is impossible");
            return;
        }

        //Apply new configuration for process
        processContext.applyConfiguration(newConfiguration);

        //Starts process with new configuration and new settings
        resetProcess(processContext);
        startProcess(processContext);
    }

    /**
     * Returns true if shutdown of process is required, otherwise returns false.
     *
     * @param processContext process context
     * @return true if shutdown is required, otherwise returns false.
     */
    private boolean isShutdownRequired(ProcessContext processContext)
    {
        final String processName = processContext.getProcessDescription().getProcessName();

        if ( !processContext.isProcessEnabled() )
        {
            if (Lib.log.isInfoEnabled())
            {
                Lib.log.info("Process " + processName + " is disabled");
            }
            return false;
        }

        if ( processContext.isInactive() )
        {
            if (Lib.log.isInfoEnabled())
            {
                Lib.log.info("Process " + processName + " is inactive");
            }
            return false;
        }

        if ( processContext.isStarterClosed() )
        {
            if (Lib.log.isInfoEnabled())
            {
                Lib.log.info("Starter of process " + processName + " was already closed");
            }
            return false;
        }

        if ( processContext.isFailedToShutDown() ) //For shut down hook
        {
            if (Lib.log.isInfoEnabled())
            {
                Lib.log.info("Process " + processName + " was failed to shut down earlier");
            }
            return false;
        }

        return true;
    }

    /**
     * Returns true if start of process is required, otherwise returns false.
     *
     * @param processContext process context
     * @return true if start is required, otherwise returns false.
     */
    private boolean isStartRequired(ProcessContext processContext)
    {
        final String processName = processContext.getProcessDescription().getProcessName();

        if ( !processContext.isProcessEnabled() )
        {
            if (Lib.log.isInfoEnabled())
            {
                Lib.log.info("Process " + processName + " is disabled");
            }
            return false;
        }

        return true;
    }

    /**
     * Waits while start attempt of process is not completed.
     * @param processContext process context
     */
    private void waitStart(ProcessContext processContext)
    {
        while ( true )
        {
            ProcessEvent event = (ProcessEvent)communicator.waitEvent();
            registerProcessChanges(event);

            if ( processContext.isFailedToStart() )
            {
                return;
            }
            else if ( processContext.isRunning() )
            {
                return;
            }
        }
    }

    /**
     * Waits while shutdown of process is not completed.
     * @param processContext process context
     */
    private void waitShutdown(ProcessContext processContext)
    {
        while ( true )
        {
            ProcessEvent event = (ProcessEvent)communicator.waitEvent();
            registerProcessChanges(event);

            if ( processContext.isFailedToShutDown() )
            {
                return;
            }

            if ( processContext.isStarterClosed() )
            {
                return;
            }
        }
    }

    /**
     * Returns spool directory as File object.
     *
     * @return spool directory.
     * @throws FileNotFoundException if value is not an existing directory path.
     */
    private File getSpoolDirectory() throws InvalidConfigurationException
    {        
        try
        {
            String spoolDirPath = ConfigManager.get().getConfig().getSpool();
            if (StringUtils.isEmpty(spoolDirPath))
            {
                Lib.log.error("Spool directory is not specified!");
                throw new FileNotFoundException();
            }        

            File spoolDir = new File(spoolDirPath);
            if ( !spoolDir.isDirectory() )
            {
                Lib.log.error("Directory '" + spoolDirPath + "' doesn't exist");
                throw new FileNotFoundException();
            }
            else
            {
                return spoolDir;
            }         
        }
        catch (FileNotFoundException ex)
        {
            throw new InvalidConfigurationException(ex);
        }
    }

    /**
     * Returns aborted flag value
     * @return is aborted
     */
    public synchronized boolean isAborted()
    {
        return aborted;
    }

    /**
     * Sets aborted flag value to true
     */
    public synchronized void abort()
    {
        this.aborted = true;
    }

    /**
     * Returns process by process name
     * @param processName process name
     * @return process
     */
    public ProcessContext getProcessByName(String processName)
    {
        return system.getProcesses().get(processName);
    }

    /**
     * Registers changes of process in the system.
     * @param event event for registration
     */
    public void registerProcessChanges(ProcessEvent event)
    {
        String eventName = event.getEventName();

        ProcessContext processContext = null;
        switch (eventName)
        {
            case AbstractEvent.PROCESS_WAS_TERMINATED:
                processContext = registerProcessTermination(event);
                break;
            case AbstractEvent.PROCESS_WAS_STARTED:
                processContext = registerProcessStart(event);
                break;
            case AbstractEvent.PROCESS_FAILED_TO_SHUT_DOWN:
                processContext = registerProcessFailedToShutDown(event);
                break;
            case AbstractEvent.PROCESS_STARTER_WAS_CLOSED:
                processContext = registerStarterClosing(event);
                break;
        }

        IEventHandler handler = EventHandlerRegistry.getHandler(eventName);
        if (handler != null && processContext != null)
        {
            handler.handle(processContext);
        }
    }

    protected ProcessContext registerStarterClosing(ProcessEvent processEvent)
    {
        final String processName            = processEvent.getProcessName();

        ProcessContext processContext = getProcessByName(processName);
        if (processContext == null)
        {
            processContext = system.getIncidentalProcesses().get(processName);
        }

        /**
         * Register changes in system
         */
        if (processContext.isFailedToShutDown())
        {
            if ( processContext.isIncidental() )
            {
                system.getIncidentalProcesses().remove(processName);
            }

            system.getHaltingProcesses().remove(processName);
            cancelHaltingState(processEvent);
        }

        processContext.setProcessInfo(processEvent);
        processContext.setStarterClosed( true );

        return processContext;
    }

    protected ProcessContext registerProcessStart(ProcessEvent processEvent)
    {
        String processName = processEvent.getProcessName();
        ProcessContext processContext = getProcessByName(processName);

        processContext.setProcessInfo(processEvent);
        processContext.setStarterClosed(false);

        return processContext;
    }

    protected ProcessContext registerProcessFailedToShutDown(ProcessEvent processEvent)
    {
        String processName = processEvent.getProcessName();
        ProcessContext processContext = getProcessByName(processName);

        processContext.setProcessInfo(processEvent);
        system.getHaltingProcesses().put(processName, processContext);

        /**
         * Set halting state if required
         */
        if ( !system.isHalting() )
        {
            system.setHalting();
        }

        return processContext;
    }

    protected ProcessContext registerProcessTermination(ProcessEvent processEvent)
    {
        final String processName = processEvent.getProcessName();

        ProcessContext processContext = getProcessByName(processName);

        /**
         * Register changes in system
         */
        if ( processContext.isFailedToRegisterNoPing() )
        {
            system.getHaltingProcesses().remove(processName);
            cancelHaltingState(processEvent);
        }

        processContext.setProcessInfo(processEvent);

        return processContext;
    }

    protected void cancelHaltingState(ProcessEvent processEvent)
    {
        if ( hasHaltingProcesses() )
        {
            return;
        }

        if ( executor != null ) // Back system to intermediate state
                                // if registration by "executor context"
        {
            SystemState intermediateState = executor.getIntermediateState();
            if (intermediateState == null)
            {
                system.setRunning(); //Temporary hack
            }
            else
            {
                system.setSystemState( intermediateState );
            }
        }
        else
        {
            system.setRunning(); //Temporary hack
        }
    }
}
