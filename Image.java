package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.ListImagesCmd;

import java.util.List;

public class Image {

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

    private void pullImage(String imageName) {
        // pull an image
        dockerClient.pullImageCmd(imageName).exec(null);
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
        // search an image
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
        // remove an image
        dockerClient.removeImageCmd(imageId).exec();
        System.out.println("Image removed successfully.");
    }

    public void showPullHistory(String imageName) {
        // show pull history of an image
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName)
                .exec();
        for (com.github.dockerjava.api.model.Image image : images) {
            System.out.println("Pull History for Image ID " + image.getId());
            ((Object) dockerClient).historyCmd(image.getId()).exec().forEach(historyItem -> {
                System.out.println("History: " + historyItem.getCreatedBy());
                System.out.println("------------------------");
            });
        }
    }

    public static void main(String[] args) {
        Image dockerOperations = new Image();
        String imageNameToPull = "your_image_name";

        dockerOperations.searchImages("search_term");
        dockerOperations.inspectImage("image_id");
        dockerOperations.removeImage("image_id");
        dockerOperations.showPullHistory(imageNameToPull);
    }
}
