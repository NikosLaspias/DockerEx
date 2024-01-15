//DataBaseManager: a class that manages the database
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

import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class DatabaseManager {

    // Method to handle the database-related option (Option 6)
    public void handleOption6() {
        System.out.println("Handling Option 6: Connect with the database to insert the measurements");

        // Create a confirmation alert to ask the user if they want to connect to the
        // database
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Connect with the database");
        alert.setHeaderText(null);
        alert.setContentText(
                "Do you want to connect with the database to insert the measurements?You shoud create a personal database in mysql to insert measurements");

        // Define Yes and No buttons
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the alert and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // Process the user's choice
        if (result.isPresent() && result.get() == yesButton) {
            // User selected Yes, proceed with connecting to the database

            // Get measurements from the MonitorThread dynamically
            List<MonitorThread.ContainerMeasurement> measurements = MonitorManager.getMeasurements();

            // Insert measurements into the database
            try (Database database = new Database()) {
                database.insertContainerMeasurements(measurements);
                database.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // User selected No or closed the alert, do nothing and return
            return;
        }
    }
}
