package de.trispeedys.resourceplanning.messaging;

public abstract class AbstractMailTemplate
{
    public abstract String getBody();
    
    public abstract String getSubject();
}