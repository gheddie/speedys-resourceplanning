package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.DomainRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.RepositoryProvider;
import static org.junit.Assert.assertTrue;

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
        
        assertTrue(RepositoryProvider.getRepository(PositionRepository.class).findPositionByPositionNumber() != null);
    }
    
    @Test
    public void testFindAll()
    {
        HibernateUtil.clearAll();
        
        // build domains
        EntityFactory.buildDomain("D1", 1).persist();
        EntityFactory.buildDomain("D2", 2).persist();
        EntityFactory.buildDomain("D3", 3).persist();
        
        System.out.println(RepositoryProvider.getRepository(DomainRepository.class).findAll());
    }
}