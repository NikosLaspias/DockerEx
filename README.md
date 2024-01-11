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

## 5.UML DIAGRAM


