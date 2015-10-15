package de.trispeedys.resourceplanning.entity;

import java.util.HashMap;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;

public class DatasourceRegistry
{
    private static DatasourceRegistry instance;
    
    private HashMap<Class<? extends AbstractDbObject>, DefaultDatasource> registeredDatasources;

    private DefaultDatasource defaultDatasource;

    private DatasourceRegistry()
    {
        registeredDatasources = new HashMap<Class<? extends AbstractDbObject>, DefaultDatasource>();
        registerDatasources();
        defaultDatasource = new DefaultDatasource();
    }

    private void registerDatasources()
    {
        //...
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