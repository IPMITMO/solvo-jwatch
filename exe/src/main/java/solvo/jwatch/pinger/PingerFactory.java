/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 12, 2015
 */

package solvo.jwatch.pinger;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import solvo.gadgets.Lib;

/**
 *
 * @author user_akrikheli
 */
public class PingerFactory
{
    private Map<String, AbstractPinger> registry;

    private PingerFactory()
    {
        init();
    }

    private void init()
    {
        registry = new HashMap<>();
        try
	{
	    for ( AbstractPinger service : ServiceLoader.load(AbstractPinger.class) )
	    {
		Class<?> serviceClass = service.getClass();
                AbstractPinger instance = registry.get( service.getPingerName());

		if ( instance != null )
		{
		    Class<?> instanceClass = instance.getClass();
		    if ( serviceClass.isAssignableFrom(instanceClass) )
		    {
			Lib.log.debug("Service: Skipping Superclass " + serviceClass
				+ " for Subclass " + instanceClass);
			continue;
		    }
		    else
		    {
			Lib.log.debug("Service: Override Superclass " + instance.getClass()
				+ " by Subclass " + serviceClass);
		    }
		}

		registry.put(service.getPingerName(), service);
	    }
	}
	catch (Throwable t)
	{
	    Lib.log.fatal("Unable to load " + AbstractPinger.class.getName()
		    + " Services", t);
	}
    }

    private static class SingletonHolder
    {
	public static final PingerFactory HOLDER_INSTANCE = new PingerFactory();
    }

    public static AbstractPinger create(String pingerName)
    {
        AbstractPinger pinger = SingletonHolder.HOLDER_INSTANCE.registry.get(pingerName);
        if (pinger == null)
        {
            return null;
        }
        else
        {
            return pinger.newInstance();
        }
    }
}
