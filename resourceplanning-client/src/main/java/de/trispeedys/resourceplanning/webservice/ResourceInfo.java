
package de.trispeedys.resourceplanning.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ResourceInfo", targetNamespace = "http://webservice.resourceplanning.trispeedys.de/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ResourceInfo {


    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void assignHelper(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        long arg1,
        @WebParam(name = "arg2", partName = "arg2")
        long arg2);

    /**
     * 
     */
    @WebMethod
    public void finishUp();

    /**
     * 
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.EventDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public EventDTOArray queryEvents();

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.HierarchicalEventItemDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public HierarchicalEventItemDTOArray getEventNodes(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        boolean arg1);

    /**
     * 
     * @param arg5
     * @param arg4
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void duplicateEvent(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1,
        @WebParam(name = "arg2", partName = "arg2")
        String arg2,
        @WebParam(name = "arg3", partName = "arg3")
        int arg3,
        @WebParam(name = "arg4", partName = "arg4")
        int arg4,
        @WebParam(name = "arg5", partName = "arg5")
        int arg5);

    /**
     * 
     * @param arg1
     * @param arg0
     */
    @WebMethod
    public void processHelperCallback(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns de.trispeedys.resourceplanning.webservice.HelperAssignmentDTOArray
     */
    @WebMethod
    @WebResult(partName = "return")
    public HelperAssignmentDTOArray queryHelperAssignments(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1);

    /**
     * 
     */
    @WebMethod
    public void sendAllMessages();

    /**
     * 
     * @param arg0
     */
    @WebMethod
    public void startProcessesForActiveHelpersByEventId(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0);

    /**
     * 
     * @param arg0
     */
    @WebMethod
    public void startProcessesForActiveHelpersByTemplateName(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

}
