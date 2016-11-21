/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 3, 2015
 */

package solvo.jwatch.web;

import solvo.jwatch.web.html.HTMLConstants;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import solvo.jwatch.config.ConfigurationModel;
import solvo.jwatch.IConfigBuilder;
import solvo.jwatch.config.WatchProcessDescription;

/**
 *
 * @author user_akrikheli
 */
public class WebConfigBuilder implements IConfigBuilder
{
    private final MultivaluedMap<String, String> inFormParams;

    /**
     * Default constructor to initialize web config builder.
     * @param formParams parameters from POST-request
     * @throws NullPointerException if @param formParams is null
     */
    public WebConfigBuilder(MultivaluedMap<String, String> formParams)
    {
        if (formParams == null)
        {
            throw new NullPointerException();
        }
        inFormParams = formParams;
    }

    @Override
    public ConfigurationModel build(ConfigurationModel currentModel)
    {
        ConfigurationModel newModel = new ConfigurationModel();

        /**
         * First of all, we need to set main jwatch properties
         */
        newModel.setHttpPort( currentModel.getHttpPort() );
        newModel.setEnvironment( currentModel.getEnvironment() );
        newModel.setMailProperties( currentModel.getMailProperties() );
        newModel.setSpool( currentModel.getSpool() );

        List<WatchProcessDescription> currentProcessDescriptionList =
                                        currentModel.getProcessDescriptionList();
        for ( WatchProcessDescription currentProcessDescription : currentProcessDescriptionList )
        {
            String processName = currentProcessDescription.getProcessName();
            String key = processName + "_" + HTMLConstants.CHECKBOX_ENABLED_PREFIX;

            boolean newState        = isEnabled( inFormParams.get(key) );
            newModel.getProcessDescriptionList().add(
                    WatchProcessDescription.make(currentProcessDescription, newState) );
        }

        return newModel;
    }

    private boolean isEnabled(List<String> checkBoxParams)
    {
        if ( checkBoxParams == null || checkBoxParams.isEmpty() )
        {
            return false;
        }
        return "Enabled".equals( checkBoxParams.get(0) );
    }

}
