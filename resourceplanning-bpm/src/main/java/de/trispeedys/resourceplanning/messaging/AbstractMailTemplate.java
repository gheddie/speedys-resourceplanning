package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;

public abstract class AbstractMailTemplate
{
    public abstract String getBody();
    
    public abstract String getSubject();
    
    public abstract MessagingFormat getMessagingFormat();

    public abstract MessagingType getMessagingType();
}