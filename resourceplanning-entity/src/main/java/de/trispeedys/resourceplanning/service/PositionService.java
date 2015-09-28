package de.trispeedys.resourceplanning.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.EventOccurence;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionService
{
    @SuppressWarnings("unchecked")
    public static List<Position> findPositionsInEventOccurence(EventOccurence eventOccurence)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query q =
                session.createQuery("From " +
                        EventPosition.class.getSimpleName() +
                        " ep INNER JOIN ep.position pos WHERE ep.eventOccurence = :eventOccurence");
        q.setParameter("eventOccurence", eventOccurence);
        List<Object[]> list = q.list();
        session.close();
        List<Position> result = new ArrayList<Position>();
        for (Object[] tuple : list)
        {
            result.add((Position) tuple[1]);
        }
        return result;
    }
}