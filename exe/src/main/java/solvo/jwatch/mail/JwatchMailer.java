/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 29, 2015
 */

package solvo.jwatch.mail;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import solvo.gadgets.Lib;
import solvo.jwatch.config.MailProperties;

/**
 *
 * @author user_akrikheli
 */
public class JwatchMailer
{
    private static final String LOG_PREFIX = "JwatchMailer: ";

    private final Properties props = new Properties();
    private String prefix;
    private Authenticator authenticator;
    private Session session;
    private InternetAddress from;
    private final MailProperties mailProperties;

    public JwatchMailer(MailProperties mailProperties) throws AddressException
    {
        if (mailProperties == null)
        {
            throw new NullPointerException("Invalid mail properties");
        }
        this.mailProperties = mailProperties;
        init();
    }

    private void init() throws AddressException
    {
        if ( mailProperties.getProtocol() == MailProperties.Protocol.SMTPS )
        {
            prefix = "mail.smtps";
        }
        else
        {
            prefix = "mail.smtp";
        }

        initProperties();
        initAuthenticator();

        session = Session.getDefaultInstance(props, authenticator);
        String fromAddrStr = mailProperties.getFromAddr();
        if ( StringUtils.isNotEmpty(fromAddrStr ) )
        {
            try
            {
                from = new InternetAddress(fromAddrStr);
            }
            catch (AddressException ex)
            {
                Lib.log.error(LOG_PREFIX + "can not be initialized", ex);
                throw ex;
            }
        }
        else
        {
            from = InternetAddress.getLocalAddress(session);
        }
    }

    private void initProperties()
    {
        Boolean auth    = mailProperties.getAuth();
        String user     = mailProperties.getUser();
        String password = mailProperties.getPassword();
        String host     = mailProperties.getHost();
        Integer port    = mailProperties.getPort();

        if (auth != null)
        {
            props.setProperty(prefix.concat(".auth"), auth.toString());
        }
        if ( StringUtils.isNotEmpty(host) )
        {
            props.setProperty(prefix.concat(".host"), host);
        }
        if (port != null)
        {
            props.setProperty(prefix.concat(".port"), port.toString());
        }
        if ( StringUtils.isNotEmpty(user) )
        {
            props.setProperty(prefix.concat(".user"), user);
        }
        if ( StringUtils.isNotEmpty( password ))
        {
            props.setProperty(prefix.concat(".password"), password);
        }
    }

    private void initAuthenticator()
    {
        final String user       = props.getProperty( prefix.concat(".user") );
        final String passwd     = props.getProperty( prefix.concat(".password") );
        final String auth       = props.getProperty( prefix.concat(".auth") );

        if ( //StringUtils.isTrue(auth) &&
              StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(passwd) )
        {
            authenticator = new Authenticator()
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(user, passwd);
                }
            };
        }
    }

    private MimeMessage makeMessage(SimpleJwatchMail jwatchMail, String toStr)
                                throws MessagingException
    {
        MimeMessage msg = new MimeMessage(session);

        msg.setText( jwatchMail.getBody() );
        msg.setSubject( jwatchMail.getSubject() );
        msg.setFrom( from );

        InternetAddress to = new InternetAddress(toStr);
        msg.setRecipient(Message.RecipientType.TO, to);

        return msg;
    }

    public void send(SimpleJwatchMail jwatchMail, String to)
    {
        String protocol = mailProperties.getProtocol().toString();
        Transport transport;
        try
        {
            transport = session.getTransport( protocol );
        }
        catch (NoSuchProviderException ex)
        {
            Lib.log.error(LOG_PREFIX + "mail sending was failed", ex);
            return;
        }

        try
        {
            MimeMessage msg = makeMessage(jwatchMail, to);
            transport.connect();
            transport.sendMessage(msg, msg.getAllRecipients());
        }
        catch (MessagingException ex)
        {
            Lib.log.error(LOG_PREFIX + "mail sending was failed", ex);
        }
        finally
        {
            try
            {
                transport.close();
            }
            catch (MessagingException ex)
            {
                Lib.log.error(LOG_PREFIX + "connection closing was failed", ex);
            }
        }
    }
}
