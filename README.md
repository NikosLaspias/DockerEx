# DockerEx
# Welcome to our docker program!We create a docker application!
## 1. Utility of Maven
This project utilizes Maven for managing dependencies and plugins. The compilation command is `mvn compile`, and for code checking, use `mvn clean javafx:run`. 

## 2. Run Application
To run the application, execute `mvn clean javafx:run`.

## 3. Usage of the Application
The Docker Application is designed to be user-friendly, offering a menu with 8 selections for convenient navigation through Docker functionalities. Users can input container statistics into a database using a monitor thread, allowing for dynamic visualization. Clear prompts and guidance are provided for each selection, ensuring a smooth user experience.
## Guidance 
The first(1) selection is for the handling of containeres.User can check the status of container with a specific id and also see the list of all runnig and excited containers.The second 2 choice includes actions like Start,Stop and execute a container.The third(3) option combines actions between containers and images and provides the oppοrtunity to delete a container.The four(4) option includes all the selections for images like search-delete-pull-list.The next selection(5) concerns to get the statistics of containers and create a bar diagram with all the active containers. After of the execution of this choice the user is flexible to insert the measuremenets in a sql database with the selection 6 that shoud have created in his own mysql server. Supplementary he shoud created two tables in mysql like this:

CREATE TABLE containermeasurements (
    container_id VARCHAR(255) PRIMARY KEY,
    image VARCHAR(255),
    status VARCHAR(255),
    ports VARCHAR(255),
    command VARCHAR(255)
);

CREATE TABLE MeasurementData (
    measurement_id INT AUTO_INCREMENT PRIMARY KEY,
    measurement_date DATETIME,
    measurement_number INT
);
This is necessary for the user to succeded connect with the database and insert these measurements.To connect with the database he/she will give his/her personal information like url-username-passward.If all goes well, he/she will receive a message that was connected to the base otherwise that was failed.
For the ending in selection 7 we provide the chance to get the measurments of the table MeasurementData after of inserting two dates(start-stop). The user gets the measurements which wants based on them.Also he/she can do actions like start-stop in a container after from question. TO close the program the omly thing needed is to press option 8. 

## 4. Structure of Our Repository
We use the DockerEx repository for collaboration and file uploads. The main branch contains classes executed on Windows, and a separate branch is dedicated to files compatible with macOS.
We create a folder demo which contains other folders for our final files.In folder src inscide we conclude the final files.
## 5.UML DIAGRAM
![UML DIAGRAM](/https://github.com/NikosLaspias/DockerEx/blob/main/uml.png)

## 6. DATA STRUCTURE

The application utilizes various data types for managing the Docker Cluster. Below are the main data types and their structures:

## Classes

1. **DesktopApplication:**
   - **Data Types:**
      - The `DesktopApplication` class uses data types such as `String`, `ComboBox<String>`, and `TextArea` for user interaction.
   - **Data Structures:**
      - The `ComboBox<String> optionsComboBox` is used to store and display available user options.
      - The `TextArea menuTextArea` is used for displaying the menu and user choices.
   - **Data Relationships:**
      - The `DesktopApplication` class interacts with other classes like `ContainerManager`, `ExecutorManager`, `AppManager`, `ImageManager`, `MonitorManager`, and `DatabaseManager`, each having its own data types for task management.
   - **External Library Usage:**
      - The application utilizes the JavaFX library for creating the graphical user interface.
      - The `Alert` class from JavaFX is used for displaying information to the user.

2. **ContainerManager:**
   - **Data Types:**
      - The `ContainerCreation` class uses various data types for managing Docker containers:
         - `DockerClient`: Represents the Docker client for interacting with the Docker daemon.
         - `String`: Used for the ID of the selected Docker container.
         - `Alert`, `TextInputDialog`: Used for displaying notifications and collecting user input.
         - Other data types for representing inspection results, logical states, etc.
   - **Data Structures:**
      - The `ContainerCreation` class uses data structures like lists (`List<Container>`) containing information about Docker containers.
      - It also uses the `InspectContainerResponse` structure for retrieving details about a specific container.
   - **Data Relationships:**
      - The `ContainerCreation` class interacts with the Docker daemon through the Docker Java API.
      - Interaction with the user is facilitated through dialog windows (`Alert`, `TextInputDialog`) for data input.
   - **External Library Usage:**
      - The `ContainerCreation` class uses the external Docker Java API library for interacting with the Docker daemon.
      - JavaFX library is used for creating the graphical user interface and communicating with the user through dialog windows.

3. **ExecutorManager:**
   - **Data Types:**
      - The `ExecutorManager` class uses data types like `ChoiceDialog<String>` and `TextInputDialog` from JavaFX for user interaction.
      - The `String` data type is used for representing the container ID.
   - **Data Structures:**
      - The `ChoiceDialog` and `TextInputDialog` structures are used for collecting information from the user.
   - **Data Relationships:**
      - The class interacts with the user through the `ChoiceDialog` and `TextInputDialog` structures for action selection and container ID input.
   - **External Library Usage:**
      - The class uses the JavaFX library for creating choice dialogs (`ChoiceDialog`) and text input dialogs (`TextInputDialog`).

4. **AppManager:**
   - **Data Types:**
      - The `AppManager` class uses the `String` data type for storing Docker image names and container IDs.
   - **Data Relationships:**
      - The class interacts with the user through input dialogs (`TextInputDialog`) for entering Docker image names and container IDs.
      - These data are then used for creating a new `AppWithContainer` entity that manages the container.
   
5. **ImageManager:**
   - **Data Types:**
      - The `ImageManager` class uses the `TextArea` data type for displaying Docker operation results.
      - It uses `TextInputDialog` for collecting image names from the user.
      - It uses `ChoiceDialog<String>` for presenting a selection window with various Docker operation choices.
   - **Data Structures:**
      - No specific data structures are used in the `ImageManager` class, but an `Optional<String>` object is used for representing user choice.
   - **Data Relationships:**
      - The class manages user choices for Docker operations related to images.
      - There is interaction with the user through `TextInputDialog` for entering image names and `ChoiceDialog` for selecting the desired action.
   - **External Library Usage:**
      - Usage of JavaFX libraries for creating dialog windows (`ChoiceDialog`, `TextInputDialog`) indicates dependency on these libraries.

6. **MonitorManager:**
   - **Data Types:**
      - The `MonitorManager` class uses data types such as `List`, `Thread`, and `MonitorThread.ContainerMeasurement` for handling measurements from the `MonitorThread` and displaying them in a chart.
   - **Data Relationships:**
      - The class collaborates with the `MonitorThread` class for collecting measurements from containers.
      - There is a dependency on `MeasurementChart` for displaying the chart.
   - **External Library Usage:**
      - No specific external libraries are mentioned, but usage of threads and potential dependencies on chart-related libraries can be inferred.

7. **DatabaseManager:** Connects to the database and manages measurements.
**Data Types:**

The DatabaseManager class uses the Alert data type from JavaFX to create a notification window for the user. Additionally, it uses the ButtonType data type to control button clicks by the user.

**Data Structures:**

The Alert data structure is used to create a notification window, while the ButtonType data structure is used to control various user options through the window.

**Data Relationships:**

The class interacts with the user through the notification window (Alert) to inquire if the user wants to connect to the database for inputting measurements.

8. **RestController:** Handles the REST API for communication with containers.

**Data Types:**

The RestController class uses the following data types:

ResponseEntity<String> from the Spring Framework to represent HTTP responses.
Optional<String> from Java to represent optional user responses.
List<Map<String, Object>> and Map<String, Object> for representing measurements and statistics.

**Data Structures:**

The class uses the following data structures:

List<Map<String, Object>> to represent measurements.
Map<String, Object> to represent statistics related to containers.
Optional<String> to represent optional advice input by the user.

**Data Relationships:**

The class interacts with the Spring Framework for HTTP requests and responses. Additionally, it interacts with the RestAPI class for executing requests related to Docker.

**Usage of External Libraries:**

The class uses the Spring Framework for implementing the REST API and JavaFX for handling the graphical user interface.

9. **RestAPI:** Implements the REST API for application functions.

**Data Types:**

The RestAPI class uses the following data types:

ResponseEntity<String> from the Spring Framework to represent HTTP responses.
DefaultDockerClientConfig, DockerClient, Container from the Docker Java API for interaction with Docker.
Various data types such as String, List<Map<String, Object>>, Map<String, Object>, List<Container>, etc., for managing measurements, statistics, and container information.

**Data Structures:**

The class uses the following data structures:

List<Map<String, Object>> to represent measurements.
Map<String, Object> to represent statistics of containers.
It also uses typical types like String, Optional, etc.

**Data Relationships:**

The class interacts with the Spring Framework for HTTP requests and responses. Additionally, it interacts with Docker through Docker Java API objects for container interaction. Finally, it interacts with the database for retrieving measurements within a specified date range.

**Usage of External Libraries:**

The class uses the Spring Framework for implementing the REST API and the Docker Java API for interaction with Docker.

10. **MonitorThread:** Executes the monitoring thread.

**Data Types:**

The MonitorThread class uses the following data types:

DockerClient from the Docker Java API for interaction with Docker.
Lists and collections (List, ArrayList, Map, ArrayList) for storing measurements and other data.
Alert from JavaFX for displaying notifications to the user.
Other types like String, boolean, Rectangle2D, Stage, Scene for representing various data.

**Data Structures:**

The class uses various data structures such as lists for storing measurements and other elements. Additionally, it uses the ContainerMeasurement type as an internal class for representing measurements of a Docker container.

**Data Relationships:**

The class interacts with the Docker Java API library to obtain information from Docker containers. Also, it uses the JavaFX library for handling the graphical environment.

**Usage of External Libraries:**

The class uses the Docker Java API library for interaction with Docker and JavaFX for handling the graphical environment. Additionally, it utilizes the Spring library for implementing the REST API.

11. **ExecutorThread:** Executes the command execution thread.

**Data Types:**

The ExecutorThread class uses the following data types:

DockerClient from the Docker Java API for interaction with Docker.
DefaultDockerClientConfig for the Docker client's configured settings.
ExposedPort from the Docker Java API to specify exposed ports of the Docker container.
PortBinding from the Docker Java API to specify port binding of the Docker container.
HostConfig from the Docker Java API for the Docker container's configured settings.
CreateContainerCmd from the Docker Java API for creating a Docker container.
CreateContainerResponse from the Docker Java API to represent the response after creating a Docker container.

**Data Structures:**

The class manages the execution, pause, and creation of Docker containers. It utilizes data structures such as ExposedPort, PortBinding, HostConfig, CreateContainerCmd, and CreateContainerResponse to configure container parameters.

**Data Relationships:**

The class interacts with the Docker Java API to manage Docker containers. Additionally, it uses JavaFX for displaying notifications to the user.

12. **Database:** Connects to the database.

**Data Types:**
The Database class uses various data types for managing interactions with the database:

String: Used for storing database connection information (databaseURL, databaseUser, databasePassword).
Connection: Represents the connection to the database.
LocalDateTime: Represents the current date and time.
int: Represents the current measurement number.

**Data Structures:**
The class uses Lists and Maps for managing measurements of a container and data retrieved from the database:

List<MonitorThread.ContainerMeasurement>: Stores container measurements for insertion into the database.
List<Map<String, Object>>: Stores measurement information retrieved from the database.

**Data Relationships:**

Database Tables:

The class interacts with two database tables: ContainerMeasurements and MeasurementData.
The ContainerMeasurements table stores container measurements, including information such as the container id, image, status, ports, and command.
The MeasurementData table stores additional data related to measurements, such as the measurement date and number.
Internal Data Structures:

The class uses structures like lists and maps for managing measurements and data retrieved from the database.

**External Libraries:**

JavaFX:

The class uses the JavaFX library for creating a graphical user interface (GUI) and interacting with the user. It utilizes elements like Alert for displaying notifications.
Docker Java API:

The class interacts with the external Docker Java API library for managing actions on the Docker daemon. It uses classes such as DockerClient, CreateContainerCmd, CreateContainerResponse, and ExecCreateCmdResponse for creating a container and executing commands on it.

13. **AppWithContainer:** Manages the container created by the `AppManager` class.

**Data Types:**

The AppWithContainer class uses various data types for managing a Docker container:
DockerClient: Represents the Docker client and is used for interacting with the Docker daemon.
String: Used for the Docker image name and container name.
List<Container>: Used for collecting information about active containers.

**Data Structures:**

The AppWithContainer class uses various data structures:
CreateContainerCmd: Used for creating a Docker container command.
CreateContainerResponse: Used for retrieving information after creating the container.
ExecCreateCmdResponse: Used for creating a Docker exec command.
Frame: Used for representing an output frame from the execution of a Docker exec command.

**Data Relationships:**

The AppWithContainer class interacts with the external Docker Java API library and various JavaFX classes, such as:
DockerClient, CreateContainerCmd, CreateContainerResponse, ExecCreateCmdResponse: Used for managing actions on the Docker daemon.
Alert, ScrollPane, TextArea: Used for interacting with the user and displaying information.

**Usage of External Libraries:**

The class AppWithContainer uses the external Docker Java API library for interacting with the Docker daemon. Additionally, it utilizes the JavaFX Platform class for synchronizing with the UI thread when displaying an Alert.


14. **Image:**

The `Image` class encapsulates Docker image-related operations. It provides methods for pulling images, displaying image information, showing a list of images, searching for a specific image, inspecting an image, removing an image, and displaying the pull history for an image.

**Data Types:**

- **DockerClient:** Represents the Docker client for interacting with the Docker daemon.
- **DefaultDockerClientConfig:** Configuration for the Docker client.
- **InspectImageResponse:** Represents the response from inspecting a Docker image.
- **TextArea:** JavaFX component for displaying text.

**Constructor:**

The constructor initializes the `Image` object with a Docker client configured to communicate with the Docker daemon running on `localhost:2375`. It also takes a `TextArea` as a parameter, where the results of Docker operations will be displayed.

**Methods:**

- **pullImageIfNotExists(String imageName):** Pulls a Docker image if it does not already exist. Displays a message indicating whether the image was already present or pulled successfully.

- **pullImage(String imageName):** Pulls a Docker image with the specified name. Displays a success message or an error message if the image is not found or an exception occurs during pulling.

- **showImageList():** Retrieves a list of Docker images and their information. Displays the information in a separate JavaFX window using a `Stage` and a `TextArea`.

- **searchImages(String imageName):** Searches for a specific Docker image and displays its information, including name, tags, and commands.

- **inspectImage(String imageId):** Retrieves detailed information about a Docker image based on its ID.

- **removeImage(String imageId):** Removes a Docker image based on its ID. Displays a success message or an error message if an exception occurs during removal.

- **showPullHistory(String imageName):** Displays the pull history for a Docker image. Retrieves and shows information about the image's ID and commands executed during pull.

- **showAlert(String title, String content):** Displays an alert using JavaFX's `Alert` class. The method is designed to be run on the JavaFX application thread.

- **showImageListDialog(String imageInfo):** Displays a dialog with the list of Docker images in a `Stage`. The information is presented in a `TextArea`.

- **showPullHistoryDialog(String pullHistory):** Displays a dialog with the pull history for a Docker image in a `Stage`. The information is presented in a `TextArea`.

- **close():** Overrides the `AutoCloseable` interface's `close` method to ensure proper closure of the Docker client when the `Image` object is used in a try-with-resources block.

**External Libraries:**

- **Docker Java API:** The class uses the Docker Java API library to interact with the Docker daemon and perform Docker-related operations.

- **JavaFX:** The class utilizes JavaFX components, such as `Stage`, `TextArea`, and `Alert`, for creating a graphical user interface (GUI) and displaying information to the user.


15.**MeasurementChart:**

The `MeasurementChart` class is responsible for creating and displaying a bar chart based on container measurements. This class utilizes the JFreeChart library for chart creation and extends the `JFrame` class for GUI functionality.

**Data Types:**

- **ContainerMeasurement[]:** An array of `ContainerMeasurement` objects representing the container measurements that will be visualized on the chart.

**Constructor:**

The class has a constructor that takes a title and an array of `ContainerMeasurement` objects. In this constructor, it initializes the `MeasurementChart` with the provided title and sets the container measurements for chart generation.

**Methods:**

- **createDataset():** A private method that creates a `CategoryDataset` from the container measurements. It uses a `DefaultCategoryDataset` to store data. In this implementation, it adds each container's ID to the dataset with a count of 1.

**Chart Creation:**

The class creates a bar chart using the `ChartFactory.createBarChart` method from JFreeChart. It sets the chart title as "Container Measurements," the X-axis label as "Containers," and the Y-axis label as "Count." The dataset created by the `createDataset` method is used to populate the chart.

**Chart Panel:**

The chart is added to a `ChartPanel` and set as the content pane of the `MeasurementChart` JFrame. The `ChartPanel` is then configured with preferred dimensions.

**Usage:**

To use this class, instantiate an object of `MeasurementChart` with the desired title and an array of `ContainerMeasurement` objects. Display the chart by showing the JFrame.


16.**ContainerCreation:**

The `ContainerCreation` class is responsible for managing Docker containers, allowing the user to start or stop a specified container and displaying information about active and inactive containers. It also provides methods to retrieve Docker client information and manage container logs.

**Data Types:**

- **DockerClient:** Represents the Docker client for interacting with the Docker daemon.
- **TextInputDialog:** A JavaFX dialog that prompts the user for input.
- **Alert:** JavaFX component for displaying alerts to the user.
- **InspectContainerResponse:** Represents the response from inspecting a Docker container.
- **Frame:** Represents a frame of output from a Docker container.

**Methods:**

- **getDockerClient():** Returns the Docker client instance.

- **manageContainers():** Manages Docker containers by prompting the user for a container ID and starting or stopping the container based on its current state. It also displays information about active and inactive containers.

- **showContainerLogs(String containerId):** Displays the logs of a specified Docker container in a JavaFX `TextArea`. The logs are fetched in real-time using the `ResultCallback` interface.

- **showErrorAlert(String message):** Displays an error alert using JavaFX's `Alert` class.

**Constructor:**

The class does not have a constructor, as it primarily provides static methods for managing containers and displaying information.

**Usage:**

1. The `manageContainers` method prompts the user to enter a Docker container ID using a `TextInputDialog`.
2. It then checks the state of the container and starts or stops it accordingly, displaying a corresponding alert.
3. After the container operation, it retrieves information about active and inactive containers and displays it in a new JavaFX window using a `TextArea`.

**External Libraries:**

- **Docker Java API:** The class uses the Docker Java API library for interacting with the Docker daemon and performing container-related operations.

- **JavaFX:** The class utilizes JavaFX components, such as `Stage`, `Scene`, `TextArea`, `Alert`, and `TextInputDialog`, for creating a graphical user interface (GUI), displaying alerts, and interacting with the user.

**Note:**
Ensure that the Docker daemon is running and accessible at "tcp://localhost:2375" for proper functionality.
