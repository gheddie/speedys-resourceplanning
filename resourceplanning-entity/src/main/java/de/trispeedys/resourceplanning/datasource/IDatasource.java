package de.trispeedys.resourceplanning.datasource;

import java.util.HashMap;
import java.util.List;

public interface IDatasource
{
    public <T> T saveOrUpdate(T dbObject);
    
    public <T> T findById(Long primaryKeyValue);
    
    public <T> List<T> find(String qryString, HashMap<String, Object> parameters);
    
    public <T> List<T> find(String qryString);

    public <T> List<T> find(String qryString, String paramaterName, Object paramaterValue);

    public <T> List<T> findAll(Class<T> entityClass);

    public <T> List<T> find(String paramaterName, Object paramaterValue);
    
    public <T> List<T> find(Object... filters);
}