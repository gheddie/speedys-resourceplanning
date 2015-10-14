package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.messaging.ProposePositionsMailTemplate;
import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.service.MessagingService;

public class MailTemplateTest
{
    @SuppressWarnings("unchecked")
    @Test
    public void testProposePositions()
    {
        // clear db
        HibernateUtil.clearAll();
        // create domains
        Domain domain1 = EntityFactory.buildDomain("dom1", 1).persist();
        Domain domain2 = EntityFactory.buildDomain("dom2", 2).persist();
        // create positions
        EntityFactory.buildPosition("Pos1", 12, domain1, false).persist();
        EntityFactory.buildPosition("Pos2", 12, domain1, false).persist();
        Position pos3 = EntityFactory.buildPosition("Pos3", 12, domain2, false).persist();
        EntityFactory.buildPosition("Pos4", 12, domain2, false).persist();
        EntityFactory.buildPosition("Pos5", 12, domain2, false).persist();
        // create helper and event
        Helper helper =
                EntityFactory.buildHelper("H1_First", "H1_Last", "testhelper1.trispeedys@gmail.com ",
                        HelperState.ACTIVE, 1, 1, 1980).persist();
        Event event = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016).persist();
        // send mail
        ProposePositionsMailTemplate template =
                new ProposePositionsMailTemplate(helper, event, DatasourceRegistry.getDatasource(Position.class)
                        .findAll(Position.class), HelperCallback.ASSIGNMENT_AS_BEFORE, pos3);
        MessagingService.createMessage("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com",
                template.getSubject(), template.getBody(), MessagingType.NONE, MessagingFormat.HTML);
        MessagingService.sendAllUnprocessedMessages();
    }
}