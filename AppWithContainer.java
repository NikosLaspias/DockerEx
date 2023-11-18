package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import java.util.Scanner;

import java.util.List;

public class AppWithContainer {
    private static String imageName;

    public static void main(String[] args) throws InterruptedException {
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        dockerClient.versionCmd().exec();

        // Εικόνα που θέλετε να χρησιμοποιήσετε
        Scanner input = new Scanner(System.in);
        boolean state = false;
        do {
            try {
                System.out.print("Enter an image name: ");
                String imageName = input.next();
                state = true;
            } catch (java.util.InputMismatchException e) {
                System.err.println("Invalid imageName.Please try again ");
                input.next();
            }
        } while (!state);
        input.close();

        // Εάν η εικόνα δεν υπάρχει τοπικά, τότε κατεβάστε την
        dockerClient.pullImageCmd(imageName)
                .exec(new PullImageResultCallback())
                .awaitCompletion();

        // Πληροφορίες για το container
        String containerName = "my-container";
        String id;

        // Δημιουργία του container
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imageName)
                .withName(containerName);
        CreateContainerResponse containerResponse = createContainerCmd.exec();
        id = containerResponse.getId();

        // Ξεκίνημα του container
        dockerClient.startContainerCmd(id).exec();

        // Εκτέλεση ενός εντολή μέσα στο container (π.χ., ls)
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(id)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd("ls")
                .exec();

        // Εκτέλεση της εντολής και εκτύπωση των αποτελεσμάτων
        dockerClient.execStartCmd(execCreateCmdResponse.getId())
                .exec(new ExecStartResultCallback(System.out, System.err))
                .awaitCompletion();

        // Περιμένουμε λίγο ώστε να είναι σίγουρο ότι έχει εκκινήσει το container
        Thread.sleep(5000);

        // Εμφάνιση των ενεργών containers
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        System.out.println("Active Containers");
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));

        // Στοπ του container
        dockerClient.stopContainerCmd(id).exec();

        // Περιμένουμε λίγο πάλι
        Thread.sleep(1000);

        // Εμφάνιση των ενεργών containers μετά τον τερματισμό
        System.out.println("-----------");
        containers = dockerClient.listContainersCmd().withShowAll(false).exec();
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));

        // Καθαρισμός του container (διαγραφή)
        dockerClient.removeContainerCmd(id).exec();
    }
}
