/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 7, 2015
 */

package solvo.jwatch.exceptions;

/**
 *
 * @author user_akrikheli
 */
public class ConfigurationNotFoundException extends JwatchConfigurationException
{
    public ConfigurationNotFoundException()
    {
        super();
    }

    public ConfigurationNotFoundException(String message)
    {
        super(message);
    }

    public ConfigurationNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public ConfigurationNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
