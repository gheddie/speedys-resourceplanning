package de.trispeedys.resourceplanning.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.EventDatasource;
import de.trispeedys.resourceplanning.entity.Event;

public class EventRepository implements DatabaseRepository<EventRepository>
{
    private EventDatasource datasource;

    public void createDataSource()
    {
        datasource = new EventDatasource();
    }

    public List<Event> findEventByTemplateOrdered(String eventTemplateName)
    {
        String queryString =
                "From " +
                        Event.class.getSimpleName() +
                        " ev INNER JOIN ev.eventTemplate et WHERE et.description = :description ORDER BY ev.eventDate ASC";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("description", eventTemplateName);
        List<Object[]> list = datasource.find(queryString, parameters);
        if (list.size() == 0)
        {
            return null;
        }
        List<Event> result = new ArrayList<Event>();
        for (Object[] obj : list)
        {
            result.add((Event) obj[0]);
        }
        return result;
    }
}