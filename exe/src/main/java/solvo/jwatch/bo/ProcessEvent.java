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
 *
 * @author user_akrikheli
 */
public class ProcessEvent extends AbstractEvent
{
    private String  processName;
    private ProcessInfo processInfo;

    public ProcessEvent()
    {
        super();
    }

    public ProcessInfo getProcessInfo()
    {
        return processInfo;
    }

    public void setProcessInfo(ProcessInfo processInfo)
    {
        this.processInfo = processInfo;
    }

    public String getProcessName()
    {
        return processName;
    }

    public void setProcessName(String processName)
    {
        this.processName = processName;
    }
}
