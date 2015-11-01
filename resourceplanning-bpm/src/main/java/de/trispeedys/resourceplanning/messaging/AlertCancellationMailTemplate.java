package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;

public class AlertCancellationMailTemplate extends AbstractMailTemplate
{
    private Helper helper;

    private Event event;

    public AlertCancellationMailTemplate(Helper aHelper, Event aEvent)
    {
        super();
        this.helper = aHelper;
        this.event = aEvent;
    }

    public String getBody()
    {
        // TODO use proper mail body !!
        return "Helfer "+helper.getLastName()+", "+helper.getFirstName()+" (Position: tralala) hat abgesagt!!";
    }

    public String getSubject()
    {
        return "Helper-Absage für den Wettkampf " + event.getDescription();
    }
}