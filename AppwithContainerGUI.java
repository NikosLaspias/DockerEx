import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppWithContainerGUI extends Application {

    private AppWithContainer appWithContainer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Docker Container Manager");

        // Create UI components
        Button createButton = new Button("Create Container");
        Button startButton = new Button("Start Container");
        Button stopButton = new Button("Stop Container");
        Button deleteButton = new Button("Delete Container");

        // Set actions for the buttons
        createButton.setOnAction(e -> createContainer());
        startButton.setOnAction(e -> startContainer());
        stopButton.setOnAction(e -> stopContainer());
        deleteButton.setOnAction(e -> deleteContainer());

        // Layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(createButton, startButton, stopButton, deleteButton);
        layout.setPadding(new Insets(10));

        // Scene
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();
    }

    private void createContainer() {
        TextInputDialog containerDialog = new TextInputDialog();
        containerDialog.setTitle("Container Creation");
        containerDialog.setHeaderText(null);
        containerDialog.setContentText("Enter the name of the container:");

        // Get user input for container name
        containerDialog.showAndWait().ifPresent(containerName -> {
            // Get user input for image name or use a default image
            TextInputDialog imageDialog = new TextInputDialog("your-docker-image");
            imageDialog.setTitle("Image Selection");
            imageDialog.setHeaderText(null);
            imageDialog.setContentText("Enter the name of the Docker image:");

            // Get user input for image name
            imageDialog.showAndWait().ifPresent(imageName -> {
                // Now you can use the containerName and imageName to create the container
                appWithContainer = new AppWithContainer("tcp://localhost:2375", imageName, containerName);

                try {
                    appWithContainer.manageContainer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void startContainer() {
        if (appWithContainer != null) {
            appWithContainer.startContainer();
        } else {
            System.out.println("Container not created. Please create a container first.");
        }
    }

    private void stopContainer() {
        if (appWithContainer != null) {
            appWithContainer.stopContainer();
        } else {
            System.out.println("Container not created. Please create a container first.");
        }
    }

    private void deleteContainer() {
        if (appWithContainer != null) {
            appWithContainer.deleteContainer();
        } else {
            System.out.println("Container not created. Please create a container first.");
        }
    }
}
