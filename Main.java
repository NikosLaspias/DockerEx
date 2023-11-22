package com.example;

public class Main {

    public static void main(String[] args) {
        ContainerCreation.manageContainers();
        Image dockerOperations = new Image();
        String imageNameToPull = "your_image_name"; 

        dockerOperations.searchImages("search_term");
        dockerOperations.inspectImage("image_id");
        dockerOperations.removeImage("image_id");
        dockerOperations.showPullHistory(imageNameToPull);


        MonitorThread monitorThread = new MonitorThread();
        monitorThread.start();

    }
}