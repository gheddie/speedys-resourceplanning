package de.trispeedys.resourceplanning.repository.base;

import java.lang.reflect.Method;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.util.StringUtil;

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
    
    public void updateSingleValue(T entity, String attribute, Object newValue)
    {
        try
        {
            Method setter = entity.getClass().getDeclaredMethod(StringUtil.setterName(attribute), new Class[] {newValue.getClass()});
            setter.invoke(entity, newValue);
            dataSource.saveOrUpdate(entity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public DefaultDatasource<T> dataSource()
    {
        return dataSource;
    }
}