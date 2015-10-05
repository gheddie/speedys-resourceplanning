package de.trispeedys.resourceplanning.datasource;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;

public interface IDatasource
{
    public <T> T saveOrUpdate(T dbObject);
    
    public List<?> find(String qryString, HashMap<String, Object> parameters);
    
    public List<?> find(String qryString);

    public List<?> find(String qryString, String paramaterName, Object paramaterValue);

    public List<?> findAll(Class<? extends AbstractDbObject> clazz);

    public <T> List<?> find(Class<T> entityClass, String paramaterName, Object paramaterValue);
}