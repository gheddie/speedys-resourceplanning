package de.trispeedys.resourceplanning.dto;

public class PositionDTO
{
    private String description;
    
    private Long positionId;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getPositionId()
    {
        return positionId;
    }

    public void setPositionId(Long positionId)
    {
        this.positionId = positionId;
    }
}