
package de.trispeedys.resourceplanning.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "SomeTestService", targetNamespace = "http://webservice.resourceplanning.trispeedys.de/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SomeTestService {


    /**
     * 
     */
    @WebMethod
    public void testStartProcess();

}
