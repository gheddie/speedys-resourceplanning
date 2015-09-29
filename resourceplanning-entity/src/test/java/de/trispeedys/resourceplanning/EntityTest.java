package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class EntityTest
{
    @Test
    public void testCreateSome()
    {
        EntityFactory.buildMessageQueue().persist();
    }
}