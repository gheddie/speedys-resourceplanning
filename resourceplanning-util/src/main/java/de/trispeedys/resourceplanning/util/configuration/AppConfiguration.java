package de.trispeedys.resourceplanning.util.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AppConfiguration
{
    private static final String CONFIG_FILE_NAME = "resource-planning.config.xml";
    
    private HashMap<String, String> configurationValues;

    public static final String HOST = "host";

    public static final String VERSION = "version";       
    
    public static final String SMTP_USER = "smtp_user";
    
    public static final String SMTP_PASSWD = "smtp_passwd";
    
    public static final String SMTP_HOST = "smtp_host";
    
    public static final String SMTP_PORT = "smtp_port";

    private static AppConfiguration instance;

    private AppConfiguration()
    {
        parseConfiguration();
    }

    private void parseConfiguration()
    {
        try
        {
            Document doc = readXml(getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
            configurationValues = new HashMap<String, String>();
            NodeList nodeList = doc.getElementsByTagName("property");
            Node node = null;
            for (int temp = 0; temp < nodeList.getLength(); temp++)
            {
                node = nodeList.item(temp);
                configurationValues.put(node.getAttributes().getNamedItem("name").getTextContent(), node.getTextContent());
            }
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        //LoggerService.log("parsed configuration : " + resource, DbLogLevel.INFO);
    }

    public static AppConfiguration getInstance()
    {
        if (AppConfiguration.instance == null)
        {
            AppConfiguration.instance = new AppConfiguration();
        }
        return AppConfiguration.instance;
    }

    public static Document readXml(InputStream is) throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(true);
        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();
        db.setEntityResolver(new NullResolver());
        return db.parse(is);
    }

    public String getConfigurationValue(String key)
    {
        return configurationValues.get(key);
    }
}