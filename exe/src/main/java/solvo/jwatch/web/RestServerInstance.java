/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 29, 2015
 */

package solvo.jwatch.web;

/**
 *
 * @author artyom
 */
public class RestServerInstance 
{
    private RestServerInstance()
    {
        
    }
    
    private static final class Singleton
    {
        private static final RestServer SINGLETON_HOLDER 
                                                    = new RestServer();
    }
    
    public static RestServer get()
    {
        return Singleton.SINGLETON_HOLDER;
    }
}
