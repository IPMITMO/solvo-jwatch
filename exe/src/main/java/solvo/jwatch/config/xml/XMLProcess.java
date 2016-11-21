/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 11, 2015
 */

package solvo.jwatch.config.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import solvo.jwatch.config.WatchProcessDescription;

/**
 *
 * @author user_akrikheli
 */

public class XMLProcess
{
    /**
     * Mandatory values
     */
    private boolean disabled    = WatchProcessDescription.DEFAULT_DISABLED_VALUE;
    private boolean pingable    = WatchProcessDescription.DEFAULT_PINGABLE_VALUE;

    private String name;
    private int order;
    private int startRetries;
    private String cmd;
    private String coreDir;
    private int startTimeout;
    private int pingAlarms      = WatchProcessDescription.DEFAULT_PING_ALARMS_VALUE;
    private int pingDelay       = WatchProcessDescription.DEFAULT_PING_DELAY_VALUE;
    private int pingInterval    = WatchProcessDescription.DEFAULT_PING_INTERVAL_VALUE;
    private String componentName;
    private String mail;
    private String pingerImpl;

    @XmlAttribute(name = "disabled")
    public boolean isDisabled()
    {
        return disabled;
    }

    public void setDisabled(boolean disabled)
    {
        this.disabled = disabled;
    }

    @XmlAttribute(name = "pingable")
    public boolean isPingable()
    {
        return pingable;
    }

    public void setPingable(boolean pingable)
    {
        this.pingable = pingable;
    }

    @XmlElement(name = "name")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlElement(name = "order")
    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    @XmlElement(name = "startRetries")
    public int getStartRetries()
    {
        return startRetries;
    }

    public void setStartRetries(int startRetries)
    {
        this.startRetries = startRetries;
    }

    @XmlElement(name = "cmd")
    public String getCmd()
    {
        return cmd;
    }

    public void setCmd(String cmd)
    {
        this.cmd = cmd;
    }

    @XmlElement(name = "coreDir")
    public String getCoreDir()
    {
        return coreDir;
    }

    public void setCoreDir(String coreDir)
    {
        this.coreDir = coreDir;
    }

    @XmlElement(name = "startTimeout")
    public int getStartTimeout()
    {
        return startTimeout;
    }

    public void setStartTimeout(int startTimeout)
    {
        this.startTimeout = startTimeout;
    }

    @XmlElement(name = "pingAlarms")
    public int getPingAlarms()
    {
        return pingAlarms;
    }

    public void setPingAlarms(int pingAlarms)
    {
        this.pingAlarms = pingAlarms;
    }

    @XmlElement(name = "pingDelay")
    public int getPingDelay()
    {
        return pingDelay;
    }

    public void setPingDelay(int pingDelay)
    {
        this.pingDelay = pingDelay;
    }

    @XmlElement(name = "pingInterval")
    public int getPingInterval()
    {
        return pingInterval;
    }

    public void setPingInterval(int pingInterval)
    {
        this.pingInterval = pingInterval;
    }

    @XmlElement(name = "pingerImpl")
    public String getPingerImpl()
    {
        return pingerImpl;
    }

    public void setPingerImpl(String pingerImpl)
    {
        this.pingerImpl = pingerImpl;
    }

    @XmlElement(name = "mail")
    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    @XmlElement(name = "componentName")
    public String getComponentName()
    {
        return componentName;
    }

    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }


}
