package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.service.HelperService;

public class HelperTest
{
    @Test
    public void testFirstAssignment()
    {
        HibernateUtil.clearAll();
        
        Helper helper = EntityFactory.buildHelper("Stefan", "Schulz", EventCommitmentTest.TEST_MAIL_ADDRESS, HelperState.ACTIVE, 13, 2, 1976).persist();
        
        assertEquals(true, HelperService.isFirstAssignment(helper.getId()));
    }
}