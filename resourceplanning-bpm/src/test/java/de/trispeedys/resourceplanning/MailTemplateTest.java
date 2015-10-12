package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.messaging.ProposePositionsMailTemplate;
import de.trispeedys.resourceplanning.service.MessagingService;

public class MailTemplateTest
{
    @SuppressWarnings("unchecked")
    @Test
    public void testProposePositions()
    {
        //clear db
        HibernateUtil.clearAll();
        //create domains
        Domain domain1 = EntityFactory.buildDomain("dom1", 1).persist();
        Domain domain2 = EntityFactory.buildDomain("dom2", 2).persist();
        // create positions
        EntityFactory.buildPosition("Pos1", 12, domain1, false).persist();
        EntityFactory.buildPosition("Pos2", 12, domain1, false).persist();
        EntityFactory.buildPosition("Pos3", 12, domain2, false).persist();
        EntityFactory.buildPosition("Pos4", 12, domain2, false).persist();
        EntityFactory.buildPosition("Pos5", 12, domain2, false).persist();
        // send mail
        ProposePositionsMailTemplate template =
                new ProposePositionsMailTemplate(DatasourceRegistry.getDatasource(Position.class).findAll(
                        Position.class));
        MessagingService.createMessage("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "moo",
                template.getBody(), MessagingType.NONE, MessagingFormat.HTML);
        MessagingService.sendAllUnprocessedMessages();
    }
}