package com.example;

import java.time.LocalDateTime;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Database implements AutoCloseable {
    private static int measurementNumber = 1;
    private static final Scanner scanner = new Scanner(System.in);
    private static String databaseURL;
    private static String databaseUser;
    private static String databasePassword;
    private Connection connection;
    private static LocalDateTime currentDateTime;

    // Constructor for creating a database connection
    public Database() {
        inputDatabaseInfo();
        initialize();
    }

    private void inputDatabaseInfo() {
        System.out.println("Enter Database URL:");
        databaseURL = scanner.nextLine();

        System.out.println("Enter Database User:");
        databaseUser = scanner.nextLine();

        System.out.println("Enter Database Password:");
        databasePassword = scanner.nextLine();
    }

    private void initialize() {
        try {
            System.out.println("Connecting to the database...");
            connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close method to close the database connection
    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Method to insert container measurements into the database
    public void insertContainerMeasurements(List<MonitorThread.ContainerMeasurement> measurements) {
        try {
            System.out.println("Successfully connected to the database!");

            // Iterate through measurements and insert each one
            for (MonitorThread.ContainerMeasurement measurement : measurements) {
                insertMeasurement(measurement);
                insertMeasurementData(getMeasurementDate(), getNextMeasurementId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert a single measurement into the database
    private void insertMeasurement(MonitorThread.ContainerMeasurement measurement) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO ContainerMeasurements (container_id, image, status, ports, command) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, measurement.getId());
            preparedStatement.setString(2, measurement.getImage());
            preparedStatement.setString(3, measurement.getStatus());
            preparedStatement.setString(4, measurement.getPorts());
            preparedStatement.setString(5, measurement.getCommand());

            System.out.println("Successfully connected to the database.");

            // Execute the update to insert data into the database
            preparedStatement.executeUpdate();
        }
    }

    // Method to insert measurement data into the database
    public void insertMeasurementData(String measurementDate, int measurementNumber) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO MeasurementData (measurement_date, measurement_number) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, measurementDate);
            preparedStatement.setInt(2, measurementNumber++);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get the current measurement date in a formatted string
    public String getMeasurementDate() {
        currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    // Method to get the next measurement ID
    public int getNextMeasurementId() {
        return measurementNumber++;
    }

    public List<MonitorThread.ContainerMeasurement> getMeasurementsByDateRange(String startDate, String endDate) {
        List<MonitorThread.ContainerMeasurement> measurements = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM ContainerMeasurements WHERE measurement_date BETWEEN ? AND ?")) {

            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Get the results for the container
                    String id = resultSet.getString("container_id");
                    String image = resultSet.getString("image");
                    String status = resultSet.getString("status");
                    String ports = resultSet.getString("ports");
                    String command = resultSet.getString("command");

                    MonitorThread.ContainerMeasurement measurement = new MonitorThread.ContainerMeasurement(
                            id, image, status, ports, command);

                    measurements.add(measurement);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return measurements;
    }

    public void disconnect() {
        try {
            System.out.println("Do you want to disconnect from the database? (y/n)");
            String userInput = scanner.nextLine();

            if (userInput.trim().equalsIgnoreCase("y")) {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Disconnected from the database.");
                } else {
                    System.out.println("Not connected to the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
