package de.trispeedys.resourceplanning.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;

public class DebugEvent
{
    public static void main(String[] args)
    {
        HibernateUtil.clearAll();
        TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.PLANNED);
        List<Event> allEvents = (List<Event>) Datasources.getDatasource(Event.class).findAll(Event.class);
        System.out.println(allEvents.size());
        for (Event ev : allEvents)
        {
            System.out.println(debugEvent(ev).toString());
        }
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

    private static String getLeveledString(int level, String s)
    {
        return doLevel(level) + s + "\n";
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