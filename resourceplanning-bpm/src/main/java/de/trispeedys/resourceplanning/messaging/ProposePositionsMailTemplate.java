package de.trispeedys.resourceplanning.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;

public class ProposePositionsMailTemplate extends AbstractMailTemplate
{
    private List<Position> positions;
    
    private Helper helper;
    
    private Event event;

    public ProposePositionsMailTemplate(Helper aHelper, Event aEvent, List<Position> aPositions)
    {
        super();
        this.helper = aHelper;
        this.event = aEvent;
        this.positions = aPositions;
    }

    public String getBody()
    {
        // group positions by domain (name)
        HashMap<String, List<Position>> grouping = new HashMap<String, List<Position>>();
        String domainName = null;
        for (Position pos : positions)
        {
            domainName = pos.getDomain().getName();
            if (grouping.get(domainName) == null)
            {
                grouping.put(domainName, new ArrayList<Position>());
            }
            grouping.get(domainName).add(pos);
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("Hallo, " + helper.getFirstName() + "!!");
        buffer.append("<br><br>");
        buffer.append("Bitte sag uns, welche Position du beim "+event.getDescription()+" besetzen m�chtest:");
        buffer.append("<br><br>");
        String entry = null;
        for (String key : grouping.keySet())
        {
            buffer.append("<li>"+key+"</li>");
            for (Position pos : grouping.get(key))
            {
                entry = "http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/ChosenPositionReceiver.jsp?chosenPosition=" +
                        pos.getId() + "&helperId=" + helper.getId() + "&eventId=" + event.getId();
                buffer.append("<ul><a href=\""+entry+"\">"+pos.getDescription()+"</a></ul>");
            }
        }
        buffer.append("<br><br>");
        buffer.append("Deine Speedys");
        return buffer.toString();
    }
}