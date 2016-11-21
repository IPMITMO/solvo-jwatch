/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 11, 2015
 */

package solvo.jwatch;

import org.apache.commons.lang3.StringUtils;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.AbstractEvent;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.ProcessEvent;
import solvo.jwatch.bo.SystemOperationEvent;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.executors.AbstractExecutor;
import solvo.jwatch.executors.ExecutorFactory;

/**
 *
 * @author user_akrikheli
 */

public class Watcher
{
    public static boolean watcherClosed      = false;

    /**
    * Watches for processes
    */
    public static void watch()
    {
        final CommunicatorHelper communicator = CommunicatorHelperInstance.get();

        while ( !watcherClosed )
        {
            AbstractEvent event = communicator.waitEvent();

            if (event instanceof ProcessEvent)
            {
                processProcessEvent((ProcessEvent) event);
            }
            else if (event instanceof AbstractOperationEvent)
            {
                processOperationEvent((AbstractOperationEvent) event);
            }
        }
    }

    private static void processProcessEvent(ProcessEvent event)
    {
        final SystemManager systemManager = SystemManagerInstance.get();
        systemManager.registerProcessChanges( (ProcessEvent)event );
    }

    private static void processOperationEvent(AbstractOperationEvent operationEvent)
    {
        final CommunicatorHelper communicator = CommunicatorHelperInstance.get();
        final String eventName = operationEvent.getEventName();
        final AbstractExecutor executor = ExecutorFactory.getInstance().createExecutor(eventName);

        if (executor != null)
        {
            try
            {
                executor.execute( operationEvent );
            }
            catch (VirtualMachineError vme)
            {
                try
                {
                    Lib.log.fatal("Serious Virtual Machine Error! Exiting", vme);
                    exitForcibly();
                    return;
                }
                catch (Throwable ex)
                {
                    // Nothing to do. Completely lack of resources
                    return;
                }
            }
            catch (Throwable ex)
            {
                Lib.log.fatal("Jwatch fatal error", ex);
                exitForcibly();
                return;
            }
            processOperationResult(operationEvent);
            communicator.notifyAboutOperationCompleted(operationEvent);
        }
        else
        {
            Lib.log.fatal("Can not find executor for system event named '" +
                    eventName + "'!" );
        }
    }

    private static void processOperationResult(AbstractOperationEvent event)
    {
        if (event == null)
        {
            return;
        }

        String eventName = event.getEventName();
        if (StringUtils.isEmpty(eventName))
        {
            return;
        }

        switch (eventName)
        {
            case AbstractEvent.SHUT_DOWN_SYSTEM:
                OperationResult result = ((SystemOperationEvent)event).
                        getOperationResult();
                if ( result.getResult() == OperationResult.ResultEnum.COMPLETED
                        && result.getNewSystemState() == SystemState.STOPPED )
                {
                    stopWatcher(); //Stop watcher if required
                }
                break;

            case AbstractEvent.SHUT_DOWN_SYSTEM_FORCIBLY:
                stopWatcher(); //Stop watcher forcibly
                break;
        }
    }

    private static void stopWatcher()
    {
        ShutdownHookManagerInstance.get().disableShutdownHook();

        watcherClosed = true;
    }

    private static void exitForcibly()
    {
        ShutdownHookManagerInstance.get().disableShutdownHook();
        Runtime.getRuntime().halt(-1);
    }
}
