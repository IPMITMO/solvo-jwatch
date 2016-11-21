/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
