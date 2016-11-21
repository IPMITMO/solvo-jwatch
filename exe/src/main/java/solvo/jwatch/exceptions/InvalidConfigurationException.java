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
public class InvalidConfigurationException extends JwatchConfigurationException
{
    public InvalidConfigurationException()
    {
        super();
    }

    public InvalidConfigurationException(String message)
    {
        super(message);
    }

    public InvalidConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public InvalidConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
