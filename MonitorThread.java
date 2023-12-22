package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.ArrayList;
import java.util.List;

public class MonitorThread implements Runnable, AutoCloseable {
    private final DockerClient dockerClient;
    private final List<ContainerMeasurement> containerMeasurements;
    private final DefaultDockerClientConfig config;

    // Constructor to initialize the MonitorThread
    public MonitorThread() {
        // Set Docker host configuration
        this.config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375").build();
        // Build Docker client using the configuration
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
        // Initialize the list to store container measurements
        this.containerMeasurements = new ArrayList<>();

    }

    // Run method for continuous monitoring of containers
    @Override
    public void run() {
        try {
            while (true) {
                monitorContainers();
                Thread.sleep(5000); // Sleep for 5 seconds before the next monitoring cycle
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Method to monitor Docker containers and collect measurements
    private synchronized void monitorContainers() {
        System.out.println("Monitoring Containers:");

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

        // Display the collected container measurements
        for (ContainerMeasurement measurement : containerMeasurements) {
            System.out.println("Container ID: " + measurement.getId());
            System.out.println("Image Name: " + measurement.getImage());
            System.out.println("Status: " + measurement.getStatus());
            System.out.println("Command: " + measurement.getCommand());
            System.out.println("Ports: " + measurement.getPorts());
            System.out.println("------------------------");
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

    // AutoCloseable interface method for proper resource cleanup
    @Override
    public void close() throws Exception {
        if (dockerClient != null) {
            dockerClient.close();
        }
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

    public List<String> getActiveDockerInstancesList() {
        List<String> activeInstances = new ArrayList<>();

        for (ContainerMeasurement measurement : containerMeasurements) {
            if (isContainerActive(measurement)) {
                activeInstances.add(measurement.getId());
            }
        }

        return activeInstances;
    }

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

        // Count inactive containers
        int inactiveInstances = 0;
        for (Container container : allContainers) {
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

    // Inner class representing a container measurement
    public static class ContainerMeasurement {
        private final String id;
        private final String image;
        private final String status;
        private final String ports;
        private final String command;

        public ContainerMeasurement(String id, String image, String status, String ports, String command) {
            this.id = id;
            this.image = image;
            this.status = status;
            this.ports = ports;
            this.command = command;
        }

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