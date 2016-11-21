/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 16, 2015
 */

package solvo.jwatch.web.html;

import java.text.SimpleDateFormat;
import java.util.Date;
import solvo.jwatch.bo.PingerInfo;
import solvo.jwatch.bo.ProcessSession;
import solvo.jwatch.web.html.pojo.ProcessHTML;
import solvo.jwatch.bo.ProcessState;
import solvo.jwatch.bo.SystemState;
import static solvo.jwatch.web.html.HTMLConstants.CHECKBOX_ENABLED_PREFIX;
import static solvo.jwatch.web.html.HTMLConstants.CLASS_INCIDENTAL_PROCESS_NAME;
import static solvo.jwatch.web.html.HTMLConstants.CLASS_STATE_INACTIVE;
import static solvo.jwatch.web.html.HTMLConstants.CLASS_STATE_RUNNING;
import static solvo.jwatch.web.html.HTMLConstants.CLASS_PROCESS_STATE_TERMINATED;
import solvo.jwatch.web.html.pojo.SimpleHtmlElement;
import solvo.jwatch.web.html.pojo.SimpleTextContent;

/**
 *
 * @author user_akrikheli
 */
public class ProcessHTMLHolder
{
    private ProcessHTML processHtml;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm:ss");

    public static ProcessHTMLHolder createByPojo(ProcessHTML pojo)
    {
        ProcessHTMLHolder holder = new ProcessHTMLHolder();
        holder.setProcessHtml(pojo);

        return holder;
    }

    protected ProcessHTMLHolder()
    {

    }

    public ProcessHTML getProcessHtml()
    {
        return processHtml;
    }

    public void setProcessHtml(ProcessHTML processHtml)
    {
        this.processHtml = processHtml;
    }

    /**
     * Checker dependent on the system state.
     * Returns true if checkboxes should be active, else - false.
     * @return true if checkboxes should be active, else - false.
     */
    public boolean isCheckboxActive()
    {
        return processHtml.getSystemState() ==  SystemState.STOPPED;
    }

    /**
     * Checker dependent on the system state and process state.
     * Returns true if management menu should be available, else - false.
     * @return true if management menu should be available, else - false.
     */
    public boolean isManagementAvailable()
    {
        return processHtml.getSystemState() == SystemState.RUNNING && processHtml.isEnabled();
    }

    /**
     * Checker dependent on the system state and process state in the aggregate with 'dead' flag.
     * Returns true if 'start process' command should be available, else - false.
     * @return true if 'start process' command should be available, else - false.
     */
    public boolean isStartAvailable()
    {
        ProcessState processState = processHtml.getProcessState();

        switch (processState)
        {
            case FAILED_TO_START:
            case SHUT_DOWN:
                return true;
            case TERMINATED:
                return processHtml.isDead();
            default:
                return false;
        }

        /*
        return processState == ProcessState.SHUT_DOWN || processState == ProcessState.INACTIVE ||
                ( processState == ProcessState.TERMINATED && processHtml.isDead() );
        */
    }

    /**
     * Checker dependent on the system state and process state.
     * Returns true if 'stop process' command should be available, else - false.
     * @return true if 'stop process' command should be available, else - false.
     */
    public boolean isStopAvailable()
    {
        ProcessState processState = processHtml.getProcessState();

        switch (processState)
        {
            case SHUT_DOWN:
            case FAILED_TO_START:
                return false;
            case TERMINATED:
                return !processHtml.isDead();
            case RUNNING:
                return true;
            default:
                return false;
        }
    }

     //
    // This methods return HTML as String
    //

    public StringBuilder getHTML()
    {
        SimpleHtmlElement trElement = SimpleHtmlElement.createTrElement();

        trElement.appendChild( getProcessName() );
        trElement.appendChild( getProcessState() );
        trElement.appendChild( getProcessEnabledCheckBox() );
        trElement.appendChild( getProcessManagement() );
        trElement.appendChild( getProcessNetworkActivity() );


        return trElement.build();
    }

    private SimpleHtmlElement getProcessName()
    {
        SimpleHtmlElement tdElement = SimpleHtmlElement.createTdElement();
        SimpleTextContent textContent = new SimpleTextContent( processHtml.getProcessName() );
        tdElement.appendChild(textContent);
        if (processHtml.isIncidental())
        {
            tdElement.addClass(CLASS_INCIDENTAL_PROCESS_NAME);
        }

        return tdElement;
    }

    private SimpleHtmlElement getProcessState()
    {
        String className = getHtmlClassByProcessState(processHtml.getProcessState());

        SimpleHtmlElement tdElement      = SimpleHtmlElement.createTdElement();
        SimpleHtmlElement spanElement    = new SimpleHtmlElement("span");
        tdElement.appendChild(spanElement);
        if (className != null)
        {
            spanElement.addClass(className);
        }


        // Build text content
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(processHtml.getProcessState());
        textBuilder.append(" (").append(processHtml.getStartRetries()).append("/").
                append(processHtml.getMaxStartRetries()).append(")");

        if ( processHtml.isDead() )
        {
            textBuilder.append(" (D)");
        }

        SimpleTextContent textContent = new SimpleTextContent(textBuilder);
        spanElement.appendChild(textContent);

        return tdElement;
    }

    private SimpleHtmlElement getProcessEnabledCheckBox()
    {
        SimpleHtmlElement tdElement       = SimpleHtmlElement.createTdElement();
        SimpleHtmlElement inputElement    = new SimpleHtmlElement("input", false);
        tdElement.appendChild(inputElement);
        inputElement.addAttribute("type",   "checkbox");
        inputElement.addAttribute("value",  "Enabled");
        inputElement.addAttribute("name",   processHtml.getProcessName() + "_" +
                CHECKBOX_ENABLED_PREFIX);
        if (processHtml.isEnabled())
        {
            inputElement.addAttribute("checked");
        }
        if ( !isCheckboxActive() )
        {
            inputElement.addAttribute("disabled");
        }

        return tdElement;
    }


    private SimpleHtmlElement getProcessManagement()
    {
        SimpleHtmlElement tdElement = SimpleHtmlElement.createTdElement();

        if ( isManagementAvailable())
        {
            /**
             * Add start
             */
            SimpleHtmlElement startCommand;
            if ( isStartAvailable() )
            {
                startCommand = SimpleHtmlElement.createActiveCommand("Start process",
                                            "../process/start/"  + processHtml.getProcessName());
            }
            else
            {
                startCommand = SimpleHtmlElement.createInactiveCommand("Start process");
            }
            startCommand.addClass(HTMLConstants.CLASS_PROCESS_CMD);


            /**
             * Add stop
             */
            SimpleHtmlElement stopCommand;
            if ( isStopAvailable() )
            {
                stopCommand = SimpleHtmlElement.createActiveCommand("Stop process",
                                            "../process/stop/" + processHtml.getProcessName());
            }
            else
            {
                stopCommand = SimpleHtmlElement.createInactiveCommand("Stop process");
            }
            stopCommand.addClass(HTMLConstants.CLASS_PROCESS_CMD);

            tdElement.appendChild(startCommand);
            tdElement.appendChild( new SimpleHtmlElement("br", false) );
            tdElement.appendChild(stopCommand);

        }

        return tdElement;
    }

    private SimpleHtmlElement getProcessNetworkActivity()
    {
        if ( !processHtml.isPingable() )
        {
            return createTdWithPAndText("-");
        }

        ProcessSession session = processHtml.getProcessSession();
        if (session == null)
        {
            return SimpleHtmlElement.createTdElement();
        }

        if (session.isClosed())
        {
            return SimpleHtmlElement.createTdElement();
        }

        PingerInfo pingerInfo = session.getPingerInfo();

        if (pingerInfo == null)
        {
            return createTdWithPAndText("No information yet");
        }

        SimpleHtmlElement tdElement = SimpleHtmlElement.createTdElement();
        SimpleHtmlElement p1Element = new SimpleHtmlElement("p");
        SimpleHtmlElement p2Element = new SimpleHtmlElement("p");

        StringBuilder text1Builder = new StringBuilder();
        StringBuilder text2Builder = new StringBuilder();
        if (pingerInfo.isStarted())
        {
            Date startDate = pingerInfo.getDate();
            text1Builder.append("Started: ");
            text1Builder.append(dateFormat.format( startDate ));

            long diffInSec = (new Date().getTime() - startDate.getTime()) / 1000;
            text2Builder.append("Performing (in sec): ");
            text2Builder.append(diffInSec);
        }
        else if (pingerInfo.isPerformed())
        {
            text1Builder.append("Performed: ");
            long diffInSec = (new Date().getTime() - pingerInfo.getDate().getTime()) / 1000;
            text1Builder.append(diffInSec).append(" sec ago");

            text2Builder.append("Ping status: ");
            if ( pingerInfo.hasNetworkActivity() )
            {
                text2Builder.append("OK");
            }
            else
            {
                text2Builder.append("NO");
                text2Builder.append(" (");
                text2Builder.append(pingerInfo.getAlarmNum());
                text2Builder.append("/");
                text2Builder.append(processHtml.getMaxAlarms());
                text2Builder.append(")");
            }
        }

        p1Element.appendChild( new SimpleTextContent(text1Builder) );
        p2Element.appendChild( new SimpleTextContent(text2Builder) );
        tdElement.appendChild(p1Element);
        tdElement.appendChild(p2Element);

        return tdElement;
    }

    //
    // Helper methods
    //

    private String getHtmlClassByProcessState(ProcessState processState)
    {
        String className = null;
        switch (processState)
        {
            case RUNNING:
                className = CLASS_STATE_RUNNING;
                break;
            case TERMINATED:
                className = CLASS_PROCESS_STATE_TERMINATED;
                break;
            case FAILED_TO_SHUT_DOWN:
            case FAILED_TO_REGISTER_NO_PING:
                className = HTMLConstants.CLASS_STATE_HALTING;
                break;
            case INACTIVE:
                className = CLASS_STATE_INACTIVE;
                break;
        }

        return className;
    }

    private SimpleHtmlElement createTdWithPAndText(String text)
    {
        SimpleTextContent textContent = new SimpleTextContent(text);
        SimpleHtmlElement pElement = new SimpleHtmlElement("p");
        pElement.appendChild(textContent);

        SimpleHtmlElement tdElement = SimpleHtmlElement.createTdElement();
        tdElement.appendChild(pElement);

        return tdElement;
    }
}
