package de.trispeedys.resourceplanning.util;

import java.text.MessageFormat;

import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class ResourcePlanningUtil
{
    private static final String BK_REQUEST_HELP_HELPERPROCESS_TEMPLATE = "bkRequestHelpHelperProcess_helper:{0}||event:{1}";

    public static String generateRequestHelpBusinessKey(Long helperId, Long eventId)
    {
        if ((helperId == null) || (eventId == null))
        {
            throw new ResourcePlanningException("helper id AND event id must be set in order to generate a business key!!");
        }
        return new MessageFormat(BK_REQUEST_HELP_HELPERPROCESS_TEMPLATE).format(new Object[] {helperId, eventId});
    }
}