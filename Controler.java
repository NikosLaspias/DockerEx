package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.command.ListVolumesResponse;
import com.github.dockerjava.api.model.Network;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Controler extends Application {
    private MonitorThread monitorThread;
    private DockerClient dockerClient;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        monitorThread = new MonitorThread();
        Thread monitorThreadExecutor = new Thread(monitorThread);
        monitorThreadExecutor.setDaemon(true);
        monitorThreadExecutor.start();
        primaryStage.setTitle("Docker Management");

        VBox root = new VBox();
        root.setSpacing(10);

        // Show Docker Instances
        TableView<String> dockerInstancesTable = new TableView<>();
        Button showRunningButton = new Button("Show Running");
        Button showPausedButton = new Button("Show Paused");

        // Stop and Restart Docker Instances
        Button stopButton = new Button("Stop");
        Button restartButton = new Button("Restart");

        // Show Used Images
        TableView<String> usedImagesTable = new TableView<>();
        Button inspectImageButton = new Button("Inspect Image");
        Button removeImageButton = new Button("Remove Image");

        // Display Disk Volumes, Subnets, Logs
        Button showVolumesButton = new Button("Show Volumes");
        Button showSubnetsButton = new Button("Show Subnets");
        Button showLogsButton = new Button("Show Logs");

        // TextArea for additional details
        TextArea detailsTextArea = new TextArea();
        detailsTextArea.setEditable(false);

        // Set actions for the buttons (implement these methods accordingly)
        showRunningButton.setOnAction(event -> showRunningDockerInstances());
        showPausedButton.setOnAction(event -> showPausedDockerInstances());
        inspectImageButton.setOnAction(event -> inspectSelectedImage());
        removeImageButton.setOnAction(event -> removeSelectedImage());
        showVolumesButton.setOnAction(event -> showVolumes());
        showSubnetsButton.setOnAction(event -> showSubnets());

        root.getChildren().addAll(
                dockerInstancesTable, showRunningButton, showPausedButton,
                stopButton, restartButton, usedImagesTable,
                inspectImageButton, removeImageButton, showVolumesButton,
                showSubnetsButton, showLogsButton, detailsTextArea);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showRunningDockerInstances() {
        List<String> activeInstances = monitorThread.getActiveDockerInstancesList();

        // Print or process the list of active instances as needed
        System.out.println("Active Docker Instances:");
        for (String instance : activeInstances) {
            System.out.println(instance);
        }
    }

    private void showPausedDockerInstances() {
        List<String> inactiveInstances = monitorThread.getInactiveDockerInstancesList();
        System.out.println("Inactive Docker Instances:");
        for (String instance : inactiveInstances) {
            System.out.println(instance);
        }
    }

    private void inspectSelectedImage() {
        // Implement logic to inspect the selected image
    }

    private void removeSelectedImage() {
        // Implement logic to remove the selected image
    }

    private void showVolumes() {
        List<MonitorThread.ContainerMeasurement> containerMeasurements = monitorThread.getContainerMeasurements();

        System.out.println("Volumes:");
        for (MonitorThread.ContainerMeasurement measurement : containerMeasurements) {
            // Implement logic to display volumes for each container
            System.out.println("Container ID: " + measurement.getId());
            ListVolumesResponse volumesResponse = dockerClient.listVolumesCmd().exec();
            List<InspectVolumeResponse> volumes = volumesResponse.getVolumes();
            for (InspectVolumeResponse volume : volumes) {
                System.out.println("Volume Name: " + volume.getName());
            }
            System.out.println("------------------------");
        }
    }

    private void showSubnets() {
        List<MonitorThread.ContainerMeasurement> containerMeasurements = monitorThread.getContainerMeasurements();

        System.out.println("Networks:");
        for (MonitorThread.ContainerMeasurement measurement : containerMeasurements) {
            // Implement logic to display subnets for each container
            System.out.println("Container ID: " + measurement.getId());
            List<Network> networks = dockerClient.listNetworksCmd().exec();
            for (Network network : networks) {
                System.out.println("Network ID: " + network.getId());
                System.out.println("Network Name: " + network.getName());
            }
            System.out.println("------------------------");
        }
    }
}