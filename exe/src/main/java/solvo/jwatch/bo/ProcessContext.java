/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 4, 2015
 */

package solvo.jwatch.bo;

import solvo.jwatch.ProcessController;
import solvo.jwatch.config.WatchProcessDescription;

/**
 * This class describes process in jwatch context.
 * @author user_akrikheli
 */
public class ProcessContext
{
    /**
     * Process configuration
     */
    private volatile WatchProcessDescription processDescription;

    /**
     * Process information
     */
    private volatile ProcessInfo processInfo;

    /**
     * Incidental marker
     */
    private volatile boolean incidental;

    /**
     * Starter closed flag
     */
    private boolean starterClosed;

    /**
     * Process controller
     */
    private final ProcessController controller;

    /**
     * Default constructor
     */
    protected ProcessContext()
    {
        this.controller     = new ProcessController();
        this.processInfo    = ProcessInfo.newBuilder().
                            setRetriesNum(0).
                            setDead(false).
                            setProcessState(ProcessState.INACTIVE).
                            build();
        this.starterClosed      = true;
        this.incidental         = false;
    }

    public static ProcessContext create(WatchProcessDescription wpd)
    {
        ProcessContext processContext = new ProcessContext();
        processContext.applyConfiguration(wpd);

        return processContext;
    }

    /**
     * Applies new configuration to process
     * @param wpd process configuration
     */
    public void applyConfiguration(WatchProcessDescription wpd)
    {
        processDescription = wpd;
        controller.applyConfiguration(processDescription);
    }

    /**
     * Sets process information by process event
     * @param event process event
     */
    public void setProcessInfo(ProcessEvent event)
    {
        processInfo = event.getProcessInfo();
    }

    /**
     * Returns process information
     * @return process information
     */
    public ProcessInfo getProcessInfo()
    {
        return processInfo;
    }

    /**
     * Returns process state
     * @return process state
     */
    public ProcessState getProcessState()
    {
        return processInfo.getProcessState();
    }

    /**
     * Returns process configuration
     * @return process configuration
     */
    public WatchProcessDescription getProcessDescription()
    {
        return processDescription;
    }

    /**
     * Return true if process is enabled, else returns false
     * @return is process enabled
     */
    public boolean isProcessEnabled()
    {
        return processDescription.isEnabled();
    }

    /**
     * Return true if <i>starter closed</i> flag is true, else returns false.
     * @return is starter closed
     */
    public boolean isStarterClosed()
    {
        return starterClosed;
    }

    /**
     * Sets <i>starter closed</i> flag
     * @param starterClosed flag
     */
    public void setStarterClosed(boolean starterClosed)
    {
        this.starterClosed = starterClosed;
    }

    /**
     * Marks process as incidental
     */
    public void markAsIncidental()
    {
        this.incidental = true;
    }

    /**
     * Returns true if process is marked as incidental,
     * else returns false
     * @return is process incidental
     */
    public boolean isIncidental()
    {
        return this.incidental;
    }

    /**
     * Returns process controller
     * @return process controller
     */
    public ProcessController getController()
    {
        return controller;
    }

    /**
     * Returns true if process state is <i>Failed to start</i>,
     * otherwise returns false
     * @return is failed to start
     */
    public boolean isFailedToStart()
    {
        return getProcessState() == ProcessState.FAILED_TO_START;
    }

    /**
     * Returns true if process state is <i>Running</i>,
     * otherwise returns false
     * @return is running
     */
    public boolean isRunning()
    {
        return getProcessState() == ProcessState.RUNNING;
    }

    /**
     * Returns true if process state is <i>Terminated</i>,
     * otherwise returns false
     * @return is terminated
     */
    public boolean isTerminated()
    {
        return getProcessState() == ProcessState.TERMINATED;
    }

    /**
     * Returns true if process state is <i>Shut down</i>,
     * otherwise returns false
     * @return is shut down
     */
    public boolean isShutDown()
    {
        return getProcessState() == ProcessState.SHUT_DOWN;
    }

    /**
     * Returns true if process state is <i>Inactive</i>,
     * otherwise returns false
     * @return is inactive
     */
    public boolean isInactive()
    {
        return getProcessState() == ProcessState.INACTIVE;
    }

    /**
     * Returns true if process state is <i>Failed to shut down</i>,
     * otherwise returns false
     * @return is failed to shut down
     */
    public boolean isFailedToShutDown()
    {
        return getProcessState() == ProcessState.FAILED_TO_SHUT_DOWN;
    }

    /**
     * Returns true if process state is <i>Failed to register no ping</i>,
     * otherwise returns false
     * @return is failed to register no ping
     */
    public boolean isFailedToRegisterNoPing()
    {
        return getProcessState() == ProcessState.FAILED_TO_REGISTER_NO_PING;
    }
}
