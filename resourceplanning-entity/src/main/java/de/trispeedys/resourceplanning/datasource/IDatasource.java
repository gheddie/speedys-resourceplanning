package de.trispeedys.resourceplanning.datasource;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;

public interface IDatasource
{
    public <T> T save(AbstractDbObject dbObject);
    
    public List<?> find(String qryString, HashMap<String, Object> parameters);
    
    public List<?> find(String qryString);

    public List<?> find(String qryString, String paramaterName, Object paramaterValue);

    public List<?> find(Class<? extends AbstractDbObject> clazz);

    public List<?> find(Class<? extends AbstractDbObject> entityClass, String paramaterName, Object paramaterValue);
}