package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;

import java.util.List;
import java.util.Scanner;

public class ContainerCreation {
    public static void main(String[] args) {
        // Δημιουργία ενός Docker client
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        // ID ή όνομα του container που θέλετε να ελέγξετε
        System.out.println("Please enter the container ID that you want to control:");
        Scanner input = new Scanner(System.in);
        String containerId = input.next();

        // Έλεγχος αν ο container είναι εκκινημένος
        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        Boolean isRunning = containerInfo.getState().getRunning();

        if (!isRunning) {
            // Εκκίνηση του container
            StartContainerCmd startCmd = dockerClient.startContainerCmd(containerId);
            startCmd.exec();
            System.out.println("Container started.");
        } else {
            // Διακοπή του container
            StopContainerCmd stopCmd = dockerClient.stopContainerCmd(containerId);
            stopCmd.exec();
            System.out.println("Container stopped.");
        }

        // Εμφάνιση λίστας ενεργών containers
        System.out.println("Active Containers:");
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));
    }
}
