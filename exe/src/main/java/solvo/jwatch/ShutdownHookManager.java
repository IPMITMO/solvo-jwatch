/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 28, 2015
 */

package solvo.jwatch;

import solvo.gadgets.Lib;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.SystemOperationEvent;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.web.RestServerInstance;

/**
 *
 * @author user_akrikheli
 */
public class ShutdownHookManager
{
    private final static String LOG_PREFIX = "ShutdownHookManager: ";
    private Thread hookThread;
    
    private volatile boolean enabled = false;

    public void createShutdownHook(boolean enabled)
    {
        if (hookThread != null)
        {
            Lib.log.warn(LOG_PREFIX + "shutdown hook was already created");
            return;
        }

        hookThread = new Thread(new ShutdownHookImpl());
        Runtime.getRuntime().addShutdownHook(hookThread);
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info(LOG_PREFIX + "shutdown hook was created");
        }
        if (enabled)
        {
            enableShutdownHook();
        }
    }

    public void enableShutdownHook()
    {
        enabled = true;
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info(LOG_PREFIX + "shutdown hook was enabled");
        }
    }

    public void disableShutdownHook()
    {
        enabled = false;
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info(LOG_PREFIX + "shutdown hook was disabled");
        }
}

    /**
     * This implementation is thread-safe
     */
    private class ShutdownHookImpl implements Runnable
    {
        @Override
        public void run()
        {
            Object syncObj = SystemManagerAPIInstance.get().getSyncObj();
            SystemManagerInstance.get().abort();

            synchronized ( syncObj )
            {
                if (enabled)
                {
                    SystemOperationEvent se = SystemOperationEvent.createShutdownForciblyEvent();
                    SystemManagerAPIInstance.get().performSystemOperation(se);

                    OperationResult shtdResult = se.getOperationResult();
                    RestServerInstance.get().stopServer();

                    if (shtdResult.getNewSystemState() == SystemState.HALTING)
                    {
                        Lib.log.fatal("Jwatch not finished correctly, system is halting");
                    }
                }
            }
        }
    }
}
