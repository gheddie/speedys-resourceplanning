package de.trispeedys.resourceplanning.datasource;

import de.trispeedys.resourceplanning.entity.DatabaseLogger;

public class DatabaseLoggerDatasource extends DefaultDatasource<DatabaseLogger>
{
    protected Class<DatabaseLogger> getType()
    {
        return null;
    }
}