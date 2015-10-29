package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class LoggerTest
{
    @Test
    public void testLogger()
    {
        HibernateUtil.clearAll();
        
        EntityFactory.buildLog("bk", "123-456-789").saveOrUpdate();
        
        EntityFactory.buildLog("bk", "123-456-789", DbLogLevel.ERROR).saveOrUpdate();
    }
}