package de.trispeedys.resourceplanning.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class DebugEvent
{
    @SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
//        HibernateUtil.clearAll();
//        TestDataProvider.createSimpleEvent();
        List<Event> allEvents = (List<Event>) DatasourceRegistry.getDatasource(null).find(Event.class);
        System.out.println(allEvents.size());
        for (Event ev : allEvents)
        {
            System.out.println(debugEvent(ev).toString());
        }
    }
    
    public static StringBuffer debugEvent(Long eventId)
    {
        return debugEvent((Event) DatasourceRegistry.getDatasource(null).findById(Event.class, eventId));
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
        List<?> commitments =
                DatasourceRegistry.getDatasource(null).find("FROM " +
                        EventCommitment.class.getSimpleName() +
                        " ec WHERE ec.position = :position AND ec.event = :event", variables);
        if ((commitments == null) || commitments.size() != 1)
        {
            return "[UNOCCUPIED]";
        }
        EventCommitment commitment = (EventCommitment) commitments.get(0);
        return commitment.getHelper().toString();
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