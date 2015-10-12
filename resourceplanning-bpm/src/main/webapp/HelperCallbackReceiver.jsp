<%@page import="de.trispeedys.resourceplanning.util.exception.ResourcePlanningException"%>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Rueckmeldung</title>
</head>
<%@ page import="java.util.Date"%>
<%@ page import="de.trispeedys.resourceplanning.entity.misc.HelperCallback"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="de.trispeedys.resourceplanning.interaction.HelperInteraction"%>
<body>
	<h3>Hi Stefan</h3>
	<br>
	<strong>Current Time is</strong>:
	<%=new Date()%>
	<%
    	Long helperId = Long.parseLong(request.getParameter("helperId"));
    	Long eventId = Long.parseLong(request.getParameter("eventId"));
    	HelperCallback callback = HelperCallback.valueOf(request.getParameter("callbackResult"));
    	try
    	{
    	    HelperInteraction.processCallback(callback, eventId, helperId);
    	}
		catch(ResourcePlanningException e)
    	{
		    out.println("Fehler : " + e.getMessage());
    	}
	%>
</body>
</html>