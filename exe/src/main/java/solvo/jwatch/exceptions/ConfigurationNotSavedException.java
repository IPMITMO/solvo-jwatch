/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 10, 2015
 */

package solvo.jwatch.exceptions;

/**
 *
 * @author user_akrikheli
 */
public class ConfigurationNotSavedException extends JwatchConfigurationException
{
    public ConfigurationNotSavedException()
    {
        super();
    }

    public ConfigurationNotSavedException(String message)
    {
        super(message);
    }

    public ConfigurationNotSavedException(Throwable cause)
    {
        super(cause);
    }

    public ConfigurationNotSavedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
