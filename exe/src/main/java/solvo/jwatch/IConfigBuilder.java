/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 3, 2015
 */

package solvo.jwatch;

import solvo.jwatch.config.ConfigurationModel;

/**
 *
 * @author user_akrikheli
 */
public interface IConfigBuilder
{
    ConfigurationModel build(ConfigurationModel currentModel);
}
