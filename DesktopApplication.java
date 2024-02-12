package com.example;

import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * The main class representing the Desktop Application for Docker Cluster
 * Management.
 *
 * This class extends the JavaFX Application class and serves as the entry point
 * for the application.
 */

public class DesktopApplication extends Application {
    private ComboBox<String> optionsComboBox;

    // Fields for the primary stage and menu text area
    private Stage primaryStage;
    private TextArea menuTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Docker Cluster Management");
        this.primaryStage = primaryStage;
        VBox root = new VBox(); // Use VBox for vertical layout
        root.setAlignment(Pos.TOP_LEFT);
        root.setSpacing(10); // Spacing between components
        root.setPadding(new Insets(10));

        // Create the title Text with dark blue color
        Text titleText = new Text("DOCKER CLUSTER MANAGEMENT");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24)); // Bold 24pt font
        titleText.setFill(Color.DARKBLUE);

        // Load the background image
        Image backgroundImage = new Image(
                "https://blog.redigit.es/wp-content/uploads/2019/08/tecnologias-virtualizacion-contenedores-imagen-contenido-7-blog-redigit.jpg");

        // Set the background image to the root
        root.setBackground(new Background(new BackgroundImage(
                backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true))));

        // Add the title to the root layout
        root.getChildren().add(titleText);

        // Add a ComboBox for user selection
        optionsComboBox = new ComboBox<>();
        for (int i = 1; i <= 8; i++) {
            optionsComboBox.getItems().add("Option " + i);
        }

        // Create the label
        Label menuLabel = createMenuLabel();

        // Add a button to trigger the selected option
        Button selectButton = new Button("Select");
        selectButton.setOnAction(event -> handleOption(optionsComboBox.getValue()));

        // Add UI elements to the root layout
        root.getChildren().addAll(menuLabel, optionsComboBox, selectButton);

        // Create and set the scene for the primary stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createMenuLabel() {
        Label menuLabel = new Label(
                "--We provide you the menu to select the state that you prefer--\n" +
                        "1.Create a container and check the list with the status with an optional id\n" +
                        "2.Start-Stop-Execute a container based on id\n" +
                        "3.Combination of containers and images\n" +
                        "4.Conduct actions in Images\n" +
                        "5.Monitoring Container-Create a bar chart with the statistics\n" +
                        "6.Connect with the database of your choice to insert the measurements\n" +
                        "7.Rest API for the handling of containers\n" +
                        "8.Exit\n" +
                        "Enter your choice: ");
        menuLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 16; " +
                        "-fx-effect: dropshadow(three-pass-box, darkblue, 10, 0, 0, 0);");
        return menuLabel;
    }

    // Method to handle the selected option
    private void handleOption(String option) {
        // Check if the selected option is null
        if (option == null) {
            showAlert("Please select an option.");
            return;
        }

        // Parse the selected option number
        int optionNumber = Integer.parseInt(option.split(" ")[1]);

        try {
            // Call the appropriate handler based on the selected option
            handleOption(optionNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to handle the selected option number
    private void handleOption(int option) throws InterruptedException {
        // Switch statement to determine the action based on the selected option
        switch (option) {
            case 1:
                new ContainerManager().handleOption1();
                break;
            case 2:
                new ExecutorManager().handleOption2();
                break;
            case 3:
                new AppManager().handleOption3();
                break;
            case 4:
                new ImageManager(menuTextArea).handleOption4();
                break;
            case 5:
                new MonitorManager().handleOption5();
                break;
            case 6:
                new DatabaseManager().handleOption6();
                break;
            case 7:
                // Create and handle the RestController for Option 7
                RestControler restOptionHandler = new RestControler(
                        new RestAPI(new MonitorThread(), new ExecutorThread(), new Database()), primaryStage);
                restOptionHandler.handleOption7();
                break;

            case 8:
                // Handle Option 8 (Exit)
                handleOption8();
                break;
            default:
                // Show an alert for an invalid choice
                showAlert("Invalid choice. Please try again.");
        }
    }

    // Method to display an information alert
    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to handle Option 8 (Exit)
    private void handleOption8() {
        // Create an alert to display "Goodbye"
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Goodbye!");
        alert.setHeaderText(null);
        alert.setContentText("Exiting the program. Goodbye!");
        // Show the alert and wait for it to be closed
        alert.showAndWait();
        // Close the JavaFX application
        Platform.exit();
        System.exit(0);
    }
}
