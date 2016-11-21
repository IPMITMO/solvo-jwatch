/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 12, 2015
 */

package solvo.jwatch.web;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author user_akrikheli
 */
public class ContainerResponseFilterImpl implements ContainerResponseFilter
{

    private static final String CONTENT_TYPE_NAME = "Content-Type";

    @Override
    public void filter( ContainerRequestContext requestContext,
                        ContainerResponseContext responseContext)
                            throws IOException
    {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        if ( !headers.containsKey(CONTENT_TYPE_NAME) )
        {
            headers.add(CONTENT_TYPE_NAME, "text/html; charset=UTF-8");
        }
    }

}
