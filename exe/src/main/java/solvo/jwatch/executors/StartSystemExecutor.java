/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 11, 2015
 */

package solvo.jwatch.executors;

import java.util.Map;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.ProcessContext;
import solvo.jwatch.bo.SystemModel;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.exceptions.InaccessibleOperationException;

/**
 *
 * @author user_akrikheli
 */
public class StartSystemExecutor extends AbstractSystemExecutor
{
    public StartSystemExecutor()
    {
        super();
    }

    @Override
    public void validateSystemState() throws InaccessibleOperationException
    {
        super.validateSystemState();
        if ( !systemManager.canSystemStart() )
        {
            SystemModel system = systemManager.getSystem();
            Lib.log.warn("System can not start now, current system state: " +
                                system.getSystemState());
            throw new InaccessibleOperationException();
        }
    }

    @Override
    public String getOperationName()
    {
        return "Start system";
    }

    @Override
    protected OperationResult perform(AbstractOperationEvent se)
    {
        Lib.log.info("Trying to start system...");

        final SystemModel system = systemManager.getSystem();
        boolean failOfStartOccured = false;
        Map<String, ProcessContext> processes = system.getProcesses();
        for (Map.Entry<String, ProcessContext> entry : processes.entrySet())
        {
            ProcessContext processContext = entry.getValue();

            systemManager.resetProcess(processContext);
            systemManager.startProcess(processContext);

            if ( processContext.isFailedToStart() && !failOfStartOccured )
            {
                failOfStartOccured = true;
            }

            if ( isOperationAborted() )
            {
                return abort();
            }
        }

        if ( !failOfStartOccured )
        {
            Lib.log.info("System was started successfully");
        }
        else
        {
            Lib.log.warn("Some processes weren't started correctly");
        }

        SystemState newState = systemManager.hasHaltingProcesses() ?
                                            SystemState.HALTING :
                                            SystemState.RUNNING;
        return OperationResult.createCompleted( newState );
    }

    @Override
    public SystemState getIntermediateState()
    {
        return SystemState.STARTING;
    }

    @Override
    public AbstractExecutor newInstance()
    {
        return new StartSystemExecutor();
    }

    protected boolean isOperationAborted()
    {
        return systemManager.isAborted();
    }

    protected OperationResult abort()
    {
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info("Operation '" + getOperationName() + "' is aborted");
        }

        SystemState newState = systemManager.hasHaltingProcesses() ?
                                            SystemState.HALTING :
                                            SystemState.RUNNING;
        return OperationResult.createAborted( newState );
    }

}
