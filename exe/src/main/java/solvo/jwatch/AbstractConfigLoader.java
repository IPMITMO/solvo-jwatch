/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Jul 24, 2015
 */

package solvo.jwatch;

import java.io.File;
import solvo.jwatch.config.ConfigurationModel;
import solvo.jwatch.exceptions.ConfigurationNotSavedException;
import solvo.jwatch.exceptions.JwatchConfigurationException;

/**
 *
 * @author user_akrikheli
 */
public abstract class AbstractConfigLoader
{
    /**
     * Configuration file
     */
    protected File configFile;

    /**
     * Configuration model
     */
    protected ConfigurationModel configModel;
    
    /**
     * Loads configuration
     */
    public abstract void load();

    /**
     * Saves configuration
     * @param model configuration model
     * @throws JwatchConfigurationException 
     */
    public abstract void save(ConfigurationModel model) 
                                throws JwatchConfigurationException;

    /**
     * Return configuration files what loader is based on.
     * @return configuration file
     */
    public File getConfigFile()
    {
        return configFile;
    }
    
    public void setConfigFile(File configFile)
    {
        this.configFile = configFile;
    }
    
    /**
     * Returns configuration model
     * @return configuration model
     */
    public ConfigurationModel getConfig()
    {
        return configModel;
    }

    /**
     * Returns true - if configuration was read successful,
     * false - if configuration wasn't read.
     * @return result of reading config
     */
    public boolean isConfigurationRead()
    {
        if (configModel == null)
        {
            return false;
        }
        if (configModel.getMailProperties() == null)
        {
            return false;
        }
        
        return !configModel.getProcessDescriptionList().isEmpty();
    }
}