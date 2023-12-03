package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.model.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AppWithContainer {

    private final DockerClient dockerClient;
    private final String imageName;
    private final String containerName;

    // Get methods
    public AppWithContainer(DockerClient dockerClient, String imageName, String containerName) {
        this.dockerClient = dockerClient;
        this.imageName = imageName;
        this.containerName = containerName;
    }

    public String getImageName() {
        return this.imageName;
    }

    public String getContainerId() {
        return this.containerName;
    }

    public AppWithContainer(String dockerHost, String imageName, String containerName) {
        this.dockerClient = DockerClientBuilder.getInstance().build();
        this.imageName = imageName;
        this.containerName = containerName;
    }

    public void manageContainer() throws InterruptedException {
        // Download the image if it is not available locally
        dockerClient.pullImageCmd(imageName)
                .exec(new PullImageResultCallback())
                .awaitCompletion();

        // Creating the container
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imageName)
                .withName(containerName);
        CreateContainerResponse containerResponse = createContainerCmd.exec();
        String id = containerResponse.getId();

        // Starting the container
        dockerClient.startContainerCmd(id).exec();

        // Execute a command inside the container (e.g., ls)
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(id)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd("ls")
                .exec();

        // Execute the command and print the results
        dockerClient.execStartCmd(execCreateCmdResponse.getId())
                .exec(new com.github.dockerjava.api.async.ResultCallback.Adapter<Frame>() {
                    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

                    @Override
                    public void onNext(Frame frame) {
                        if (frame != null) {
                            try {
                                switch (frame.getStreamType()) {
                                    case STDOUT:
                                    case RAW:
                                        System.out.write(frame.getPayload());
                                        System.out.flush();
                                        break;
                                    case STDERR:
                                        System.err.write(frame.getPayload());
                                        System.err.flush();
                                        break;
                                    default:
                                        LOGGER.error("unknown stream type:" + frame.getStreamType());
                                }
                            } catch (IOException e) {
                                onError(e);
                            }

                            LOGGER.debug(frame.toString());
                        }
                    }
                })
                .awaitCompletion();

        // Allow some time for the container to start up
        Thread.sleep(5000);

        // Show active containers
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        System.out.println("Active Containers");
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));

        // Stop the container
        dockerClient.stopContainerCmd(id).exec();

        // We wait a while again
        Thread.sleep(1000);

        // Show active containers after the termination
        System.out.println("-----------");
        containers = dockerClient.listContainersCmd().withShowAll(false).exec();
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));

        // Cleaning the container (deletion)
        System.out.println("Do you want to delete the container?");
        System.out.println("Valid responses Y or N");
        try (Scanner input = new Scanner(System.in)) {
            String response = input.next();
            if (response.equals("Y")) {
                dockerClient.removeContainerCmd(id).exec();
            } else {
                System.exit(0);
            }
        }
    }
}
