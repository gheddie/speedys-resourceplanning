package de.trispeedys.resourceplanning.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.EventOccurence;
import de.trispeedys.resourceplanning.entity.Helper;

public class HelperService
{
    @SuppressWarnings("unchecked")
    public static EventCommitment getLastConfirmedAssignmentForHelper(Long helperId)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query q =
                session.createQuery("From " +
                        EventCommitment.class.getSimpleName() +
                        " ec INNER JOIN ec.eventOccurence eo WHERE ec.helperId = :helperId AND ec.commitmentState = '" +
                        EventCommitmentState.CONFIRMED + "' ORDER BY eo.eventDate DESC");
        q.setParameter("helperId", helperId);
        List<Object[]> list = q.list();
        session.close();
        if (list.size() == 0)
        {
            return null;
        }
        return (EventCommitment) list.get(0)[0];
    }

    public static void isHelperReassignableToSamePosition(EventOccurence eventOccurence, Helper helper)
    {
        EventCommitment lastConfirmedAssignment = getLastConfirmedAssignmentForHelper(helper.getId());
    }

    public static boolean isFirstAssignment(Long helperId)
    {
        CommitmentService.getAllConfirmedCommitments(helperId);
        return false;
    }
}