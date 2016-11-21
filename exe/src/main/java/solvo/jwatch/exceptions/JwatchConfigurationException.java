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
public abstract class JwatchConfigurationException extends Exception
{
    public JwatchConfigurationException()
    {
        super();
    }

    public JwatchConfigurationException(String message)
    {
        super(message);
    }

    public JwatchConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public JwatchConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
