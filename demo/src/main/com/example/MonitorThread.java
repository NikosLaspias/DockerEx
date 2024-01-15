package com.example;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Screen;
import javafx.stage.Stage;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * The MonitorThread class is responsible for monitoring Docker containers
 * and collecting measurements at regular intervals.
 */

public class MonitorThread implements Runnable, AutoCloseable {
    private final DockerClient dockerClient;
    private final List<ContainerMeasurement> containerMeasurements;
    private final DefaultDockerClientConfig config;
    private boolean executedOnce = false;

    /**
     * Constructor to initialize the MonitorThread.
     */
    public MonitorThread() {
        // Set Docker host configuration
        this.config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375").build();
        // Build Docker client using the configuration
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
        // Initialize the list to store container measurements
        this.containerMeasurements = new ArrayList<>();

    }

    @Override
    public void run() {
        try {
            // Monitor containers only if measurements haven't been displayed yet
            if (!executedOnce) {
                monitorContainers();
                executedOnce = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays an information alert using JavaFX.
     *
     * @param title   The title of the alert.
     * @param content The content text of the alert.
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

    /**
     * Monitors Docker containers and collects measurements.
     */
    private synchronized void monitorContainers() {
        showAlert("Monitoring Containers", "Containers Measurements");
        // Get the list of Docker containers
        List<Container> containers = dockerClient.listContainersCmd().exec();

        // Clear the previous container measurements
        containerMeasurements.clear();

        // Iterate through containers and create ContainerMeasurement objects
        for (Container container : containers) {
            ContainerMeasurement measurement = new ContainerMeasurement(
                    container.getId(),
                    container.getImage(),
                    container.getStatus(),
                    getPortsAsString(container),
                    container.getCommand());
            containerMeasurements.add(measurement);
        }

    }

    // Method to get ports as a formatted string from a Docker container
    private String getPortsAsString(Container container) {
        ContainerPort[] ports = container.getPorts();
        StringBuilder result = new StringBuilder();
        for (ContainerPort port : ports) {
            result.append(port.getIp()).append(":").append(port.getPublicPort())
                    .append("->").append(port.getPrivatePort()).append(" ");
        }
        return result.toString();
    }

    // Method to display all measurements covering the entire screen
    public void displayAllMeasurements() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("All Active Container Information");

            // Create and configure the JavaFX TextArea
            TextArea infoTextArea = new TextArea();
            infoTextArea.setEditable(false);
            infoTextArea.setWrapText(true);

            // Display a list of active containers with their respective details
            infoTextArea.appendText("All Active Container Measurements:\n");
            for (ContainerMeasurement measurement : containerMeasurements) {
                infoTextArea.appendText("\nContainer ID: " + measurement.getId() + "\n");
                infoTextArea.appendText("Image Name: " + measurement.getImage() + "\n");
                infoTextArea.appendText("Status: " + measurement.getStatus() + "\n");
                infoTextArea.appendText("Command: " + measurement.getCommand() + "\n");
                infoTextArea.appendText("Ports: " + measurement.getPorts() + "\n");
                infoTextArea.appendText("------------------------\n");
            }

            // Create the scene
            Scene scene = new Scene(infoTextArea);

            // Get the primary screen dimensions
            Screen primaryScreen = Screen.getPrimary();
            Rectangle2D screenBounds = primaryScreen.getVisualBounds();

            // Set the size of the stage to cover the entire screen
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());

            // Set the scene to the center of the screen
            stage.setScene(scene);
            stage.centerOnScreen();

            // Show the stage
            stage.show();
        });
    }

    // Method to get the container measurements
    public List<ContainerMeasurement> getContainerMeasurements() {
        return new ArrayList<>(containerMeasurements);
    }

    public int getActiveDockerInstances() {
        int activeInstances = 0;

        // Control the list of containers to get the active containers
        for (ContainerMeasurement measurement : containerMeasurements) {
            if (isContainerActive(measurement)) {
                activeInstances++;
            }
        }

        return activeInstances;
    }

    // Get a list of IDs for active Docker instances
    public List<String> getActiveDockerInstancesList() {
        List<String> activeInstances = new ArrayList<>();

        for (ContainerMeasurement measurement : containerMeasurements) {
            if (isContainerActive(measurement)) {
                activeInstances.add(measurement.getId());
            }
        }

        return activeInstances;
    }

    // Get a list of IDs for inactive Docker instances
    public List<String> getInactiveDockerInstancesList() {
        List<String> inactiveInstances = new ArrayList<>();

        for (ContainerMeasurement measurement : containerMeasurements) {
            if (!isContainerActive(measurement)) {
                inactiveInstances.add(measurement.getId());
            }
        }

        return inactiveInstances;
    }

    // Method to get the number of inactive Docker instances
    public int getInactiveDockerInstances() {
        List<Container> allContainers = dockerClient.listContainersCmd().exec();

        // Debug print
        System.out.println("Debug: All Containers: " + allContainers);

        // Count inactive containers
        int inactiveInstances = 0;
        for (Container container : allContainers) {
            // Debug print
            System.out.println("Debug: Container State: " + container.getState());

            if (!container.getState().equals("running")) {
                inactiveInstances++;
            }
        }
        return inactiveInstances;
    }

    // Control if a container is running
    private boolean isContainerActive(ContainerMeasurement measurement) {
        return "running".equalsIgnoreCase(measurement.getStatus());
    }

    // AutoCloseable interface method for proper resource cleanup
    @Override
    public void close() throws Exception {
        if (dockerClient != null) {
            dockerClient.close();
        }
    }

    // Inner class representing a container measurement
    public static class ContainerMeasurement {
        private final String id;
        private final String image;
        private final String status;
        private final String ports;
        private final String command;

        /**
         * Constructor to initialize a container measurement.
         *
         * @param id      The ID of the container.
         * @param image   The image associated with the container.
         * @param status  The status of the container.
         * @param ports   The ports of the container.
         * @param command The command executed by the container.
         */

        public ContainerMeasurement(String id, String image, String status, String ports, String command) {
            this.id = id;
            this.image = image;
            this.status = status;
            this.ports = ports;
            this.command = command;
        }

        // Getter methods for container attributes
        public String getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public String getStatus() {
            return status;
        }

        public String getPorts() {
            return ports;
        }

        public String getCommand() {
            return command;
        }
    }
}