/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Nov 16, 2015
 */

package solvo.jwatch.handlers;

import javax.mail.internet.AddressException;
import org.apache.commons.lang3.StringUtils;
import solvo.gadgets.Lib;
import solvo.jwatch.ConfigManager;
import solvo.jwatch.bo.AbstractEvent;
import solvo.jwatch.bo.ProcessContext;
import solvo.jwatch.bo.ProcessInfo;
import solvo.jwatch.bo.ProcessState;
import solvo.jwatch.config.ConfigurationModel;
import solvo.jwatch.config.MailProperties;
import solvo.jwatch.mail.JwatchMailer;
import solvo.jwatch.mail.SimpleJwatchMail;

/**
 *
 * @author user_akrikheli
 */
public class StarterClosingEventHandler implements IEventHandler
{
    private static class DeathMailer
    {
        private final ConfigurationModel    configModel;
        private final String                toAddr;
        private final String                processName;

        public DeathMailer( ConfigurationModel configModel,
                            String processName,
                            String toAddr)
        {
            this.configModel    = configModel;
            this.processName    = processName;
            this.toAddr         = toAddr;
        }

        public void sendMail()
        {
            final MailProperties mailProps = configModel.getMailProperties();

            if (Lib.log.isDebugEnabled())
            {
                Lib.log.debug("JwatchMailer trying to send mail to " + toAddr
                        + " about process death (" + processName + ")" );
            }

            JwatchMailer mailer;
            try
            {
                mailer = new JwatchMailer(mailProps);
            }
            catch (AddressException | NullPointerException ex)
            {
                Lib.log.error("Can not create mailer to send mail to " + toAddr
                        + " about process death (" + processName + ")", ex);
                return;
            }

            SimpleJwatchMail mail = makeMail();
            mailer.send( mail, toAddr );
        }

        protected SimpleJwatchMail makeMail()
        {
            final StringBuilder subjectBuilder  = new StringBuilder();
            final StringBuilder bodyBuilder     = new StringBuilder();

            String env = configModel.getEnvironment();
            subjectBuilder.append("JwatchMailer: ");
            if ( StringUtils.isNotEmpty(env) )
            {
                subjectBuilder.append( env );
                subjectBuilder.append(" - ");
            }
            subjectBuilder.append(processName);
            subjectBuilder.append(" is dead");

            bodyBuilder.append("I'm sorry to have to inform you that process '");
            bodyBuilder.append(processName);
            bodyBuilder.append("' ");
            if ( StringUtils.isNotEmpty(env) )
            {
                bodyBuilder.append("(");
                bodyBuilder.append(env);
                bodyBuilder.append(") ");
            }
            bodyBuilder.append("is dead.\n\n");
            bodyBuilder.append("Please, do not reply to this message.");

            SimpleJwatchMail mail = new SimpleJwatchMail();
            mail.setBody(bodyBuilder.toString());
            mail.setSubject(subjectBuilder.toString());

            return mail;
        }

    }


    @Override
    public void handle(ProcessContext processContext)
    {
        sendMail(processContext);
    }

    protected void sendMail(ProcessContext processContext)
    {
        ProcessInfo pi = processContext.getProcessInfo();
        if (! (pi.isDead() && pi.getProcessState() == ProcessState.TERMINATED) )
        {
            return;
        }

        final String toAddr = processContext.getProcessDescription().getEmailAddress();
        final String processName = processContext.getProcessDescription().getProcessName();
        if ( StringUtils.isEmpty(toAddr) )
        {
            return;
        }

        ConfigurationModel configModel = ConfigManager.get().getConfig();


        final DeathMailer dm = new DeathMailer(configModel, processName, toAddr);
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                dm.sendMail();
            }
        }).start();
    }

    @Override
    public String getEventName()
    {
        return AbstractEvent.PROCESS_STARTER_WAS_CLOSED;
    }
}
