package de.trispeedys.resourceplanning.entity;

import java.util.HashMap;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.HelperDatasource;

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
        registeredDatasources.put(Helper.class, new HelperDatasource());
    }

    private static DatasourceRegistry getInstance()
    {
        if (DatasourceRegistry.instance == null)
        {
            DatasourceRegistry.instance = new DatasourceRegistry();
        }
        return DatasourceRegistry.instance;
    }

    private <T> DefaultDatasource datasource(Class<T> entityClass)
    {
        DefaultDatasource<T> dataSource = registeredDatasources.get(entityClass);
        return (dataSource != null ? dataSource : defaultDatasource);
    }

    public static <T> DefaultDatasource getDatasource(Class<T> entityClass)
    {
        return getInstance().datasource(entityClass);
    }
}