package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Position extends AbstractDbObject
{
    private String description;
    
    @Column(name = "minimal_age")
    private int minimalAge;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getMinimalAge()
    {
        return minimalAge;
    }

    public void setMinimalAge(int minimalAge)
    {
        this.minimalAge = minimalAge;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+description+", "+minimalAge+"]";
    }
}