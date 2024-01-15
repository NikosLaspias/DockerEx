package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.util.List;
import javafx.stage.Stage;

/**
 * The Image class encapsulates Docker image-related operations.
 */

public class Image implements AutoCloseable {

    private DockerClient dockerClient;
    private DefaultDockerClientConfig config;

    /**
     * Constructs an Image object with the specified output TextArea.
     *
     * @param outputTextArea The TextArea where the results of Docker operations
     *                       will be displayed.
     */

    public Image(TextArea outputTextArea) {
        this.config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375").build();
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    /**
     * Pulls the Docker image if it does not already exist.
     *
     * @param imageName The name of the Docker image to pull.
     */

    public void pullImageIfNotExists(String imageName) {
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName)
                .exec();
        StringBuilder message = new StringBuilder();
        if (images.isEmpty()) {
            pullImage(imageName);
            message.append("Image was not already pulled. Image pulled successfully.\n");
        } else {
            message.append("Image already exists.\n");
        }
        showAlert("Image Pull Result", message.toString());
    }

    /**
     * Pulls the Docker image with the specified name.
     *
     * @param imageName The name of the Docker image to pull.
     */

    public void pullImage(String imageName) {
        try {
            dockerClient.pullImageCmd(imageName).exec(null);
            showAlert("Image Pulled", "Image pulled successfully.");
        } catch (com.github.dockerjava.api.exception.NotFoundException e) {
            showAlert("Error", "Error pulling image: Image not found - " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Error pulling image: " + e.getMessage());
        }
    }

    /**
     * Shows a list of Docker images and their information.
     */

    public void showImageList() {
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().exec();
        StringBuilder imageInfo = new StringBuilder("List of Docker images:\n");

        for (com.github.dockerjava.api.model.Image image : images) {
            imageInfo.append("Image ID: ").append(image.getId()).append("\n");
            imageInfo.append("Repo Tags: ").append(String.join(", ", image.getRepoTags())).append("\n");
            imageInfo.append("Created: ").append(image.getCreated()).append("\n");
            imageInfo.append("Size: ").append(image.getSize()).append("\n");
            imageInfo.append("------------------------\n");
        }

        Platform.runLater(() -> {
            showImageListDialog(imageInfo.toString());
        });
    }

    // Show the list of all images
    private void showImageListDialog(String imageInfo) {
        Stage imageListStage = new Stage();
        imageListStage.setTitle("Docker Image List");

        TextArea imageInfoTextArea = new TextArea();
        imageInfoTextArea.setEditable(false);
        imageInfoTextArea.setWrapText(true);
        imageInfoTextArea.setText(imageInfo);

        Scene imageListScene = new Scene(imageInfoTextArea, 800, 600);
        imageListStage.setScene(imageListScene);
        imageListStage.show();
    }

    // search a specific image
    public void searchImages(String imageName) {
        try {
            InspectImageResponse inspectResponse = dockerClient.inspectImageCmd(imageName).exec();
            showAlert("Docker Image Information",
                    "Image Name: " + imageName + "\n" +
                            "Tags: " + String.join(", ", inspectResponse.getRepoTags()) + "\n" +
                            "Commands: " + inspectResponse.getConfig().getCmd());
        } catch (NotFoundException e) {
            showAlert("Image Not Found", "The image with name " + imageName + " was not found.");
        }
    }

    public InspectImageResponse inspectImage(String imageId) {
        return dockerClient.inspectImageCmd(imageId).exec();
    }

    public void removeImage(String imageId) {
        try {
            dockerClient.removeImageCmd(imageId).exec();
            showAlert("Image Removed", "Image removed successfully.");
        } catch (Exception e) {
            showAlert("Error Removing Image", "Error removing image: " + e.getMessage());
        }
    }

    /**
     * Displays a pull history for a Docker image.
     *
     * @param imageName The name of the Docker image.
     */
    public void showPullHistory(String imageName) {
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName)
                .exec();

        StringBuilder pullHistory = new StringBuilder("Pull History for Image:\n");

        for (com.github.dockerjava.api.model.Image image : images) {
            pullHistory.append("Image ID: ").append(image.getId()).append("\n");
            pullHistory.append("Commands executed during pull: ")
                    .append(inspectImage(image.getId()).getContainerConfig().getCmd()).append("\n");
            pullHistory.append("------------------------\n");
        }

        Platform.runLater(() -> {
            showPullHistoryDialog(pullHistory.toString());
        });
    }

    private void showPullHistoryDialog(String pullHistory) {
        Stage pullHistoryStage = new Stage();
        pullHistoryStage.setTitle("Pull History for Image");

        TextArea pullHistoryTextArea = new TextArea();
        pullHistoryTextArea.setEditable(false);
        pullHistoryTextArea.setWrapText(true);
        pullHistoryTextArea.setText(pullHistory);

        Scene pullHistoryScene = new Scene(pullHistoryTextArea, 800, 600);
        pullHistoryStage.setScene(pullHistoryScene);
        pullHistoryStage.show();
    }

    /**
     * Displays an alert with the specified title and content.
     *
     * @param title   The title of the alert.
     * @param content The content of the alert.
     */

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @Override
    public void close() {
        if (dockerClient != null) {
            try {
                dockerClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
