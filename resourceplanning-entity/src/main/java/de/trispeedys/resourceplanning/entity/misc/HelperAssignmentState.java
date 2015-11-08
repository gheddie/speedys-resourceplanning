package de.trispeedys.resourceplanning.entity.misc;

public enum HelperAssignmentState
{
    // result of request process --> rebooking still possible
    PLANNED,
    
    // final state
    CONFIRMED,
    
    // cancelled by helper
    CANCELLED
}