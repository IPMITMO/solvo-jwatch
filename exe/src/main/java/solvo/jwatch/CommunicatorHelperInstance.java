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
public class CommunicatorHelperInstance 
{
    private CommunicatorHelperInstance()
    {
        
    }
    
    private static final class Singleton
    {
        private static final CommunicatorHelper SINGLETON_HOLDER 
                                                    = new CommunicatorHelper();
    }
    
    public static CommunicatorHelper get()
    {
        return Singleton.SINGLETON_HOLDER;
    }
}
