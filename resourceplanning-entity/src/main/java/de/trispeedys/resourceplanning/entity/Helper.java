package de.trispeedys.resourceplanning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.HelperState;

@Entity
public class Helper extends AbstractDbObject
{
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "helper_state")
    @NotNull
    private HelperState helperState;

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public Date getDateOfBirth()
    {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public HelperState getHelperState()
    {
        return helperState;
    }
    
    public void setHelperState(HelperState helperState)
    {
        this.helperState = helperState;
    }
}