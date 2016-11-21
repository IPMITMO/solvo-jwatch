/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 17, 2015
 */

package solvo.jwatch.executors;

import solvo.gadgets.Lib;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.ProcessContext;
import solvo.jwatch.bo.ProcessInfo;
import solvo.jwatch.bo.SystemModel;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.exceptions.InaccessibleOperationException;

/**
 *
 * @author user_akrikheli
 */
public class StopProcessExecutor extends AbstractProcessExecutor
{
    @Override
    public void validateSystemState() throws InaccessibleOperationException
    {
        super.validateSystemState();
        SystemModel system = systemManager.getSystem();
        if ( !system.isRunning() )
        {
            Lib.log.warn("Process can not stop now, current system state: " +
                    system.getSystemState());
            throw new InaccessibleOperationException();
        }
    }

    @Override
    public String getOperationName()
    {
        return "Stop process";
    }

    @Override
    public void validateProcessState(ProcessContext processContext)
                                                        throws InaccessibleOperationException
    {
        ProcessInfo pi = processContext.getProcessInfo();
        String processName = processContext.getProcessDescription().getProcessName();

        if ( processContext.isStarterClosed() )
        {
            throw new InaccessibleOperationException("Process '" + processName + "' is " +
                    pi.getProcessState());
        }
    }

    @Override
    protected OperationResult perform(ProcessContext processContext)
    {
        String processName = processContext.getProcessDescription().getProcessName();
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info("Trying to stop process " +
                                processName + "...");
        }
        systemManager.shutDownProcess(processContext);

        if ( processContext.isFailedToShutDown() )
        {
            Lib.log.error("Process '" + processName + "' wasn't destroyed correctly");
        }
        else
        {
            Lib.log.info("Process '" + processName + "' was stopped successfully");
        }

        return OperationResult.createCompleted( systemManager.getSystem().getSystemState() );
    }

    @Override
    public AbstractExecutor newInstance()
    {
        return new StopProcessExecutor();
    }

    @Override
    public SystemState getIntermediateState()
    {
        return null;
    }

}
