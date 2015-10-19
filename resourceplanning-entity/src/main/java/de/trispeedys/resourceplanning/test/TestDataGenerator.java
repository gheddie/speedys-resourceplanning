package de.trispeedys.resourceplanning.test;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class TestDataGenerator
{
    private static final String MAIL_ADDRESS = "testhelper1.trispeedys@gmail.com";

    /**
     * creates an {@link Event} like {@link TestDataGenerator#createMinimalEvent(String, String, int, int, int)}, but
     * without assignments of positions to helpers.
     * 
     * @return
     */
    public static Event createSimpleUnassignedEvent(String description, String eventKey, int day, int month,
            int year)
    {
        EventTemplate template = EntityFactory.buildEventTemplate("123").persist();

        // build event
        Event myLittleEvent =
                EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED,
                        template).persist();

        // create helpers
        EntityFactory.buildHelper("H1_First", "H1_Last", MAIL_ADDRESS, HelperState.ACTIVE, 1, 1, 1980)
                .persist();
        EntityFactory.buildHelper("H3_First", "H3_Last", MAIL_ADDRESS, HelperState.ACTIVE, 3, 1, 1980)
                .persist();

        // build domains
        Domain domain1 = EntityFactory.buildDomain("D1", 1).persist();
        Domain domain2 = EntityFactory.buildDomain("D2", 1).persist();
        // build positions
        Position pos1 = EntityFactory.buildPosition("P1", 12, domain1, false).persist();
        Position pos2 = EntityFactory.buildPosition("P2", 12, domain1, false).persist();
        Position pos3 = EntityFactory.buildPosition("P3", 12, domain2, false).persist();
        Position pos4 = EntityFactory.buildPosition("P4", 12, domain2, false).persist();
        Position pos5 = EntityFactory.buildPosition("P5", 12, domain2, false).persist();
        // assign positions to event
        SpeedyRoutines.relatePositionsToEvent(myLittleEvent, pos1, pos2, pos3, pos4, pos5);

        return myLittleEvent;
    }

    /**
     * creates a litte test event ('My little event') with 2 domains D1 (positios: P1, P2) and D2 (positios: P3, P4,
     * P5). Every position is assigned to helper with a similar name (H1<->P1 etc. pp.). Every domain is lead by by the
     * first helper (H1 leads D1, H3 leads D2).
     * 
     * @param k
     * @param j
     * @param i
     * @param string2
     * @param string
     * @return
     */
    public static Event createSimpleEvent(String description, String eventKey, int day, int month, int year)
    {
        EventTemplate template = EntityFactory.buildEventTemplate("123").persist();

        // build event
        Event myLittleEvent =
                EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED,
                        template).persist();
        // create helpers
        Helper helper1 =
                EntityFactory.buildHelper("H1_First", "H1_Last", "a1@b.de", HelperState.ACTIVE, 1, 2, 1980)
                        .persist();
        Helper helper2 =
                EntityFactory.buildHelper("H2_First", "H2_Last", "a2@b.de", HelperState.ACTIVE, 2, 2, 1980)
                        .persist();
        Helper helper3 =
                EntityFactory.buildHelper("H3_First", "H3_Last", "a3@b.de", HelperState.ACTIVE, 3, 2, 1980)
                        .persist();
        Helper helper4 =
                EntityFactory.buildHelper("H4_First", "H4_Last", "a4@b.de", HelperState.ACTIVE, 4, 2, 1980)
                        .persist();
        Helper helper5 =
                EntityFactory.buildHelper("H5_First", "H5_Last", "a5@b.de", HelperState.ACTIVE, 5, 2, 1980)
                        .persist();
        // build domains
        Domain domain1 = EntityFactory.buildDomain("D1", 1).persist();
        Domain domain2 = EntityFactory.buildDomain("D2", 1).persist();
        // build positions
        Position pos1 = EntityFactory.buildPosition("P1", 12, domain1, true).persist();
        Position pos2 = EntityFactory.buildPosition("P2", 12, domain1, true).persist();
        Position pos3 = EntityFactory.buildPosition("P3", 12, domain2, true).persist();
        Position pos4 = EntityFactory.buildPosition("P4", 12, domain2, true).persist();
        Position pos5 = EntityFactory.buildPosition("P5", 12, domain2, true).persist();
        // assign positions to event
        SpeedyRoutines.relatePositionsToEvent(myLittleEvent, pos1, pos2, pos3, pos4, pos5);
        // assign helpers to positions
        SpeedyRoutines.assignHelperToPositions(helper1, myLittleEvent, pos1);
        SpeedyRoutines.assignHelperToPositions(helper2, myLittleEvent, pos2);
        SpeedyRoutines.assignHelperToPositions(helper3, myLittleEvent, pos3);
        SpeedyRoutines.assignHelperToPositions(helper4, myLittleEvent, pos4);
        SpeedyRoutines.assignHelperToPositions(helper5, myLittleEvent, pos5);

        return myLittleEvent;
    }

    /**
     * creates the minimal event : one domain, one position and one helper assigned to it.
     * 
     * @param description
     * @param eventKey
     * @param day
     * @param month
     * @param year
     * @return
     */
    public static Event createMinimalEvent(String description, String eventKey, int day, int month, int year)
    {
        // build event
        Event myMinimalEvent =
                EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED, null)
                        .persist();
        // create helper
        Helper helper =
                EntityFactory.buildHelper("H1_First", "H1_Last", MAIL_ADDRESS, HelperState.ACTIVE, 1, 1, 1980)
                        .persist();
        // build domain
        Domain domain = EntityFactory.buildDomain("D1", 1).persist();
        // build position
        Position pos = EntityFactory.buildPosition("P1", 12, domain, false).persist();
        // assign position to event
        SpeedyRoutines.relatePositionsToEvent(myMinimalEvent, pos);
        // assign helper to position
        SpeedyRoutines.assignHelperToPositions(helper, myMinimalEvent, pos);
        return myMinimalEvent;
    }

    public static Event createRealLifeEvent(String description, String eventKey, int day, int month, int year)
    {
        // build event template
        EventTemplate template = EntityFactory.buildEventTemplate("123").persist();

        // build event
        Event event =
                EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED,
                        template).persist();

        // ------------------------ create helpers ('old')
        EntityFactory.buildHelper("Schulz", "Stefan", "a@b.de", HelperState.ACTIVE, 13, 2, 1976).persist();
        EntityFactory.buildHelper("Beyer", "Lars", "a@b.de", HelperState.ACTIVE, 4, 4, 1971).persist();
        EntityFactory.buildHelper("Elsner", "Conny", "a@b.de", HelperState.ACTIVE, 25, 7, 1973).persist();
        EntityFactory.buildHelper("Deyhle", "Ingo", "a@b.de", HelperState.ACTIVE, 1, 8, 1968).persist();
        EntityFactory.buildHelper("Meitzner", "Daniela", "a@b.de", HelperState.ACTIVE, 16, 12, 1961)
                .persist();
        EntityFactory.buildHelper("Grabbe", "Jimi", "a@b.de", HelperState.ACTIVE, 7, 5, 1991).persist();
        EntityFactory.buildHelper("Päge", "Denny", "a@b.de", HelperState.ACTIVE, 29, 5, 1964).persist();
        EntityFactory.buildHelper("Thierse", "Ulrich", "a@b.de", HelperState.ACTIVE, 16, 5, 1983).persist();

        // ------------------------ create helpers ('new')
        EntityFactory.buildHelper("Klemm", "Peter", "a@b.de", HelperState.ACTIVE, 17, 7, 1983).persist();
        EntityFactory.buildHelper("Walther", "Tina", "a@b.de", HelperState.ACTIVE, 28, 4, 1967).persist();
        EntityFactory.buildHelper("Klopp", "Willi", "a@b.de", HelperState.ACTIVE, 13, 11, 1964).persist();

        // ------------------------ create event templates
        EntityFactory.buildEventTemplate("TriathlonTemplate").persist();
        EntityFactory.buildEventTemplate("CrosszalesTemplate").persist();

        // ------------------------ create triathlon 2015

        // Domain 'Laufstrecke'
        Domain domLauf = EntityFactory.buildDomain("Laufstrecke", 1).persist();
        Position posAnsageZieleinlauf = EntityFactory.buildPosition("Ansage Zieleinlauf", 12, domLauf, false).persist();
        Position posVerpflegungPark = EntityFactory.buildPosition("Verpflegung Park", 12, domLauf, false).persist();
        SpeedyRoutines.relatePositionsToEvent(event, posAnsageZieleinlauf, posVerpflegungPark);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("SCST13021976"), event, posAnsageZieleinlauf);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("BELA04041971"), event, posVerpflegungPark);

        // Domain 'Radstrecke'
        Domain domRad = EntityFactory.buildDomain("Radstrecke", 1).persist();
        Position posKontrolleAbstieg = EntityFactory.buildPosition("Kontrolle Abstieg", 12, domRad, false).persist();
        Position posEinweisungNachStartnummerWZ = EntityFactory.buildPosition("Einweisung nach Startnummer WZ", 12, domRad, false).persist();
        Position posSicherungAbzweigRunde = EntityFactory.buildPosition("Sicherung Abzweig Runde 2/Zieleinfahrt", 12, domRad, false).persist();
        Position posMotorrad1 = EntityFactory.buildPosition("Motorrad 1", 12, domRad, false).persist();
        SpeedyRoutines.relatePositionsToEvent(event, posKontrolleAbstieg, posEinweisungNachStartnummerWZ, posSicherungAbzweigRunde, posMotorrad1);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("PADE29051964"), event, posKontrolleAbstieg);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("ELCO25071973"), event, posEinweisungNachStartnummerWZ);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("DEIN01081968"), event, posSicherungAbzweigRunde);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("THUL16051983"), event, posMotorrad1);

        // Domain 'Zielverpflegung'
        Domain domZiel = EntityFactory.buildDomain("Zielverpflegung", 1).persist();
        Position posAusgabeGetraenke = EntityFactory.buildPosition("Ausgabe Getränke", 12, domZiel, false).persist();
        Position posObstschneiden = EntityFactory.buildPosition("Obstschneiden", 12, domZiel, false).persist();
        SpeedyRoutines.relatePositionsToEvent(event, posAusgabeGetraenke, posObstschneiden);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("MEDA16121961"), event, posAusgabeGetraenke);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("PADE29051964"), event, posObstschneiden);

        // Domain 'Siegerehrung'
        Domain domSieger = EntityFactory.buildDomain("Siegerehrung", 1).persist();
        Position posModeration = EntityFactory.buildPosition("Moderation", 12, domSieger, false).persist();
        Position posAnreichenUrkunden = EntityFactory.buildPosition("Anreichen Urkunden", 12, domSieger, false).persist();
        SpeedyRoutines.relatePositionsToEvent(event, posModeration, posAnreichenUrkunden);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("THUL16051983"), event, posModeration);
        SpeedyRoutines.assignHelperToPositions(findHelperByCode("SCST13021976"), event, posAnreichenUrkunden);

        return event;
    }

    // ---

    private static Helper findHelperByCode(String helperCode)
    {
        return DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_CODE, helperCode).get(0);
    }

    public static void main(String[] args)
    {
        HibernateUtil.clearAll();
        // ---
// Long event1 = createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId();
// Long event2 = DatabaseRoutines.duplicateEvent(event1, "Triathlon 2016", "TRI-2016", 21, 6, 2016).getId();
// System.out.println(DebugEvent.debugEvent(event1));
// System.out.println(DebugEvent.debugEvent(event2));
        // ---
// Long eventId = createMinimalEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId();
// System.out.println(DebugEvent.debugEvent(eventId));
        // ---
        Long evtId = createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId();
        SpeedyRoutines.duplicateEvent(evtId, "Triathlon 2016", "TRI-2016", 21, 6, 2016);
    }
}