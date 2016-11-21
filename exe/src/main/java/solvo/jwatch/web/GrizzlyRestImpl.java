/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 22, 2015
 */

package solvo.jwatch.web;

import solvo.jwatch.bo.OperationResult;

/**
 *
 * @author user_akrikheli
 */
public abstract class GrizzlyRestImpl
{
    protected static volatile boolean locked;
    private static final Object syncObj = new Object();

    public static OperationResult invoke(GrizzlyRestImpl impl)
    {
        if ( isLocked() )
        {
            return null;
        }
        else
        {
            synchronized (syncObj)
            {
                lock();
                OperationResult result = impl.invoke();
                unlock();
                return result;
            }
        }
    }

    protected static void lock()
    {
        locked = true;
    }

    protected static void unlock()
    {
        locked = false;
    }

    protected static boolean isLocked()
    {
        return locked;
    }

    protected abstract OperationResult invoke();
}
