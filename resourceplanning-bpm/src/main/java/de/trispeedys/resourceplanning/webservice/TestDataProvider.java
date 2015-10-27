package de.trispeedys.resourceplanning.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.camunda.bpm.BpmPlatform;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.EventDatasource;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.DomainRepository;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.PositionInclude;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

@SuppressWarnings("restriction")
@WebService
@SOAPBinding(style = Style.RPC)
public class TestDataProvider
{
    public void startHelperRequestProcess(Long helperId, Long eventId)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helperId));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(eventId));
        BpmPlatform.getDefaultProcessEngine()
                .getRuntimeService()
                .startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG,
                        ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId), variables);
    }

    public void startSomeProcesses()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015",
                        "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);
        List<Helper> helpers =
                Datasources.getDatasource(Helper.class).find(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        for (Helper helper : helpers)
        {
            startHelperRequestProcess(helper.getId(), event2016.getId());
        }
    }

    /**
     * starts a process and blocks a position in order to test {@link HelperCallback#CHANGE_POS} with an already blocked
     * position chosen.
     */
    public void prepareBlockedChoosePosition()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015",
                        "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // block one of the positions with a new helper
        Helper blockingHelper =
                EntityFactory.buildHelper("New1", "New1", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
        AssignmentService.assignHelper(blockingHelper, event2016,
                (Position) Datasources.getDatasource(Position.class).findAll().get(0));

        // start process for the created helper 'H2_Last'
        startHelperRequestProcess(
                ((Helper) Datasources.getDatasource(Helper.class)
                        .find(Helper.ATTR_LAST_NAME, "H2_Last")
                        .get(0)).getId(), event2016.getId());
    }

    /**
     * Duplicates the smimple event and creates two new helpers. Idea:
     * 
     * + Let one of the 'old helpers' choose option {@link HelperCallback#CHANGE_POS} (receives). + Inbetween, block two
     * of the positions with the new helpers. + Old helper can either choose a free or occupied positions (which causes
     * an assignment or a second proposal mail).
     */
    public void prepareSimpleEventWithFloatingHelpers()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015",
                        "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        startHelperRequestProcess(
                ((Helper) Datasources.getDatasource(Helper.class).findAll().get(0)).getId(),
                event2016.getId());

        EntityFactory.buildHelper("New1", "New1", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
        EntityFactory.buildHelper("New2", "New2", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
    }

    /**
     * like the 'status quo' - example {@link ResourceInfo#startSomeProcesses()}, but with 2 new helpers which block 2
     * positions, so 2 of 5 {@link HelperCallback#ASSIGNMENT_AS_BEFORE} will not work (and alternative positions will be
     * proposed).
     */
    public void startSomeProcessesWithNewHelpers()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015",
                        "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);
        List<Helper> helpers =
                Datasources.getDatasource(Helper.class).find(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        List<Position> positions = Datasources.getDatasource(Position.class).findAll();
        // new helper 1 with assignment
        Helper newHelper1 =
                EntityFactory.buildHelper("New1", "New1", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
        AssignmentService.assignHelper(newHelper1, event2016, positions.get(1));
        // new helper 2 with assignment
        Helper newHelper2 =
                EntityFactory.buildHelper("New2", "New2", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
        AssignmentService.assignHelper(newHelper2, event2016, positions.get(3));
        for (Helper helper : helpers)
        {
            startHelperRequestProcess(helper.getId(), event2016.getId());
        }
    }

    public void startOneProcesses()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015",
                        "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);
        List<Helper> helpers =
                Datasources.getDatasource(Helper.class).find(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        startHelperRequestProcess(helpers.get(0).getId(), event2016.getId());
    }

    public void prepareRealLife()
    {
        // clear db
        HibernateUtil.clearAll();

        // there is a new event (with 7 active helpers)...
        SpeedyRoutines.duplicateEvent(TestDataGenerator.createRealLifeEvent("Triathlon 2016", "TRI-2016", 21,
                6, 2016, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016", "TRI-2016", 21,
                6, 2016, null, null);
    }

    // --- Real life test

    public void finish2015()
    {
        HibernateUtil.clearAll();
        TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2016, EventState.FINISHED,
                EventTemplate.TEMPLATE_TRI);
    }
    
    public void duplicate2015()
    {
        // create new position (no. 7777) for domain [D2]
        Domain dom2 = RepositoryProvider.getRepository(DomainRepository.class).findDomainByNumber(2);
        EntityFactory.buildPosition("7777", 12, dom2, false, 7777).persist();
        
        // create new position (no. 8888) for domain [D92]
        Domain dom92 = RepositoryProvider.getRepository(DomainRepository.class).findDomainByNumber(92);
        EntityFactory.buildPosition("8888", 12, dom92, false, 8888).persist();
        
        List<Integer> excludes = new ArrayList<Integer>();
        excludes.add(137);
        excludes.add(232);
        excludes.add(39);
        excludes.add(93);
        
        List<PositionInclude> includes = new ArrayList<PositionInclude>();
        includes.add(new PositionInclude(dom2, 7777));
        includes.add(new PositionInclude(dom92, 8888));
                
        // real life event for 2015
        Event event2015 = RepositoryProvider.getRepository(EventRepository.class).findEventByEventKey("TRI-2015");
        SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, excludes, includes);        
    }

    public void debugEvent(Long eventId)
    {
        DefaultDatasource<Event> datasource = Datasources.getDatasource(Event.class);
        SpeedyRoutines.debugEvent((Event) datasource.findById(eventId));
    }

    // --- main

    public static void main(String[] args)
    {
        new TestDataProvider().debugEvent(6291l);
    }
}