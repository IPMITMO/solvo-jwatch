/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 4, 2015
 */

package solvo.jwatch.bo;

/**
 * This is an enumeration of process states in runtime.
 * @author user_akrikheli
 */
public enum ProcessState
{
    /**
     * Process is inactive (default status).
     *
     * This state is used as default process status when configuration
     * has been loaded but no action over the process has been performed yet.
     */
    INACTIVE
    {
        @Override
        public String toString()
        {
            return "Inactive";
        }
    },

    /**
     * Process not started because of some error
     */
    FAILED_TO_START
    {
        @Override
        public String toString()
        {
            return "Failed to start";
        }
    },

    /**
     *
     */
    FAILED_TO_REGISTER_NO_PING
    {
        @Override
        public String toString()
        {
            return "Halting";
        }
    },

    /**
     *
     */
    FAILED_TO_SHUT_DOWN
    {
        @Override
        public String toString()
        {
            return "Halting";
        }
    },

    /**
     * Process is running
     */
    RUNNING
    {
        @Override
        public String toString()
        {
            return "Running";
        }
    },

    /**
     * Process is terminated
     */
    TERMINATED
    {
        @Override
        public String toString()
        {
            return "Terminated";
        }
    },

    /**
     * Process is shut down
     */
    SHUT_DOWN
    {
        @Override
        public String toString()
        {
            return "Shut down";
        }
    },
}


