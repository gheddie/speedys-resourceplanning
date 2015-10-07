<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>First JSP</title>
</head>
<%@ page import="java.util.Date"%>
<%@ page import="org.camunda.bpm.BpmPlatform"%>
<%@ page
	import="de.trispeedys.resourceplanning.entity.misc.HelperCallback"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="de.trispeedys.resourceplanning.messages.BpmMessages"%>
<%@ page import="de.trispeedys.resourceplanning.variables.BpmVariables"%>
<%@ page
	import="de.trispeedys.resourceplanning.util.ResourcePlanningUtil"%>
<body>
	<h3>Hi Stefan</h3>
	<br>
	<strong>Current Time is</strong>:
	<%=new Date()%>
	<%
        System.out.println("the helper chosen a position...");
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION,
                Long.parseLong(request.getParameter("chosenPosition")));
        Long helperId = Long.parseLong(request.getParameter("helperId"));
        Long eventId = Long.parseLong(request.getParameter("eventId"));
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        BpmPlatform.getDefaultProcessEngine()
                .getRuntimeService()
                .correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey, variables);
	%>
</body>
</html>