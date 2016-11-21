/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 12, 2015
 */

package solvo.jwatch.executors;

import java.util.Map;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.SystemModel;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.ProcessContext;
import solvo.jwatch.exceptions.InaccessibleOperationException;

/**
 *
 * @author user_akrikheli
 */
public class StopSystemExecutor extends AbstractSystemExecutor
{
    public StopSystemExecutor()
    {
        super();
    }

    @Override
    public void validateSystemState() throws InaccessibleOperationException
    {
        super.validateSystemState();
        if ( !systemManager.canSystemStop())
        {
            SystemModel system = systemManager.getSystem();
            Lib.log.warn("System can not stop now, current system state: " +
                                system.getSystemState());
            throw new InaccessibleOperationException();
        }
    }

    @Override
    public String getOperationName()
    {
        return "Stop system";
    }

    @Override
    protected OperationResult perform(AbstractOperationEvent se)
    {
        Lib.log.info("Trying to stop system...");

        final SystemModel system = systemManager.getSystem();
        Map<String, ProcessContext> processes = system.getProcesses();
        for (Map.Entry<String, ProcessContext> entry : processes.entrySet())
        {
            ProcessContext processContext = entry.getValue();
            systemManager.shutDownProcess(processContext);
        }

        SystemState newState = systemManager.hasHaltingProcesses() ?
                                            SystemState.HALTING :
                                            SystemState.STOPPED;
        if (newState == SystemState.HALTING)
        {
            Lib.log.error("System was not stopped correctly");
        }
        else
        {
            Lib.log.info("System was stopped successfully");
        }

        return OperationResult.createCompleted( newState );
    }

    @Override
    public SystemState getIntermediateState()
    {
        return SystemState.STOPPING;
    }

    @Override
    public AbstractExecutor newInstance()
    {
        return new StopSystemExecutor();
    }
}
