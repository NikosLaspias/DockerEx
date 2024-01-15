//ImageManager: a class that manages the docker images
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

import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * The ImageManager class handles user input related to Docker image operations.
 */
public class ImageManager {

    private TextArea resultTextArea;

    /**
     * Constructs an ImageManager with a specified resultTextArea.
     *
     * @param resultTextArea The TextArea where the results of Docker operations
     *                       will be displayed.
     */
    public ImageManager(TextArea resultTextArea) {
        this.resultTextArea = resultTextArea;
    }

    /**
     * Handles the user-selected option 4 from the main menu.
     * Prompts the user to enter an image name and then presents a choice dialog for
     * various Docker image operations.
     */
    public void handleOption4() {
        // Prompt user for image name
        TextInputDialog dialog = new TextInputDialog("ImageName");
        dialog.setTitle("Enter Image Name");
        dialog.setHeaderText("Please enter the name of the image: (example:nignx)");
        dialog.setContentText("Image Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(this::performDockerOperation);
    }

    /**
     * Performs the Docker operation based on the user's selected image name and
     * operation.
     *
     * @param selectedImageName The name of the Docker image provided by the user.
     */
    private void performDockerOperation(String selectedImageName) {
        // Present choice dialog for Docker operations
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Search an image",
                "showPullHistory", "removeImage", "listImages", "pullImageIfNotExists", "pullImage");
        choiceDialog.setTitle("Choose Action");
        choiceDialog.setHeaderText("Select the action you want to perform:");
        choiceDialog.setContentText("Action:");

        Optional<String> result = choiceDialog.showAndWait();
        result.ifPresent(selectedOperation -> {
            try (Image dockerOperations = new Image(resultTextArea)) {
                // Perform the selected Docker operation
                switch (selectedOperation) {
                    case "Search an image":
                        dockerOperations.searchImages(selectedImageName);
                        break;
                    case "showPullHistory":
                        dockerOperations.showPullHistory(selectedImageName);
                        break;
                    case "removeImage":
                        dockerOperations.removeImage(selectedImageName);
                        break;
                    case "listImages":
                        dockerOperations.showImageList();
                        break;
                    case "pullImageIfNotExists":
                        dockerOperations.pullImageIfNotExists(selectedImageName);
                        break;
                    case "pullImage":
                        dockerOperations.pullImage(selectedImageName);
                        break;
                    default:
                        System.out.println("Invalid operation selected.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
