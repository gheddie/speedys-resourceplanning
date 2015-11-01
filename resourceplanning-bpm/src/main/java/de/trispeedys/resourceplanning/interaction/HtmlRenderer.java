package de.trispeedys.resourceplanning.interaction;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class HtmlRenderer
{
    public static String renderCorrelationSuccess(Long helperId)
    {
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(helperId);
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak()
                .withParagraph("Danke, wir haben deine erhalten.")
                .withLinebreak()
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    /**
     * RETURN_MESSAGE_UNPROCESSABLE
     * 
     * @param helperId
     * @return
     */
    public static String renderCorrelationFault(Long helperId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak()
                .withParagraph("Das war ein Fehler. Diese Nachricht haben wir bereits von Dir erhalten.")
                .withLinebreak()
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    /**
     * called from chosen position receiver jsp. Informs the user that his message has been received and gives an
     * warning, that he will receive another mail if the chosen position is not available.
     * 
     * HelperInteraction.RETURN_POS_CHOSEN_POS_TAKEN
     * 
     * @param chosenPositionId
     * @return
     */
    public static String renderChosenPositionUnavailableCallback(Long helperId, Long chosenPositionId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Position chosenPosition = RepositoryProvider.getRepository(PositionRepository.class).findById(chosenPositionId);
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak(2)
                .withParagraph("Deine Nachricht ist angekommen.")
                .withLinebreak()
                .withParagraph(
                        "Leider ist die von dir gew�hlte Position (" +
                                chosenPosition.getDescription() + ") bereits besetzt. " +
                                "Du wirst in K�rze eine Mail mit Alternativvorschl�gen erhalten.")
                .withLinebreak(2)
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    /**
     * HelperInteraction.RETURN_POS_CHOSEN_NOMINAL
     * 
     * @param helperId
     * @param chosenPositionId
     * @return
     */
    public static String renderChosenPositionAvailableCallback(Long helperId, Long chosenPositionId)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Position chosenPosition = RepositoryProvider.getRepository(PositionRepository.class).findById(chosenPositionId);
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak(2)
                .withParagraph("Deine Nachricht ist angekommen.")
                .withLinebreak()
                .withParagraph(
                        "Die von dir gew�hlte Position (" +
                                chosenPosition.getDescription() + ") ist verf�gbar und wurde Dir zugewiesen. " +
                                "Du wirst hierzu in K�rze eine Best�tigungs-Mail erhalten.")
                .withLinebreak(2)
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }
}