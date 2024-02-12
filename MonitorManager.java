package com.example;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class MonitorManager {
    public void handleOption5() {
        System.out.println("Handling Option 5: Monitoring Container - Create a chart");

        // Create an instance of MonitorThread
        MonitorThread monitorThread = new MonitorThread();

        // Start the monitorThread to collect measurements
        Thread thread = new Thread(monitorThread);
        thread.start();

        // Wait for the monitorThread to complete-measurements collected
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        monitorThread.displayAllMeasurements();
        // Get measurements from the MonitorThread dynamically
        List<MonitorThread.ContainerMeasurement> measurements = monitorThread.getContainerMeasurements();

        // Display the measurement chart
        SwingUtilities.invokeLater(() -> displayMeasurementChart(measurements));

        try {
            monitorThread.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to get measurements from the MonitorThread
    public static List<MonitorThread.ContainerMeasurement> getMeasurements() {
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

    // Method to display the measurement chart
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