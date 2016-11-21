/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Jul 28, 2015
 */

package solvo.jwatch;

import java.io.File;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import solvo.gadgets.Lib;
import solvo.jwatch.config.ConfigurationModel;
import solvo.jwatch.config.WatchProcessDescription;
import solvo.jwatch.exceptions.ConfigurationNotFoundException;
import solvo.jwatch.exceptions.ConfigurationNotSavedException;

/**
 * Manager of jwatch configuration files.
 *
 * @author user_akrikheli
 */
public class ConfigManager
{   
    private enum ConfigurationFileExtensions
    {
        XML
        {
            @Override
            public String toString()
            {
                return "xml";
            }
        }        
    }
    
    private AbstractConfigLoader configLoader;

    private String configFilePath;        
    
    /**
     * Default constructor
     */
    private ConfigManager()
    {
        //Singleton
    }

    private static class SingletonHolder
    {
	public static final ConfigManager HOLDER_INSTANCE = new ConfigManager();
    }

    public static ConfigManager get()
    {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public void setConfigFilePath(String filePath)
    {
        this.configFilePath = filePath;
    }
    
    public String getConfigFilePath()
    {
        return this.configFilePath;
    }
    
    /**
     * Returns current config loader.
     * @return config loader
     */
    public AbstractConfigLoader getConfigLoader()
    {
        return configLoader;
    }
    
    public void setConfigLoader(AbstractConfigLoader loader)
    {
        this.configLoader = loader;
    }

    public ConfigurationModel getConfig()
    {
        return  configLoader != null     ?
                configLoader.getConfig() :
                null;
    }

    /**
     * Loads configuration file. If there are several configuration files, method
     * will load the first workable configuration files in order of priority.
     * If there aren't workable configuration files, method throws ConfigurationNotFoundException.
     *
     * @return 
     * @throws ConfigurationNotFoundException if there aren't working configuration
     */
    public AbstractConfigLoader loadConfig() throws ConfigurationNotFoundException
    {                     
        AbstractConfigLoader cl = buildConfigLoader(new File(configFilePath));
        cl.load();

        if ( cl.isConfigurationRead() )
        {
            if (Lib.log.isInfoEnabled())
            {
                Lib.log.info("Config '" + configFilePath + "' was read");
            }        
            return cl;
        }
        else
        {
            Lib.log.fatal("Config '" + configFilePath + "' couldn't be read");
            throw new ConfigurationNotFoundException();
        }                
    }

    public XMLConfigLoader saveToXml(ConfigurationModel configModel)
                                    throws ConfigurationNotSavedException
    {
        if (configModel == null)
        {
            throw new ConfigurationNotSavedException();
        }

        File configFile = configLoader.getConfigFile();

        XMLConfigLoader xmlConfigLoader = new XMLConfigLoader();
        xmlConfigLoader.setConfigFile(configFile);
        if (Lib.log.isDebugEnabled())
        {
            Lib.log.debug("Saving configuration as xml in '" + configFile + "'");
        }
        xmlConfigLoader.save(configModel);
        if (Lib.log.isDebugEnabled())
        {
            Lib.log.debug("Configuration was saved as xml format in '" + configFile + "'");
        }
        return xmlConfigLoader;
    }

    public void logConfiguration() throws ConfigurationNotFoundException
    {
        if (configLoader == null)
        {
            throw new ConfigurationNotFoundException();
        }

        /* Log configuration */
        if (Lib.log.isDebugEnabled())
        {
            Lib.log.debug("=== JWATCH CONFIGURATION ===");
            List<WatchProcessDescription> wpdList = getConfig().getProcessDescriptionList();
            for (WatchProcessDescription wpd : wpdList)
            {
                if ( wpd.isEnabled() )
                {
                    Lib.log.debug("Process " + wpd.getProcessName() + " is enabled");
                }
                else
                {
                    Lib.log.debug("Process " + wpd.getProcessName() + " is disabled");
                }
            }            
        }
    }
   
    protected AbstractConfigLoader buildConfigLoader(File configFile)
    {        
        String extension = FilenameUtils.getExtension(configFile.getName());
        
        if ( Objects.equals(extension, 
                    ConfigurationFileExtensions.XML.toString()) )
        {
            XMLConfigLoader xmlConfigLoader = new XMLConfigLoader();
            xmlConfigLoader.setConfigFile(configFile);
            
            return xmlConfigLoader;
        }

        throw new RuntimeException();
    }       
}
