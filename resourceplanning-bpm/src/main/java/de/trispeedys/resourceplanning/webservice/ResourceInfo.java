package de.trispeedys.resourceplanning.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import de.trispeedys.resourceplanning.dto.EventCommitmentDTO;

@SuppressWarnings("restriction")
@WebService
@SOAPBinding(style = Style.RPC)
public class ResourceInfo
{
    public EventCommitmentDTO[] queryCommitments(String firstName, String lastName)
    {
        List<EventCommitmentDTO> commitmentList = new ArrayList<EventCommitmentDTO>();
        return commitmentList.toArray(new EventCommitmentDTO[commitmentList.size()]);
    }
}