/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Nov 16, 2015
 */

package solvo.jwatch.handlers;

import solvo.jwatch.bo.ProcessContext;

/**
 *
 * @author user_akrikheli
 */
public interface IEventHandler
{
    /**
     * Handles event
     * @param processContext process context
     */
    void handle(ProcessContext processContext);

    /**
     * Returns event name
     * @return event name
     */
    String getEventName();
}
