/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Jul 30, 2015
 */

package solvo.jwatch.bo;

import java.util.Date;

/**
 * This is an abstract event description.
 *
 * Used as the parent for all classes describing events.
 * @author user_akrikheli
 */
public abstract class AbstractEvent
{
    /**
     * Process events
     */
    public static final String PROCESS_WAS_TERMINATED   = "process_was_terminated";
    public static final String PROCESS_WAS_STARTED      = "process_was_started";
    public static final String PROCESS_FAILED_TO_SHUT_DOWN  = "process_failed_to_shut_down";
    public static final String PROCESS_STARTER_WAS_CLOSED   = "process_starter_was_closed";

    /**
     * Operations (user event)
     */
    public static final String STOP_SYSTEM       = "stop_system";
    public static final String START_SYSTEM      = "start_system";
    public static final String RELOAD_SYSTEM     = "reload_system";
    public static final String SAVE_SYSTEM       = "save_system";
    public static final String SHUT_DOWN_SYSTEM  = "shut_down_system";
    public static final String SHUT_DOWN_SYSTEM_FORCIBLY = "shut_down_system_forcibly";

    public static final String START_PROCESS    = "start_process";
    public static final String STOP_PROCESS     = "stop_process";

    protected Date eventDate;
    private String eventName;

    protected AbstractEvent()
    {
        eventDate = new Date();
    }

    public String getEventName()
    {
        return eventName;
    }

    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }
}
