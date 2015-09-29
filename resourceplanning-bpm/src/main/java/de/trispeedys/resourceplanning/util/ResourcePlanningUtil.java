package de.trispeedys.resourceplanning.util;

import java.text.MessageFormat;

public class ResourcePlanningUtil
{
    private static final String BK_REQUEST_HELP_HELPERPROCESS_TEMPLATE = "bkRequestHelpHelperProcess_helper:{0}||event:{1}";

    public static String generateRequestHelpBusinessKey(Long helperId, Long eventId)
    {
        return new MessageFormat(BK_REQUEST_HELP_HELPERPROCESS_TEMPLATE).format(new Object[] {helperId, eventId});
    }
}