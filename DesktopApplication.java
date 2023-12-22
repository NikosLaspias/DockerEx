package com.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class DesktopApplication extends Application {
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Docker Cluster Management");
        VBox root = new VBox();
        root.setSpacing(10);
        root.setAlignment(Pos.CENTER);

        TextArea menuTextArea = new TextArea();
        menuTextArea.setEditable(false);
        menuTextArea.setWrapText(true);
        menuTextArea.setText(
                "DOCKER CLUSTER MANAGEMENT\n" +
                        "--We provide you the menu to select the state that you prefer--\n" +
                        "--1.Create a container and check the list with the status with an optional  id--\n" +
                        "--2.Start-stop-execute a container based on id--\n" +
                        "--3.Create a container based on image--\n" +
                        "--4.Conduct actions in Images--\n" +
                        "--5.Monitoring Container-Create a bar chart with the statistics\n" +
                        "--6.Connect with the database of your choice to insert the measurements\n" +
                        "--7.Rest API for the handling of containers-\n" +
                        "--8. Exit--\n" +
                        "Enter your choice: ");

        // Add a ComboBox for user selection
        ComboBox<String> optionsComboBox = new ComboBox<>();
        for (int i = 1; i <= 8; i++) {
            optionsComboBox.getItems().add("Option " + i);
        }

        Button selectButton = new Button("Select");
        selectButton.setOnAction(event -> handleOption(optionsComboBox.getValue()));

        root.getChildren().addAll(menuTextArea, optionsComboBox, selectButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleOption(String option) {
        if (option == null) {
            showAlert("Please select an option.");
            return;
        }

        int optionNumber = Integer.parseInt(option.split(" ")[1]);

        try {
            handleOption(optionNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleOption(int option) throws InterruptedException {
        switch (option) {
            case 1:
                handleOption1();
                break;
            case 2:
                handleOption2();
                break;
            case 3:
                handleOption3();
                break;
            case 4:
                handleOption4();
                break;
            case 5:
                handleOption5();
                break;
            case 6:
                handleOption6();
                break;
            case 7:
                handleOption7();
                break;
            case 8:
                handleOption8();
                break;
            default:
                showAlert("Invalid choice. Please try again.");
        }
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleOption1() {
        System.out.println("Handling Option 1: Create a container and check the list with status with an optional id");
        ContainerCreation.manageContainers();
    }

    private void handleOption2() {
        System.out.println("Handling Option 2: Start-stop-execute a container based on id");
        try {
            ExecutorThread executorThread = new ExecutorThread();
            executorThread.executeContainer();
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Insert the container id that you want to start: ");
                String containerId = scanner.nextLine();
                executorThread.startContainer(containerId);
                System.out.print("Insert the container id that you want to stop: ");
                executorThread.stopContainer(containerId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleOption3() throws InterruptedException {
        System.out.println("Handling Option 3: Create a container based on image");
        // Create and use the AppWithContainer class
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter the Docker image name:");
            String imageName = scanner.nextLine();

            System.out.println("Enter the Docker container ID:");
            String containerId = scanner.nextLine();

            // Create an instance of AppWithContainer and manage the container
            AppWithContainer app = new AppWithContainer("tcp://localhost:2375", imageName, containerId);
            app.manageContainer();
        }
    }

    private void handleOption4() {
        System.out.println("Handling Option 4: Conduct actions in Images");
        // Docker image operations
        try (Image dockerOperations = new Image()) {
            // Perform various Docker image operations
            String imageName = dockerOperations.getImageName();
            dockerOperations.searchImages(imageName);
            System.out.println("Attempting to pull image: " + imageName);
            dockerOperations.searchImages(imageName);
            dockerOperations.inspectImage(imageName);
            dockerOperations.removeImage(imageName);
            dockerOperations.showPullHistory(imageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleOption5() {
        System.out.println("Handling Option 5: Monitoring Container - Create a chart");
        Thread monitorThread = new Thread(new MonitorThread());
        monitorThread.start();
        // Use the executorService for executing the MonitorThread at fixed intervals
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            // Get measurements from the MonitorThread dynamically
            List<MonitorThread.ContainerMeasurement> measurements = getMeasurements();
            // Display the measurement chart only if isRunning is true
            if (isRunning) {
                SwingUtilities.invokeLater(() -> displayMeasurementChart(measurements));
                isRunning = false;
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    // Existing methods...

    // Method to get measurements from the MonitorThread
    private static List<MonitorThread.ContainerMeasurement> getMeasurements() {
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

    private void handleOption6() {
        System.out.println("Handling Option 6: Connect with the database to insert the measurements");

        // Get measurements from the MonitorThread dynamically
        List<MonitorThread.ContainerMeasurement> measurements = getMeasurements();

        // Insert measurements into the database
        try (Database database = new Database()) {
            database.insertContainerMeasurements(measurements);
            database.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleOption7() {
        System.out.println("Handling Option 7: Rest API for the handling of containers");

        RestAPI restAPI = new RestAPI(new MonitorThread(), new ExecutorThread(), new Database());

        // Get measurements
        ResponseEntity<List<MonitorThread.ContainerMeasurement>> measurementsResponse = restAPI
                .getMeasurements("start_date_value", "end_date_value");
        List<MonitorThread.ContainerMeasurement> measurements = measurementsResponse.getBody();

        // Get active and inactive instances
        ResponseEntity<Integer> activeInstances = restAPI.getActiveDockerInstances();
        ResponseEntity<Integer> inactiveInstances = restAPI.getInactiveDockerInstances();

        // Start and stop containers
        ResponseEntity<String> startResult = restAPI.startContainer("containerId");
        ResponseEntity<String> stopResult = restAPI.stopContainer("containerId");

        // Print results
        System.out.println("Measurements: " + measurements);
        System.out.println("Active Instances: " + activeInstances);
        System.out.println("Inactive Instances: " + inactiveInstances);
        System.out.println("Start Container Result: " + startResult.getBody());
        System.out.println("Stop Container Result: " + stopResult.getBody());

        // Scheduled task to get measurements periodically
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            restAPI.getMeasurements("start_date",
                    "end_date");

        }, 0, 5, TimeUnit.SECONDS);
    }

    private void handleOption8() {
        System.out.println("Exiting the program. Goodbye!");
        System.exit(0);
    }
}