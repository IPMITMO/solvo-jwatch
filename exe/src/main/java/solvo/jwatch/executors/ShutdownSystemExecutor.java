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
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.ProcessContext;
import solvo.jwatch.bo.SystemModel;

/**
 *
 * @author user_akrikheli
 */
public class ShutdownSystemExecutor extends AbstractSystemExecutor
{
    public ShutdownSystemExecutor()
    {
        super();
    }

    @Override
    public String getOperationName()
    {
        return "Shutdown system";
    }

    @Override
    protected OperationResult perform(AbstractOperationEvent se)
    {
        Lib.log.info("Trying to shut down system...");

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
            Lib.log.error("System was not shut down correctly");
        }
        else
        {
            Lib.log.info("System was shut down successfully");
        }

        return OperationResult.createCompleted(newState);
    }

    @Override
    public SystemState getIntermediateState()
    {
        return SystemState.STOPPING;
    }

    @Override
    public AbstractExecutor newInstance()
    {
        return new ShutdownSystemExecutor();
    }

}
