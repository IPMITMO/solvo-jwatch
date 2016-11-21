/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Jul 22, 2015
 */

package solvo.jwatch;

import solvo.jwatch.pinger.AbstractPinger;
import solvo.jwatch.bo.ProcessState;
import solvo.jwatch.bo.ProcessInfo;
import solvo.jwatch.bo.AbstractEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.PingerInfo;
import solvo.jwatch.config.WatchProcessDescription;
import solvo.jwatch.bo.ProcessEvent;
import solvo.jwatch.bo.ProcessSession;
import solvo.jwatch.pinger.PingerFactory;

/**
 * This class represents process controller implementation in jwatch.
 * @author user_akrikheli
 */
public class ProcessController
{
    private static final String LOG_PREFIX = "Controller: ";

    private volatile File processLogDirectory;

    private Thread  startProcessThread;
    private Thread  pingerThread;

    /* The internal state of the controller*/
    private volatile ProcessState   processState    = ProcessState.INACTIVE;
    private volatile boolean        isProcessDead   = false;
    private volatile int            restartCount    = 0;

    private volatile Process        process;
    private volatile ProcessSession currentSession;

    /* Controller configuration */
    private volatile String     processName;
    private volatile String     startCmd;
    private volatile String     coreDirPath;
    private volatile int        startRetries;
    private volatile int        startTimeout;
    private volatile int        pingDelay;
    private volatile int        pingInterval;
    private volatile int        pingAlarms;
    private volatile String     pingerImpl;
    private volatile String     componentName;
    private volatile boolean    pingable;

    private class ProcessStarter implements Runnable
    {
        /**
        * Process builder
        */
        private ProcessBuilder  processBuilder;

        /**
        * Initializes process builder
        */
        private void initProcessBuilder()
        {
           String[] commands = startCmd.trim().split("\\s+");

           processBuilder = new ProcessBuilder(commands);

           if ( StringUtils.isEmpty(coreDirPath) )
           {
               Lib.log.warn(LOG_PREFIX + "core directory path is not specified for '"
                       + processName + "'");
           }
           else
           {
               File coreDir = new File(coreDirPath);
               if ( !coreDir.isDirectory() &&
                                    !coreDir.mkdir() )
               {
                   Lib.log.warn(LOG_PREFIX + "can not set core directory '" +
                       coreDirPath + "' for '" + processName + "'");
               }
               else
               {
                   processBuilder.directory(coreDir);
               }
           }
        }

        @Override
        public void run()
        {
            try
            {
                initProcessLogDirectory( SystemManagerInstance.get().
                        getSystem().getSpoolDirectory() );
                delayStart();

                synchronized (ProcessController.this)
                {
                    initProcessBuilder();
                    restartCount = 0;
                }

                while ( true )
                {
                    synchronized (ProcessController.this)
                    {
                        // Possible input state:
                        //
                        // *INACTIVE
                        // *SHUT_DOWN
                        // *TERMINATED
                        //

                        // Close starter if required
                        if ( processState == ProcessState.SHUT_DOWN ||
                                (processState == ProcessState.TERMINATED && isProcessDead) )
                        {
                            notifyAboutEvent(AbstractEvent.PROCESS_STARTER_WAS_CLOSED);
                            return;
                        }

                        currentSession = ProcessSession.openNewSession(processName);
                        if (Lib.log.isDebugEnabled())
                        {
                            Lib.log.debug(LOG_PREFIX + "session with id '" +
                                    currentSession.getSessionId() + "' is opened");
                        }

                        startProcess();

                        if (processState == ProcessState.RUNNING)
                        {
                            notifyAboutEvent(AbstractEvent.PROCESS_WAS_STARTED);
                        }
                        else if (processState == ProcessState.FAILED_TO_START)
                        {
                            // Close starter if process was failed to start                        
                            notifyAboutEvent(AbstractEvent.PROCESS_STARTER_WAS_CLOSED);
                            return;
                        }

                        if (pingable)
                        {
                            startPinger(currentSession);
                        }
                    }

                    boolean isTerminated = false;
                    while ( !isTerminated )
                    {
                        try
                        {
                            process.waitFor();
                            isTerminated = true;
                        }
                        catch (InterruptedException ex)
                        {
                            Lib.log.warn(LOG_PREFIX + "InterruptedException occured in '"
                                    + processName + "' starter");
                        }
                    }

                    synchronized (ProcessController.this)
                    {
                        finishProcess();
                    }
                }
            }
            catch (Exception ex)
            {
                Lib.log.fatal(LOG_PREFIX + "process starter fatal error: " + processName, ex);
            }
        }

        private void delayStart()
        {
            try
            {
                Thread.sleep( startTimeout );
            }
            catch (InterruptedException ex)
            {
                Lib.log.error("Interrupted exception", ex);
            }
        }

        private void startPinger(ProcessSession session)
        {
            ProcessPinger processPinger = new ProcessPinger(session);
            pingerThread = new Thread(processPinger);
            pingerThread.setDaemon(true);
            pingerThread.start();
        }

        private void startProcess()
        {

            // Possible input state:
            //
            // *INACTIVE
            // *TERMINATED
            //

            try
            {
                File stdoutLogFile = createStdoutLogFile();
                if (stdoutLogFile != null)
                {
                    processBuilder.redirectOutput( stdoutLogFile );
                }

                File stderrLogFile = createErrLogFile();
                if (stderrLogFile != null)
                {
                    processBuilder.redirectError( stderrLogFile );
                }

                process = processBuilder.start();
            }
            catch (IOException ex)
            {
                Lib.log.warn(LOG_PREFIX + "process '" + processName + "' was failed to start", ex);

                processState = ProcessState.FAILED_TO_START;
                closeSession(currentSession, ProcessSession.PROCESS_DESTROYER_OUTER);
                return;
            }

            processState   = ProcessState.RUNNING;
            ++restartCount;

            if (Lib.log.isInfoEnabled())
            {
                Lib.log.info( String.format("%s'%s' was started (%d/%d)",
                        LOG_PREFIX, processName, restartCount, startRetries) );
            }

            // Possible output state:
            //
            // *RUNNING
            // *FAILED_TO_START
            //
        }

        private void finishProcess()
        {
            closeSession(currentSession, ProcessSession.PROCESS_DESTROYER_OUTER);

            switch (processState)
            {
                case RUNNING:
                    // <editor-fold defaultstate="collapsed" desc="Running">
                    processState = ProcessState.TERMINATED;
                    break;
                    // </editor-fold>
                case FAILED_TO_REGISTER_NO_PING:
                    // <editor-fold defaultstate="collapsed" desc="Failed to register no ping">
                    processState = ProcessState.TERMINATED;
                    break;
                    // </editor-fold>
                case FAILED_TO_SHUT_DOWN:
                    // <editor-fold defaultstate="collapsed" desc="Failed to shut down">
                    processState = ProcessState.SHUT_DOWN;
                    break;
                     // </editor-fold>
                case SHUT_DOWN:
                    // <editor-fold defaultstate="collapsed" desc="Shut down">
                    //Nothing to do
                    break;
                    // </editor-fold>

            }

            if (processState == ProcessState.TERMINATED)
            {
                if (Lib.log.isInfoEnabled())
                {
                    Lib.log.info( String.format("%s'%s' was terminated, destroyer: %s (%d/%d)",
                                LOG_PREFIX, processName, currentSession.getProcessDestroyer(),
                                restartCount, startRetries) );
                }

                if (restartCount >= startRetries)
                {
                    isProcessDead = true;
                    if (Lib.log.isInfoEnabled())
                    {
                        Lib.log.info(String.format("%s'%s' is dead", LOG_PREFIX, processName));
                    }
                }
                notifyAboutEvent(AbstractEvent.PROCESS_WAS_TERMINATED);
            }
        }
    }

    private class ProcessPinger implements Runnable
    {
        private final ProcessSession processSession;

        public ProcessPinger(ProcessSession session)
        {
            this.processSession = session;
        }

        @Override
        public void run()
        {
            /**
             * Primary delay
             */
            try
            {
                Thread.sleep(pingDelay);
            }
            catch (InterruptedException ex)
            {
                Lib.log.error("Interrupted exception", ex);
            }

            /**
             * Create new pinger
             */
            AbstractPinger pinger = null;
            if ( StringUtils.isNotEmpty(pingerImpl) )
            {
                pinger = PingerFactory.create(pingerImpl);
            }

            if (pinger == null)
            {
                Lib.log.warn(LOG_PREFIX + "can not find implementation for '"
                        + processName + "' pinger");
                return;
            }

            pinger.setComponentName(componentName);
            pinger.init();

            int pingResult;
            int alarmCount = 0;

            while (true)
            {
                delayPing();

                synchronized (ProcessController.this)
                {
                    if ( processSession.isClosed())
                    {
                        return;
                    }

                    processSession.registerPingerInfo( PingerInfo.createStartedInfo(alarmCount) );
                }

                pingResult = pinger.ping();

                synchronized (ProcessController.this)
                {
                    if ( processSession.isClosed() )
                    {
                        return;
                    }

                    if (Lib.log.isDebugEnabled())
                    {
                        Lib.log.debug(LOG_PREFIX + "'" + processName + "' was pinged,"
                                + " ping result: " + pingResult + ", sessionId: '"
                                + processSession.getSessionId() + "'");
                    }
                    boolean hasNetworkActivity = pingResult == AbstractPinger.PING_OK;
                    if ( hasNetworkActivity )
                    {
                        alarmCount = 0; //Reset ping attempts, if process has network activity
                    }
                    else
                    {
                        ++alarmCount;
                    }
                    processSession.registerPingerInfo( PingerInfo.
                                            createPerformedInfo(alarmCount, hasNetworkActivity) );
                    /**
                     * Register no ping
                     */
                    if ( !hasNetworkActivity && alarmCount >= pingAlarms )
                    {
                        ProcessController.this.registerNoPing(processSession);
                        return;
                    }
                }
            }
        }

        private void delayPing()
        {
            try
            {
                Thread.sleep(pingInterval);
            }
            catch (InterruptedException ex)
            {
                Lib.log.error("Interrupted exception", ex);
            }
        }
    }

    /**
     * Starts process
     */
    public void startProcess()
    {
        startProcessThread = new Thread(new ProcessStarter());
        startProcessThread.start();
    }

    public synchronized void applyConfiguration(WatchProcessDescription wpd)
    {
        processName     = wpd.getProcessName();
        startCmd        = wpd.getStartCmd();
        coreDirPath     = wpd.getCoreDir();
        startRetries    = wpd.getStartRetries();
        startTimeout    = wpd.getStartTimeout();
        pingable        = wpd.isPingable();
        pingAlarms      = wpd.getPingAlarms();
        pingDelay       = wpd.getPingDelay();
        pingInterval    = wpd.getPingInterval();
        pingerImpl      = wpd.getPingerImpl();
        componentName   = wpd.getComponentName();
    }

    /**
     * Shuts down controller and kill process if it is active.
     */
    public synchronized void shutDown()
    {
        // Possible input states:
        //
        // *FAILED_TO_REGISTER_NO_PING
        // *RUNNING
        // *TERMINATED
        //

        switch (processState)
        {
            // We need to take into account that pinger has tried to destroy this process,
            // at the same time when user has tried to stop this process.

            // Since shutDown() method and registerNoPing(ProcessSession session) method are locked
            // by the same monitor (ProcessController object), method shutDown(), we need to handle
            // the next situation: method shutDown begins to invoke after pinger registers no ping activity.
            // When pinger registers no ping, controller tries to destroy process. If controller can't
            // destroy process, it sets FAILED_TO_SHUT_DOWN process state, but system may be not notified
            // by controller about this event to moment of shutDown() method invocation.
            case FAILED_TO_REGISTER_NO_PING:
            // <editor-fold defaultstate="collapsed" desc="Failed to register no ping">
                // Do not try to shut down process againg
                processState = ProcessState.FAILED_TO_SHUT_DOWN;
                break;
            //</editor-fold>
            case RUNNING:
            // <editor-fold defaultstate="collapsed" desc="Running">
                if (Lib.log.isDebugEnabled())
                {
                    Lib.log.debug(LOG_PREFIX + "trying to shut down process '"
                            + processName + "'");
                }
                // First of all, we need to close the current session to stop pinger
                closeSession(currentSession, ProcessSession.PROCESS_DESTROYER_USER);

                // If the destruction of process was failed, registering it in controller
                boolean isDestroyed = destroyProcess();
                if ( isDestroyed )
                {
                    processState = ProcessState.SHUT_DOWN;
                }
                else
                {
                    processState = ProcessState.FAILED_TO_SHUT_DOWN;
                    Lib.log.warn(LOG_PREFIX + "'" + processName + "' was failed to shut down" );
                }
                break;
            // </editor-fold>
            case TERMINATED:
            // <editor-fold defaultstate="collapsed" desc="Terminated">
                if ( !isProcessDead )
                {
                    processState = ProcessState.SHUT_DOWN;
                }
                break;
            // </editor-fold>

        }

        // Possible output states:
        //
        // *FAILED_TO_SHUT_DOWN
        // *SHUT_DOWN
        // *TERMINATED (with death)
        //

        switch (processState)
        {
            case SHUT_DOWN:
                if (Lib.log.isDebugEnabled())
                {
                    Lib.log.debug(LOG_PREFIX + "'" + processName + "' was shut down" );
                }
                break;
            case FAILED_TO_SHUT_DOWN:
                notifyAboutEvent(AbstractEvent.PROCESS_FAILED_TO_SHUT_DOWN);
                break;
            case TERMINATED:
                //Nothing to do
                break;
        }
    }


    public synchronized void reset()
    {
        isProcessDead   = false;
        processState    = ProcessState.INACTIVE;
        restartCount    = 0;
        process         = null;
    }

    protected boolean destroyProcess()
    {
        if ( !isProcessDestroyed() )
        {
            process.destroy();
        }
        else
        {
            return true;
        }
        for (int i = 0; i < 50; ++i)
        {
            try
            {
                Thread.sleep( 100 );
            }
            catch (InterruptedException ex)
            {
                Lib.log.error("Interrupted exception occured when trying to destroy process", ex);
            }

            if (isProcessDestroyed())
            {
                return true;
            }
        }
        return false;
    }

    private boolean isProcessDestroyed()
    {
        try
        {
            process.exitValue();
            return true;
        }
        catch (IllegalThreadStateException itse)
        {
            return false;
        }
    }

    private void notifyAboutEvent(String eventName)
    {
        ProcessEvent event = new ProcessEvent();
        event.setEventName( eventName );
        event.setProcessName(processName);

        event.setProcessInfo( createProcessInfo() );

        CommunicatorHelperInstance.get().notifyAboutProcessEvent(event);
    }

    private ProcessInfo createProcessInfo()
    {
        return ProcessInfo.newBuilder().
                        setRetriesNum(restartCount).
                        setDead(isProcessDead).
                        setProcessState(processState).
                        build();
    }

    private void registerNoPing(ProcessSession session)
    {
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info(LOG_PREFIX + "'" + processName
                    + "' has no ping activity, registering it...");
        }

        closeSession(session, ProcessSession.PROCESS_DESTROYER_PINGER);

        boolean isDestroyed = destroyProcess();

        // If the destruction of process was failed, registering
        // it in controller and notifying about it...
        if ( !isDestroyed )
        {
            Lib.log.warn(LOG_PREFIX + "pinger failed to register no ping activity for '"
                    + processName + "'");
            processState = ProcessState.FAILED_TO_REGISTER_NO_PING;
            notifyAboutEvent( AbstractEvent.PROCESS_FAILED_TO_SHUT_DOWN );
        }
    }

    private void initProcessLogDirectory(File spoolDirectory)
    {
        processLogDirectory = null;
        File logDirectory = new File(spoolDirectory, processName);

        if ( !logDirectory.isDirectory() &&
                    !logDirectory.mkdir() )
        {
            Lib.log.warn(LOG_PREFIX + "can not create log directory for '" +
                                            processName + "'");
        }
        else
        {
            processLogDirectory = logDirectory;
        }
    }

    private File createStdoutLogFile()
    {
        if (processLogDirectory == null)
        {
            Lib.log.warn(LOG_PREFIX + "stdout of '" + processName + "' can not be logged");
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("-yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();

        String stdoutLogFileName = processName.concat(dateFormat.format( date ));

        File stdoutLogFile = new File(processLogDirectory, stdoutLogFileName);

        return stdoutLogFile;
    }

    private File createErrLogFile()
    {
        if (processLogDirectory == null)
        {
            Lib.log.warn(LOG_PREFIX + "stderr of '" + processName + "' can not be logged");
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("-yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();

        String stderrLogFileName = processName.concat("_err").
                                            concat(dateFormat.format( date ));

        File stderrLogFile = new File(processLogDirectory, stderrLogFileName);

        return stderrLogFile;
    }

    public ProcessSession getCurrentSession()
    {
        return currentSession;
    }

    public void closeSession(ProcessSession session, String destroyer)
    {
        if (session.isClosed())
        {
            return;
        }

        session.close(destroyer);
        if (Lib.log.isDebugEnabled())
        {
            Lib.log.debug(LOG_PREFIX + "session with id '" +
                    currentSession.getSessionId() + "' is closed");
        }
    }
}
