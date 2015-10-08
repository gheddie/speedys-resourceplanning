package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class DatabaseOperationsTest
{
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchListByQuery()
    {        
        //clear db
        HibernateUtil.clearAll();
        
        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).persist();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.INACTIVE, 1, 1, 1980).persist();
        
        String qry = "FROM " + Helper.class.getSimpleName() + " h WHERE h."+Helper.ATTR_HELPER_STATE+" = :helperState";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        DatasourceRegistry.getDatasource(Helper.class).find(qry, parameters);
    }
    
    @Test
    public void testFetchDedicated()
    {        
        //clear db
        HibernateUtil.clearAll();
        
        Helper helper = EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).persist();
        
        Helper found = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, helper.getId());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFetchListWithOneParamter()
    {        
        //clear db
        HibernateUtil.clearAll();
        
        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).persist();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.ACTIVE, 1, 1, 1980).persist();
        
        List<Helper> found = DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        assertEquals(2, found.size());
    }
}