/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 11, 2015
 */

package solvo.jwatch.config.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author user_akrikheli
 */

@XmlRootElement(name = "watchConfig")
public class XMLWatchConfig
{
    private XMLProperties properties;
    private List<XMLProcess> processList;

    @XmlElement(name = "properties")
    public XMLProperties getProperties()
    {
        return properties;
    }

    public void setProperties(XMLProperties properties)
    {
        this.properties = properties;
    }

    @XmlElement(name = "process")
    public List<XMLProcess> getProcessList()
    {
        return processList;
    }

    public void setProcessList(List<XMLProcess> processList)
    {
        this.processList = processList;
    }
}
