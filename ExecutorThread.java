package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import java.io.IOException;

public class ExecutorThread {

    private final DockerClient dockerClient;

    public ExecutorThread() {
        // Configure Docker client
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375").build();
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    // Method to start a Docker container
    public void startContainer(String containerId) {
        try {
            // Start the container based on the containerId
            dockerClient.startContainerCmd(containerId).exec();

            // If everything goes well, print a message
            System.out.println("Container started: " + containerId);
        } catch (Exception e) {
            // If an error occurs, print the error
            e.printStackTrace();
        }
    }

    // Method to stop a Docker container
    public void stopContainer(String containerId) {
        try {
            // Stop the container based on the containerId
            dockerClient.stopContainerCmd(containerId).exec();

            // If everything goes well, print a message
            System.out.println("Container stopped: " + containerId);
        } catch (Exception e) {
            // If an error occurs, print the error
            e.printStackTrace();
        }
    }

    // Method to execute a Docker container
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
