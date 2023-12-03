package com.example;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.example.MonitorThread.ContainerMeasurement;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Δημιουργία και διαχείριση containers
        ContainerCreation.manageContainers();
        ExecutorThread.executeContainer();

        // Δημιουργία και χρήση της κλάσης AppWithContainer
        AppWithContainer app = new AppWithContainer("tcp://localhost:2375", getId(), getConatainerId());
        app.manageContainer();

        // Εργασίες πάνω στις εικόνες Docker
        try (Image dockerOperations = new Image()) {
            dockerOperations.searchImages(getId());
            dockerOperations.inspectImage(getId());
            dockerOperations.removeImage(getId());
            dockerOperations.showPullHistory(getId());
        }

        // Εκκίνηση του MonitorThread για τη συλλογή μετρήσεων
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new MonitorThread(), 0, 5, TimeUnit.SECONDS);

        // Εμφάνιση γραφικού πίνακα με τις μετρήσεις
        SwingUtilities.invokeLater(() -> displayMeasurementChart());

        // Εισαγωγή μετρήσεων στη βάση δεδομένων
        Database database = new Database();
        List<MonitorThread.ContainerMeasurement> measurements = getMeasurements();
        database.insertContainerMeasurements(measurements);
        database.closeConnection();
    }

    private static void displayMeasurementChart() {
        ContainerMeasurement[] measurements = {
                new ContainerMeasurement("id1", "image1", "running", "ports1", "command1"),
                new ContainerMeasurement("id2", "image2", "stopped", "ports2", "command2"),

        };

        MeasurementChart chart = new MeasurementChart("Container Measurements", measurements);
        chart.setSize(800, 600);
        chart.setLocationRelativeTo(null);
        chart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        chart.setVisible(true);
    }

    private static List<MonitorThread.ContainerMeasurement> getMeasurements() {
        MonitorThread monitorThread = new MonitorThread();
        monitorThread.run();
        return MonitorThread.getContainerMeasurements(monitorThread.containerMeasurements);
    }

}