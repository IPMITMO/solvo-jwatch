/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 29, 2015
 */

package solvo.jwatch;

/**
 *
 * @author artyom
 */
public class SystemManagerAPIInstance 
{
    private SystemManagerAPIInstance()
    {
        
    }
    
    private static final class Singleton
    {
        private static final SystemManagerAPI SINGLETON_HOLDER 
                                                    = new SystemManagerAPI();
    }
    
    public static SystemManagerAPI get()
    {
        return Singleton.SINGLETON_HOLDER;
    }
}
