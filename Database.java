package com.example;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The Database class handles the interaction with the database,
 * including establishing connections, inserting measurements, and querying
 * data.
 */

public class Database implements AutoCloseable {
    // Static variables for database connection information
    private static String databaseURL;
    private static String databaseUser;
    private static String databasePassword;

    // Instance variables
    private Connection connection;
    private static LocalDateTime currentDateTime;
    private static int measurementNumber = 1;

    /**
     * Constructor for creating a database connection.
     * Prompts the user for database connection information.
     */
    public Database() {
        // Prompt user for database connection information
        inputDatabaseInfo();
        // Initialize the database connection
        initialize();
    }

    // Method to prompt the user for database connection information
    private void inputDatabaseInfo() {
        // Use TextInputDialog to get database URL, user, and password
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Database Connection");
        dialog.setHeaderText("Enter Database Connection Information");

        dialog.setContentText("Database URL:");
        Optional<String> urlResult = dialog.showAndWait();
        urlResult.ifPresent(url -> databaseURL = url);

        dialog.setContentText("Database User:");
        Optional<String> userResult = dialog.showAndWait();
        userResult.ifPresent(user -> databaseUser = user);

        dialog.setContentText("Database Password:");
        Optional<String> passwordResult = dialog.showAndWait();
        passwordResult.ifPresent(password -> databasePassword = password);
    }

    /**
     * Initializes the database connection.
     */
    private void initialize() {
        try {
            System.out.println("Connecting to the database...");
            // Establish the database connection
            connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
            showAlert("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error connecting to the database. Please check your database credentials.");
        }
    }

    // Close method to close the database connection
    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Inserts container measurements into the database.
     *
     * @param measurements The list of container measurements to be inserted.
     */

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

    /**
     * Inserts a single measurement into the database.
     *
     * @param measurement The container measurement to be inserted.
     * @throws SQLException If a database access error occurs.
     */

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
            preparedStatement.setInt(2, measurementNumber);

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

    public int getNextMeasurementId() {
        return measurementNumber++;
    }

    // Method to retrieve measurements within a date range
    public List<Map<String, Object>> getMeasurementsByDateRange(String startDate, String endDate) {
        List<Map<String, Object>> measurements = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT measurement_date, measurement_id FROM MeasurementData WHERE measurement_date BETWEEN ? AND ?")) {

            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> measurementInfo = new HashMap<>();
                    measurementInfo.put("measurement_date", resultSet.getString("measurement_date"));
                    measurementInfo.put("measurement_id", resultSet.getInt("measurement_id"));

                    measurements.add(measurementInfo);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return measurements;
    }

    /**
     * Disconnects from the database.
     */

    public void disconnect() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Disconnect from Database");
            alert.setHeaderText(null);
            alert.setContentText("Do you want to disconnect from the database?");

            if (alert.showAndWait().orElse(null) == ButtonType.OK) {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    showAlert("Disconnected from the database.");
                } else {
                    showAlert("Not connected to the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to display an information alert

    /**
     * Displays an information alert using JavaFX.
     *
     * @param content The content text of the alert.
     */
    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
