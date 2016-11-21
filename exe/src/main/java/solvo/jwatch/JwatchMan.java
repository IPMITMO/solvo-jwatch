/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Jul 22, 2015
 */

package solvo.jwatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.AbstractEvent;
import solvo.jwatch.config.ConfigurationModel;
import solvo.jwatch.exceptions.ConfigurationNotFoundException;
import solvo.jwatch.exceptions.InvalidConfigurationException;
import solvo.jwatch.executors.ExecutorFactory;
import solvo.jwatch.executors.ReloadingExecutor;
import solvo.jwatch.executors.SaveConfigExecutor;
import solvo.jwatch.executors.ShutdownSystemExecutor;
import solvo.jwatch.executors.ShutdownSystemForciblyExecutor;
import solvo.jwatch.executors.StartProcessExecutor;
import solvo.jwatch.executors.StartSystemExecutor;
import solvo.jwatch.executors.StopProcessExecutor;
import solvo.jwatch.executors.StopSystemExecutor;
import solvo.jwatch.web.RestServerInstance;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 *
 * @author user_akrikheli
 */
public class JwatchMan
{   
    private static final Options options = new Options();
    private static CommandLine commandLine;
        
    private static void init()
    {        
        Option configOption = Option.builder("c")
                         .longOpt("config")
                         .required(true)
                         .desc("Path to configuration file")
                         .hasArg(true)                         
                         .build();
        options.addOption(configOption);
    }
    
    private static void parseArgs(String[] args)
    {
        CommandLineParser parser = new DefaultParser();
        try
        {
            commandLine = parser.parse(options, args);            
        }
        catch (ParseException ex)
        {
            Lib.log.error("Error occurred while parse arguments", ex);
            System.err.println("ERROR: can not start jwatch, see log for more details");
            System.exit(-1);
        }
    }
       
    public static void main(String[] args)
    {                          
        init();
        parseArgs(args);
        String pathToConfig = commandLine.getOptionValue("c");
        
        ConfigManager configManager = ConfigManager.get();
        configManager.setConfigFilePath(pathToConfig);
        
        try
        {            
            AbstractConfigLoader loader = configManager.loadConfig();
            configManager.setConfigLoader(loader);
            configManager.logConfiguration();
        }
        catch (ConfigurationNotFoundException cnfe)
        {
            Lib.log.fatal("Jwatch configuration not found", cnfe);
            System.err.println("ERROR: can not read configuration, see log for more details");
            System.exit(-1);
        }        

        final SystemManagerAPI api  = SystemManagerAPIInstance.get();
        final SystemManager sm      = SystemManagerInstance.get();
        try
        {
            sm.initialize();
        }
        catch (InvalidConfigurationException ex)
        {
            Lib.log.fatal("Jwatch configuration invalid", ex);
            System.err.println("ERROR: can not initialize system, see log for more details");
            System.exit(-1);
        }

        initExecutors();
        assignHandlers(sm);
        final ShutdownHookManager shutdownHookManager = ShutdownHookManagerInstance.get();
        shutdownHookManager.createShutdownHook(true);
        startHttpServerIfRequired();

        Runnable startSystem = () -> 
        {
            api.startSystem();
        };
        Thread startThread = new Thread(startSystem);
        startThread.start();

        Watcher.watch();
        RestServerInstance.get().stopServer();
    }

    private static void assignHandlers(final SystemManager sm)
    {
        //
        // Add HUP handler
        //
        SignalHandler sh = (Signal signal) -> {
            SystemManagerAPIInstance.get().reloadSystem();
        };
        Signal.handle(new Signal("HUP"), sh);
    }

    private static void startHttpServerIfRequired()
    {
        ConfigurationModel cm = ConfigManager.get().getConfig();
        Integer httpPort = cm.getHttpPort();

        if (httpPort != null)
        {
            if (Lib.log.isInfoEnabled())
            {
                Lib.log.info("Trying to start http server, port: " + httpPort);
            }
            try
            {
                RestServerInstance.get().startServer(httpPort);
            }
            catch (Exception ex)
            {
                Lib.log.error("Can not start http server", ex);
            }
        }
        else
        {
            Lib.log.info("Http port not defined in configuration file");
        }
    }
    
    private static void initExecutors()
    {
        ExecutorFactory registry = ExecutorFactory.getInstance();

        registry.addExecutor(AbstractEvent.START_SYSTEM,        new StartSystemExecutor());
        registry.addExecutor(AbstractEvent.STOP_SYSTEM,         new StopSystemExecutor());
        registry.addExecutor(AbstractEvent.RELOAD_SYSTEM,       new ReloadingExecutor());
        registry.addExecutor(AbstractEvent.STOP_SYSTEM,         new StopSystemExecutor());
        registry.addExecutor(AbstractEvent.SAVE_SYSTEM,         new SaveConfigExecutor());
        registry.addExecutor(AbstractEvent.SHUT_DOWN_SYSTEM,    new ShutdownSystemExecutor());
        registry.addExecutor(AbstractEvent.SHUT_DOWN_SYSTEM_FORCIBLY,
                                            new ShutdownSystemForciblyExecutor());

        registry.addExecutor(AbstractEvent.START_PROCESS,       new StartProcessExecutor());
        registry.addExecutor(AbstractEvent.STOP_PROCESS,        new StopProcessExecutor());
    }    
}
