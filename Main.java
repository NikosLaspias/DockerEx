package com.example;
public class Main {
    
    public static void main(String[] args) {
        Image dockerOperations = new Image();
        String imageNameToPull = "your_image_name"; // Πρέπει να αντικατασταθεί με το όνομα της εικόνας που θέλετε να
                                                    // πραγματοποιήσετε ενέργειες

        dockerOperations.searchImages("search_term");
        dockerOperations.inspectImage("image_id");
        dockerOperations.removeImage("image_id");
        dockerOperations.showPullHistory(imageNameToPull);
    }
}