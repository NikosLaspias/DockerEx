//MeasurementChart: a class that creates a chart from the data
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

    // Constructor to initialize the MeasurementChart
    public MeasurementChart(String title, ContainerMeasurement[] containerMeasurements) {
        super(title);
        this.containerMeasurements = containerMeasurements;

        // Create dataset from measurement data
        CategoryDataset dataset = createDataset();

        // Create the chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Container Measurements", // Chart title
                "Containers", // X-axis label
                "Count", // Y-axis label
                dataset);

        // Add the chart to the panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(560, 370));
        setContentPane(chartPanel);
    }

    // Method to create the dataset from container measurements
    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Add data from the list of measurements
        for (ContainerMeasurement measurement : containerMeasurements) {
            dataset.addValue(1, "Containers", measurement.getId());
        }

        return dataset;
    }

}
