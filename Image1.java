package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.util.List;

public class Image1 {

    private DockerClient dockerClient;

    public Image1() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    public void pullImageIfNotExists(String imageName) {
        List<Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName).exec();

        if (images.isEmpty()) {
            pullImage(imageName);
            System.out.println("Image was not already pulled. Image pulled successfully.");
        } else {
            System.out.println("Image already exists.");
        }
    }

    private void pullImage(String imageName) {
        ResultCallback<PullResponseItem> callback = dockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback());
        dockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback()).awaitSuccess();
    }

    public void listImages() {
        List<Image> images = dockerClient.listImagesCmd().exec();
        System.out.println("List of Docker images:");
        for (Image image : images) {
            System.out.println("Image ID: " + image.getId());
            System.out.println("Repo Tags: " + image.getRepoTags());
            System.out.println("Created: " + image.getCreated());
            System.out.println("Size: " + image.getSize());

            InspectImageResponse inspectResponse = dockerClient.inspectImageCmd(image.getId()).exec();
            System.out.println("Pull History: " + inspectResponse.getContainerConfig().getCmd());
            System.out.println("------------------------");
        }
    }

    public void searchImages(String searchTerm) {
        List<SearchItem> searchResults = dockerClient.searchImagesCmd(searchTerm).exec();

        System.out.println("Search results for term '" + searchTerm + "':");
        for (SearchItem result : searchResults) {
            System.out.println("Description: " + result.getDescription());
            System.out.println("Name: " + result.getName());
            System.out.println("Is Official: " + result.isOfficial());
            System.out.println("------------------------");
        }
    }

    public InspectImageResponse inspectImage(String imageId) {
        return dockerClient.inspectImageCmd(imageId).exec();
    }

    public void removeImage(String imageId) {
        dockerClient.removeImageCmd(imageId).exec();
        System.out.println("Image removed successfully.");
    }

    public static void main(String[] args) {
        Image1 dockerClient = new Image1();
        String imageNameToPull = "your_image_name";

        dockerClient.pullImageIfNotExists(imageNameToPull);
        dockerClient.listImages();
        dockerClient.searchImages("search_term");
        dockerClient.inspectImage("image_id");
        dockerClient.removeImage("image_id");
    }
}
