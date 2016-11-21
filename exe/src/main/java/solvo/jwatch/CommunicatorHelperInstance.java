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
