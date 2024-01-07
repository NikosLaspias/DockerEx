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
import java.io.IOException;

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
     * Starts a Docker container based on the provided container ID.
     *
     * @param containerId The ID of the container to start.
     */
    public void startContainer(String containerId) {
        try {
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
     * Stops a Docker container based on the provided container ID.
     *
     * @param containerId The ID of the container to stop.
     */
    public void stopContainer(String containerId) {
        try {
            // Stop the container based on the containerId
            dockerClient.stopContainerCmd(containerId).exec();
            // If everything goes well, show an alert message
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

    /**
     * Executes a Docker container.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void executeContainer() throws IOException {
        ExposedPort tcp80 = ExposedPort.tcp(80);
        PortBinding portBinding = PortBinding.parse("0.0.0.0:8080");
        HostConfig hostConfig = new HostConfig().withPortBindings(portBinding);

        // Create and start the container
        CreateContainerResponse container = createAndStartContainer(tcp80, hostConfig);

        // Stop the container (replace "CONTAINER_ID" with the actual container ID)
        dockerClient.stopContainerCmd(container.getId()).exec();
    }

    private CreateContainerResponse createAndStartContainer(ExposedPort exposedPort, HostConfig hostConfig) {
        // Create the container
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd("nginx")
                .withExposedPorts(exposedPort)
                .withHostConfig(hostConfig);
        CreateContainerResponse container = createContainerCmd.exec();

        // Start the container
        dockerClient.startContainerCmd(container.getId()).exec();

        return container;
    }
}
