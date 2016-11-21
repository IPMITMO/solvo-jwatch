/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 11, 2015
 */

package solvo.jwatch.config.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author user_akrikheli
 */
public class XMLProperties
{
    private Integer httpPort;
    private String  environment;
    private String spool;
    private XMLMailProperties mailProperties;

    @XmlElement(name = "httpPort")
    public Integer getHttpPort()
    {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort)
    {
        this.httpPort = httpPort;
    }
    
    @XmlElement(name = "spool")
    public String getSpool()
    {
        return spool;
    }

    public void setSpool(String spool)
    {
        this.spool = spool;
    }

    @XmlElement(name = "environment")
    public String getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(String environment)
    {
        this.environment = environment;
    }

    @XmlElement(name = "mail")
    public XMLMailProperties getMailProperties()
    {
        return mailProperties;
    }

    public void setMailProperties(XMLMailProperties mailProperties)
    {
        this.mailProperties = mailProperties;
    }
}
