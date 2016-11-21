/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solvo.jwatch.web;

import solvo.jwatch.*;

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
