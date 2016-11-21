/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 11, 2015
 */

package solvo.jwatch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import solvo.jwatch.config.xml.XMLProcess;
import solvo.jwatch.config.xml.XMLWatchConfig;
import javax.xml.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import solvo.gadgets.Lib;
import solvo.jwatch.config.ConfigurationModel;
import solvo.jwatch.config.MailProperties;
import solvo.jwatch.config.WatchProcessDescription;
import solvo.jwatch.config.xml.XMLErrorHandler;
import solvo.jwatch.config.xml.XMLMailProperties;
import solvo.jwatch.config.xml.XMLProperties;
import solvo.jwatch.exceptions.ConfigurationNotSavedException;

/**
 *
 * @author user_akrikheli
 */

public class XMLConfigLoader extends AbstractConfigLoader
{
    private static final String XSD_RESOURCE_NAME =  "watch-config-schema.xsd";
    private static final JAXBContext context = initContext();
    private static JAXBContext initContext()
    {
        try
        {
            return JAXBContext.newInstance(XMLWatchConfig.class);
        }
        catch (JAXBException jaxbex)
        {
            Lib.log.error("JAXBException occured", jaxbex);
            return null;
        }
    }

    public XMLConfigLoader()
    {
        
    }

    @Override
    public void load()
    {
        configModel = null;
        if (context == null)
        {
            return;
        }
        
        synchronized (context)
        {
            try
            {
                Unmarshaller unmarshaller = context.createUnmarshaller();
                XMLWatchConfig xml = (XMLWatchConfig)unmarshaller.unmarshal(configFile);
                validateXmlBySchema(xml);
                createConfigModel(xml);
            }
            catch (JAXBException | SAXException | IOException | NullPointerException ex)
            {
                Lib.log.error("XML config loader exception", ex);
            }
        }
    }

    @Override
    public void save(ConfigurationModel newModel) throws ConfigurationNotSavedException
    {
        XMLWatchConfig xml = new XMLWatchConfig();
        List<XMLProcess> xmlProcessList = new ArrayList<>();
        XMLProperties xmlProperties = new XMLProperties();

        xml.setProcessList(xmlProcessList);
        xml.setProperties(xmlProperties);

        List<WatchProcessDescription> wpdList = newModel.getProcessDescriptionList();
        int order = 0;
        for (WatchProcessDescription wpd : wpdList)
        {
            ++order;
            XMLProcess xmlProcess = buildXMLProcessByWatchProcessDescription(wpd, order);
            xmlProcessList.add(xmlProcess);
        }

        xmlProperties.setHttpPort( newModel.getHttpPort() );
        xmlProperties.setEnvironment( newModel.getEnvironment() );
        xmlProperties.setSpool( newModel.getSpool() );

        XMLMailProperties xmlMailProps;
        xmlMailProps = buildXMLMailPropertiesByMailProperties(newModel.getMailProperties());
        xmlProperties.setMailProperties( xmlMailProps );

        synchronized (context)
        {
            try
            {
                validateXmlBySchema(xml);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(xml, configFile);
            }
            catch (JAXBException | SAXException | IOException | NullPointerException ex)
            {
                Lib.log.error("XML config saver exception", ex);
                throw new ConfigurationNotSavedException(ex);
            }
        }
    }

    private void validateXmlBySchema(XMLWatchConfig xml)
                                        throws  JAXBException, SAXException, IOException
    {
        JAXBSource source = new JAXBSource(context, xml);
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        InputStream xmlSchemaInputStream = Thread.currentThread().getContextClassLoader().
                                    getResourceAsStream(XSD_RESOURCE_NAME);

        Schema schema;

        try
        {
            schema = sf.newSchema(new StreamSource(xmlSchemaInputStream));
        }
        catch (SAXException saxex)
        {
            Lib.log.error("SAX error occurs during parsing " + XSD_RESOURCE_NAME);
            throw saxex;
        }
        catch (NullPointerException npex)
        {
            Lib.log.error("Can not find resource with name " + XSD_RESOURCE_NAME);
            throw npex;
        }

        Validator validator = schema.newValidator();
        validator.setErrorHandler( new XMLErrorHandler() );
        validator.validate(source);
    }

    protected void createConfigModel(XMLWatchConfig xml)
    {
        configModel = new ConfigurationModel();

        XMLProperties xmlProps            = xml.getProperties();
        XMLMailProperties xmlMailProps    = xmlProps.getMailProperties();

        List<XMLProcess> xmlProcessList = xml.getProcessList();
        Collections.sort(xmlProcessList, 
                (XMLProcess process1, XMLProcess process2) -> 
                    Integer.compare(process1.getOrder(), process2.getOrder()));

        /**
         * First of all, we need to set main jwatch properties
         */
        configModel.setSpool( xmlProps.getSpool() );
        configModel.setHttpPort( xmlProps.getHttpPort() );
        configModel.setEnvironment( xmlProps.getEnvironment() );

        MailProperties mailProps = buildMailPropertiesByXml(xmlMailProps);
        configModel.setMailProperties( mailProps );

        List<WatchProcessDescription> processDescriptionList =
                                        configModel.getProcessDescriptionList();
        for (XMLProcess xmlProcess : xmlProcessList)
        {
            processDescriptionList.add( buildWatchProcessDescriptionByXml(xmlProcess) );
        }
    }

    protected WatchProcessDescription buildWatchProcessDescriptionByXml(XMLProcess xmlProcess)
    {
        String componentName = xmlProcess.getComponentName();
        if ( StringUtils.isEmpty(xmlProcess.getComponentName()) )
        {
            componentName = xmlProcess.getName();
        }

        return WatchProcessDescription.newBuilder().
                setProcessName( xmlProcess.getName() ).
                setCoreDir( xmlProcess.getCoreDir() ).
                setEnabled( !xmlProcess.isDisabled() ).
                setStartCmd( xmlProcess.getCmd() ).
                setStartTimeout( xmlProcess.getStartTimeout() ).
                setStartRetries( xmlProcess.getStartRetries() ).
                setPingable( xmlProcess.isPingable() ).
                setPingDelay( xmlProcess.getPingDelay() ).
                setPingInterval( xmlProcess.getPingInterval() ).
                setPingAlarms( xmlProcess.getPingAlarms() ).
                setPingerImpl( xmlProcess.getPingerImpl() ).
                setEmailAddress( xmlProcess.getMail() ).
                setComponentName(componentName).
            build();
    }

    protected MailProperties buildMailPropertiesByXml(XMLMailProperties xmlMailProps)
    {
        if (xmlMailProps == null)
        {
            return MailProperties.newBuilder().
                    setProtocol( MailProperties.DEFAULT_PROTOCOL ).
                build();
        }

        return MailProperties.newBuilder().
                setAuth( xmlMailProps.getAuth() ).
                setHost( xmlMailProps.getHost() ).
                setPassword( xmlMailProps.getPassword() ).
                setPort( xmlMailProps.getPort() ).
                setProtocol( xmlMailProps.getProtocol() ).
                setUser( xmlMailProps.getUser() ).
                setFromAddr( xmlMailProps.getFromAddr() ).
            build();
    }

    protected XMLProcess buildXMLProcessByWatchProcessDescription(WatchProcessDescription wpd,
                                                                    int order)
    {
        XMLProcess xmlProcess = new XMLProcess();

        xmlProcess.setCmd( wpd.getStartCmd() );
        xmlProcess.setCoreDir( wpd.getCoreDir() );
        xmlProcess.setDisabled( !wpd.isEnabled() );
        xmlProcess.setName( wpd.getProcessName() );
        xmlProcess.setOrder( order );
        xmlProcess.setStartRetries( wpd.getStartRetries() );
        xmlProcess.setStartTimeout( wpd.getStartTimeout() );
        xmlProcess.setPingable( wpd.isPingable() );
        xmlProcess.setPingDelay( wpd.getPingDelay() );
        xmlProcess.setPingInterval( wpd.getPingInterval() );
        xmlProcess.setPingAlarms( wpd.getPingAlarms() );
        xmlProcess.setPingerImpl( wpd.getPingerImpl() );
        xmlProcess.setMail( wpd.getEmailAddress() );
        xmlProcess.setComponentName( wpd.getComponentName() );

        return xmlProcess;
    }

    protected XMLMailProperties buildXMLMailPropertiesByMailProperties(MailProperties mailProps)
    {
        XMLMailProperties xmlMailProps = new XMLMailProperties();

        if (mailProps == null)
        {
            xmlMailProps.setProtocol( MailProperties.Protocol.SMTP.toString() );
            return xmlMailProps;
        }

        xmlMailProps.setAuth( mailProps.getAuth() );
        xmlMailProps.setFromAddr( mailProps.getFromAddr() );
        xmlMailProps.setHost( mailProps.getHost() );
        xmlMailProps.setPassword( mailProps.getPassword() );
        xmlMailProps.setPort( mailProps.getPort() );
        xmlMailProps.setProtocol( mailProps.getProtocol().toString() );
        xmlMailProps.setUser( mailProps.getUser() );

        return xmlMailProps;
    }
}
