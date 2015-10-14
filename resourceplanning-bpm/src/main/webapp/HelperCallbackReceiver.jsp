<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Rueckmeldung</title>
</head>
<%@ page import="de.trispeedys.resourceplanning.entity.misc.HelperCallback"%>
<%@page import="org.camunda.bpm.engine.MismatchingMessageCorrelationException"%>
<%@ page import="de.trispeedys.resourceplanning.interaction.HelperInteraction"%>
<%@page import="de.trispeedys.resourceplanning.interaction.HtmlRenderer"%>
<body>
	<%
    	try
    	{
    	    HelperInteraction.processReminderCallback(request);
	        //the message could be correlated
	        out.println(HtmlRenderer.renderCorrelationSuccess(request));
    	}
		catch(MismatchingMessageCorrelationException e)
    	{
	        //the message could not be correlated
	        out.println(HtmlRenderer.renderCorrelationFault(request));
    	}
	%>
</body>
</html>