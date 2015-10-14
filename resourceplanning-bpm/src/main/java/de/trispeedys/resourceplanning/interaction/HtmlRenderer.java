package de.trispeedys.resourceplanning.interaction;

import javax.servlet.http.HttpServletRequest;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Helper;

public class HtmlRenderer
{
    @SuppressWarnings("unchecked")
    public static String renderCorrelationSuccess(HttpServletRequest request)
    {
        Helper helper = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, Long.parseLong(request.getParameter("helperId")));
        StringBuffer buffer = new StringBuffer();
        buffer.append("<h1>Hallo "+helper.getFirstName()+",</h1>");
        buffer.append("<br><br>");
        buffer.append("<img src=\"img/OK.png\" width=\"64\" height=\"64\" />");
        buffer.append("<br><br>");
        buffer.append("Nachricht erhalten.");
        return buffer.toString();
    }
    
    @SuppressWarnings("unchecked")
    public static String renderCorrelationFault(HttpServletRequest request)
    {
        Helper helper = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, Long.parseLong(request.getParameter("helperId")));
        StringBuffer buffer = new StringBuffer();
        buffer.append("<h1>Hallo "+helper.getFirstName()+",</h1>");
        buffer.append("<br><br>");
        buffer.append("<img src=\"img/faulty.png\" width=\"64\" height=\"64\" />");
        buffer.append("<br><br>");
        buffer.append("Nachricht wurde bereits zugestellt.");
        return buffer.toString();
    }
}