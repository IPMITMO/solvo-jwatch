/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Jul 28, 2015
 */

package solvo.jwatch.config;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user_akrikheli
 */
public class ConfigurationModel
{
    /**
     * Default values
     */
    public static int DEFAULT_MAX_PROCESS_COUNT = 100;
    public static int MAX_START_RETRIES         = 50;
    public static int RESTART_IS_FORBIDDEN      = -1;
    public static int DEFAULT_START_TIMEOUT     = 5 * 1000;

    private final List<WatchProcessDescription> processDescriptionList = new ArrayList<>();
    private MailProperties mailProperties;
    private Integer httpPort;
    private String  environment;
    private String  spool;

    public String getSpool() 
    {
        return spool;
    }

    public void setSpool(String spool) 
    {
        this.spool = spool;
    }
        
    public String getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(String environment)
    {
        this.environment = environment;
    }

    public Integer getHttpPort()
    {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort)
    {
        this.httpPort = httpPort;
    }

    public List<WatchProcessDescription> getProcessDescriptionList()
    {
        return processDescriptionList;
    }

    public MailProperties getMailProperties()
    {
        return mailProperties;
    }

    public void setMailProperties(MailProperties mailProperties)
    {
        this.mailProperties = mailProperties;
    }
}
