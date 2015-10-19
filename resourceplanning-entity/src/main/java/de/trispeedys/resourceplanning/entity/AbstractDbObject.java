package de.trispeedys.resourceplanning.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;

@MappedSuperclass
public abstract class AbstractDbObject
{
    @Id
    @GeneratedValue
    private Long id;
    
    public Long getId()
    {
        return id;
    }
    
    protected void setId(Long id)
    {
        this.id = id;
    }
    
    public <T> T persist()
    {
        DefaultDatasource<T> datasource = (DefaultDatasource<T>) DatasourceRegistry.getDatasource(getClass());
        return (T) datasource.saveOrUpdate(this);
    }

    public boolean isNew()
    {
        return (id == null);
    }
}