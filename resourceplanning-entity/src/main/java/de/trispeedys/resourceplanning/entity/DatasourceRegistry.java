package de.trispeedys.resourceplanning.entity;

import java.util.HashMap;

import de.trispeedys.resourceplanning.datasource.DatabaseLoggerDatasource;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.DomainDatasource;
import de.trispeedys.resourceplanning.datasource.EventDatasource;
import de.trispeedys.resourceplanning.datasource.EventPositionDatasource;
import de.trispeedys.resourceplanning.datasource.EventTemplateDatasource;
import de.trispeedys.resourceplanning.datasource.HelperAssignmentDatasource;
import de.trispeedys.resourceplanning.datasource.HelperDatasource;
import de.trispeedys.resourceplanning.datasource.MessageQueueDatasource;
import de.trispeedys.resourceplanning.datasource.PositionDatasource;

public class DatasourceRegistry
{
    private static DatasourceRegistry instance;
    
    private HashMap<Class<? extends AbstractDbObject>, DefaultDatasource> registeredDatasources;

    private DefaultDatasource defaultDatasource;

    private DatasourceRegistry()
    {
        registeredDatasources = new HashMap<Class<? extends AbstractDbObject>, DefaultDatasource>();
        registerDatasources();
//        defaultDatasource = new DefaultDatasource();
    }

    private void registerDatasources()
    {
        registeredDatasources.put(Helper.class, new HelperDatasource());
        registeredDatasources.put(MessageQueue.class, new MessageQueueDatasource());
        registeredDatasources.put(Domain.class, new DomainDatasource());
        registeredDatasources.put(Position.class, new PositionDatasource());
        registeredDatasources.put(EventTemplate.class, new EventTemplateDatasource());
        registeredDatasources.put(Event.class, new EventDatasource());
        registeredDatasources.put(EventPosition.class, new EventPositionDatasource());
        registeredDatasources.put(HelperAssignment.class, new HelperAssignmentDatasource());
        registeredDatasources.put(DatabaseLogger.class, new DatabaseLoggerDatasource());
    }

    private static DatasourceRegistry getInstance()
    {
        if (DatasourceRegistry.instance == null)
        {
            DatasourceRegistry.instance = new DatasourceRegistry();
        }
        return DatasourceRegistry.instance;
    }

    @SuppressWarnings("unchecked")
    private <T> DefaultDatasource<T> datasource(Class<T> entityClass)
    {
        DefaultDatasource<T> dataSource = registeredDatasources.get(entityClass);
        return (dataSource != null ? dataSource : defaultDatasource);
    }

    public static <T> DefaultDatasource<T> getDatasource(Class<T> entityClass)
    {
        return getInstance().datasource(entityClass);
    }
}