/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 17, 2015
 */

package solvo.jwatch.web;

import java.net.URI;
import solvo.jwatch.bo.OperationResult;

/**
 *
 * @author user_akrikheli
 */
public class RestServiceHelper
{
    public static URI getRedirectUriByResul(OperationResult operationResult)
    {
        return getRedirectUriByResult(operationResult, RestServer.JWATCH_SUFFIX);
    }

    public static URI getRedirectUriByResult(OperationResult operationResult,
                                            String completedUrl)
    {
        URI uri = null;
        if (operationResult == null)
        {
            uri = URI.create(RestServer.JWATCH_SUFFIX + "?" + RestServer.ERROR_PARAMETER +
                        "=locked");
            return uri;
        }

        OperationResult.ResultEnum result = operationResult.getResult();

        switch (result)
        {
            case COMPLETED:
                uri = URI.create(completedUrl);
                break;
            case INACCESSIBLE:
                uri = URI.create(RestServer.JWATCH_SUFFIX + "?" + RestServer.ERROR_PARAMETER +
                        "=InaccessibleOperation");
                break;
            case FAILING:
                Throwable ex = operationResult.getEx();
                uri = URI.create(RestServer.JWATCH_SUFFIX + "?" + RestServer.ERROR_PARAMETER +
                        "=" + ex.getClass().getSimpleName());
                break;
        }
        return uri;
    }
}
