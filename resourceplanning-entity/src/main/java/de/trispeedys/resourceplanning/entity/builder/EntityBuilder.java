package de.trispeedys.resourceplanning.entity.builder;

import java.util.Calendar;

import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.EventOccurence;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperState;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;

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

    public static EventCommitment buildEventCommitment(Helper helper, EventOccurence eventOccurence, Position position)
    {
        return new EventCommitmentBuilder().withHelper(helper)
                .withPosition(position)
                .withEventOccurence(eventOccurence)
                .withCommitmentState(EventCommitmentState.COMFIRMED)
                .build();
    }

    public static Position buildPosition(String description, int minimalAge)
    {
        return new PositionBuilder().withDescription(description).withMinimalAge(minimalAge).build();
    }

    public static EventOccurence buildEventOccurence(String description, String eventKey, int day, int month, int year)
    {
        Calendar dateOfOccurence = Calendar.getInstance();
        dateOfOccurence.set(Calendar.DAY_OF_MONTH, day);
        dateOfOccurence.set(Calendar.MONTH, month - 1);
        dateOfOccurence.set(Calendar.YEAR, year);
        return new EventOccurenceBuilder().withDescription(description)
                .withDate(dateOfOccurence.getTime())
                .withEventKey(eventKey)
                .build();
    }

    public static MessageQueue buildMessageQueue()
    {
        return new MessageQueueBuilder().withFromAddress("noreply@sternico.de").withToAddress("klaus@peter.de").withSubject("Hallo").withBody("123ß\n456\n789").build();
    }
}