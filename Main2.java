
package com.example;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main2 {
    private static volatile boolean isRunning = true;
    private static volatile boolean listState = true;

    public static void main(String[] args) throws IOException, InterruptedException {

        // Δημιουργία και χρήση της κλάσης AppWithContainer
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter the Docker image name:");
            String imageName = scanner.nextLine();

            System.out.println("Enter the Docker container ID:");
            String containerId = scanner.nextLine();

            AppWithContainer app = new AppWithContainer("tcp://localhost:2375", imageName, containerId);
            app.manageContainer();
        }
    }

}
