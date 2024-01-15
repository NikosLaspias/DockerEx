//ExecutorManager: a class that manages the executor thread
//Copyright(C) 2023/24 Eleutheria Koutsiouri
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.

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
                            executorThread.executeContainer();
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

        // Return the container id entered by the user
        return dialog.showAndWait().orElse("");
    }
}
