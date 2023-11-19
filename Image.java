package com.example;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.ContainerConfig;

import java.util.List;

public class Image {

    private DockerClient dockerClient;

    public Image() {
        // Δημιουργία ενός Docker client κατά την αρχικοποίηση του αντικειμένου Image
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    public void pullImageIfNotExists(String imageName) {
        // Λίστα εικόνων που πληρούν τα κριτήρια του φίλτρου
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName)
                .exec();

        if (images.isEmpty()) {
            // Εάν η λίστα είναι κενή, τότε η εικόνα δεν υπάρχει, οπότε πραγματοποιούμε το
            // pull
            pullImage(imageName);
            System.out.println("Image was not already pulled. Image pulled successfully.");
        } else {
            System.out.println("Image already exists.");
        }
    }

    private void pullImage(String imageName) {
        // Πραγματοποίηση pull μιας εικόνας
        dockerClient.pullImageCmd(imageName).exec(null);
    }

    public void listImages() {
        // Λίστα όλων των εικόνων
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
        // Αναζήτηση εικόνων βάσει ενός όρου
        dockerClient.searchImagesCmd(searchTerm).exec().forEach(result -> {
            System.out.println("Image Name: " + result.getName());
            System.out.println("Description: " + result.getDescription());
            System.out.println("------------------------");
        });
    }

    public InspectImageResponse inspectImage(String imageId) {
        // Επισκόπηση μιας συγκεκριμένης εικόνας
        return dockerClient.inspectImageCmd(imageId).exec();
    }

    public void removeImage(String imageId) {
        // Διαγραφή μιας εικόνας
        dockerClient.removeImageCmd(imageId).exec();
        System.out.println("Image removed successfully.");
    }

    public void showPullHistory(String imageName) {
        // Εμφάνιση του ιστορικού pull μιας εικόνας
        List<com.github.dockerjava.api.model.Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName)
                .exec();
        for (com.github.dockerjava.api.model.Image image : images) {
            System.out.println("Pull History for Image ID " + image.getId());

            // Προσπελαύνουμε τη διαμόρφωση του container που χρησιμοποιήθηκε για το pull
            // της εικόνας
            ContainerConfig containerConfig = dockerClient.inspectImageCmd(image.getId()).exec().getContainerConfig();

            // Εμφανίζουμε τις εντολές (CMD) που εκτελέστηκαν κατά το pull
            System.out.println("Commands executed during pull: " + containerConfig.getCmd());
            System.out.println("------------------------");
        }
    }

}
