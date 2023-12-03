package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Database implements AutoCloseable {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/MySQL80";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "Ele131104@";

    private Connection connection;

    public Database() throws SQLException {
        this.connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    // Other methods in your Database class...

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void insertContainerMeasurements(List<MonitorThread.ContainerMeasurement> measurements) {
        try {
            for (MonitorThread.ContainerMeasurement measurement : measurements) {
                insertMeasurement(measurement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
