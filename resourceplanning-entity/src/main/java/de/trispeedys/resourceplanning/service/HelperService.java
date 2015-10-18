package de.trispeedys.resourceplanning.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;

public class HelperService
{
    public static HelperAssignment getPriorAssignment(Helper helper, EventTemplate eventTemplate)
    {
        String queryString =
                "From " +
                        HelperAssignment.class.getSimpleName() +
                        " ha INNER JOIN ha.event ev WHERE ha.helperId = :helperId AND ev.eventTemplate = :eventTemplate ORDER BY ev.eventDate DESC";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helperId", helper.getId());
        parameters.put("eventTemplate", eventTemplate);
        List<Object[]> list = DatasourceRegistry.getDatasource(null).find(queryString, parameters);
        if (list.size() == 0)
        {
            return null;
        }
        return (HelperAssignment) list.get(0)[0];
    }

    public static List<Long> queryActiveHelperIds()
    {
        List<Long> result = new ArrayList<Long>();
        for (Object helper : DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, "helperState",
                HelperState.ACTIVE))
        {
            result.add(((AbstractDbObject) helper).getId());
        }
        return result;
    }

    public static void deactivateHelper(Long helperId)
    {
        DefaultDatasource<Helper> datasource = DatasourceRegistry.getDatasource(Helper.class);
        Helper helper = (Helper) datasource.findById(Helper.class, helperId);
        helper.setHelperState(HelperState.INACTIVE);
        helper.persist();
    }

    public static boolean isHelperAssignedForPosition(Helper helper, Event event, Position position)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helper", helper);
        parameters.put("event", event);
        parameters.put("position", position);
        List<HelperAssignment> list =
                DatasourceRegistry.getDatasource(Helper.class)
                        .find("FROM " +
                                HelperAssignment.class.getSimpleName() +
                                " ec WHERE ec.helper = :helper AND ec.event = :event AND ec.position = :position",
                                parameters);
        return ((list != null) || (list.size() == 1));
    }

    public static Position getHelperAssignment(Helper helper, Event event)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helper", helper);
        parameters.put("event", event);
        List<HelperAssignment> helperAssignments =
                DatasourceRegistry.getDatasource(HelperAssignment.class).find(
                        "FROM " +
                                HelperAssignment.class.getSimpleName() +
                                " ec WHERE ec.event = :event AND ec.helper = :helper", parameters);
        if ((helperAssignments == null) || (helperAssignments.size() == 0))
        {
            return null;
        }
        return helperAssignments.get(0).getPosition();
    }
}