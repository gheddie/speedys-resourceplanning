package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class LifeTest
{
    @Test
    public void import2015()
    {
        HibernateUtil.clearAll();

        // Startunterlagen Samstag
        // ---------------------------------------------------------------
        Domain domain1 = EntityFactory.buildDomain("", 1, (Helper) EntityFactory.buildHelper("123", "123", "", HelperState.ACTIVE, 1, 1, 1980).persist()).persist();
        // ---------------------------------------------------------------
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 01", 16, domain1).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 02", 16, domain1).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 03", 16, domain1).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 04", 16, domain1).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 05", 16, domain1).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 06", 16, domain1).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 07", 16, domain1).persist();
        
        // Startunterlagen Sonntag
        // ---------------------------------------------------------------
        Domain domain2 = EntityFactory.buildDomain("", 1, (Helper) EntityFactory.buildHelper("123", "123", "", HelperState.ACTIVE, 1, 1, 1980).persist()).persist();
        // ---------------------------------------------------------------
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 01", 16, domain2).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 02", 16, domain2).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 03", 16, domain2).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 04", 16, domain2).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 05", 16, domain2).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 06", 16, domain2).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 07", 16, domain2).persist();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 08", 16, domain2).persist();
        EntityFactory.buildPosition("Hand- bzw. Wadenbeschriftung 09", 16, domain2).persist();
        EntityFactory.buildPosition("Hand- bzw. Wadenbeschriftung 10", 16, domain2).persist();
        EntityFactory.buildPosition("Hand- bzw. Wadenbeschriftung 11", 16, domain2).persist();
        EntityFactory.buildPosition("Hand- bzw. Wadenbeschriftung 12", 16, domain2).persist();
        
        System.out.println(HibernateUtil.fetchResults(Position.class).size() + " positions created.");
    }
}