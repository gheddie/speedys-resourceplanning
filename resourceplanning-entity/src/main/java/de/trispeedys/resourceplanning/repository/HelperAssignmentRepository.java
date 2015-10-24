package de.trispeedys.resourceplanning.repository;

import java.util.List;

import de.trispeedys.resourceplanning.datasource.HelperAssignmentDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.HelperAssignment;

public class HelperAssignmentRepository implements DatabaseRepository<HelperAssignmentRepository>
{
    private HelperAssignmentDatasource datasource;
    
    public List<HelperAssignment> findByEvent(Event event)
    {
        return datasource.find(HelperAssignment.ATTR_EVENT, event);
    }

    public void createDataSource()
    {
        datasource = new HelperAssignmentDatasource();
    }
}