/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 29, 2015
 */

package solvo.jwatch.mail;

/**
 *
 * @author user_akrikheli
 */
public class SimpleJwatchMail
{
    protected String subject;
    protected String body;

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
}
