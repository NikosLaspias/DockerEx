package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
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

    public AppWithContainer(DockerClient dockerClient, String imageName, String containerName) {
        this.dockerClient = dockerClient;
        this.imageName = imageName;
        this.containerName = containerName;
    }

    public AppWithContainer(String dockerHost, String imageName, String containerName) {
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost(dockerHost);
        this.dockerClient = DockerClientBuilder.getInstance(builder.build()).build();
        this.imageName = imageName;
        this.containerName = containerName;
    }

    public void manageContainer() throws InterruptedException {
        // Κατεβάστε την εικόνα αν δεν υπάρχει τοπικά
        dockerClient.pullImageCmd(imageName)
                .exec(new PullImageResultCallback())
                .awaitCompletion();

        // Δημιουργία του container
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imageName)
                .withName(containerName);
        CreateContainerResponse containerResponse = createContainerCmd.exec();
        String id = containerResponse.getId();

        // Εκκίνηση του container
        dockerClient.startContainerCmd(id).exec();

        // Εκτέλεση ενός εντολής μέσα στο container (π.χ., ls)
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(id)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd("ls")
                .exec();

        // Εκτέλεση της εντολής και εκτύπωση των αποτελεσμάτων
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

        // Αφήνουμε λίγο χρόνο για να εκκινήσει το container
        Thread.sleep(5000);

        // Εμφάνιση των ενεργών containers
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        System.out.println("Active Containers");
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));

        // Σταματήστε το container
        dockerClient.stopContainerCmd(id).exec();

        // Περιμένουμε λίγο ξανά
        Thread.sleep(1000);

        // Εμφάνιση των ενεργών containers μετά τον τερματισμό
        System.out.println("-----------");
        containers = dockerClient.listContainersCmd().withShowAll(false).exec();
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));

        // Καθαρισμός του container (διαγραφή)
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
