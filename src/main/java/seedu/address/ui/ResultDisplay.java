package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * A ui for the status bar that is displayed at the header of the application.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    public ResultDisplay() {
        super(FXML);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        String formattedFeedback = formatFeedback(feedbackToUser);
        resultDisplay.setText(formattedFeedback);
    }

    /**
     * Gets the current feedback text.
     */
    public String getText() {
        return resultDisplay.getText();
    }

    /**
     * Formats the feedback text for better readability.
     */
    private String formatFeedback(String feedback) {
        String[] lines = feedback.split("\\n");
        StringBuilder formatted = new StringBuilder();

        for (String line : lines) {
            if (line.contains(";")) {
                String prefix = line.contains(":") ? line.substring(0, line.indexOf(":") + 1) + "\n" : "";
                formatted.append(prefix);
                String paramsSection = line.substring(prefix.isEmpty() ? 0 : line.indexOf(":") + 1);
                String[] params = paramsSection.split(";");
                
                for (String param : params) {
                    param = param.trim();
                    if (!param.isEmpty()) {
                        formatted.append("  • ").append(param).append("\n");
                    }
                }
            } else {
                if (!line.trim().isEmpty()) {
                    formatted.append(line.trim()).append("\n");
                }
            }
        }

        return formatted.toString().trim();
    }
}
