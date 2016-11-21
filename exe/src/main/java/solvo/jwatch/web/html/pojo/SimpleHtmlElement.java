/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 9, 2015
 */

package solvo.jwatch.web.html.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import solvo.jwatch.web.html.HTMLConstants;

/**
 *
 * @author user_akrikheli
 */
public class SimpleHtmlElement implements IHTMLElement
{
    private final List<IHTMLElement>    childs      = new ArrayList<>();
    private final Map<String, String>   attributes  = new HashMap<>();
    private String  tag;
    private boolean closeTag;

    public SimpleHtmlElement(String tag)
    {
        this(tag, true);
    }

    public SimpleHtmlElement(String tag, boolean closeTag)
    {
        this.tag        = tag;
        this.closeTag   = closeTag;
    }

    public static SimpleHtmlElement createTdElement()
    {
        return new SimpleHtmlElement("td");
    }

    public static SimpleHtmlElement createTrElement()
    {
        return new SimpleHtmlElement("tr");
    }

    public static SimpleHtmlElement createActiveCommand(String title, String href)
    {
        SimpleHtmlElement aElement = new SimpleHtmlElement("a");
        SimpleTextContent textContent = new SimpleTextContent(title);

        aElement.appendChild(textContent);
        aElement.addAttribute("href", href);
        aElement.addClass(HTMLConstants.CLASS_ACTIVE_CMD);

        return aElement;
    }

    public static SimpleHtmlElement createInactiveCommand(String title)
    {
        SimpleHtmlElement aElement = new SimpleHtmlElement("a");
        SimpleTextContent textContent = new SimpleTextContent(title);

        aElement.appendChild(textContent);
        aElement.addClass(HTMLConstants.CLASS_INACTIVE_CMD);

        return aElement;
    }

    @Override
    public StringBuilder build()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("<");
        sb.append(tag);
        if ( !attributes.isEmpty() )
        {
            for (Entry<String, String> entry : attributes.entrySet())
            {
                sb.append(" ");
                sb.append(entry.getKey());
                if ( StringUtils.isNotEmpty(entry.getValue()) )
                {
                    sb.append(" = ");
                    sb.append("\""); sb.append(entry.getValue()); sb.append("\"");
                }
            }
        }
        if (closeTag)
        {
            sb.append(">");
            for (IHTMLElement child : childs)
            {
                sb.append( child.build() );
            }
            sb.append("</"); sb.append(tag); sb.append(">");
        }
        else
        {
            sb.append("/>");
        }

        return sb;
    }

    public SimpleHtmlElement setTag(String tag)
    {
        this.tag = tag;
        return this;
    }

    public String getTag()
    {
        return tag;
    }

    public void appendChild(IHTMLElement shc)
    {
        childs.add(shc);
    }

    public SimpleHtmlElement addAttribute(String attrName, String attrValue)
    {
        attributes.put(attrName, attrValue);
        return this;
    }

    public SimpleHtmlElement addAttribute(String attrName)
    {
        attributes.put(attrName, "");
        return this;
    }

    public SimpleHtmlElement addClass(String className)
    {
        String classes = attributes.get("class");
        if (classes == null)
        {
            classes = className;
        }
        else
        {
            classes = classes.concat(" ").concat(className);
        }
        attributes.put("class", classes);

        return this;
    }
}
