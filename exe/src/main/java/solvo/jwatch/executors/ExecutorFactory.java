/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 14, 2015
 */

package solvo.jwatch.executors;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author user_akrikheli
 */
public class ExecutorFactory
{
    private final Map<String, AbstractExecutor> registry = new HashMap<>();

    private ExecutorFactory()
    {
        //Singleton
    }

    public AbstractExecutor addExecutor(String key, AbstractExecutor executor)
    {
        return registry.put(key, executor);
    }

    private static class SingletonHolder
    {
	public static final ExecutorFactory HOLDER_INSTANCE = new ExecutorFactory();
    }

    public AbstractExecutor createExecutor(String eventName)
    {
        AbstractExecutor executor = registry.get(eventName);
        if (executor != null)
        {
            return executor.newInstance();
        }
        else
        {
            return null;
        }
    }

    public static ExecutorFactory getInstance()
    {
        return SingletonHolder.HOLDER_INSTANCE;
    }
}
