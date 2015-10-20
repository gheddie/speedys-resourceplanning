package de.trispeedys.resourceplanning.test;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class SpeedyRoutines
{
    public static Event duplicateEvent(Long eventId, String description, String eventKey, int day, int month,
            int year)
    {
        Event event = (Event) DatasourceRegistry.getDatasource(Event.class).findById(eventId);
        if (event == null)
        {
            return null;
        }
        Event newEvent =
                EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED,
                        event.getEventTemplate()).persist();
        List<EventPosition> positions = DatasourceRegistry.getDatasource(EventPosition.class).find("event", event);
        System.out.println(positions.size());
        Position newPosRelation = null;
        for (EventPosition evtpos : positions)
        {
            newPosRelation = evtpos.getPosition();
            // attach every old position to the new event
            EntityFactory.buildEventPosition(newEvent, newPosRelation).persist();
        }
        return newEvent;
    }

    public static String createHelperCode(Helper helper)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(helper.getDateOfBirth());
        StringBuffer result = new StringBuffer();
        result.append(helper.getLastName()
                .substring(0, 2)
                .toUpperCase()
                .replaceAll("Ä", "A")
                .replaceAll("Ö", "O")
                .replaceAll("Ü", "U"));
        result.append(helper.getFirstName()
                .substring(0, 2)
                .toUpperCase()
                .replaceAll("Ä", "A")
                .replaceAll("Ö", "O")
                .replaceAll("Ü", "U"));
        String dayStr = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        result.append(dayStr.length() == 2
                ? dayStr
                : "0" + dayStr);
        String monthStr = String.valueOf(cal.get(Calendar.MONTH) + 1);
        result.append(monthStr.length() == 2
                ? monthStr
                : "0" + monthStr);
        result.append(cal.get(Calendar.YEAR));
        return result.toString();
    }
    
    public static void relatePositionsToEvent(Event event, Position... positions)
    {
        for (Position position : positions)
        {
            EntityFactory.buildEventPosition(event, position).persist();
        }
    }
    
    public static void relateEventsToPosition(Position position, Event... events)
    {
        for (Event event : events)
        {
            EntityFactory.buildEventPosition(event, position).persist();
        }
    }

    public static void assignHelperToPositions(Helper helper, Event event, Position... positions)
    {
        for (Position position : positions)
        {
            EntityFactory.buildHelperAssignment(helper, event, position).persist();
        }       
    }
}