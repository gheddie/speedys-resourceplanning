package de.trispeedys.resourceplanning.dto;

public class HierarchicalEventItemDTO
{
    private String itemType;
    
    private String infoString;

    private int hierarchyLevel;
    
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
}