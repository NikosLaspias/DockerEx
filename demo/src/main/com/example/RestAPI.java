package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST API controller for handling Docker-related operations.
 *
 * This class provides various endpoints to interact with Docker containers and
 * retrieve measurements.
 */
@RestController
@RequestMapping("/api")
public class RestAPI {

    private final MonitorThread monitorThread;
    private final ExecutorThread executorThread;
    private final Database database;
    private static DockerClient dockerClient;

    // Constructor to initialize RestAPI with necessary threads
    public RestAPI(MonitorThread monitorThread, ExecutorThread executorThread, Database database) {
        this.monitorThread = monitorThread;
        this.executorThread = executorThread;
        this.database = database;
        initDockerClient();
    }

    /**
     * Endpoint for the Initialization of DockerClient.
     */
    @PostMapping("/init-docker-client")
    public ResponseEntity<String> initDockerClient() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375").build();
        dockerClient = DockerClientBuilder.getInstance(config).build();
        return ResponseEntity.ok("DockerClient initialized successfully.");
    }

    // Endpoint to get measurements within a date range
    @GetMapping("/measurements")
    public ResponseEntity<List<Map<String, Object>>> getMeasurements(
            @RequestParam(name = "start_date") String startDate,
            @RequestParam(name = "end_date") String endDate) {
        try {
            // Retrieve measurements from the database
            List<Map<String, Object>> measurements = database.getMeasurementsByDateRange(startDate, endDate);

            if (measurements.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(measurements);
            } else {
                return ResponseEntity.ok(measurements);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/container-stats")
    public ResponseEntity<Map<String, Object>> getContainerStats() {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        long activeCount = containers.stream().filter(c -> c.getState().equalsIgnoreCase("running")).count();
        long inactiveCount = containers.size() - activeCount;

        List<String> activeContainerIds = containers.stream()
                .filter(c -> c.getState().equalsIgnoreCase("running"))
                .map(Container::getId)
                .collect(Collectors.toList());

        List<String> inactiveContainerIds = containers.stream()
                .filter(c -> !c.getState().equalsIgnoreCase("running"))
                .map(Container::getId)
                .collect(Collectors.toList());

        Map<String, Object> statsMap = new HashMap<>();
        statsMap.put("activeCount", activeCount);
        statsMap.put("inactiveCount", inactiveCount);
        statsMap.put("activeContainerIds", activeContainerIds);
        statsMap.put("inactiveContainerIds", inactiveContainerIds);

        return ResponseEntity.ok(statsMap);
    }

    // Endpoint to get ports of a specific Docker container
    @GetMapping("/docker/containers/{containerId}/ports")
    public ResponseEntity<String> getContainerPorts(@PathVariable String containerId) {
        Optional<MonitorThread.ContainerMeasurement> containerMeasurement = monitorThread.getContainerMeasurements()
                .stream()
                .filter(container -> container.getId().equals(containerId))
                .findFirst();

        return containerMeasurement.map(measurement -> ResponseEntity.ok(measurement.getPorts()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint to start a Docker container
    @PostMapping("/docker/containers/start/{containerId}")
    public ResponseEntity<String> startContainer(@PathVariable String containerId) {
        try {
            executorThread.startContainer(containerId);
            return ResponseEntity.ok("Container started: " + containerId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start container: " + containerId);
        }
    }

    // Endpoint to stop a Docker container
    @PostMapping("/docker/containers/stop/{containerId}")
    public ResponseEntity<String> stopContainer(@PathVariable String containerId) {
        try {
            executorThread.stopContainer(containerId);
            return ResponseEntity.ok("Container stopped: " + containerId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to stop container: " + containerId);
        }
    }
}
