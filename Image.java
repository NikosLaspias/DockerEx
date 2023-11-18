import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.List;

public class Image {

    private DockerClient dockerClient;

    public Image() {
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
        // pull an image
        dockerClient.pullImageCmd(imageName).exec();
    }

    public void listImages() {
        List<Image> images = dockerClient.listImagesCmd().exec();
        System.out.println("List of Docker images:");
        for (Image image : images) {
            System.out.println("Image ID: " + image.getId());
            System.out.println("Repo Tags: " + image.getRepoTags());
            System.out.println("Created: " + image.getCreated());
            System.out.println("Size: " + image.getSize());
            System.out.println("------------------------");
        }
    }

    public void searchImages(String searchTerm) {
        // search an image
        dockerClient.searchImagesCmd(searchTerm).exec();
    }

    public Image inspectImage(String imageId) {
        return dockerClient.inspectImageCmd(imageId).exec();
    }

    public void removeImage(String imageId) {
        // remove an image
        dockerClient.removeImageCmd(imageId).exec();
        System.out.println("Image removed successfully.");
    }

    public void showPullHistory(String imageName) {
        // show pull history of an image
        List<Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName).exec();
        for (Image image : images) {
            System.out.println("Pull History for Image ID " + image.getId());
            System.out.println("History: " + image.getHistory());
            System.out.println("------------------------");
        }
    }

    public static void main(String[] args) {
        DockerImageOperations dockerOperations = new DockerImageOperations();
        String imageNameToPull = "your_image_name";

        dockerOperations.pullImageIfNotExists(imageNameToPull);
        dockerOperations.listImages();
        dockerOperations.searchImages("search_term");
        dockerOperations.inspectImage("image_id");
        dockerOperations.removeImage("image_id");
        dockerOperations.showPullHistory(imageNameToPull);
    }
}
