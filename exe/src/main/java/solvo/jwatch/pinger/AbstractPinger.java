/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 30, 2015
 */

package solvo.jwatch.pinger;

/**
 *
 * @author user_akrikheli
 */
public abstract class AbstractPinger
{
    public final static int NO_PING    = -1;
    public final static int PING_OK    = 0;

    protected String componentName;

    protected AbstractPinger()
    {

    }

    public abstract int ping();
    public abstract AbstractPinger newInstance();
    public abstract String getPingerName();
    public abstract void init();

    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    public String getComponentName()
    {
        return componentName;
    }
}
