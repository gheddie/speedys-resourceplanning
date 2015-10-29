package de.trispeedys.resourceplanning.dto;

import de.trispeedys.resourceplanning.entity.misc.annotation.Display;

public class HelperDTO
{
    @Display
    private String firstName;
    
    @Display
    private String lastName;
    
    private String email;
    
    private String code;
    
    private String helperState;

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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getHelperState()
    {
        return helperState;
    }
    
    public void setHelperState(String helperState)
    {
        this.helperState = helperState;
    }
}