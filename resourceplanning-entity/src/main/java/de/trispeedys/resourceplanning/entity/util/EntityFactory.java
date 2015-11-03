package de.trispeedys.resourceplanning.entity.util;

import java.util.Calendar;

import de.trispeedys.resourceplanning.entity.DatabaseLogger;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.DomainResponsibility;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.DatabaseLoggerBuilder;
import de.trispeedys.resourceplanning.entity.builder.DomainBuilder;
import de.trispeedys.resourceplanning.entity.builder.DomainResponsibilityBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventPositionBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventTemplateBuilder;
import de.trispeedys.resourceplanning.entity.builder.HelperAssignmentBuilder;
import de.trispeedys.resourceplanning.entity.builder.HelperBuilder;
import de.trispeedys.resourceplanning.entity.builder.MessageQueueBuilder;
import de.trispeedys.resourceplanning.entity.builder.PositionBuilder;
import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class EntityFactory
{
    public static Helper buildHelper(String lastName, String firstName, String email,
            HelperState helperState, int dayOfBirth, int monthOfBirth, int yearOfBirth)
    {
        Calendar dateOfBirth = Calendar.getInstance();
        dateOfBirth.set(Calendar.DAY_OF_MONTH, dayOfBirth);
        dateOfBirth.set(Calendar.MONTH, monthOfBirth - 1);
        dateOfBirth.set(Calendar.YEAR, yearOfBirth);
        Helper result =
                new HelperBuilder().withFirstName(firstName)
                        .withLastName(lastName)
                        .withDateOfBirth(dateOfBirth.getTime())
                        .withEmail(email)
                        .withHelperState(helperState)
                        .build();
        result.setCode(SpeedyRoutines.createHelperCode(result));
        return result;
    }
    
    public static HelperAssignment buildHelperAssignment(Helper helper, Event event, Position position)
    {
        return buildHelperAssignment(helper, event, position, HelperAssignmentState.CONFIRMED);
    }

    public static HelperAssignment buildHelperAssignment(Helper helper, Event event, Position position,
            HelperAssignmentState helperAssignmentState)
    {
        if (!(PositionService.isPositionPresentInEvent(position, event)))
        {
            throw new ResourcePlanningException("helper '" +
                    helper + "' can not be commited to position '" + position +
                    "' as it is not present in event '" + event + "'.");
        }
        return new HelperAssignmentBuilder().withHelper(helper)
                .withPosition(position)
                .withEvent(event)
                .withHelperAssignmentState(helperAssignmentState)
                .build();
    }

    public static Position buildPosition(String description, int minimalAge, Domain domain,
            boolean authorityOverride, int positionNumber, boolean choosable)
    {
        return new PositionBuilder().withDescription(description)
                .withMinimalAge(minimalAge)
                .withDomain(domain)
                .withAuthorityOverride(authorityOverride)
                .withPositionNumber(positionNumber)
                .withChoosable(choosable)
                .build();
    }

    public static EventTemplate buildEventTemplate(String description)
    {
        return new EventTemplateBuilder().withDescription(description).build();
    }

    public static Event buildEvent(String description, String eventKey, int day, int month, int year,
            EventState eventState, EventTemplate eventTemplate)
    {
        Calendar eventDate = Calendar.getInstance();
        eventDate.set(Calendar.DAY_OF_MONTH, day);
        eventDate.set(Calendar.MONTH, month - 1);
        eventDate.set(Calendar.YEAR, year);
        return new EventBuilder().withDescription(description)
                .withDate(eventDate.getTime())
                .withEventKey(eventKey)
                .withEventState(eventState)
                .withEventTemplate(eventTemplate)
                .build();
    }

    public static MessageQueue buildMessageQueue(String fromAddress, String toAddress, String subject,
            String body, MessagingFormat messagingFormat)
    {
        return buildMessageQueue(fromAddress, toAddress, subject, body, MessagingType.NONE, messagingFormat);
    }

    public static MessageQueue buildMessageQueue(String fromAddress, String toAddress, String subject,
            String body, MessagingType messagingType, MessagingFormat messagingFormat)
    {
        return new MessageQueueBuilder().withFromAddress(fromAddress)
                .withToAddress(toAddress)
                .withSubject(subject)
                .withBody(body)
                .withMessagingType(messagingType)
                .withMessagingFormat(messagingFormat)
                .build();
    }

    public static EventPosition buildEventPosition(Event event, Position position)
    {
        return new EventPositionBuilder().withEvent(event).withPosition(position).build();
    }

    public static DomainResponsibility buildDomainResponsibility(Domain domain, Helper helper)
    {
        return new DomainResponsibilityBuilder().withDomain(domain).withHelper(helper).build();
    }

    public static Domain buildDomain(String name, int domainNumber)
    {
        return new DomainBuilder().withDomainNumber(domainNumber).withName(name).build();
    }

    public static DatabaseLogger buildLog(String businessKey, String message)
    {
        return buildLog(businessKey, message, DbLogLevel.INFO);
    }

    public static DatabaseLogger buildLog(String businessKey, String message, DbLogLevel logLevel)
    {
        return new DatabaseLoggerBuilder().withBusinessKey(businessKey)
                .withMessage(message)
                .withLogLevel(logLevel)
                .build();
    }
}