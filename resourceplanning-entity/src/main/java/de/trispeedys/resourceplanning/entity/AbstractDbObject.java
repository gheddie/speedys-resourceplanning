package de.trispeedys.resourceplanning.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import de.trispeedys.resourceplanning.HibernateUtil;

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
    
    @SuppressWarnings("unchecked")
    public <T> T persist()
    {
        return (T) HibernateUtil.persistSimple(this);
    }
}