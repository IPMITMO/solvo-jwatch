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
public class SystemManagerInstance 
{
    private SystemManagerInstance()
    {
        
    }
    
    private static final class Singleton
    {
        private static final SystemManager SINGLETON_HOLDER 
                                                    = new SystemManager();
    }
    
    public static SystemManager get()
    {
        return Singleton.SINGLETON_HOLDER;
    }
}
