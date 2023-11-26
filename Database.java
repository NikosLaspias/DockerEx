package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Database {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String DATABASE_USER = "your_username";
    private static final String DATABASE_PASSWORD = "your_password";

    private Connection connection;

    public Database() {
        try {
            this.connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertContainerMeasurements(List<MonitorThread.ContainerMeasurement> measurements) {
        try {
            for (MonitorThread.ContainerMeasurement measurement : measurements) {
                // Εισαγωγή των μετρήσεων στη βάση δεδομένων
                insertMeasurement(measurement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void insertMeasurement(MonitorThread.ContainerMeasurement measurement) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO ContainerMeasurements (container_id, image, status, ports, command) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, measurement.getId());
            preparedStatement.setString(2, measurement.getImage());
            preparedStatement.setString(3, measurement.getStatus());
            preparedStatement.setString(4, measurement.getPorts());
            preparedStatement.setString(5, measurement.getCommand());

            preparedStatement.executeUpdate();
        }
    }
}
