package de.trispeedys.resourceplanning.entity;

public enum MessagingType
{
    // initial reminding (with repetitions)
    REMINDER_STEP_0,
    REMINDER_STEP_1,
    REMINDER_STEP_2,

    DEACTIVATION_REQUEST,
    
    // propose positions to helper, e.g. 'ASSIGNMENT_AS_BEFORE' fails
    PROPOSE_POSITIONS,
    
    // confirmation of an assignment sent to the helper
    CONFIRMATION,
    
    // a helper has cancelled his assignment (someone will be noticed)
    ALERT_BOOKING_CANCELLED,

    // fallback
    NONE
}