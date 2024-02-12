package com.example;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.http.ResponseEntity;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

public class RestControler {
    private final RestAPI restAPI;
    private final Stage primaryStage;

    public RestControler(RestAPI restAPI, Stage primaryStage) {
        this.restAPI = restAPI;
        this.primaryStage = primaryStage;
    }

    public void handleOption7() {
        System.out.println("Handling Option 7: Rest API for the handling of containers");

        // Request dates with TextInputDialog
        Optional<String> startDateResult = showInputDialog("Enter start date:");
        Optional<String> endDateResult = showInputDialog("Enter end date:");

        String startDate = startDateResult.orElse("");
        String endDate = endDateResult.orElse("");

        ResponseEntity<List<Map<String, Object>>> measurementsResponse = restAPI.getMeasurements(startDate, endDate);

        Platform.runLater(() -> {
            if (measurementsResponse.getStatusCode().is2xxSuccessful()) {
                List<Map<String, Object>> measurements = measurementsResponse.getBody();

                // Create a new Stage for displaying the results
                Stage resultsStage = new Stage();
                resultsStage.setTitle("Option 7 Results");

                // Create a TextArea to display the results
                TextArea resultsTextArea = new TextArea();
                resultsTextArea.setEditable(false);
                resultsTextArea.setWrapText(true);

                // Display measurements
                resultsTextArea.appendText("Measurements:\n");
                if (!measurements.isEmpty()) {
                    for (Map<String, Object> measurement : measurements) {
                        resultsTextArea.appendText(measurement.toString() + "\n");
                    }
                } else {
                    resultsTextArea.appendText("No measurements found.\n");
                }
                resultsTextArea.appendText(System.lineSeparator());

                ResponseEntity<Map<String, Object>> containerStatsResponse = restAPI.getContainerStats();
                Map<String, Object> containerStats = containerStatsResponse.getBody();

                // Access the information from containerStats
                long activeCount = (long) containerStats.get("activeCount");
                long inactiveCount = (long) containerStats.get("inactiveCount");

                @SuppressWarnings("unchecked")
                List<String> activeContainerIds = (List<String>) containerStats.get("activeContainerIds");

                @SuppressWarnings("unchecked")
                List<String> inactiveContainerIds = (List<String>) containerStats.get("inactiveContainerIds");

                // Display active containers
                resultsTextArea.appendText("Active Containers: " + activeCount + "\n");
                resultsTextArea.appendText("Inactive Containers: " + inactiveCount + "\n");
                resultsTextArea.appendText(System.lineSeparator());

                // Display active containers
                resultsTextArea.appendText("Active Container IDs:\n");
                if (!activeContainerIds.isEmpty()) {
                    resultsTextArea.appendText(String.join("\n\n", activeContainerIds) + "\n\n");
                } else {
                    resultsTextArea.appendText("No active containers found.\n");
                }
                resultsTextArea.appendText(System.lineSeparator());

                // Display inactive containers
                resultsTextArea.appendText("Inactive Container IDs:\n");
                if (!inactiveContainerIds.isEmpty()) {
                    resultsTextArea.appendText(String.join("\n\n", inactiveContainerIds) + "\n\n");
                } else {
                    resultsTextArea.appendText("No inactive containers found.\n");
                }

                // Use showResultsDialog to display the results
                showResultsDialog("Option 7 Results", resultsTextArea.getText(), resultsStage);

                // Ask the user if they want to continue with start/stop actions
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Continue with Start/Stop Actions");
                alert.setHeaderText(null);
                alert.setContentText("Do you want to continue with start/stop actions on Docker containers?");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Ask the user for the Docker container ID to start (or "none")
                    Optional<String> startContainerIdResult = showInputDialog(
                            "Enter Docker container ID to start (or type 'none'):");
                    String startContainerId = startContainerIdResult.orElse("");

                    // if it is not "none" perform the start action
                    if (!startContainerId.equalsIgnoreCase("none")) {
                        ResponseEntity<String> startResult = restAPI.startContainer(startContainerId);
                        resultsTextArea.appendText("Start Container Result: " + startResult.getBody() + "\n");
                    }

                    // Ask the user for the Docker container ID to stop (or "none")
                    Optional<String> stopContainerIdResult = showInputDialog(
                            "Enter Docker container ID to stop (or type 'none'):");
                    String stopContainerId = stopContainerIdResult.orElse("");

                    // if it is not "none" perform the stop action
                    if (!stopContainerId.equalsIgnoreCase("none")) {
                        ResponseEntity<String> stopResult = restAPI.stopContainer(stopContainerId);
                        resultsTextArea.appendText("Stop Container Result: " + stopResult.getBody() + "\n");
                    }
                } else {
                    // User chose not to continue, you can add relevant actions or simply exit
                    resultsTextArea.appendText("User chose not to continue with start/stop actions.\n");
                }
            }
        });
    }

    // Show the results dialog
    private void showResultsDialog(String title, String content, Stage resultsStage) {
        resultsStage.setTitle(title);

        TextArea resultsTextArea = new TextArea();
        resultsTextArea.setEditable(false);
        resultsTextArea.setWrapText(true);
        resultsTextArea.setText(content);

        Scene resultsScene = new Scene(resultsTextArea, 800, 600);
        resultsStage.setScene(resultsScene);
        resultsStage.show();

        primaryStage.close(); // Optionally close the main stage
    }

    private Optional<String> showInputDialog(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);

        return dialog.showAndWait();
    }
}
