package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.RepositoryProvider;

public class RepositoryProviderTest
{
    @Test
    public void testBasics()
    {
        HibernateUtil.clearAll();
        
        // build domain
        Domain domain = EntityFactory.buildDomain("D1", 1).persist();
        
        // build position
        Position pos = EntityFactory.buildPosition("P1", 12, domain, false, 0).persist();
        
        System.out.println(RepositoryProvider.getRepository(PositionRepository.class).findPositionByPositionNumber());
        
        System.out.println(RepositoryProvider.getRepository(PositionRepository.class).findPositionByPositionNumber());
    }
}