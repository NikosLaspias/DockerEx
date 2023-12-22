package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RestAPI {

    private final MonitorThread monitorThread;
    private final ExecutorThread executorThread;
    private final Database database;

    public RestAPI(MonitorThread monitorThread, ExecutorThread executorThread, Database database) {
        this.monitorThread = monitorThread;
        this.executorThread = executorThread;
        this.database = database;
    }

    @GetMapping("/measurements")
    public ResponseEntity<List<MonitorThread.ContainerMeasurement>> getMeasurements(
            @RequestParam(name = "start_date") String startDate,
            @RequestParam(name = "end_date") String endDate) {
        List<MonitorThread.ContainerMeasurement> measurements = database.getMeasurementsByDateRange(startDate, endDate);

        if (measurements.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(measurements);
        } else {
            return ResponseEntity.ok(measurements);
        }
    }

    @GetMapping("/docker/instances")
    public ResponseEntity<Map<String, List<String>>> getDockerInstances() {
        List<String> activeInstances = monitorThread.getActiveDockerInstancesList();
        List<String> inactiveInstances = monitorThread.getInactiveDockerInstancesList();

        Map<String, List<String>> instancesMap = new HashMap<>();
        instancesMap.put("active", activeInstances);
        instancesMap.put("inactive", inactiveInstances);

        return ResponseEntity.ok(instancesMap);
    }

    @GetMapping("/docker/active-instances")
    public ResponseEntity<Integer> getActiveDockerInstances() {
        int activeInstances = monitorThread.getActiveDockerInstances();
        return ResponseEntity.ok(activeInstances);
    }

    @GetMapping("/docker/inactive-instances")
    public ResponseEntity<Integer> getInactiveDockerInstances() {
        int inactiveInstances = monitorThread.getInactiveDockerInstances();
        return ResponseEntity.ok(inactiveInstances);
    }

    @GetMapping("/docker/containers/{containerId}/ports")
    public ResponseEntity<String> getContainerPorts(@PathVariable String containerId) {
        Optional<MonitorThread.ContainerMeasurement> containerMeasurement = monitorThread.getContainerMeasurements()
                .stream()
                .filter(container -> container.getId().equals(containerId))
                .findFirst();

        return containerMeasurement.map(measurement -> ResponseEntity.ok(measurement.getPorts()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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
