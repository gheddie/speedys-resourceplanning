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
    @SuppressWarnings("unchecked")
    @Test
    public void fetchListWithOneParamter()
    {        
        //clear db
        HibernateUtil.clearAll();
        
        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).persist();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.ACTIVE, 1, 1, 1980).persist();
        
        List<Helper> found = DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        assertEquals(2, found.size());
    }
}