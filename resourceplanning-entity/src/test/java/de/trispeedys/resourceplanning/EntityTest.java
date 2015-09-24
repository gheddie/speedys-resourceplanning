package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.builder.EntityBuilder;

public class EntityTest
{
    @Test
    public void testCreateSome()
    {
        EntityBuilder.buildMessageQueue().persist();
    }
}