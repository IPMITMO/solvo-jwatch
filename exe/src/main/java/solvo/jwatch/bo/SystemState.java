/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 28, 2015
 */

package solvo.jwatch.bo;

/**
 *
 * @author user_akrikheli
 */
public enum SystemState
{
    STARTING
    {
        @Override
        public String toString()
        {
            return "Starting...";
        }

        @Override
        public boolean isIntermediate()
        {
            return true;
        }
    },

    RELOADING
    {
        @Override
        public String toString()
        {
            return "Reloading...";
        }

        @Override
        public boolean isIntermediate()
        {
            return true;
        }
    },
    STOPPING
    {
         @Override
        public String toString()
        {
            return "Stopping...";
        }

        @Override
        public boolean isIntermediate()
        {
            return true;
        }
    },
    RUNNING
    {
        @Override
        public String toString()
        {
            return "Running";
        }

        @Override
        public boolean isIntermediate()
        {
            return false;
        }
    },
    STOPPED
    {
        @Override
        public String toString()
        {
            return "Stopped";
        }

        @Override
        public boolean isIntermediate()
        {
            return false;
        }
    },
    HALTING
    {
        @Override
        public String toString()
        {
            return "Halting";
        }

        @Override
        public boolean isIntermediate()
        {
            return false;
        }
    };

    public abstract boolean isIntermediate();

}
