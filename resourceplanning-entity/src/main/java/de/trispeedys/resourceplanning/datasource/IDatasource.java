package de.trispeedys.resourceplanning.datasource;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.MessageQueue;

public interface IDatasource
{
    public <T> T saveOrUpdate(T dbObject);
    
    public <T> T findById(Class<T> entityClass, Long primaryKeyValue);
    
    public <T> List<T> find(String qryString, HashMap<String, Object> parameters);
    
    public <T> List<T> find(String qryString);

    public <T> List<T> find(String qryString, String paramaterName, Object paramaterValue);

    public <T> List<T> findAll(Class<T> entityClass);

    public <T> List<?> find(Class<T> entityClass, String paramaterName, Object paramaterValue);
    
    public <T> List<T> find(Class<MessageQueue> entityClass, Object... filters);
}