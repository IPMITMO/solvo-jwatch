/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 3, 2015
 */

package solvo.jwatch.bo;

/**
 * This class describes an event of system operation.
 * @author user_akrikheli
 */
public class SystemOperationEvent extends AbstractOperationEvent
{
    protected SystemOperationEvent()
    {
        super();
    }

    /**
     * Creates system start event object and returns it.
     *
     * @return system start event object
     */
    public static SystemOperationEvent createStartEvent()
    {
        SystemOperationEvent se = new SystemOperationEvent();
        se.setEventName(START_SYSTEM);

        return se;
    }

    /**
     * Creates system stop event object and returns it.
     *
     * @return system stop event object
     */
    public static SystemOperationEvent createStopEvent()
    {
        SystemOperationEvent se = new SystemOperationEvent();
        se.setEventName(STOP_SYSTEM);

        return se;
    }

    /**
     * Creates system shutdown event object and returns it.
     *
     * @return system shutdown event object
     */
    public static SystemOperationEvent createShutdownEvent()
    {
        SystemOperationEvent se = new SystemOperationEvent();
        se.setEventName(SHUT_DOWN_SYSTEM);

        return se;
    }

    /**
     * Creates system shutdown forcibly event object and returns it.
     *
     * @return system shutdown forcibly event object
     */
    public static SystemOperationEvent createShutdownForciblyEvent()
    {
        SystemOperationEvent se = new SystemOperationEvent();
        se.setEventName(SHUT_DOWN_SYSTEM_FORCIBLY);

        return se;
    }

     /**
     * Creates system reloading event object and returns it.
     *
     * @return system reloading event object
     */
    public static SystemOperationEvent createReloadingEvent()
    {
        SystemOperationEvent se = new SystemOperationEvent();
        se.setEventName(RELOAD_SYSTEM);

        return se;
    }
}
