import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class ContainerCreationGUI extends Application {

    private ContainerCreation containerCreation;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Docker Container Management");

        // Create UI components
        Label label = new Label("Enter the ID of the container:");

        TextField containerIdField = new TextField();
        Button manageButton = new Button("Manage Container");

        // Set actions for the button
        manageButton.setOnAction(e -> manageContainer(containerIdField.getText()));

        // Layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, containerIdField, manageButton);
        layout.setPadding(new Insets(10));

        // Scene
        Scene scene = new Scene(layout, 300, 150);
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();
    }

    private void manageContainer(String containerId) {
        containerCreation = new ContainerCreation(containerId);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Container Management");
        alert.setHeaderText(null);
        alert.setContentText("Choose an action:");

        ButtonType startButton = new ButtonType("Start Container");
        ButtonType stopButton = new ButtonType("Stop Container");
        ButtonType showInfoButton = new ButtonType("Show Container Info");
        ButtonType showActiveContainersButton = new ButtonType("Show Active Containers");

        alert.getButtonTypes().setAll(startButton, stopButton, showInfoButton, showActiveContainersButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == startButton) {
                containerCreation.startContainer();
            } else if (result.get() == stopButton) {
                containerCreation.stopContainer();
            } else if (result.get() == showInfoButton) {
                containerCreation.showContainerInfo();
            } else if (result.get() == showActiveContainersButton) {
                containerCreation.showActiveContainers();
            }
        }
    }
}