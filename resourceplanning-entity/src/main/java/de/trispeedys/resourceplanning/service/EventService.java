package de.trispeedys.resourceplanning.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
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
}