//MonitorManager: a class that manages the monitor thread
//Copyright(C) 2023/24 Eleutheria Koutsiouri
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.

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

        // Wait for the monitorThread to complete (measurements collected)
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

        // Clean up resources
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
