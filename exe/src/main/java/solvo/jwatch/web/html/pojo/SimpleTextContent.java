/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 9, 2015
 */

package solvo.jwatch.web.html.pojo;

/**
 *
 * @author user_akrikheli
 */
public class SimpleTextContent implements IHTMLElement
{
    private StringBuilder textContent;

    public SimpleTextContent()
    {

    }

    public SimpleTextContent(String text)
    {
        this.textContent = new StringBuilder(text);
    }

    public SimpleTextContent(StringBuilder text)
    {
        this.textContent = text;
    }

    public StringBuilder getTextContent()
    {
        return textContent;
    }

    public void setTextContent(StringBuilder text)
    {
        this.textContent = text;
    }

    @Override
    public StringBuilder build()
    {
        if (textContent == null)
        {
            return new StringBuilder("");
        }
        else
        {
            return textContent;
        }
    }
}
