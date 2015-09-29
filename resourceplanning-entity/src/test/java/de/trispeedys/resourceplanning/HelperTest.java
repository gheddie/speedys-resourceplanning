package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperState;
import de.trispeedys.resourceplanning.entity.builder.EntityBuilder;
import de.trispeedys.resourceplanning.service.HelperService;

import static org.junit.Assert.assertEquals;

public class HelperTest
{
    @Test
    public void testFirstAssignment()
    {
        HibernateUtil.clearAll();
        
        Helper helper = EntityBuilder.buildHelper("Stefan", "Schulz", EventCommitmentTest.TEST_MAIL_ADDRESS, HelperState.ACTIVE, 13, 2, 1976).persist();
        
        assertEquals(true, HelperService.isFirstAssignment(helper.getId()));
    }
}