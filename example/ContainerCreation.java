package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.command.ListVolumesResponse;

import java.util.List;
import java.util.Scanner;

public class ContainerCreation {
    private static String containerId;

    public static void manageContainers() {
        // Creating a Docker client
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        Scanner input = new Scanner(System.in);
        boolean state = false;
        do {
            try {
                // ID ή όνομα του container που θέλετε να ελέγξετε
                System.out.println("Please enter the container ID that you want to control:");
                containerId = input.next();
                state = true;
            } catch (java.util.InputMismatchException e) {
                System.err.println("Invalid container ID.Please try again ");
                input.next();
            }
        } while (!state);
        input.close();

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

        // Εμφάνιση λίστας ενεργών containers με το αντιστοιχο id και την κατάσταση τους
        System.out.println("Active Containers:");
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        containers.forEach(c -> System.out.println(c.getId() + " " + c.getState()));
        ListVolumesResponse volumesResponse = dockerClient.listVolumesCmd().exec();
        List<InspectVolumeResponse> volumes = volumesResponse.getVolumes();

        for (InspectVolumeResponse volume : volumes) {
            System.out.println("Volume Name: " + volume.getName());
        }
    }
}
