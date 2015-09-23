package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.EventOccurence;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperState;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.EntityBuilder;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.helper.CommitmentHelper;

public class EventCommitmentTest
{
    private static final String TEST_MAIL_ADDRESS = "testhelper1.trispeedys@gmail.com";
    
    private static final String TEST_MAIL_PASSWORD = "trispeedys1234";

	/**
     * Denselben Helfer in der selben Veranstaltung auf zwei Positionen bestätigen
     * 
     * @throws ResourcePlanningException
     */
    @Test(expected = ResourcePlanningException.class)
    public void testDuplicateCommitment() throws ResourcePlanningException
    {
        HibernateUtil.clearAll();
        
        EventOccurence eventOccurence = EntityBuilder.buildEventOccurence("DM AK 2015", "DM-AK-2015", 21, 6, 2015).persist();
        
        Position position1 = EntityBuilder.buildPosition("Radverpflegung", 12).persist();
        Position position2 = EntityBuilder.buildPosition("Laufverpflegung", 16).persist();
        
        Helper helper = EntityBuilder.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 13, 2, 1976).persist();
        
        EntityBuilder.buildEventCommitment(helper, eventOccurence, position2).persist();
        
        //confirm helper for another position of the same event
        CommitmentHelper.confirmHelper(helper, eventOccurence, position1);
    }
    
    /**
     * Einen Helfer für eine Position bestätigen, für die er zu jung ist
     * @throws ResourcePlanningException 
     */
    @Test(expected = ResourcePlanningException.class)
    public void testCommitmentUnderAge() throws ResourcePlanningException
    {
        HibernateUtil.clearAll();
        
        EventOccurence eventOccurence = EntityBuilder.buildEventOccurence("DM AK 2015", "DM-AK-2015", 21, 6, 2016).persist();
        
        //Helfer ist zum Datum der Veranstaltung erst 15 
        Helper helper = EntityBuilder.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000).persist();
        
        //Position erfordert Mindest-Alter 16 Jahre
        Position position = EntityBuilder.buildPosition("Laufverpflegung", 16).persist();
        
        //Muss zu Ausnahme führen
        CommitmentHelper.confirmHelper(helper, eventOccurence, position);
    }
}