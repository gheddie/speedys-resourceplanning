package de.trispeedys.resourceplanning.webservice;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.camunda.bpm.BpmPlatform;

@SuppressWarnings("restriction")
@WebService
@SOAPBinding(style = Style.RPC)
public class SomeTestService
{
    public void testStartProcess()
    {
        System.out.println("starting process...");
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().startProcessInstanceByKey("SpeedysTestProcess");
    }
}