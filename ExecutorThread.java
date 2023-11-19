package com.example;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import java.io.IOException;

public class ExecutorThread {

    public static void main(String[] args) throws IOException {
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        // Create a thread to start a Docker container
        Thread startThread = new Thread(() -> {
            // Define the configuration for the container
            ExposedPort tcp80 = ExposedPort.tcp(80);

            // Use PortBinding.parse to set the IP address and host port
            PortBinding portBinding = PortBinding.parse("0.0.0.0:8080");

            HostConfig hostConfig = new HostConfig();
            hostConfig.withPortBindings(portBinding);

            // Create the container
            CreateContainerResponse container = createAndStartContainer(dockerClient, tcp80, hostConfig);

            // Stop the container (replace "CONTAINER_ID" with the actual container ID)
            dockerClient.stopContainerCmd(container.getId()).exec();
        });

        // Start the thread
        startThread.start();

        // Wait for the thread to finish (optional)
        try {
            startThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static CreateContainerResponse createAndStartContainer(DockerClient dockerClient, ExposedPort exposedPort,
            HostConfig hostConfig) {
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
