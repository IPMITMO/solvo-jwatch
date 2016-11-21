/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 27, 2015
 */

package solvo.jwatch.web;

import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.SystemManagerAPIInstance;

/**
 *
 * @author user_akrikheli
 */
@Path("/system")
public class RestSystemService
{
    @GET
    @Path("/start")
    public Response start() throws Throwable
    {
        Lib.log.info("System service: starting system...");
        
        OperationResult result = GrizzlyRestImpl.invoke(new GrizzlyRestImpl()
        {
            @Override
            protected OperationResult invoke()
            {
                return SystemManagerAPIInstance.get().startSystem();
            }
        });                
        
        return Response.temporaryRedirect(RestServiceHelper.getRedirectUriByResul(result)).
                build();        
    }
    
    @GET
    @Path("/stop")
    public Response stop() throws Throwable
    {
        Lib.log.info("System service: stopping system...");
        OperationResult result = GrizzlyRestImpl.invoke(new GrizzlyRestImpl()
        {
            @Override
            protected OperationResult invoke()
            {
                return SystemManagerAPIInstance.get().stopSystem();
            }
        });

        return Response.temporaryRedirect(RestServiceHelper.getRedirectUriByResul(result)).
                build();
    }

    @GET
    @Path("/reload")
    public Response reload() throws Throwable
    {
        Lib.log.info("System service: reloading system...");
        OperationResult result = GrizzlyRestImpl.invoke(new GrizzlyRestImpl()
        {
            @Override
            protected OperationResult invoke()
            {
                return SystemManagerAPIInstance.get().reloadSystem();
            }
        });

        return Response.temporaryRedirect(RestServiceHelper.getRedirectUriByResul(result)).
                build();
    }

    @GET
    @Path("/shutDown")
    public Response shutDown() throws Throwable
    {
        Lib.log.info("System service: shutting down system...");
        Thread shutdownThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SystemManagerAPIInstance.get().shutDownSystem();
            }
        });

        shutdownThread.start();
        return Response.temporaryRedirect(new URI(RestServer.GOODBYE_SUFFIX)).build();

    }

    @POST
    @Path("/save")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response save(MultivaluedMap<String, String> inFormParams) throws Throwable
    {
        Lib.log.info("System service: savig system configuration...");
        final WebConfigBuilder wcb = new WebConfigBuilder(inFormParams);
        OperationResult result = GrizzlyRestImpl.invoke(new GrizzlyRestImpl()
        {
            @Override
            protected OperationResult invoke()
            {
                return SystemManagerAPIInstance.get().saveConfig(wcb);
            }
        });

        return Response.seeOther(RestServiceHelper.getRedirectUriByResul(result)).
                build();
    }
}
