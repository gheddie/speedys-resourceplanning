package de.trispeedys.resourceplanning.configuration;

import java.io.InputStream;

import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.service.LoggerService;

public class AppConfiguration
{
    private static final String CONFIG_FILE_NAME = "resource-planning.config.xml";

    private static AppConfiguration instance;

    private AppConfiguration()
    {
        parseConfiguration();
    }

    private void parseConfiguration()
    {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
        LoggerService.log("parsed configuraTION : " + resource, DbLogLevel.INFO);
    }

    public static AppConfiguration getInstance()
    {
        if (AppConfiguration.instance == null)
        {
            AppConfiguration.instance = new AppConfiguration();
        }
        return AppConfiguration.instance;
    }
}