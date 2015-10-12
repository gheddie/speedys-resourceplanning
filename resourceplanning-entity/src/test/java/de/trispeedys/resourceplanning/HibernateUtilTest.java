package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class HibernateUtilTest
{
    @Test
    public void testFindSome()
    {
        // clear db
        HibernateUtil.clearAll();
        // create positions
        for (int i = 1; i <= 10; i++)
        {
            EntityFactory.buildPosition("Pos" + i, i, SpeedyTestUtil.buildDefaultDomain(i), false).persist();
        }
        //fetch w/o parameters (all entries)
        assertEquals(10, DatasourceRegistry.getDatasource(null).find("FROM " + Position.class.getSimpleName()).size());
        //fetch with class (all entries)
        assertEquals(10, DatasourceRegistry.getDatasource(null).findAll(Position.class).size());
        //fetch with query string
        assertEquals(1, DatasourceRegistry.getDatasource(null).find("FROM " + Position.class.getSimpleName() + " pos WHERE pos.minimalAge = 3").size());
        assertEquals(4, DatasourceRegistry.getDatasource(null).find("FROM " + Position.class.getSimpleName() + " pos WHERE pos.minimalAge >= 3 AND pos.minimalAge <= 6").size());        
        //fetch with parameters
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("minimalAge", 5);
        assertEquals(1, DatasourceRegistry.getDatasource(null).find("FROM " + Position.class.getSimpleName() + " pos WHERE pos.minimalAge = :minimalAge", parameters).size());
        assertEquals(1, DatasourceRegistry.getDatasource(null).find("FROM " + Position.class.getSimpleName() + " pos WHERE pos.minimalAge = :minimalAge", "minimalAge", 5).size());
    }
    
    @Test
    public void testFindById()
    {
        // clear db
        HibernateUtil.clearAll();
        //create position
        Position pos = EntityFactory.buildPosition("Pos", 87, SpeedyTestUtil.buildDefaultDomain(1), false).persist();
        //find by id
        Position foundPosition = (Position) DatasourceRegistry.getDatasource(null).findById(Position.class, pos.getId());
        assertTrue(foundPosition != null);
        assertEquals(87, foundPosition.getMinimalAge());
    }
}