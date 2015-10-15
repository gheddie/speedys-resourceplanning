package de.trispeedys.resourceplanning.entity.misc;

public enum HelperCallback
{
    /** some procedure as last year... */
    ASSIGNMENT_AS_BEFORE("Wie immer"),
    
    /** not this time... */
    PAUSE_ME("Diesmal nicht"),
    
    /** i want to do something else this time... */
    CHANGE_POS("Woanders helfen");
    
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