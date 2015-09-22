package de.trispeedys.resourceplanning;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

public class RequestHelpTest
{
    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();
    
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testParsingAndDeployment()
    {

    }
    
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testReceiveReminderMail()
    {

    }
}