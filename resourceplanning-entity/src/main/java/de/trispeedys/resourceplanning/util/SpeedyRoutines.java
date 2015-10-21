package de.trispeedys.resourceplanning.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class SpeedyRoutines
{
    public static Event duplicateEvent(Long eventId, String description, String eventKey, int day, int month,
            int year)
    {
        Event event = (Event) Datasources.getDatasource(Event.class).findById(eventId);
        if (event == null)
        {
            return null;
        }
        Event newEvent =
                EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED,
                        event.getEventTemplate()).persist();
        List<EventPosition> positions = Datasources.getDatasource(EventPosition.class).find("event", event);
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
    
    public static EntityTreeNode eventAsTree(Long eventId)
    {
        return eventAsTree((Event) Datasources.getDatasource(Event.class).findById(eventId));
    }

    public static EntityTreeNode eventAsTree(Event event)
    {
        HashMap<Domain, List<Position>> positionsPerDomain = new HashMap<Domain, List<Position>>();
        Domain key = null;
        for (EventPosition pos : event.getEventPositions())
        {
            key = pos.getPosition().getDomain();
            if (positionsPerDomain.get(key) == null)
            {
                positionsPerDomain.put(key, new ArrayList<Position>());
            }
            positionsPerDomain.get(key).add(pos.getPosition());
        }
        EntityTreeNode eventNode = new EntityTreeNode(event);
        EntityTreeNode domainNode = null;
        for (Domain dom : positionsPerDomain.keySet())
        {
            domainNode = new EntityTreeNode(dom);
            eventNode.acceptChild(domainNode);
            for (Position pos : positionsPerDomain.get(dom))
            {
                domainNode.acceptChild(new EntityTreeNode(pos));
            }
        }
        
        return eventNode;
    }   
    
    public static List<AbstractDbObject> flattenedEventTree(Event event)
    {
        EntityTreeNode root = eventAsTree(event);
        return flattenedEventTreeRecursive(root, new ArrayList<AbstractDbObject>());        
    }
    
    private static List<AbstractDbObject> flattenedEventTreeRecursive(EntityTreeNode root, List<AbstractDbObject> values)
    {
        values.add((AbstractDbObject) root.getPayLoad());
        if ((root.getChildren() != null) && (root.getChildren().size() > 0))
        {
            for (Object child : root.getChildren())
            {
                flattenedEventTreeRecursive((EntityTreeNode) child, values);
            }   
        }
        return values;
    }

    public static void debugEvent(Event event)
    {
        HierarchicalEventItem item = null;
        for (AbstractDbObject object : flattenedEventTree(event))
        {
            item = (HierarchicalEventItem) object;
            switch (item.getHierarchyLevel())
            {
                case HierarchicalEventItem.LEVEL_EVENT:
                    System.out.println(" + " + item);
                    break;
                case HierarchicalEventItem.LEVEL_DOMAIN:                    
                    System.out.println("   + " + item);
                    break;                    
                case HierarchicalEventItem.LEVEL_POSITION:
                    System.out.println("      + " + item);
                    break;                    
            }            
        }
    }

    public static String eventOutline(Event event)
    {
        String result = "";
        for (AbstractDbObject obj : flattenedEventTree(event))
        {
            result += ((HierarchicalEventItem) obj).getOutline();
        }        
        return result;
    }
}