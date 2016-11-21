/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 14, 2015
 */

package solvo.jwatch.bo;

/**
 *
 * @author user_akrikheli
 */
public class ProcessOperationEvent extends AbstractOperationEvent
{
    private String processName;

    protected ProcessOperationEvent()
    {
        super();
    }

    /**
     * Creates process operation event named START and returns it.
     *
     * @param processName name of process which is used for the operation
     * @return start process event object
     */
    public static ProcessOperationEvent createStartEvent(String processName)
    {
        ProcessOperationEvent poe = new ProcessOperationEvent();

        poe.processName = processName;
        poe.setEventName(START_PROCESS);
        return poe;
    }


    /**
     * Creates process operation event named STOP and returns it.
     *
     * @param processName name of process which is used for the operation
     * @return stop process event object
     */
    public static ProcessOperationEvent createStopEvent(String processName)
    {
        ProcessOperationEvent poe = new ProcessOperationEvent();

        poe.processName = processName;
        poe.setEventName(STOP_PROCESS);
        return poe;
    }

    public String getProcessName()
    {
        return processName;
    }
}
