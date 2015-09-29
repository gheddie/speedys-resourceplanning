package de.trispeedys.resourceplanning.entity.builder;

import java.util.Calendar;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;

public class EntityBuilder
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

    public static EventCommitment buildEventCommitment(Helper helper, Event event, Position position, EventCommitmentState eventCommitmentState)
    {
        return new EventCommitmentBuilder().withHelper(helper)
                .withPosition(position)
                .withEvent(event)
                .withCommitmentState(eventCommitmentState)
                .build();
    }

    public static Position buildPosition(String description, int minimalAge)
    {
        return new PositionBuilder().withDescription(description).withMinimalAge(minimalAge).build();
    }

    public static Event buildEvent(String description, String eventKey, int day, int month, int year)
    {
        Calendar eventDate = Calendar.getInstance();
        eventDate.set(Calendar.DAY_OF_MONTH, day);
        eventDate.set(Calendar.MONTH, month - 1);
        eventDate.set(Calendar.YEAR, year);
        return new EventBuilder().withDescription(description)
                .withDate(eventDate.getTime())
                .withEventKey(eventKey)
                .build();
    }

    public static MessageQueue buildMessageQueue()
    {
        return new MessageQueueBuilder().withFromAddress("noreply@sternico.de").withToAddress("klaus@peter.de").withSubject("Hallo").withBody("123�\n456\n789").build();
    }

    public static EventPosition buildEventPosition(Event event, Position position)
    {
        return new EventPositionBuilder().withEvent(event).withPosition(position).build();
    }
}