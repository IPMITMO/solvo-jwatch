/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 17, 2015
 */

package solvo.jwatch.web.html.pojo;

import solvo.jwatch.bo.SystemState;

/**
 *
 * @author user_akrikheli
 */
public class SystemHTML
{
    private SystemState systemState;
    private String errorMessage;
    private String environment;

    public SystemState getSystemState()
    {
        return systemState;
    }

    public void setSystemState(SystemState systemState)
    {
        this.systemState = systemState;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(String environment)
    {
        this.environment = environment;
    }
}
