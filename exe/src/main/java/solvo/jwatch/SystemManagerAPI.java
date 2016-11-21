/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 23, 2015
 */

package solvo.jwatch;

import solvo.gadgets.Lib;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.ProcessOperationEvent;
import solvo.jwatch.bo.SaveConfigEvent;
import solvo.jwatch.bo.SystemOperationEvent;

/**
 * Thread-safe API for system/process operation performing.
 *
 * Synchronization of system/process operation methods guarantees that until
 * the operation is completed none system operation event can be put
 * into the event channel.
 *
 * In other words, if one system operation is performed, the next system operation
 * will be performed only after end of the first system operation.
 *
 * In this way, at the time of execution system operation only process events
 * can be put in the event channel. So, system state can't be changed by another system
 * operation at the time of any system operation execution.
 * However, processes state can be changed from the outside. For example, user can kill
 * any process. Then process controller will generate notice (event) about process terminated
 * and it will be registered by manager as soon as manager takes this event.
 *
 * <i>Never call {@link solvo.jwatch.impl.CommunicatorHelper#notifyAboutEvent(AbstractEvent)
 * notifyAboutEvent method} directly, use only methods this class provides.</i>.
 *
 * @author user_akrikheli
 */

public class SystemManagerAPI
{
    private final Object syncObj = new Object();
    private static final String LOG_PREFIX = "System Manager API: ";

    /**
     * Starts system.
     *
     * <i>This method blocks thread until the operation completed.</i>
     * @return result of operation execution
     */
    public OperationResult startSystem()
    {
        synchronized (syncObj)
        {
            SystemOperationEvent se = SystemOperationEvent.createStartEvent();
            performSystemOperation( se );

            return se.getOperationResult();
        }
    }

    public OperationResult startProcess(String processName)
    {
        synchronized (syncObj)
        {
            ProcessOperationEvent poe = ProcessOperationEvent.createStartEvent(processName);
            performSystemOperation( poe );

            return poe.getOperationResult();
        }
    }

    /**
     * Stops system.
     *
     * <i>This method blocks thread until the operation completed.</i>
     * @return result of operation execution
     */
    public OperationResult stopSystem()
    {
        synchronized (syncObj)
        {
            SystemOperationEvent se = SystemOperationEvent.createStopEvent();
            performSystemOperation( se );

            return se.getOperationResult();
        }
    }

    public OperationResult stopProcess(String processName)
    {
        synchronized (syncObj)
        {
            ProcessOperationEvent poe = ProcessOperationEvent.createStopEvent(processName);
            performSystemOperation( poe );
            return poe.getOperationResult();
        }
    }

    /**
     * Shuts down system, used for REST calls.
     *
     * <i>This method blocks thread until the operation completed.</i>
     * @return result of operation execution
     */
    public OperationResult shutDownSystem()
    {
        synchronized (syncObj)
        {
            SystemOperationEvent se = SystemOperationEvent.createShutdownEvent();
            performSystemOperation(se);

            return se.getOperationResult();
        }
    }

    /**
     * Reloads system.
     *
     * <i>This method blocks thread until the operation completed.</i>
     * @return result of operation execution
     */
    public OperationResult reloadSystem()
    {
        synchronized (syncObj)
        {
            SystemOperationEvent se = SystemOperationEvent.createReloadingEvent();
            performSystemOperation( se );

            return se.getOperationResult();
        }
    }


    /**
     * Saves configuration.
     *
     * <i>This method blocks thread until the operation completed.</i>
     * @param builder
     * @return result of operation execution
     */
    public OperationResult saveConfig(IConfigBuilder builder)
    {
        synchronized (syncObj)
        {
            SaveConfigEvent sce = SaveConfigEvent.createEvent(builder);
            performSystemOperation( sce );

            return sce.getOperationResult();
        }
    }

    public Object getSyncObj()
    {
        return syncObj;
    }

    /**
     *
     * Puts system event into event channel and waits until operation be completed.
     * @param operationEvent system event for adding into event channel.
     */
    public void performSystemOperation(AbstractOperationEvent operationEvent)
    {
        String operationName = operationEvent.getEventName().toUpperCase();
        if (Watcher.watcherClosed)
        {
            Lib.log.warn(LOG_PREFIX + "watcher is closed, operation "
                    + operationName + " is inaccessible");
            operationEvent.setOperationResult( OperationResult.createInaccessible() );
            return;
        }

        CommunicatorHelper communicator = CommunicatorHelperInstance.get();
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info(LOG_PREFIX + "operation " + operationName + " is started");
        }
        
        communicator.notifyAboutOperationEvent(operationEvent);
        communicator.waitOperationCompleted();
        
        if (Lib.log.isDebugEnabled())
        {
            Lib.log.info(LOG_PREFIX + "operation " + operationName + " is completed");
        }
    }
}
