package de.trispeedys.resourceplanning.entity.misc;

public enum HelperCallback
{
    /** some procedure as last year... */
    ASSIGNMENT_AS_BEFORE("Ich möchte auf derselben Position helfen"),
    
    /** not this time... */
    PAUSE_ME("Diesmal habe ich keine Zeit zum Helfen"),
    
    /** i want to do something else this time... */
    CHANGE_POS("Ich möchte auf einer anderen Position helfen");
    
    private String description;
    
    HelperCallback(String aDescription)
    {
        this.description = aDescription;
    }

    public String getDescription()
    {
        return description;
    }
}