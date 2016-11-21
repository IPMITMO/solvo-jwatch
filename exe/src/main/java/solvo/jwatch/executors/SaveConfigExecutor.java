/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 12, 2015
 */

package solvo.jwatch.executors;

import solvo.gadgets.Lib;
import solvo.jwatch.bo.SystemModel;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.ConfigManager;
import solvo.jwatch.config.ConfigurationModel;
import solvo.jwatch.AbstractConfigLoader;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.SaveConfigEvent;
import solvo.jwatch.exceptions.ConfigurationNotFoundException;
import solvo.jwatch.exceptions.ConfigurationNotSavedException;
import solvo.jwatch.exceptions.InaccessibleOperationException;
import solvo.jwatch.exceptions.JwatchConfigurationException;

/**
 *
 * @author user_akrikheli
 */
public class SaveConfigExecutor extends AbstractSystemExecutor
{
    public SaveConfigExecutor()
    {
        super();
    }

    @Override
    public void validateSystemState() throws InaccessibleOperationException
    {
        super.validateSystemState();
        if ( !systemManager.canSystemSave())
        {
            SystemModel system = systemManager.getSystem();
            Lib.log.warn("System changes can not be saved now, current system state: " +
                                system.getSystemState());
            throw new InaccessibleOperationException();
        }
    }

    @Override
    public String getOperationName()
    {
        return "Save configuration";
    }

    @Override
    protected OperationResult perform(AbstractOperationEvent se) throws JwatchConfigurationException
    {
        Lib.log.info("Trying to save system changes...");
        SaveConfigEvent sce = (SaveConfigEvent)se;

        ConfigManager configManager = ConfigManager.get();
        AbstractConfigLoader configLoader = configManager.getConfigLoader();
        if (configLoader == null)
        {
            Lib.log.error("Current configuration file not found, can not apply changes");
            throw new ConfigurationNotFoundException();
        }

        ConfigurationModel model = sce.getConfigBuilder().
                build( configLoader.getConfig() );
        try
        {
            configManager.saveToXml(model);
            Lib.log.info("New configuration file is saved in " + configLoader.getConfigFile());
        }
        catch (ConfigurationNotSavedException ex)
        {
            Lib.log.error("Can not save configuration");
            throw ex;
        }

        try
        {
            systemManager.reloadConfiguration();
            systemManager.configureSystem();
        }
        catch (ConfigurationNotFoundException ex)
        {
            Lib.log.error("Can not load configuration file");
            throw ex;
        }
        return OperationResult.createCompleted(SystemState.STOPPED);
    }

    @Override
    public SystemState getIntermediateState()
    {
        return null;
    }

    @Override
    public AbstractExecutor newInstance()
    {
        return new SaveConfigExecutor();
    }

}
