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
public class InaccessibleOperationException extends Exception
{
    public InaccessibleOperationException()
    {
        super();
    }

    public InaccessibleOperationException(String message)
    {
        super(message);
    }

    public InaccessibleOperationException(Throwable cause)
    {
        super(cause);
    }

    public InaccessibleOperationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
