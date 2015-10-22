package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.RepositoryProvider;

public class RepositoryProviderTest
{
    @Test
    public void testBasics()
    {
        RepositoryProvider.getRepository(PositionRepository.class).findPositionByPositionNumber();
    }
}