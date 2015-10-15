package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class DatabaseOperationsTest
{
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchListByQuery()
    {
        // clear db
        HibernateUtil.clearAll();

        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).persist();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.INACTIVE, 1, 1, 1980).persist();

        String qry =
                "FROM " + Helper.class.getSimpleName() + " h WHERE h." + Helper.ATTR_HELPER_STATE + " = :helperState";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        DatasourceRegistry.getDatasource(Helper.class).find(qry, parameters);
    }

    @Test
    public void testFetchDedicated()
    {
        // clear db
        HibernateUtil.clearAll();

        Helper helper = EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).persist();

        DefaultDatasource<Helper> datasource = DatasourceRegistry.getDatasource(Helper.class);
        Helper found = datasource.findById(Helper.class, helper.getId());
    }

    @Test
    public void testFetchListWithOneParamter()
    {
        // clear db
        HibernateUtil.clearAll();

        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).persist();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.ACTIVE, 1, 1, 1980).persist();

        List<Helper> found = DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_HELPER_STATE,
                HelperState.ACTIVE);
        assertEquals(2, found.size());
    }

    @Test
    public void testFetchByClassWithMutlipleParamters()
    {
        // clear db
        HibernateUtil.clearAll();

        // create messages
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "SUB1", "BODY1",
                MessagingFormat.PLAIN).persist();
        EntityFactory.buildMessageQueue("klaus", "testhelper1.trispeedys@gmail.com", "SUB1", "BODY1",
                MessagingFormat.PLAIN).persist();
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "SUB1", "BODY2",
                MessagingFormat.PLAIN).persist();

        assertEquals(
                1,
                DatasourceRegistry.getDatasource(MessageQueue.class)
                        .find(MessageQueue.class, MessageQueue.ATTR_SUBJECT, "SUB1", MessageQueue.ATTR_BODY, "BODY1",
                                MessageQueue.ATTR_FROM_ADDRESS, "klaus")
                        .size());

        ;
    }
    
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
}