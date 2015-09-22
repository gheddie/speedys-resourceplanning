package de.trispeedys.resourceplanning.helper;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.Days;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.EventOccurence;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.EntityBuilder;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.util.DateHelper;

public class CommitmentHelper
{
    public static void confirmHelper(Helper helper, EventOccurence eventOccurence, Position position)
            throws ResourcePlanningException
    {
        if (isHelperConfirmedForEvent(eventOccurence, helper))
        {
            throw new ResourcePlanningException("helper is already confirmed for another position!");
        }
        int dayDiff =
                Days.daysBetween(DateHelper.toDateTime(eventOccurence.getEventDate()),
                        DateHelper.toDateTime(helper.getDateOfBirth()).plusYears(position.getMinimalAge())).getDays();
        if (dayDiff > 0)
        {
            throw new ResourcePlanningException("helper is " + dayDiff + " days to young for this position!");
        }
        EntityBuilder.buildEventCommitment(helper, eventOccurence, position).persist();
    }

    /**
     * Ist der Helfer für irgendeine Position im gegebenen Event bestätigt?
     * 
     * @param eventOccurence
     * @param helper
     * @return
     * @throws ResourcePlanningException
     */
    @SuppressWarnings("unchecked")
    private static boolean isHelperConfirmedForEvent(EventOccurence eventOccurence, Helper helper)
            throws ResourcePlanningException
    {
        // get confirmed positions for this helper in this event occurence
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query q =
                session.createQuery("From " +
                        EventCommitment.class.getSimpleName() +
                        " ec WHERE ec.helper = :helper AND ec.eventOccurence = :eventOccurence AND ec.commitmentState = '" +
                        EventCommitmentState.COMFIRMED + "'");
        q.setParameter("eventOccurence", eventOccurence);
        q.setParameter("helper", helper);
        List<EventCommitment> list = q.list();
        session.close();
        return (list.size() > 0);
    }
}