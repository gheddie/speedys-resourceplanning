package de.trispeedys.resourceplanning.entity.util;

import java.util.Calendar;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.DomainBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventCommitmentBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventPositionBuilder;
import de.trispeedys.resourceplanning.entity.builder.HelperBuilder;
import de.trispeedys.resourceplanning.entity.builder.MessageQueueBuilder;
import de.trispeedys.resourceplanning.entity.builder.PositionBuilder;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class EntityFactory
{
    public static Helper buildHelper(String firstName, String lastName, String email, HelperState helperState,
            int dayOfBirth, int monthOfBirth, int yearOfBirth)
    {
        Calendar dateOfBirth = Calendar.getInstance();
        dateOfBirth.set(Calendar.DAY_OF_MONTH, dayOfBirth);
        dateOfBirth.set(Calendar.MONTH, monthOfBirth - 1);
        dateOfBirth.set(Calendar.YEAR, yearOfBirth);
        return new HelperBuilder().withFirstName(firstName)
                .withLastName(lastName)
                .withDateOfBirth(dateOfBirth.getTime())
                .withEmail(email)
                .withHelperState(helperState)
                .build();
    }

    public static EventCommitment buildEventCommitment(Helper helper, Event event, Position position)
    {
        if (!(PositionService.isPositionPresentInEvent(position, event)))
        {
            throw new ResourcePlanningException("helper '"+helper+"' can not be commited to position '"+position+"' as it is not present in event '"+event+"'.");
        }
        return new EventCommitmentBuilder().withHelper(helper)
                .withPosition(position)
                .withEvent(event)
                .build();
    }

    public static Position buildPosition(String description, int minimalAge, Domain domain, boolean authorityOverride)
    {
        return new PositionBuilder().withDescription(description).withMinimalAge(minimalAge).withDomain(domain).withAuthorityOverride(authorityOverride).build();
    }
    
    public static Event buildEvent(String description, String eventKey, int day, int month, int year)
    {
        return buildEvent(description, eventKey, day, month, year, false);
    }    

    public static Event buildEvent(String description, String eventKey, int day, int month, int year, boolean helpersReminded)
    {
        Calendar eventDate = Calendar.getInstance();
        eventDate.set(Calendar.DAY_OF_MONTH, day);
        eventDate.set(Calendar.MONTH, month - 1);
        eventDate.set(Calendar.YEAR, year);
        return new EventBuilder().withDescription(description)
                .withDate(eventDate.getTime())
                .withEventKey(eventKey)
                .withHelpersReminded(helpersReminded)
                .build();
    }
    
    public static MessageQueue buildMessageQueue(String fromAddress, String toAddress, String subject, String body)
    {
        return buildMessageQueue(fromAddress, toAddress, subject, body, MessagingType.NONE);
    }

    public static MessageQueue buildMessageQueue(String fromAddress, String toAddress, String subject, String body, MessagingType messagingType)
    {
        return new MessageQueueBuilder().withFromAddress(fromAddress).withToAddress(toAddress).withSubject(subject).withBody(body).withMessagingType(messagingType).build();
    }

    public static EventPosition buildEventPosition(Event event, Position position)
    {
        return new EventPositionBuilder().withEvent(event).withPosition(position).build();
    }
    
    public static Domain buildDomain(String name, int domainNumber, Helper leader)
    {
        //if a leader is set for the domain, he must be an active helper!!
        if (leader != null)
        {
            if (!(leader.isActive()))
            {
                throw new ResourcePlanningException("helper '"+leader+"' can not be the leader of a domain is he is not active!!.");
            }
        }
        return new DomainBuilder().withDomainNumber(domainNumber).withName(name).withLeader(leader).build();
    }  
}