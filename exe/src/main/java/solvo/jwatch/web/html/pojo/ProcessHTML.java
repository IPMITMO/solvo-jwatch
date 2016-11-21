/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 17, 2015
 */

package solvo.jwatch.web.html.pojo;

import solvo.jwatch.bo.ProcessSession;
import solvo.jwatch.bo.ProcessState;
import solvo.jwatch.bo.SystemState;

/**
 *
 * @author user_akrikheli
 */
public class ProcessHTML
{
    /**
     * System state
     */
    private SystemState systemState;

    /**
    * Process name
    */
    private String processName;

    /**
     * Process state
     */
    private ProcessState processState;

    /**
     * Is process enabled
     */
    private boolean enabled;

    /**
     * Is process dead
     */
    private boolean dead;

    /**
     * Start retries
     */
    private int startRetries;

    /**
     * Max number of start retries
     */
    private int maxStartRetries;

    /**
     * Perform ping
     */
    private boolean pingable;

    /**
     * Max number of ping alarms
     */
    private int maxAlarms;

    /**
     * Current session info
     */
    private ProcessSession processSession;

    private boolean incidental;

    public String getProcessName()
    {
        return processName;
    }

    public void setProcessName(String processName)
    {
        this.processName = processName;
    }

    public ProcessState getProcessState()
    {
        return processState;
    }

    public void setProcessState(ProcessState processState)
    {
        this.processState = processState;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isDead()
    {
        return dead;
    }

    public void setDead(boolean dead)
    {
        this.dead = dead;
    }

    public int getStartRetries()
    {
        return startRetries;
    }

    public void setStartRetries(int startRetries)
    {
        this.startRetries = startRetries;
    }

    public int getMaxStartRetries()
    {
        return maxStartRetries;
    }

    public void setMaxStartRetries(int maxStartRetries)
    {
        this.maxStartRetries = maxStartRetries;
    }

    public SystemState getSystemState()
    {
        return systemState;
    }

    public void setSystemState(SystemState systemState)
    {
        this.systemState = systemState;
    }

    public ProcessSession getProcessSession()
    {
        return processSession;
    }

    public void setProcessSession(ProcessSession processSession)
    {
        this.processSession = processSession;
    }

    public boolean isPingable()
    {
        return pingable;
    }

    public void setPingable(boolean pingable)
    {
        this.pingable = pingable;
    }

    public int getMaxAlarms()
    {
        return maxAlarms;
    }

    public void setMaxAlarms(int maxAlarms)
    {
        this.maxAlarms = maxAlarms;
    }

    public boolean isIncidental()
    {
        return incidental;
    }

    public void setIncidental(boolean incidental)
    {
        this.incidental = incidental;
    }

}
