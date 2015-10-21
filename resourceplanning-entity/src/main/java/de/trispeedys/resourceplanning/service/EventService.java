package de.trispeedys.resourceplanning.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;

public class EventService
{
    public static List<Event> findEventsByTemplateAndStatus(String templateName, EventState eventState)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Event.ATTR_EVENT_STATE, eventState);
        parameters.put(EventTemplate.ATTR_DESCRIPTION, templateName);
        List<Object[]> list =
                Datasources.getDatasource(EventTemplate.class)
                        .find("FROM " +
                                Event.class.getSimpleName() +
                                " ev INNER JOIN ev.eventTemplate et WHERE ev.eventState = :eventState AND et.description = :description", parameters);
        List<Event> result = new ArrayList<Event>();
        for (Object[] o : list)
        {
            result.add((Event) o[0]);
        }
        return result;
    }
    
    public static StringBuffer debugEvent(Long eventId)
    {
        return debugEvent((Event) Datasources.getDatasource(Event.class).findById(eventId));
    }

    public static StringBuffer debugEvent(Event event)
    {
        HashMap<String, List<Position>> positionsPerDomain = new HashMap<String, List<Position>>();
        String key = null;
        for (EventPosition pos : event.getEventPositions())
        {
            key = pos.getPosition().getDomain().getName();
            if (positionsPerDomain.get(key) == null)
            {
                positionsPerDomain.put(key, new ArrayList<Position>());
            }
            positionsPerDomain.get(key).add(pos.getPosition());
        }
        System.out.println("------------------------------------------------");
        StringBuffer buffer = new StringBuffer();
        buffer.append(getLeveledString(0, event.getEventKey()));
        for (String domainKey : positionsPerDomain.keySet())
        {
            buffer.append(getLeveledString(1, domainKey));
            String tmp = "";
            String helperString = "";
            for (Position pos : positionsPerDomain.get(domainKey))
            {
                HashMap<String, Object> variables = new HashMap<String, Object>();
                variables.put("position", pos);
                variables.put("event", event);
                helperString = getHelperString(variables);
                tmp = pos.getDescription() + " ---> (" + helperString + ")";
                buffer.append(getLeveledString(3, tmp));
            }
        }
        return buffer;
    }    
    
    private static String getLeveledString(int level, String s)
    {
        return doLevel(level) + s + "\n";
    }
    
    private static String getHelperString(HashMap<String, Object> variables)
    {
        List<?> helperAssignments =
                Datasources.getDatasource(HelperAssignment.class).find("FROM " +
                        HelperAssignment.class.getSimpleName() +
                        " ec WHERE ec.position = :position AND ec.event = :event", variables);
        if ((helperAssignments == null) || helperAssignments.size() != 1)
        {
            return "[UNOCCUPIED]";
        }
        HelperAssignment helperAssignment = (HelperAssignment) helperAssignments.get(0);
        return helperAssignment.getHelper().toString();
    }
    
    private static String doLevel(int level)
    {
        String s = "";
        for (int i = 0; i < level; i++)
        {
            s += "---";
        }
        return s;
    }    
}