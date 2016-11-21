/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Nov 16, 2015
 */

package solvo.jwatch.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import solvo.gadgets.Lib;

/**
 *
 * @author user_akrikheli
 */
public class EventHandlerRegistry
{
    private Map<String, IEventHandler> registry;

    private void init()
    {
        registry = new HashMap<>();
        try
	{
	    for ( IEventHandler service : ServiceLoader.load(IEventHandler.class) )
	    {
		Class<?> serviceClass = service.getClass();
                IEventHandler instance = registry.get( service.getEventName());

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

		registry.put(service.getEventName(), service);
	    }
	}
	catch (Throwable t)
	{
	    Lib.log.fatal("Unable to load " + IEventHandler.class.getName()
		    + " Services", t);
	}
    }

    private EventHandlerRegistry()
    {
        init();
    }

    private static class SingletonHolder
    {
	public static final EventHandlerRegistry HOLDER_INSTANCE = new EventHandlerRegistry();
    }

    public static IEventHandler getHandler(String eventName)
    {
        return SingletonHolder.HOLDER_INSTANCE.registry.get(eventName);
    }
}
