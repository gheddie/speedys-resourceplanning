package de.trispeedys.resourceplanning.service;

import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class LoggerService
{
    public static void log(String businessKey, String message)
    {
        log(businessKey, message, DbLogLevel.INFO);
    }
    
    public static void log(String businessKey, String message, DbLogLevel logLevel)
    {
        EntityFactory.buildLog(businessKey, message, logLevel).saveOrUpdate();
    }

    public static void log(String message)
    {
        log(message, DbLogLevel.INFO);
    }
    
    public static void log(String message, DbLogLevel logLevel)
    {
        EntityFactory.buildLog(null, message, logLevel).saveOrUpdate();
    }
}