package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.ContainerConfig;

import java.io.IOException;
import java.util.List;

public class Image implements AutoCloseable {

    // Docker client and configuration objects
    private DockerClient dockerClient;
    private DefaultDockerClientConfig config;

    // Constructor to initialize Docker client and configuration
    public Image() {
        // Set Docker host configuration
        this.config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375").build();
        // Build Docker client using the configuration
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    // Method to pull an image if it does not exist locally
    public void pullImageIfNotExists(String imageName) {
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName)
                .exec();

        if (images.isEmpty()) {
            pullImage(imageName);
            System.out.println("Image was not already pulled. Image pulled successfully.");
        } else {
            System.out.println("Image already exists.");
        }
    }

    // Getter method to retrieve Docker host information
    public String getImageName() {
        return config.getDockerHost().getHost();
    }

    // Private method to pull a Docker image
    private void pullImage(String imageName) {
        try {
            dockerClient.pullImageCmd(imageName).exec(null);
            System.out.println("Image pulled successfully.");
        } catch (com.github.dockerjava.api.exception.NotFoundException e) {
            System.err.println("Error pulling image: Image not found - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error pulling image: " + e.getMessage());
        }
    }

    // Method to list Docker images
    public void listImages() {
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().exec();
        System.out.println("List of Docker images:");
        for (com.github.dockerjava.api.model.Image image : images) {
            // Display image information
            System.out.println("Image ID: " + image.getId());
            System.out.println("Repo Tags: " + String.join(", ", image.getRepoTags()));
            System.out.println("Created: " + image.getCreated());
            System.out.println("Size: " + image.getSize());
            System.out.println("------------------------");
        }
    }

    // Method to search for Docker images using a search term
    public void searchImages(String searchTerm) {
        dockerClient.searchImagesCmd(searchTerm).exec().forEach(result -> {
            // Display search results
            System.out.println("Image Name: " + result.getName());
            System.out.println("Description: " + result.getDescription());
            System.out.println("------------------------");
        });
    }

    // Method to inspect a Docker image
    public InspectImageResponse inspectImage(String imageId) {
        return dockerClient.inspectImageCmd(imageId).exec();
    }

    // Method to remove a Docker image
    public void removeImage(String imageId) {
        try {
            dockerClient.removeImageCmd(imageId).exec();
            System.out.println("Image removed successfully.");
        } catch (Exception e) {
            System.err.println("Error removing image: " + e.getMessage());
        }
    }

    // Method to show pull history for a Docker image
    public void showPullHistory(String imageName) {
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName)
                .exec();
        for (com.github.dockerjava.api.model.Image image : images) {
            // Display pull history information
            System.out.println("Pull History for Image ID " + image.getId());
            ContainerConfig containerConfig = inspectImage(image.getId()).getContainerConfig();
            System.out.println("Commands executed during pull: " + containerConfig.getCmd());
            System.out.println("------------------------");
        }
    }

    // AutoCloseable interface method for proper resource cleanup
    @Override
    public void close() {
        // Close the Docker client if it's not null
        if (dockerClient != null) {
            try {
                dockerClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
