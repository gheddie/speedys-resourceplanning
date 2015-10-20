package de.trispeedys.resourceplanning.interaction;

import javax.servlet.http.HttpServletRequest;

import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;

public class HtmlRenderer
{
    public static String renderCorrelationSuccess(HttpServletRequest request)
    {
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(Long.parseLong(request.getParameter("helperId")));
        StringBuffer buffer = new StringBuffer();
        buffer.append("<h1>Hallo "+helper.getFirstName()+",</h1>");
        buffer.append("<br><br>");
        buffer.append("<img src=\"img/OK.png\" width=\"64\" height=\"64\" />");
        buffer.append("<br><br>");
        buffer.append("Nachricht erhalten.");
        return buffer.toString();
    }
    
    public static String renderCorrelationFault(HttpServletRequest request)
    {
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(Long.parseLong(request.getParameter("helperId")));
        StringBuffer buffer = new StringBuffer();
        buffer.append("<h1>Hallo "+helper.getFirstName()+",</h1>");
        buffer.append("<br><br>");
        buffer.append("<img src=\"img/faulty.png\" width=\"64\" height=\"64\" />");
        buffer.append("<br><br>");
        buffer.append("Nachricht wurde bereits zugestellt.");
        return buffer.toString();
    }
    
    public static String renderChosenPosAvailable(HttpServletRequest request, boolean positionAvailable)
    {
        Position chosenPosition = Datasources.getDatasource(Position.class).findById(Long.parseLong(request.getParameter("chosenPosition")));
        if (positionAvailable)
        {
            return "<br><br>Deine gewünschte Position ("+chosenPosition.getDescription()+") ist verfuegbar und wird dir zugewiesen.<br><br>";
        }
        else
        {
            return "<br><br>Deine gewünschte Position ("+chosenPosition.getDescription()+") ist leider nicht mehr verfuegbar. Du erhältst eine weitere Mail mit Vorschlaegen.<br><br>";
        }
    }
}