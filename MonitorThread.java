package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.core.DockerClientBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.Closeable;

public class MonitorThread implements Runnable, Closeable {

    private final DockerClient dockerClient;
    final List<ContainerMeasurement> containerMeasurements;

    public MonitorThread() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
        this.containerMeasurements = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            while (true) {
                monitorContainers();
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void monitorContainers() {
        System.out.println("Monitoring Containers:");

        List<Container> containers = dockerClient.listContainersCmd().exec();

        containerMeasurements.clear(); // Καθαρίζει τη λίστα πριν από την ανανέωση

        for (Container container : containers) {
            ContainerMeasurement measurement = new ContainerMeasurement(
                    container.getId(),
                    container.getImage(),
                    container.getStatus(),
                    getPortsAsString(container),
                    container.getCommand());
            containerMeasurements.add(measurement);
        }

        for (ContainerMeasurement measurement : containerMeasurements) {
            System.out.println("Container ID: " + measurement.getId());
            System.out.println("Image Name: " + measurement.getImage());
            System.out.println("Status: " + measurement.getStatus());
            System.out.println("Command: " + measurement.getCommand());
            System.out.println("Ports: " + measurement.getPorts());
            System.out.println("------------------------");
        }
    }

    private String getPortsAsString(Container container) {
        ContainerPort[] ports = container.getPorts();
        StringBuilder result = new StringBuilder();
        for (ContainerPort port : ports) {
            result.append(port.getIp()).append(":").append(port.getPublicPort())
                    .append("->").append(port.getPrivatePort()).append(" ");
        }
        return result.toString();
    }

    @Override
    public void close() throws IOException {
        if (dockerClient != null) {
            try {
                dockerClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ContainerMeasurement {
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

    public static List<ContainerMeasurement> getContainerMeasurements(
            List<ContainerMeasurement> containerMeasurements) {
        return new ArrayList<>(containerMeasurements);
    }
}
