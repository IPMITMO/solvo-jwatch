/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 11, 2015
 */

package solvo.jwatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.AbstractEvent;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.ProcessEvent;

/**
 *
 * @author user_akrikheli
 */

public class CommunicatorHelper
{
    private final BlockingQueue<AbstractEvent> eventChannel = 
                                new LinkedBlockingQueue<>();
    
    private final BlockingQueue<AbstractOperationEvent> completed = 
                                new LinkedBlockingQueue<>(1);

    /**
     * Blocks manager until obtaining of any event.
     *
     * @return event
     */
    public AbstractEvent waitEvent()
    {
        try
        {
            return eventChannel.take();
        }
        catch (InterruptedException iex)
        {
            Lib.log.error("Interrupted exception", iex);
            throw new RuntimeException(iex);
        }
    }

    public void waitOperationCompleted()
    {
        try
        {
            completed.take();
        }
        catch (InterruptedException iex)
        {
            Lib.log.error("Interrupted exception", iex);
            throw new RuntimeException(iex);
        }                                   
    }

    public void notifyAboutOperationCompleted(AbstractOperationEvent event)
    {
        try
        {
            completed.put(event);
        }
        catch (InterruptedException iex)
        {
            Lib.log.error("Interrupted exception", iex);
            throw new RuntimeException(iex);
        }
    }

    public void notifyAboutProcessEvent(ProcessEvent processEvent)
    {
        notifyAboutEvent(processEvent);
    }

    public void notifyAboutOperationEvent(AbstractOperationEvent operationEvent)
    {        
        notifyAboutEvent(operationEvent);
    }

    protected void notifyAboutEvent(AbstractEvent event)
    {
        try
        {
            eventChannel.put(event);
        }
        catch (InterruptedException iex)
        {
            Lib.log.error("Interrupted exception", iex);
            throw new RuntimeException(iex);
        }
    }
}
