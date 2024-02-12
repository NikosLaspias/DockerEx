package com.example;

import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

public class ExecutorManager {
    // Method to handle Option 2: Start-stop-execute a container based on id
    public void handleOption2() {
        System.out.println("Handling Option 2: Start-stop-execute a container based on id");

        // Let the user choose the action (Start, Stop, Execute)
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Execute", "Start", "Stop");
        choiceDialog.setTitle("Choose Action");
        choiceDialog.setHeaderText("Select the action you want to perform:");
        choiceDialog.setContentText("Action:");

        // Use Platform.runLater to ensure UI updates on the JavaFX Application Thread
        Platform.runLater(() -> {
            choiceDialog.showAndWait().ifPresent(action -> {
                // Create an ExecutorThread for container actions
                ExecutorThread executorThread = new ExecutorThread();
                String containerId;
                switch (action) {
                    case "Execute":
                        try {
                            // Execute the container
                            // Ask the user for the container id to stop
                            containerId = askForContainerId("Enter the container id that you want to stop:");
                            // Stop the specified container
                            executorThread.executeContainer(containerId);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Start":
                        // Ask the user for the container id to start
                        containerId = askForContainerId("Enter the container id that you want to start:");
                        // Start the specified container
                        executorThread.startContainer(containerId);
                        break;
                    case "Stop":
                        // Ask the user for the container id to stop
                        containerId = askForContainerId("Enter the container id that you want to stop:");
                        // Stop the specified container
                        executorThread.stopContainer(containerId);
                        break;
                }
            });
        });
    }

    // Method to prompt the user for a container id
    private String askForContainerId(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Container ID");
        dialog.setHeaderText(prompt);
        return dialog.showAndWait().orElse("");
    }
}
