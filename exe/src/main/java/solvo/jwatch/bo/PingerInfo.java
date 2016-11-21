/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 7, 2015
 */

package solvo.jwatch.bo;

import java.util.Date;


/**
 *
 * @author user_akrikheli
 */
public class PingerInfo
{
    public static final char STARTED_INFO   = 'S';
    public static final char PERFORMED_INFO = 'P';

    private final Date date;
    private final char type;

    private Boolean networkActivity;
    private int alarmNum;

    private PingerInfo(char type)
    {
        this.date = new Date();
        this.type = type;
    }

    public static PingerInfo createStartedInfo(int alarmNum)
    {
        PingerInfo pingerInfo = new PingerInfo(STARTED_INFO);
        pingerInfo.alarmNum  = alarmNum;

        return pingerInfo;
    }

    public static PingerInfo createPerformedInfo(int alarmNum, boolean networkActivity)
    {
        PingerInfo pingerInfo = new PingerInfo(PERFORMED_INFO);

        pingerInfo.alarmNum  = alarmNum;
        pingerInfo.networkActivity = networkActivity;

        return pingerInfo;
    }
    
    public Date getDate()
    {
        return date;
    }

    public Boolean hasNetworkActivity()
    {
        return networkActivity;
    }

    public int getAlarmNum()
    {
        return alarmNum;
    }

    public boolean isStarted()
    {
        return type == STARTED_INFO;
    }

    public boolean isPerformed()
    {
        return type == PERFORMED_INFO;
    }
}
