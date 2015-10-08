package de.trispeedys.resourceplanning.entity.builder;

import java.util.Date;

import de.trispeedys.resourceplanning.entity.DatabaseLogger;
import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;

public class DatabaseLoggerBuilder extends AbstractEntityBuilder<DatabaseLogger>
{
    private String businessKey;
    
    private String message;

    private DbLogLevel logLevel;
    
    public DatabaseLoggerBuilder withMessage(String aMessage)
    {
        message = aMessage;
        return this;
    }

    public DatabaseLoggerBuilder withBusinessKey(String aBusinessKey)
    {
        businessKey = aBusinessKey;
        return this;
    }
    
    public DatabaseLoggerBuilder withLogLevel(DbLogLevel aLogLevel)
    {
        logLevel = aLogLevel;
        return this;
    }

    public DatabaseLogger build()
    {
        DatabaseLogger logger = new DatabaseLogger();
        logger.setBusinessKey(businessKey);
        logger.setDate(new Date());
        logger.setMessage(message);
        logger.setLogLevel(logLevel);
        return logger;
    }
}