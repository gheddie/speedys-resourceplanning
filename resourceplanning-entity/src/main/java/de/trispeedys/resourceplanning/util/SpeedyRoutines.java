package de.trispeedys.resourceplanning.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.comparator.EnumeratedEventItemComparator;
import de.trispeedys.resourceplanning.util.comparator.TreeNodeComparator;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class SpeedyRoutines
{
    public static Event duplicateEvent(Event event, String description, String eventKey, int day, int month,
            int year, List<Integer> positionExcludes, List<PositionInclude> includes)
    {
        if (event == null)
        {
            return null;
        }
        if (!(event.getEventState().equals(EventState.FINISHED)))
        {
            throw new ResourcePlanningException("only a finished event can be duplicated!!");
        }
        checkExcludes(event, positionExcludes);
        Event newEvent =
                EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED,
                        event.getEventTemplate()).saveOrUpdate();
        List<EventPosition> posRelations =
                Datasources.getDatasource(EventPosition.class).find("event", event);
        Position pos = null;
        for (EventPosition evtpos : posRelations)
        {
            pos = evtpos.getPosition();
            // attach every old position to the new event
            if (!(excludePosition(pos, positionExcludes)))
            {
                EntityFactory.buildEventPosition(newEvent, pos).saveOrUpdate();
            }
        }
        // process includes
        if (includes != null)
        {
            Position additionalPosition = null;
            for (PositionInclude include : includes)
            {
                additionalPosition = RepositoryProvider.getRepository(PositionRepository.class)
                        .findPositionByPositionNumber(include.getPositionNumber());
                if (additionalPosition == null)
                {
                    throw new ResourcePlanningException("unable to find position by position number '"+include.getPositionNumber()+"'!!");
                }
                EntityFactory.buildEventPosition(
                        newEvent,
                        additionalPosition).saveOrUpdate();
            }
        }
        return newEvent;
    }

    private static void checkExcludes(Event event, List<Integer> positionExcludes)
    {
        if ((positionExcludes == null) || (positionExcludes.size() == 0))
        {
            return;
        }
        // list with all pos numbers in the given event...
        List<Integer> posNumbersInEvent = new ArrayList<Integer>();
        for (Position pos : RepositoryProvider.getRepository(PositionRepository.class).findPositionsInEvent(
                event))
        {
            posNumbersInEvent.add(pos.getPositionNumber());
        }
        for (Integer exclude : positionExcludes)
        {
            if (!(posNumbersInEvent.contains(exclude)))
            {
                throw new ResourcePlanningException("pos number '" +
                        exclude + "' can not be excluded from event as it is not present!!");
            }
        }
    }

    private static boolean excludePosition(Position position, List<Integer> positionExcludes)
    {
        return ((positionExcludes != null) && (positionExcludes.contains(position.getPositionNumber())));
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
            EntityFactory.buildEventPosition(event, position).saveOrUpdate();
        }
    }

    public static void relateEventsToPosition(Position position, Event... events)
    {
        for (Event event : events)
        {
            EntityFactory.buildEventPosition(event, position).saveOrUpdate();
        }
    }

    public static void assignHelperToPositions(Helper helper, Event event, Position... positions)
    {
        for (Position position : positions)
        {
            EntityFactory.buildHelperAssignment(helper, event, position).saveOrUpdate();
        }
    }

    public static EntityTreeNode eventAsTree(Long eventId)
    {
        return eventAsTree(RepositoryProvider.getRepository(EventRepository.class).findById(eventId));
    }

    public static EntityTreeNode eventAsTree(Event event)
    {
        if (event == null)
        {
            return null;
        }
        List<HelperAssignment> helperAssignments =
                RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByEvent(event);
        HashMap<Long, HelperAssignment> assignmentMap = new HashMap<Long, HelperAssignment>();
        for (HelperAssignment assignment : helperAssignments)
        {
            if (!(assignment.isCancelled()))
            {
                // ignore cancelled asignments here...
                assignmentMap.put(assignment.getPosition().getId(), assignment);   
            }
        }
        HashMap<Domain, List<Position>> positionsPerDomain = new HashMap<Domain, List<Position>>();
        EntityTreeNode<Event> eventNode = new EventTreeNode<Event>();
        eventNode.setPayLoad(event);
        Domain key = null;
        List<EventPosition> eventPositions = event.getEventPositions();
        if ((eventPositions == null) || (eventPositions.size() == 0))
        {
            return eventNode;
        }
        for (EventPosition pos : eventPositions)
        {
            key = pos.getPosition().getDomain();
            if (positionsPerDomain.get(key) == null)
            {
                positionsPerDomain.put(key, new ArrayList<Position>());
            }
            positionsPerDomain.get(key).add(pos.getPosition());
        }
        EntityTreeNode<Domain> domainNode = null;
        // build tree
        EnumeratedEventItemComparator itemComparator = new EnumeratedEventItemComparator();
        List<EntityTreeNode<Domain>> domainNodes = new ArrayList<EntityTreeNode<Domain>>();
        for (Domain dom : positionsPerDomain.keySet())
        {
            domainNode = new DomainTreeNode<Domain>();
            domainNode.setPayLoad(dom);
            List<Position> positionList = positionsPerDomain.get(dom);
            // sort positions
            Collections.sort(positionList, itemComparator);
            for (Position pos : positionList)
            {
                PositionTreeNode<Position> childPosNode = new PositionTreeNode<Position>();
                childPosNode.setPayLoad(new AssignmentContainer(pos, getAssignment(assignmentMap, pos)));
                domainNode.acceptChild(childPosNode);
            }
            domainNodes.add(domainNode);
        }
        // add domain nodes to root (sort domains before)...
        Collections.sort(domainNodes, new TreeNodeComparator());
        for (Object domNode : domainNodes)
        {
            eventNode.acceptChild(domNode);
        }
        // return result
        return eventNode;
    }

    private static Helper getAssignment(HashMap<Long, HelperAssignment> assignmentMap, Position pos)
    {
        return (assignmentMap.get(pos.getId()) != null
                ? assignmentMap.get(pos.getId()).getHelper()
                : null);
    }

    public static List<EntityTreeNode> flattenedEventNodes(Event event)
    {
        EntityTreeNode<Event> root = eventAsTree(event);
        return flattenedEventNodesRecursive(root, new ArrayList<EntityTreeNode>());
    }

    private static List<EntityTreeNode> flattenedEventNodesRecursive(EntityTreeNode root,
            List<EntityTreeNode> nodes)
    {
        nodes.add(root);
        if ((root.getChildren() != null) && (root.getChildren().size() > 0))
        {
            for (Object child : root.getChildren())
            {
                flattenedEventNodesRecursive((EntityTreeNode) child, nodes);
            }
        }
        return nodes;
    }

    public static List<AbstractDbObject> flattenedEventTree(Event event)
    {
        EntityTreeNode<Event> root = eventAsTree(event);
        return flattenedEventTreeRecursive(root, new ArrayList<AbstractDbObject>());
    }

    private static List<AbstractDbObject> flattenedEventTreeRecursive(EntityTreeNode root,
            List<AbstractDbObject> values)
    {
        values.add((AbstractDbObject) root.getHierarchicalItem());
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
        System.out.println(" ---------- EVENT --------------------------------- ");
        EntityTreeNode node = null;
        for (EntityTreeNode object : flattenedEventNodes(event))
        {
            node = (EntityTreeNode) object;
            switch (node.getHierarchyLevel())
            {
                case HierarchicalEventItem.LEVEL_EVENT:
                    System.out.println(" + " + node.infoString());
                    break;
                case HierarchicalEventItem.LEVEL_DOMAIN:
                    System.out.println("   + " + node.infoString());
                    break;
                case HierarchicalEventItem.LEVEL_POSITION:
                    System.out.println("      + " + node.infoString());
                    break;
            }
        }
        System.out.println(" ---------- EVENT --------------------------------- ");
    }

    public static String eventOutline(Event event)
    {
        String result = "";
        for (AbstractDbObject obj : flattenedEventTree(event))
        {
            result += ((HierarchicalEventItem) obj).getDifferentiator();
        }
        return result;
    }
}