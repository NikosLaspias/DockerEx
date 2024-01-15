//AppManager: a class who manages the application
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

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class AppManager {
    public void handleOption3() {
        System.out.println("Handling Option 3: Create a container based on image");

        // Create a dialog for user input
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Container Creation");
        dialog.setHeaderText("Enter Docker Image Name and Container ID");
        dialog.setContentText("Docker Image Name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String imageName = result.get();

            // Prompt for Container ID
            TextInputDialog containerIdDialog = new TextInputDialog();
            containerIdDialog.setTitle("Container Creation");
            containerIdDialog.setHeaderText("Enter Docker Container ID");
            containerIdDialog.setContentText("Docker Container ID:");

            Optional<String> containerIdResult = containerIdDialog.showAndWait();
            if (containerIdResult.isPresent()) {
                String containerId = containerIdResult.get();
                // Create an instance of AppWithContainer and manage the container
                AppWithContainer app = new AppWithContainer("tcp://localhost:2375", imageName, containerId);
                try {
                    app.manageContainer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("Invalid Container ID");
            }
        } else {
            showAlert("Invalid Image Name");
        }
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
