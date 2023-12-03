package com.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import com.example.MonitorThread.ContainerMeasurement;

import java.awt.*;

import javax.swing.JFrame;

public class MeasurementChart extends JFrame {

    private ContainerMeasurement[] containerMeasurements;

    public MeasurementChart(String title, ContainerMeasurement[] containerMeasurements) {
        super(title);
        this.containerMeasurements = containerMeasurements;

        // Creation of dataset from the measurement data
        CategoryDataset dataset = createDataset();

        // Δημιουργία του γραφήματος
        JFreeChart chart = ChartFactory.createBarChart(
                "Container Measurements", // Τίτλος γραφήματος
                "Containers", // Ετικέτα άξονα x
                "Count", // Ετικέτα άξονα y
                dataset);

        // Προσθήκη του γραφήματος στο πάνελ
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(560, 370));
        setContentPane(chartPanel);
    }

    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Προσθήκη δεδομένων από τη λίστα των μετρήσεων
        for (ContainerMeasurement measurement : containerMeasurements) {
            dataset.addValue(1, "Containers", measurement.getId());
        }

        return dataset;
    }

}
