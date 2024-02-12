package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.List;

/**
 * The ExecutorThread class is responsible for starting, stopping, and executing
 * Docker containers.
 */
public class ExecutorThread {

    private final DockerClient dockerClient;

    /**
     * Constructor to initialize the ExecutorThread.
     * Configures the Docker client.
     */
    public ExecutorThread() {
        // Configure Docker client
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375").build();
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    /**
     * Checks if a Docker container is active.
     *
     * @param containerId The ID of the container to check.
     * @return True if the container is active, false otherwise.
     */
    private boolean isContainerActive(String containerId) {
        List<com.github.dockerjava.api.model.Container> containers = dockerClient.listContainersCmd().withShowAll(true)
                .exec();
        return containers.stream()
                .anyMatch(container -> container.getId().equals(containerId) && container.getState().equals("running"));
    }

    /**
     * Starts a Docker container based on the provided container ID.
     *
     * @param containerId The ID of the container to start.
     */

    public void startContainer(String containerId) {
        try {
            // Check if the container is already active
            if (isContainerActive(containerId)) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Container Already Active");
                    alert.setHeaderText(null);
                    alert.setContentText("Container is already active: " + containerId);
                    alert.showAndWait();
                });
            } else {
                // Start the container based on the containerId
                dockerClient.startContainerCmd(containerId).exec();
                // If everything goes well, show an alert message
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Container Started");
                    alert.setHeaderText(null);
                    alert.setContentText("Container started: " + containerId);
                    alert.showAndWait();
                });
            }
        } catch (Exception e) {
            // If an error occurs, show an alert message
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error starting container: " + e.getMessage());
                alert.showAndWait();
            });
            e.printStackTrace();
        }
    }

    /**
     * Checks if a Docker container is stopped.
     *
     * @param containerId The ID of the container to check.
     * @return True if the container is stopped, false otherwise.
     */
    private boolean isContainerStopped(String containerId) {
        List<com.github.dockerjava.api.model.Container> containers = dockerClient.listContainersCmd().withShowAll(true)
                .exec();
        return containers.stream()
                .anyMatch(container -> container.getId().equals(containerId) && container.getState().equals("exited"));
    }

    /**
     * Stops a Docker container based on the provided container ID.
     *
     * @param containerId The ID of the container to stop.
     */
    public void stopContainer(String containerId) {
        try {
            // Check if the container is already stopped
            if (isContainerStopped(containerId)) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Container Already Stopped");
                    alert.setHeaderText(null);
                    alert.setContentText("Container is already stopped: " + containerId);
                    alert.showAndWait();
                });
                return; // Εάν το container είναι ήδη σταματημένο, τότε δεν χρειάζεται να συνεχίσετε
            }

            // Stop the container based on the containerId
            dockerClient.stopContainerCmd(containerId).exec();

            // Show an alert message
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Container Stopped");
                alert.setHeaderText(null);
                alert.setContentText("Container stopped: " + containerId);
                alert.showAndWait();
            });
        } catch (Exception e) {
            // If an error occurs, show an alert message
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error stopping container: " + e.getMessage());
                alert.showAndWait();
            });
            e.printStackTrace();
        }
    }

    public void executeContainer(String containerId) throws IOException {
        ExposedPort tcp80 = ExposedPort.tcp(80);
        PortBinding portBinding = PortBinding.parse("0.0.0.0:8080");
        HostConfig hostConfig = new HostConfig().withPortBindings(portBinding);

        // Check if the container already exists
        String existingContainerId = containerId;

        if (dockerClient.listContainersCmd().withShowAll(true).exec().stream()
                .anyMatch(container -> container.getId().equals(existingContainerId))) {
            // Container already exists
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Container already exists: " + existingContainerId);
                alert.showAndWait();
            });
        } else {
            // Create and start the container
            CreateContainerResponse container = createAndStartContainer(tcp80, hostConfig);

            // Display a confirmation dialog
            Platform.runLater(() -> {
                Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationDialog.setTitle("Container Action");
                confirmationDialog.setHeaderText(null);
                confirmationDialog.setContentText("Do you want to start or stop the container?");
                ButtonType buttonTypeStart = new ButtonType("Start");
                ButtonType buttonTypeStop = new ButtonType("Stop");
                confirmationDialog.getButtonTypes().setAll(buttonTypeStart, buttonTypeStop);

                java.util.Optional<ButtonType> result = confirmationDialog.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == buttonTypeStart) {
                        // User chose to start the container
                        showAlert("Container started. Container ID: " + container.getId());
                    } else if (result.get() == buttonTypeStop) {
                        // User chose to stop the container
                        dockerClient.stopContainerCmd(container.getId()).exec();
                        showAlert("Container stopped. Container ID: " + container.getId());
                    }
                }
            });
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private CreateContainerResponse createAndStartContainer(ExposedPort exposedPort, HostConfig hostConfig) {
        // Create the container
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd("mongo")
                .withExposedPorts(exposedPort)
                .withHostConfig(hostConfig);
        CreateContainerResponse container = createContainerCmd.exec();
        // Start the container
        dockerClient.startContainerCmd(container.getId()).exec();
        return container;
    }
}