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

import java.util.List;
import java.util.Scanner;

public class AppWithContainer {
    private static String imageName;
    private static String containerName;

    public static void main(String[] args) throws InterruptedException {
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        builder.withDockerHost("tcp://localhost:2375");

        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        dockerClient.versionCmd().exec();

        Scanner input = new Scanner(System.in);
        boolean state = false;
        do {
            try {
                // Εισαγωγή του ονόματος της εικόνας
                System.out.print("Enter an image name: ");
                imageName = input.next();
                state = true;
            } catch (java.util.InputMismatchException e) {
                System.err.println("Invalid image name.Please try again ");
                input.next();
            }
        } while (!state);
        input.close();
        // Κατεβάστε την εικόνα αν δεν υπάρχει τοπικά
        dockerClient.pullImageCmd(imageName)
                .exec(new PullImageResultCallback())
                .awaitCompletion();

        // Εισαγωγή του ID ή ονόματος του container
        System.out.print("Please enter the container ID that you want to control: ");
        boolean st = false;
        do {
            try {
                // ID ή όνομα του container που θέλετε να ελέγξετε
                System.out.println("Please enter the container ID that you want to control:");
                String containerId = input.next();
                st = true;
            } catch (java.util.InputMismatchException e) {
                System.err.println("Invalid container ID.Please try again ");
                input.next();
            }
        } while (!st);
        input.close();

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
                .exec(new ExecStartResultCallback(System.out, System.err))
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
        String response = input.next();
        if (response.equals("Y")) {
            dockerClient.removeContainerCmd(id).exec();
        } else {
            System.exit(0);
        }
    }
}
