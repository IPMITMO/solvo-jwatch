/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 16, 2015
 */

package solvo.jwatch.web.html;

import org.apache.commons.lang3.StringUtils;
import solvo.jwatch.web.html.pojo.SystemHTML;
import solvo.jwatch.bo.SystemState;
import static solvo.jwatch.web.html.HTMLConstants.CLASS_ACTIVE_CMD;
import static solvo.jwatch.web.html.HTMLConstants.CLASS_SHUT_DOWN_CMD;
import static solvo.jwatch.web.html.HTMLConstants.CLASS_SYSTEM_CMD;
import static solvo.jwatch.web.html.HTMLConstants.CLASS_TEXT_ERROR_INFO;
import solvo.jwatch.web.html.pojo.SimpleHtmlElement;
import solvo.jwatch.web.html.pojo.SimpleTextContent;

/**
 *
 * @author user_akrikheli
 */
public class SystemHTMLHolder
{
    private SystemHTML systemHtml;

    public static SystemHTMLHolder createByPojo(SystemHTML pojo)
    {
        SystemHTMLHolder holder = new SystemHTMLHolder();
        holder.setSystemHtml(pojo);

        return holder;
    }

    protected SystemHTMLHolder()
    {

    }

    public SystemHTML getSystemHtml()
    {
        return systemHtml;
    }

    public void setSystemHtml(SystemHTML systemHtml)
    {
        this.systemHtml = systemHtml;
    }

    /**
     * Checker dependent on the system state.
     * Returns true if 'start' command should be active, else - false.
     * @return true if 'start' command should be active, else - false.
     */
    public boolean isStartAvailable()
    {
        return systemHtml.getSystemState() == SystemState.STOPPED;
    }

    /**
     * Checker dependent on the system state.
     * Returns true if 'stop' command should be active, else - false.
     * @return true if 'stop' command should be active, else - false.
     */
    public boolean isStopAvailable()
    {
        return systemHtml.getSystemState() == SystemState.RUNNING;
    }

    /**
     * Checker dependent on the system state.
     * Returns true if 'reload' command should be active, else - false.
     * @return true if 'reload' command should be active, else - false.
     */
    public boolean isReloadAvailable()
    {
        SystemState systemState = systemHtml.getSystemState();
        return systemState == SystemState.RUNNING || systemState == SystemState.STOPPED;
    }

    /**
     * Checker dependent on the system state.
     * Returns true if 'save' command should be active, else - false.
     * @return true if 'save' command should be active, else - false.
     */
    public boolean isSaveAvailable()
    {
        return systemHtml.getSystemState() == SystemState.STOPPED;
    }

    /**
     * Checker dependent on the system state.
     * Returns true if 'shut down' command should be active, else - false.
     * @return true if 'shut down' command should be active, else - false.
     */
    public boolean isShutDownAvailable()
    {
        return systemHtml.getSystemState() != SystemState.HALTING;
    }

    //
    // This methods return HTML as String
    //

    public String getStartCmd()
    {
        SimpleHtmlElement cmd;
        if ( isStartAvailable() )
        {
            cmd = SimpleHtmlElement.createActiveCommand("Start system", "../system/start");
        }
        else
        {
            cmd = SimpleHtmlElement.createInactiveCommand("Start system");
        }
        cmd.addClass(CLASS_SYSTEM_CMD);
        return cmd.build().toString();
    }

    public String getStopCmd()
    {
        SimpleHtmlElement cmd;
        if ( isStopAvailable())
        {
            cmd = SimpleHtmlElement.createActiveCommand("Stop system", "../system/stop");
        }
        else
        {
            cmd = SimpleHtmlElement.createInactiveCommand("Stop system");
        }
        cmd.addClass(CLASS_SYSTEM_CMD);
        return cmd.build().toString();
    }

    public String getJsShutdownListener()
    {
        if ( isShutDownAvailable() )
        {
            return " document.getElementById(\"shutdown_cmd\")."
                    + "addEventListener(\"click\", shutDown_onclick); ";
        }
        else
        {
            return " ";
        }
    }

    public String getShutDownCmd()
    {
        SimpleHtmlElement cmd;
        if ( isShutDownAvailable() )
        {
            SimpleTextContent textContent = new SimpleTextContent("Shut down");

            cmd = new SimpleHtmlElement("a");

            cmd.addClass(CLASS_ACTIVE_CMD);
            cmd.addClass(CLASS_SHUT_DOWN_CMD);
            cmd.addAttribute("id", "shutdown_cmd");

            cmd.appendChild(textContent);
        }
        else
        {
            cmd = SimpleHtmlElement.createInactiveCommand("Shut down");
        }
        cmd.addClass(CLASS_SYSTEM_CMD);

        return cmd.build().toString();
    }

    public String getReloadCmd()
    {
        SimpleHtmlElement cmd;
        if ( isReloadAvailable())
        {
            cmd = SimpleHtmlElement.createActiveCommand("Reload system", "../system/reload");
        }
        else
        {
            cmd = SimpleHtmlElement.createInactiveCommand("Reload system");
        }
        cmd.addClass(CLASS_SYSTEM_CMD);
        return cmd.build().toString();
    }

    public String getEnvironmentName()
    {
        String environmentName = systemHtml.getEnvironment();
        SimpleHtmlElement h2 = new SimpleHtmlElement("h2");
        if ( StringUtils.isNotEmpty(environmentName) )
        {
            h2.appendChild(new SimpleTextContent(environmentName));
        }
        else
        {
            h2.appendChild(new SimpleTextContent("Untitled"));
        }
        return  h2.build().toString();
    }

    public String getSystemState()
    {
        SimpleHtmlElement spanElement = new SimpleHtmlElement("span");
        SystemState systemState = systemHtml.getSystemState();

        switch (systemState)
        {
            case RUNNING:
                spanElement.addClass(HTMLConstants.CLASS_STATE_RUNNING);
                break;
            case HALTING:
                spanElement.addClass(HTMLConstants.CLASS_STATE_HALTING);
        }
        spanElement.appendChild(new SimpleTextContent(systemHtml.getSystemState().toString()));

        return spanElement.build().toString();
    }

    public String getErrorMessage()
    {
        String errorMessage = systemHtml.getErrorMessage();
        if (errorMessage != null)
        {
            SimpleHtmlElement pElement1 = new SimpleHtmlElement("p");
            SimpleHtmlElement pElement2 = new SimpleHtmlElement("p");

            if (errorMessage.equals("locked"))
            {
                pElement1.appendChild(new
                        SimpleTextContent("Jwatch is locked, another operation is performing now"));
                pElement2.addClass(CLASS_TEXT_ERROR_INFO);
                pElement2.appendChild(new SimpleTextContent("Update and waiting..."));
            }
            else
            {
                pElement1.appendChild(new
                        SimpleTextContent("Error occured, see log fore more details..."));
                pElement2.addClass(CLASS_TEXT_ERROR_INFO);
                pElement2.appendChild(new SimpleTextContent(errorMessage));
            }

            return pElement1.build().toString().concat( pElement2.build().toString() );
        }
        else
        {
            return "";
        }
    }

    public String getSaveBtn()
    {
        SimpleHtmlElement inputElement = new SimpleHtmlElement("input", false);
        inputElement.addClass("btn");
        inputElement.addAttribute("type", "submit");
        inputElement.addAttribute("value", "Save configuration");

        if (!isSaveAvailable())
        {
            inputElement.addAttribute("disabled");
        }

        return inputElement.build().toString();
    }

}
