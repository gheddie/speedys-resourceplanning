package de.trispeedys.resourceplanning.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Domain extends AbstractDbObject
{
    private String name;
    
    /**
     * the {@link Helper} who is in charge for this {@link Domain}
     */
    @OneToOne
    @JoinColumn(name = "helper_id")
    private Helper leader;
    
    @Column(name = "domain_number")
    private int domainNumber;

    @OneToMany
    @JoinColumn(name = "domain_id")
    private List<Position> positions;
    
    public int getDomainNumber()
    {
        return domainNumber;
    }
    
    public void setDomainNumber(int domainNumber)
    {
        this.domainNumber = domainNumber;
    }
    
    public Helper getLeader()
    {
        return leader;
    }
    
    public void setLeader(Helper leader)
    {
        this.leader = leader;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public List<Position> getPositions()
    {
        return positions;
    }
    
    public void setPositions(List<Position> positions)
    {
        this.positions = positions;
    }
}