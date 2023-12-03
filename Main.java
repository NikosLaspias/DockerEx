package com.example;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        // Δημιουργία και χρήση της κλάσης AppWithContainer
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter the Docker image name:");
            String imageName = scanner.nextLine();

            System.out.println("Enter the Docker container ID:");
            String containerId = scanner.nextLine();

            AppWithContainer app = new AppWithContainer("tcp://localhost:2375", imageName, containerId);
            app.manageContainer();
        }

        // Εργασίες πάνω στις εικόνες Docker
        try (Image dockerOperations = new Image()) {
            String imageName = dockerOperations.getImageName();
            dockerOperations.searchImages(imageName);
            System.out.println("Attempting to pull image: " + imageName);
            dockerOperations.searchImages(imageName);
            dockerOperations.inspectImage(imageName);
            dockerOperations.removeImage(imageName);
            dockerOperations.showPullHistory(imageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Start the MonitorThread as a separate thread
        Thread monitorThread = new Thread(new MonitorThread());
        monitorThread.start();

        // 2. Use the executorService for executing the MonitorThread at fixed intervals
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            // 3. Get measurements from the MonitorThread dynamically
            List<MonitorThread.ContainerMeasurement> measurements = getMeasurements();

            // 4. Insert measurements into the database
            try (Database database = new Database()) {
                database.insertContainerMeasurements(measurements);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 5. Display the measurement chart
            SwingUtilities.invokeLater(() -> displayMeasurementChart(measurements));
        }, 0, 5, TimeUnit.SECONDS);
    }

    private static List<MonitorThread.ContainerMeasurement> getMeasurements() {
        MonitorThread monitorThread = new MonitorThread();
        Thread thread = new Thread(monitorThread);
        thread.start();

        try {
            // Allow some time for the MonitorThread to collect measurements
            thread.join(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return monitorThread.getContainerMeasurements();
    }

    // Modify the displayMeasurementChart method to accept actual measurements
    private static void displayMeasurementChart(List<MonitorThread.ContainerMeasurement> measurements) {
        // Create the measurement chart with actual measurements
        MeasurementChart chart = new MeasurementChart("Container Measurements",
                measurements.toArray(new MonitorThread.ContainerMeasurement[0]));
        chart.setSize(800, 600);
        chart.setLocationRelativeTo(null);
        chart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        chart.setVisible(true);
    }
}
