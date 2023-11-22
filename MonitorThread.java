package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.ArrayList;
import java.util.List;

public class MonitorThread extends Thread {

    private DockerClient dockerClient;
    private List<ContainerMeasurement> containerMeasurements;

    public MonitorThread() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
        this.containerMeasurements = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Ελέγχετε τα containers και καταγράφετε τις μετρήσεις
                monitorContainers();

                // Περιμένετε για κάποιο χρονικό διάστημα πριν ελέγξετε ξανά
                Thread.sleep(5000); // π.χ., 5 δευτερόλεπτα
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void monitorContainers() {
        System.out.println("Monitoring Containers:");

        // Λίστα όλων των containers
        List<Container> containers = dockerClient.listContainersCmd().exec();

        // Καταγραφή των στοιχείων των containers στον πίνακα containerMeasurements
        for (Container container : containers) {
            ContainerMeasurement measurement = new ContainerMeasurement(
                    container.getId(),
                    container.getImage(),
                    container.getStatus(),
                    getPortsAsString(container),
                    container.getCommand());
            containerMeasurements.add(measurement);
        }

        // Εμφανίζετε τα στοιχεία των containers
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

    static class ContainerMeasurement {
        private String id;
        private String image;
        private String status;
        private String ports;
        private String command;

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
