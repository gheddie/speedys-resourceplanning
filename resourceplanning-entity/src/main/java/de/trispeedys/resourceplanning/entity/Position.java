package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

@Entity
public class Position extends AbstractDbObject implements HierarchicalEventItem
{
    // @Min(value = 2)
    private String description;
    
    @Column(name = "minimal_age")
    private int minimalAge;
    
    @NotNull
    @ManyToOne
    private Domain domain;
    
    /**
     * If true, a helper ca be assigned to this position (even though under age)
     * if a parent (or another person with similar authority) is assigned to a position
     * in the same domain (and the same event).
     */
    @Column(name = "authority_override")
    private boolean authorityOverride;

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
    
    public Domain getDomain()
    {
        return domain;
    }
    
    public void setDomain(Domain domain)
    {
        this.domain = domain;
    }
    
    public boolean isAuthorityOverride()
    {
        return this.authorityOverride;
    }
    
    public void setAuthorityOverride(boolean authorityOverride)
    {
        this.authorityOverride = authorityOverride;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+description+", "+minimalAge+"]";
    }

    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_POSITION;
    }
    
    public String getOutline()
    {
        return "[P]";
    }
}