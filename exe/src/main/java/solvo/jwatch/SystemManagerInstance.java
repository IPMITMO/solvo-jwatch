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
