/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Jul 28, 2015
 */

package solvo.jwatch.config;

/**
 * This class encapsulating description (settings) of child process.
 * @author user_akrikheli
 */
public class WatchProcessDescription
{
    public static final boolean DEFAULT_DISABLED_VALUE      = false;
    public static final boolean DEFAULT_PINGABLE_VALUE      = false;
    public static final int DEFAULT_PING_DELAY_VALUE        = 20000; //in ms
    public static final int DEFAULT_PING_INTERVAL_VALUE     = 10000; //in ms
    public static final int DEFAULT_PING_ALARMS_VALUE       = 3;

    protected WatchProcessDescription()
    {

    }

    /**
     * Process name
     */
    private String  processName;

    /**
     * Process status (enabled/disabled)
     */
    private boolean enabled;

    /**
     * Start command
     */
    private String  startCmd;

    /**
     * Email address
     */
    private String  emailAddress;

    /**
     * Start retries number
     */
    private int     startRetries;

    /**
     * Core directory path
     */
    private String  coreDir;

    /**
     * Start timeout
     */
    private int     startTimeout;

    /**
     * Is process pingable
     */
    private boolean pingable;

    /**
     * Ping delay (in ms)
     */
    private int pingDelay;

    /**
     * Ping interval (in ms)
     */
    private int pingInterval;

    /**
     * Ping alarms
     */
    private int pingAlarms;

    /**
     * Implementation of pinger
     */
    private String pingerImpl;

    /**
     * Component name
     */
    private String componentName;

    /**
     * Returns start timeout.
     * @return start timeout
     */
    public int getStartTimeout()
    {
        return startTimeout;
    }

    /**
     * Returns core directory path.
     * @return core directory path
     */
    public String getCoreDir()
    {
        return coreDir;
    }

    /**
     * Return process name.
     * @return process name
     */
    public String getProcessName()
    {
        return processName;
    }

    /**
     * Returns process status (enabled/disabled).
     * @return if process is enabled - returns true, else - false.
     */
    public boolean isEnabled()
    {
        return enabled;
    }


    /**
     * Returns start command.
     * @return start command
     */
    public String getStartCmd()
    {
        return startCmd;
    }

    /**
     * Return email address.
     * @return email address
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * Returns start retries number
     * @return start retries number
     */
    public int getStartRetries()
    {
        return startRetries;
    }

    /**
     * Returns pingable value
     * @return is process pingable
     */
    public boolean isPingable()
    {
        return pingable;
    }

    /**
     * Returns ping delay (in ms)
     * @return ping delay
     */
    public int getPingDelay()
    {
        return pingDelay;
    }

    /**
     * Returns ping interval (in ms)
     * @return ping interval
     */
    public int getPingInterval()
    {
        return pingInterval;
    }

    /**
     * Returns ping alarms
     * @return ping alarms
     */
    public int getPingAlarms()
    {
        return pingAlarms;
    }

    /**
     * Returns implementation of pinger
     * @return implementation of pinger
     */
    public String getPingerImpl()
    {
        return pingerImpl;
    }

    /**
     * Returns component name
     * @return component name
     */
    public String getComponentName()
    {
        return componentName;
    }

    public static WatchProcessDescription make(WatchProcessDescription wpd,
                                                boolean enabled)
    {
        WatchProcessDescription instace = new WatchProcessDescription();

        /**
         * Parameters copy
         */
        instace.coreDir         = wpd.coreDir;
        instace.emailAddress    = wpd.emailAddress;
        instace.processName     = wpd.processName;
        instace.startCmd        = wpd.startCmd;
        instace.startRetries    = wpd.startRetries;
        instace.startTimeout    = wpd.startTimeout;
        instace.pingable        = wpd.pingable;
        instace.pingInterval    = wpd.pingInterval;
        instace.pingAlarms      = wpd.pingAlarms;
        instace.pingDelay       = wpd.pingDelay;
        instace.pingerImpl      = wpd.pingerImpl;
        instace.componentName   = wpd.componentName;

        instace.enabled         = enabled;

        return instace;
    }

    public static Builder newBuilder()
    {
        return new WatchProcessDescription().new Builder();
    }

    public class Builder
    {
        private Builder()
        {

        }

        public WatchProcessDescription build()
        {
            return WatchProcessDescription.this;
        }

        /**
        * Sets pingable value
        * @param pingable is process pingable
        * @return builder
        */
        public Builder setPingable(boolean pingable)
        {
            WatchProcessDescription.this.pingable = pingable;
            return this;
        }

        /**
        * Sets start retries number
        * @param startRetries start retries number
        * @return  builder
        */
        public Builder setStartRetries(int startRetries)
        {
            WatchProcessDescription.this.startRetries = startRetries;
            return this;
        }

        /**
         * Sets email address.
         * @param emailAddress email address
         * @return builder
         */
        public Builder setEmailAddress(String emailAddress)
        {
            WatchProcessDescription.this.emailAddress = emailAddress;
            return this;
        }

        /**
        * Sets start command.
        * @param startCmd start command
        * @return builder
        */
        public Builder setStartCmd(String startCmd)
        {
            WatchProcessDescription.this.startCmd = startCmd;
            return this;
        }

        /**
        * Sets process name.
        * @param processName process name
        * @return builder
        */
        public Builder setProcessName(String processName)
        {
            WatchProcessDescription.this.processName = processName;
            return this;
        }

        /**
        * Sets core directory path.
        * @param coreDir core directory path
        * @return builder
        */
        public Builder setCoreDir(String coreDir)
        {
            WatchProcessDescription.this.coreDir = coreDir;
            return this;
        }

        /**
        * Sets start timeout.
        * @param startTimeout
        * @return builder
        */
       public Builder setStartTimeout(int startTimeout)
       {
           WatchProcessDescription.this.startTimeout = startTimeout;
           return this;
       }

       /**
        * Sets process status (enabled/disabled).
        * @param isEnabled enabled flag
        * @return builder
        */
        public Builder setEnabled(boolean isEnabled)
        {
            WatchProcessDescription.this.enabled = isEnabled;
            return this;
        }

        /**
         * Sets ping delay (in ms)
         * @param pingDelay ping delay
         * @return builder
         */
        public Builder setPingDelay(int pingDelay)
        {
            WatchProcessDescription.this.pingDelay = pingDelay;
            return this;
        }

        /**
         * Sets ping interval (in ms)
         * @param pingInterval
         * @return builder
         */
        public Builder setPingInterval(int pingInterval)
        {
            WatchProcessDescription.this.pingInterval = pingInterval;
            return this;
        }

        /**
         * Sets ping alarms
         * @param pingAlarms ping alarms
         * @return builder
         */
        public Builder setPingAlarms(int pingAlarms)
        {
            WatchProcessDescription.this.pingAlarms = pingAlarms;
            return this;
        }

        /**
         * Sets implementation of pinger
         * @param pingerImpl implementation of pinger
         * @return builder
         */
        public Builder setPingerImpl(String pingerImpl)
        {
            WatchProcessDescription.this.pingerImpl = pingerImpl;
            return this;
        }

        /**
         * Sets component name
         * @param componentName component name
         * @return builder
         */
        public Builder setComponentName(String componentName)
        {
            WatchProcessDescription.this.componentName = componentName;
            return this;
        }
    }
}
