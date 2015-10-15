package de.trispeedys.resourceplanning.configuration;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class AppConfiguration
{
    private static final String CONFIG_FILE_NAME = "resource-planning.config.xml";

//    private static final String VERSION = "0.0.1-SNAPSHOT";

//    private static final String URL = "http://localhost:8080";

    private static final String CONF_PARAM_HOST = "host";

    private static final String CONF_PARAM_VERSION = "version";

    private static AppConfiguration instance;

    private String version;

    private String host;

    private AppConfiguration()
    {
        parseConfiguration();
    }

    private void parseConfiguration()
    {
        try
        {
            Document xml = readXml(getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
            host = getConfigurationValue(CONF_PARAM_HOST, xml.getDocumentElement());
            System.out.println("read conf value ["+CONF_PARAM_HOST+"] : " + host);
            version = getConfigurationValue(CONF_PARAM_VERSION, xml.getDocumentElement());
            System.out.println("read conf value ["+CONF_PARAM_VERSION+"] : " + version);
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

    private String getConfigurationValue(String parameterName, Element root)
    {
        Node node = root.getElementsByTagName(parameterName).item(0);
        return node.getTextContent();
    }

    public static AppConfiguration getInstance()
    {
        if (AppConfiguration.instance == null)
        {
            AppConfiguration.instance = new AppConfiguration();
        }
        return AppConfiguration.instance;
    }

    public String getVersion()
    {
        return version;
    }

    public String getHost()
    {
        return host;
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
}