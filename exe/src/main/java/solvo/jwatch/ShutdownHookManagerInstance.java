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
public class ShutdownHookManagerInstance 
{
    private ShutdownHookManagerInstance()
    {
        
    }
    
    private static final class Singleton
    {
        private static final ShutdownHookManager SINGLETON_HOLDER 
                                                    = new ShutdownHookManager();
    }
    
    public static ShutdownHookManager get()
    {
        return Singleton.SINGLETON_HOLDER;
    }
}
