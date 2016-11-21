/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 11, 2015
 */

package solvo.jwatch.config.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author user_akrikheli
 */
public class XMLErrorHandler implements ErrorHandler
{
    @Override
    public void warning(SAXParseException saxpex) throws SAXException
    {
        throw saxpex;
    }

    @Override
    public void error(SAXParseException saxpex) throws SAXException
    {
        throw saxpex;
    }

    @Override
    public void fatalError(SAXParseException saxpex) throws SAXException
    {
        throw saxpex;
    }
}

