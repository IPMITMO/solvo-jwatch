/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 14, 2015
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
public class StartProcessExecutor extends AbstractProcessExecutor
{
    @Override
    public void validateSystemState() throws InaccessibleOperationException
    {
        super.validateSystemState();
        SystemModel system = systemManager.getSystem();
        if ( !system.isRunning() )
        {
            Lib.log.warn("Process can not start now, current system state: " +
                    system.getSystemState());
            throw new InaccessibleOperationException();
        }
    }

    @Override
    public String getOperationName()
    {
        return "Start process";
    }

    @Override
    protected OperationResult perform(ProcessContext processContext)
    {
        String processName = processContext.getProcessDescription().getProcessName();
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info("Trying to start process '" +
                                processName + "'...");
        }
        systemManager.resetProcess(processContext);
        systemManager.startProcess(processContext);
        if (processContext.isFailedToStart())
        {
            Lib.log.info("Process '" + processName + "' wasn't started correctly");
        }
        else
        {
            Lib.log.info("Process '" + processName + "' was started successfully");
        }

        return OperationResult.createCompleted(
                    systemManager.getSystem().getSystemState() );
    }

    @Override
    public void validateProcessState(ProcessContext processContext) throws InaccessibleOperationException
    {
        ProcessInfo pi = processContext.getProcessInfo();
        String processName = processContext.getProcessDescription().getProcessName();

        if ( !processContext.isStarterClosed() )
        {
            throw new InaccessibleOperationException("Process '" + processName + "' is " +
                    pi.getProcessState());
        }
    }

    @Override
    public AbstractExecutor newInstance()
    {
        return new StartProcessExecutor();
    }

    @Override
    public SystemState getIntermediateState()
    {
        return null;
    }
}
