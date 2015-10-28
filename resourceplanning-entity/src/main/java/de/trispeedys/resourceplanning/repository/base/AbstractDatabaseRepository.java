package de.trispeedys.resourceplanning.repository.base;

import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.HelperAssignment;

public abstract class AbstractDatabaseRepository<T>
{
    private DefaultDatasource<T> dataSource;

    public AbstractDatabaseRepository()
    {
        super();
        dataSource = createDataSource();
    }
    
    protected abstract DefaultDatasource<T> createDataSource();

    public List<T> findAll()
    {
        return dataSource.findAll();
    }
    
    public T findById(Long id)
    {
        return dataSource.findById(id);
    }
    
    public void saveOrUpdate(T entity)
    {
        dataSource.saveOrUpdate(entity);
    }
    
    public DefaultDatasource<T> dataSource()
    {
        return dataSource;
    }
}