/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 27, 2015
 */

package solvo.jwatch.web;


import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import solvo.gadgets.Lib;

/**
 *
 * @author user_akrikheli
 */
public class RestServer
{
    public static final String JWATCH_SUFFIX    = "/jwatch";
    public static final String GOODBYE_SUFFIX   = "/goodBye";
    public static final String ERROR_PARAMETER  = "error";

    private HttpServer httpServer;
    private int httpPort;

    public synchronized void startServer(int port) throws IOException
    {
        if (httpServer != null)
        {
            return;
        }

        httpPort = port;

        ResourceConfig config = new ResourceConfig();
        config.register(RestProcessService.class);
        config.register(RestSystemService.class);      
        config.register(new ContainerResponseFilterImpl());

        Lib.log.info("Starting grizzly...");
        httpServer =  GrizzlyHttpServerFactory.
                    createHttpServer(URI.create("http://0.0.0.0:" + httpPort), config, false);
        httpServer.getServerConfiguration().addHttpHandler(new HttpJwatchPageHandler(),
                                                            JWATCH_SUFFIX);
        httpServer.getServerConfiguration().addHttpHandler(new HttpGoodbyePageHandler(),
                                                            GOODBYE_SUFFIX);

        httpServer.start();
    }

    public synchronized void stopServer()
    {
	if (httpServer != null)
	{
	    Lib.log.info("Stopping grizzly...");
	    httpServer.shutdownNow();
	}
        else
        {
            Lib.log.info("Http server is not found, skip stopping server...");
        }
    }
}
