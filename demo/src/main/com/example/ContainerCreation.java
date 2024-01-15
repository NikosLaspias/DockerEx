package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.async.ResultCallback;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

/**
 * The ContainerCreation class is responsible for managing Docker containers,
 * including starting and stopping them.
 */
public class ContainerCreation {

    private static String containerId;
    private static DockerClient dockerClient;

    /**
     * Manages Docker containers by prompting the user for a container ID and
     * starting or stopping the container based on its current state.
     * Additionally, displays information about active and inactive containers,
     * volumes, and networks.
     */

    public static DockerClient getDockerClient() {
        return dockerClient;
    }

    public static void manageContainers() {
        // Creation of docker client
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375").build();
        dockerClient = DockerClientBuilder.getInstance(config).build();

        boolean state = false;
        do {
            try {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Containers");
                dialog.setHeaderText(
                        "Enter the container id that you want to control-Example of valid container id 05007d...");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    containerId = result.get();
                    state = true;
                }
            } catch (Exception e) {
                showErrorAlert("Invalid container ID. Please try again.");
            }
        } while (!state);

        // Control if the container is running
        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        Boolean isRunning = containerInfo.getState().getRunning();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Container Status");

        if (!isRunning) {
            // Start the container
            StartContainerCmd startCmd = dockerClient.startContainerCmd(containerId);
            startCmd.exec();
            alert.setHeaderText("Container Started");
            alert.setContentText("Container has been started.");
            showContainerLogs(containerId);
        } else {
            // Stop the container
            StopContainerCmd stopCmd = dockerClient.stopContainerCmd(containerId);
            stopCmd.exec();
            alert.setHeaderText("Container Stopped");
            alert.setContentText("Container has been stopped.");
            showContainerLogs(containerId);
        }
        alert.showAndWait();

        // Display information about active and inactive containers in a window
        Platform.runLater(() -> {
            Stage infoStage = new Stage();
            infoStage.setTitle("Container Information");

            // Create and configure the JavaFX TextArea
            TextArea infoTextArea = new TextArea();
            infoTextArea.setEditable(false);
            infoTextArea.setWrapText(true);

            List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
            containers.sort(Comparator.comparing(c -> c.getState().equalsIgnoreCase("running") ? 0 : 1));

            long activeCount = containers.stream().filter(c -> c.getState().equalsIgnoreCase("running")).count();
            long inactiveCount = containers.size() - activeCount;

            infoTextArea.appendText("Active Containers (" + activeCount + "):\n");
            infoTextArea.appendText("Inactive Containers (" + inactiveCount + "):\n");

            containers.forEach(c -> {
                infoTextArea.appendText("Container ID: " + c.getId() + "\n");
                infoTextArea.appendText("State: " + c.getState() + "\n");

                // Display additional details of the container
                InspectContainerResponse containerDetails = dockerClient.inspectContainerCmd(c.getId()).exec();
                infoTextArea.appendText("Details:\n");
                infoTextArea.appendText("State: " + containerDetails.getState().toString() + "\n");
                infoTextArea.appendText("Image: " + containerDetails.getConfig().getImage() + "\n");
                infoTextArea.appendText("\n");
            });

            Scene infoScene = new Scene(infoTextArea, 600, 400);
            infoStage.setScene(infoScene);
            infoStage.showAndWait();
        });
    }

    // Show the logs of a container in JavaFX TextArea.
    private static void showContainerLogs(String containerId) {
        Platform.runLater(() -> {
            Stage logStage = new Stage();
            logStage.setTitle("Container Logs");

            TextArea logTextArea = new TextArea();
            logTextArea.setEditable(false);
            logTextArea.setWrapText(true);

            logStage.setOnCloseRequest(event -> {
                logTextArea.clear();
            });

            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(true)
                    .exec(new ResultCallback.Adapter<Frame>() {
                        @Override
                        public void onNext(Frame item) {
                            Platform.runLater(() -> {
                                logTextArea.appendText(item.toString());
                            });
                        }
                    });

            Scene logScene = new Scene(logTextArea, 800, 600);
            logStage.setScene(logScene);
            logStage.show();
        });
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
