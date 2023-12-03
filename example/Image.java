package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.ContainerConfig;

import java.io.IOException;
import java.util.List;

public class Image implements AutoCloseable {
    private DockerClient dockerClient;

    public Image() {

        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

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

    public String getImageName() {
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().exec();

        if (!images.isEmpty()) {
            // Select the first image from the list (you may need to customize
            // depending on your needs)
            String imageName = images.get(0).getRepoTags()[0];
            return imageName;
        } else {
            throw new RuntimeException("No Docker images found.");
        }
    }

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

    public void listImages() {
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().exec();
        System.out.println("List of Docker images:");
        for (com.github.dockerjava.api.model.Image image : images) {
            System.out.println("Image ID: " + image.getId());
            System.out.println("Repo Tags: " + String.join(", ", image.getRepoTags()));
            System.out.println("Created: " + image.getCreated());
            System.out.println("Size: " + image.getSize());
            System.out.println("------------------------");
        }
    }

    public void searchImages(String searchTerm) {
        dockerClient.searchImagesCmd(searchTerm).exec().forEach(result -> {
            System.out.println("Image Name: " + result.getName());
            System.out.println("Description: " + result.getDescription());
            System.out.println("------------------------");
        });
    }

    public InspectImageResponse inspectImage(String imageId) {
        return dockerClient.inspectImageCmd(imageId).exec();
    }

    public void removeImage(String imageId) {
        try {
            dockerClient.removeImageCmd(imageId).exec();
            System.out.println("Image removed successfully.");
        } catch (Exception e) {
            System.err.println("Error removing image: " + e.getMessage());
        }
    }

    public void showPullHistory(String imageName) {
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName)
                .exec();
        for (com.github.dockerjava.api.model.Image image : images) {
            System.out.println("Pull History for Image ID " + image.getId());

            ContainerConfig containerConfig = inspectImage(image.getId()).getContainerConfig();

            System.out.println("Commands executed during pull: " + containerConfig.getCmd());
            System.out.println("------------------------");
        }
    }

    @Override
    public void close() {
        if (dockerClient != null) {
            try {
                dockerClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
