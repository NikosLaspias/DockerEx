package com.example;

import java.util.ArrayList;
import java.util.List;

public class MonitorThread extends Thread {
    private List<TopologyMeasurement> measurements = new ArrayList<>();

    public void run() {
        while (true) {
            // Υποθέτουμε ότι εδώ γίνονται μετρήσεις της τοπολογίας και επιστρέφεται ένας
            // δείκτης απόδοσης
            double performanceMetric = measureTopology();

            // Καταγραφή της μέτρησης
            recordMeasurement(performanceMetric);

            // Υποθέτουμε ότι ο Monitor Thread κοιμάται για ένα διάστημα πριν κάνει την
            // επόμενη μέτρηση
            try {
                Thread.sleep(5000); // Κοιμάται για 5 δευτερόλεπτα
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private double measureTopology() {
        // Υποθέτουμε ότι γίνονται μετρήσεις της τοπολογίας και επιστρέφεται ένας
        // δείκτης απόδοσης
        return Math.random(); // Προσομοίωση μετρήσεων
    }

    private synchronized void recordMeasurement(double performanceMetric) {
        String timestamp = getCurrentTimestamp();
        TopologyMeasurement measurement = new TopologyMeasurement(timestamp, performanceMetric);
        measurements.add(measurement);
    }

    private String getCurrentTimestamp() {
        // Υποθέτουμε ότι εδώ γίνεται η λήψη του τρέχοντος χρόνου
        // Εξαρτάται από την υλοποίηση του συστήματός σας
        return String.valueOf(System.currentTimeMillis());
    }

    public List<TopologyMeasurement> getMeasurements() {
        return measurements;
    }
}

class TopologyMeasurement {
    private String timestamp;
    private double performanceMetric;

    public TopologyMeasurement(String timestamp, double performanceMetric) {
        this.timestamp = timestamp;
        this.performanceMetric = performanceMetric;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getPerformanceMetric() {
        return performanceMetric;
    }
}
