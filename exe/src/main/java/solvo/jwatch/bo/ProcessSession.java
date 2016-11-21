/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 30, 2015
 */

package solvo.jwatch.bo;

import java.util.Date;

/**
 *
 * @author user_akrikheli
 */
public class ProcessSession
{
    public static final String PROCESS_DESTROYER_PINGER = "pinger";
    public static final String PROCESS_DESTROYER_USER   = "user";
    public static final String PROCESS_DESTROYER_OUTER  = "outer";

    private volatile boolean closed;
    private volatile Date startDate;
    private volatile Date finishDate;
    private volatile PingerInfo pingerInfo;
    private volatile String processDestroyer;
    private String sessionId;

    private ProcessSession()
    {
        closed = false;
    }

    public static ProcessSession openNewSession(String processName)
    {
        ProcessSession session = new ProcessSession();
        session.startDate = new Date();

        session.sessionId = processName.concat(
                String.valueOf( session.startDate.getTime() )
                                                );

        return session;
    }

    public synchronized void close(String destroyer)
    {
        this.finishDate = new Date();
        this.closed     = true;
        this.processDestroyer = destroyer;
    }

    public synchronized boolean isClosed()
    {
        return this.closed;
    }

    public synchronized Date getStartDate()
    {
        return this.startDate;
    }

    public synchronized Date getFinishDate()
    {
        return this.finishDate;
    }

    public synchronized void registerPingerInfo(PingerInfo pingerInfo)
    {
        this.pingerInfo = pingerInfo;
    }

    public synchronized PingerInfo getPingerInfo()
    {
        return this.pingerInfo;
    }

    public synchronized String getProcessDestroyer()
    {
        return processDestroyer;
    }

    public String getSessionId()
    {
        return sessionId;
    }
}
