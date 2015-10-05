package de.trispeedys.resourceplanning;

import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import static org.junit.Assert.assertEquals;

public class DatabaseOperationsTest
{
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