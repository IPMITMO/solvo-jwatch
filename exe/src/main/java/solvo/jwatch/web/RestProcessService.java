/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 14, 2015
 */

package solvo.jwatch.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import solvo.gadgets.Lib;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.SystemManagerAPIInstance;

/**
 *
 * @author user_akrikheli
 */
@Path("/process")
public class RestProcessService
{
    @GET
    @Path("/start/{processName}")
    public Response start(@PathParam("processName") String processName)
    {
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info("Process service: starting process '" + processName + "'...");
        }
        OperationResult result = SystemManagerAPIInstance.get().startProcess(processName);

        return Response.temporaryRedirect( RestServiceHelper.getRedirectUriByResul(result) ).
                build();
    }

    @GET
    @Path("/stop/{processName}")
    public Response stop(@PathParam("processName") String processName)
    {
        if (Lib.log.isInfoEnabled())
        {
            Lib.log.info("Process service: stopping process '" + processName + "'...");
        }
        OperationResult result = SystemManagerAPIInstance.get().stopProcess(processName);

        return Response.temporaryRedirect( RestServiceHelper.getRedirectUriByResul(result) ).
                build();
    }
}
