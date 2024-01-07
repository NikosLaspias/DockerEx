package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.model.Frame;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

public class AppWithContainer {

    private final DockerClient dockerClient;
    private final String imageName;
    private final String containerName;

    // Constructor
    public AppWithContainer(DockerClient dockerClient, String imageName, String containerName) {
        this.dockerClient = dockerClient;
        this.imageName = imageName;
        this.containerName = containerName;
    }

    // Methods to get the name of the image and the id of container
    public String getImageName() {
        return this.imageName;
    }

    public String getContainerId() {
        return this.containerName;
    }

    public AppWithContainer(String dockerHost, String imageName, String containerName) {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost).build();
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
        this.imageName = imageName;
        this.containerName = containerName;
    }

    public void manageContainer() throws InterruptedException {
        // Download image if not exists locally
        dockerClient.pullImageCmd(imageName)
                .exec(new PullImageResultCallback())
                .awaitCompletion();

        // Container creation
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imageName)
                .withName(containerName);
        CreateContainerResponse containerResponse = createContainerCmd.exec();
        String id = containerResponse.getId();

        // Container start
        dockerClient.startContainerCmd(id).exec();

        // Executing a command inside the container (e.g., ls)
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(id)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd("ls")
                .exec();

        // Execute the command and print the results
        dockerClient.execStartCmd(execCreateCmdResponse.getId())
                .exec(new com.github.dockerjava.api.async.ResultCallback.Adapter<Frame>() {
                    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

                    @Override
                    public void onNext(Frame frame) {
                        if (frame != null) {
                            try {
                                switch (frame.getStreamType()) {
                                    case STDOUT:
                                    case RAW:
                                        System.out.write(frame.getPayload());
                                        System.out.flush();
                                        break;
                                    case STDERR:
                                        System.err.write(frame.getPayload());
                                        System.err.flush();
                                        break;
                                    default:
                                        LOGGER.error("unknown stream type:" + frame.getStreamType());
                                }
                            } catch (IOException e) {
                                onError(e);
                            }

                            LOGGER.debug(frame.toString());
                        }
                    }
                })
                .awaitCompletion();

        // Allow some time for the container to start
        Thread.sleep(5000);

        // List of active containers
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        showAlert("Active Containers", getContainerInfo(containers));

        // Container stop
        dockerClient.stopContainerCmd(id).exec();

        // Allow some time again
        Thread.sleep(1000);

        // Show active containers after close
        showAlert("Active Containers After Stop",
                getContainerInfo(dockerClient.listContainersCmd().withShowAll(false).exec()));

        // Delete the Container after a question
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Delete Container");
            dialog.setHeaderText(null);
            dialog.setContentText("Do you want to delete the container? (Valid responses Y or N)");

            dialog.showAndWait().ifPresent(response -> {
                if (response.equals("Y")) {
                    dockerClient.removeContainerCmd(id).exec();
                    showAlert("Container Deleted", "Container deleted successfully.");
                } else {
                    System.exit(0);
                }
            });
        });
    }

    private String getContainerInfo(List<Container> containers) {
        // Create a StringBuilder for collecting information about containers
        StringBuilder info = new StringBuilder("Container Info:\n");

        // Add information for each container to the StringBuilder
        containers.forEach(c -> info.append(c.getId()).append(" ").append(c.getState()).append("\n"));

        // Return the String containing information about containers
        return info.toString();
    }

    // Display an Alert with only content text
    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}