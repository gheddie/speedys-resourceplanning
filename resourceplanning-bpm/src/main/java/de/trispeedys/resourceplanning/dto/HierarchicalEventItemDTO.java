package de.trispeedys.resourceplanning.dto;

public class HierarchicalEventItemDTO
{
    private String itemType;
    
    private String infoString;

    private int hierarchyLevel;

    private String itemKey;
    
    private String assignmentString;

    private Long entityId;
    
    public String getItemType()
    {
        return itemType;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }
    
    public String getInfoString()
    {
        return infoString;
    }

    public void setInfoString(String infoString)
    {
        this.infoString = infoString;
    }
    
    public int getHierarchyLevel()
    {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(int hierarchyLevel)
    {
        this.hierarchyLevel = hierarchyLevel;
    }
    
    public String getItemKey()
    {
        return itemKey;
    }

    public void setItemKey(String itemKey)
    {
        this.itemKey = itemKey;
    }
    
    public String getAssignmentString()
    {
        return assignmentString;
    }
    
    public void setAssignmentString(String assignmentString)
    {
        this.assignmentString = assignmentString;
    }
    
    public Long getEntityId()
    {
        return entityId;
    }

    public void setEntityId(Long entityId)
    {
        this.entityId = entityId;
    }
}