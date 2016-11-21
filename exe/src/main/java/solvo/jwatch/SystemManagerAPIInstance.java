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
