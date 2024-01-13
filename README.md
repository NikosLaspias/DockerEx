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
![UML DIAGRAM](/https://github.com/NikosLaspias/DockerEx/blob/main/uml.png)

## 6.DATA STRUCTURE
Η εφαρμογή χρησιμοποιεί διάφορους τύπους δεδομένων για τη διαχείριση του Docker Cluster. Αναφέρουμε τους βασικούς τύπους δεδομένων και τις δομές τους που χρησιμοποιούνται:

## Κλάσεις

1. **DesktopApplication:** Η κύρια κλάση που εκτελεί την εφαρμογή γραφικού περιβάλλοντος χρήστη.

 **Τύποι Δεδομένων:**
Η κλάση `DesktopApplication` χρησιμοποιεί διάφορους τύπους δεδομένων όπως `String`, `ComboBox<String>`, και `TextArea` για τη διαχείριση των επιλογών του χρήστη.

**Δομές Δεδομένων:**
   - Η λίστα (`ComboBox<String> optionsComboBox`) χρησιμοποιείται για την αποθήκευση και προβολή των διαθέσιμων επιλογών για τον χρήστη.
   - Το πεδίο κειμένου (`TextArea menuTextArea`) χρησιμοποιείται για την προβολή του μενού και των επιλογών του χρήστη.

 **Σχέσεις Δεδομένων:**
   - Η κλάση `DesktopApplication` αλληλεπιδρά με διάφορες άλλες κλάσεις, όπως `ContainerManager`, `ExecutorManager`, `AppManager`, `ImageManager`, `MonitorManager`, και `DatabaseManager`, κάθε μία από τις οποίες χρησιμοποιεί δικούς της τύπους δεδομένων για τη διαχείριση των εργασιών της.

 **Χρήση Εξωτερικών Βιβλιοθηκών:**
   - Η εφαρμογή χρησιμοποιεί τη βιβλιοθήκη JavaFX για τη δημιουργία γραφικού περιβάλλοντος χρήστη.
   - Επίσης, χρησιμοποιείται η κλάση `Alert` από τη JavaFX για την εμφάνιση πληροφοριών στον χρήστη.


2. **ContainerManager:** Διαχειρίζεται τις λειτουργίες σχετικές με τη δημιουργία και την επιθεώρηση των containers.

**Τύποι Δεδομένων:**

Η κλάση ContainerCreation χρησιμοποιεί διάφορους τύπους δεδομένων για τη διαχείριση των Docker containers:
DockerClient: Αντιπροσωπεύει τον Docker client και χρησιμοποιείται για την αλληλεπίδραση με το Docker daemon.
String: Χρησιμοποιείται για το ID του επιλεγμένου Docker container.
Alert: Χρησιμοποιείται για την εμφάνιση ειδοποιήσεων προς τον χρήστη.
TextInputDialog: Χρησιμοποιείται για την εμφάνιση παραθύρου διαλόγου για την εισαγωγή δεδομένων από τον χρήστη.
Άλλοι τύποι δεδομένων που χρησιμοποιούνται για την αναπαράσταση των αποτελεσμάτων επιθεώρησης, των λογικών καταστάσεων, κ.λπ.

**Δομές Δεδομένων:**

Η κλάση ContainerCreation χρησιμοποιεί δομές δεδομένων όπως λίστες (List<Container>), τα οποία περιέχουν πληροφορίες σχετικά με τα Docker containers.
Επίσης, χρησιμοποιείται η δομή InspectContainerResponse για την ανάκτηση λεπτομερειών για ένα συγκεκριμένο container.

**Σχέσεις Δεδομένων:**

Η κλάση ContainerCreation αλληλεπιδρά με το Docker daemon μέσω της βιβλιοθήκης Docker Java API.
Επίσης, αλληλεπιδρά με τον χρήστη μέσω παραθύρων διαλόγου (Alert, TextInputDialog) για την εισαγωγή δεδομένων.

**Χρήση Εξωτερικών Βιβλιοθηκών:**

Η κλάση ContainerCreation χρησιμοποιεί την εξωτερική βιβλιοθήκη Docker Java API για την αλληλεπίδραση με το Docker daemon.
Επίσης, χρησιμοποιεί τη βιβλιοθήκη JavaFX για τη δημιουργία γραφικού περιβάλλοντος χρήστη και την επικοινωνία με τον χρήστη μέσω παραθύρων διαλόγου.


3. **ExecutorManager:** Υλοποιεί λειτουργίες εκτέλεσης για τα containers.

**Τύποι Δεδομένων:**

Η κλάση ExecutorManager χρησιμοποιεί τους τύπους δεδομένων ChoiceDialog<String> και TextInputDialog από το JavaFX για την επικοινωνία με τον χρήστη.
Υπάρχει επίσης η χρήση των τύπων δεδομένων String για αναπαράσταση του ID του container.

**Δομές Δεδομένων:**

 Οι δομές ChoiceDialog και TextInputDialog χρησιμοποιούνται για τη συλλογή πληροφοριών από τον χρήστη.

**Σχέσεις Δεδομένων:**

Η κλάση αλληλεπιδρά με τον χρήστη μέσω των δομών δεδομένων ChoiceDialog και TextInputDialog για την επιλογή ενεργειών και την εισαγωγή ID container αντίστοιχα.

**Χρήση Εξωτερικών Βιβλιοθηκών:**

Η κλάση χρησιμοποιεί τη βιβλιοθήκη JavaFX για τη δημιουργία διαλόγων επιλογής (ChoiceDialog) και εισαγωγής κειμένου (TextInputDialog).

4. **AppManager:** Διαχειρίζεται τις λειτουργίες που σχετίζονται με την εφαρμογή και τα containers.

**Τύποι Δεδομένων:**

 Η κλάση AppManager χρησιμοποιεί τον τύπο δεδομένων String για την αποθήκευση του ονόματος της εικόνας Docker και του αναγνωριστικού του container.

**Σχέσεις Δεδομένων:**

 Η κλάση AppManager αλληλεπιδρά με τον χρήστη μέσω των διαλόγων εισόδου (TextInputDialog) για την εισαγωγή ονόματος εικόνας Docker και αναγνωριστικού 
container. Αυτά τα δεδομένα στη συνέχεια χρησιμοποιούνται για τη δημιουργία μιας νέας ενότητας AppWithContainer που διαχειρίζεται το container.

   
5. **ImageManager:** Χειρίζεται ενέργειες σχετικά με τις εικόνες Docker.
**Τύποι Δεδομένων:**

Η κλάση ImageManager χρησιμοποιεί τον τύπο δεδομένων TextArea για την εμφάνιση των αποτελεσμάτων των λειτουργιών του Docker.
Η κλάση χρησιμοποιεί τον τύπο δεδομένων TextInputDialog για τη συλλογή του ονόματος της εικόνας από τον χρήστη.
Η κλάση χρησιμοποιεί τον τύπο δεδομένων ChoiceDialog<String> για την παρουσίαση ενός παραθύρου επιλογής με διάφορες επιλογές Docker λειτουργιών.

**Δομές Δεδομένων:**

Δεν χρησιμοποιούνται ιδιαίτερες δομές δεδομένων στην κλάση ImageManager, αλλά χρησιμοποιείται ένα αντικείμενο τύπου Optional<String> για την αντιπροσώπευση της επιλογής του χρήστη.

**Σχέσεις Δεδομένων:**

Η κλάση ImageManager διαχειρίζεται τις επιλογές χρήστη για λειτουργίες Docker σχετικά με εικόνες.
Υπάρχει αλληλεπίδραση με τον χρήστη μέσω του TextInputDialog για την εισαγωγή του ονόματος της εικόνας και του ChoiceDialog για την επιλογή της ενέργειας που επιθυμεί να εκτελέσει.

**Χρήση Εξωτερικών Βιβλιοθηκών:**

 Χρήση των βιβλιοθηκών JavaFX για τη δημιουργία των παραθύρων διαλόγου (ChoiceDialog, TextInputDialog) υποδηλώνει την εξάρτηση από τις βιβλιοθήκες αυτές.


6. **MonitorManager:** Παρέχει λειτουργίες παρακολούθησης των containers.

**Τύποι Δεδομένων:**

Η κλάση MonitorManager χρησιμοποιεί τους τύπους δεδομένων List, Thread και MonitorThread.ContainerMeasurement για τη διαχείριση των μετρήσεων του MonitorThread και την εμφάνιση τους σε ένα γράφημα.

**Σχέσεις Δεδομένων:**

Η κλάση συνεργάζεται με την κλάση MonitorThread για τη συλλογή μετρήσεων από τα containers.
Υπάρχει μια εξάρτηση από την MeasurementChart για την εμφάνιση του γραφήματος.

7. **DatabaseManager:** Συνδέεται με τη βάση δεδομένων και διαχειρίζεται τις μετρήσεις.
**Τύποι Δεδομένων:**

Η κλάση DatabaseManager χρησιμοποιεί τον τύπο δεδομένων Alert από το JavaFX για τη δημιουργία παραθύρου ειδοποίησης προς τον χρήστη. Επιπλέον, χρησιμοποιεί τον τύπο δεδομένων ButtonType για τον έλεγχο του πατήματος κουμπιών από τον χρήστη.

**Δομές Δεδομένων:**

Η δομή δεδομένων Alert χρησιμοποιείται για τη δημιουργία παραθύρου ειδοποίησης, ενώ η δομή ButtonType χρησιμοποιείται για τον έλεγχο των διάφορων επιλογών που μπορεί να κάνει ο χρήστης μέσω του παραθύρου.

**Σχέσεις Δεδομένων:**

Η κλάση αλληλεπιδρά με τον χρήστη μέσω του παραθύρου ειδοποίησης (Alert) για να ρωτήσει αν ο χρήστης θέλει να συνδεθεί με τη βάση δεδομένων για την εισαγωγή μετρήσεων.

8. **RestControler:** Χειρίζεται το REST API για την επικοινωνία με τα containers.

**Τύποι Δεδομένων:**

Η κλάση RestController χρησιμοποιεί τους εξής τύπους δεδομένων:

ResponseEntity<String> από το Spring Framework για την αντιπροσώπευση των απαντήσεων HTTP.
Optional<String> από το Java για την αντιπροσώπευση τυχόν προαιρετικών απαντήσεων χρήστη.
List<Map<String, Object>> και Map<String, Object> για την αναπαράσταση μετρήσεων και στατιστικών στοιχείων.

**Δομές Δεδομένων:**

Η κλάση χρησιμοποιεί τις εξής δομές δεδομένων:

List<Map<String, Object>> για την αναπαράσταση των μετρήσεων.
Map<String, Object> για την αναπαράσταση στατιστικών στοιχείων σχετικά με τα containers.
Optional<String> για την αναπαράσταση προαιρετικών συμβουλών που εισάγει ο χρήστης.

**Σχέσεις Δεδομένων:**

Η κλάση αλληλεπιδρά με το Spring Framework για τις HTTP αιτήσεις και απαντήσεις. Επιπλέον, αλληλεπιδρά με την κλάση RestAPI για την εκτέλεση αιτήσεων σχετικά με το Docker.

**Χρήση Εξωτερικών Βιβλιοθηκών:**

Η κλάση χρησιμοποιεί το Spring Framework για την υλοποίηση του REST API και το JavaFX για τον χειρισμό του γραφικού περιβάλλοντος χρήστη.
 
9. **RestAPI:** Υλοποιεί το REST API για τις λειτουργίες της εφαρμογής.

**Τύποι Δεδομένων:**

Η κλάση RestAPI χρησιμοποιεί τους εξής τύπους δεδομένων:

ResponseEntity<String> από το Spring Framework για την αντιπροσώπευση των απαντήσεων HTTP.
DefaultDockerClientConfig, DockerClient, Container από το Docker Java API για την αλληλεπίδραση με το Docker.
Επίσης, χρησιμοποιείται ένας αριθμός τύπων δεδομένων όπως String, List<Map<String, Object>>, Map<String, Object>, List<Container>, κλπ., για τη διαχείριση μετρήσεων, στατιστικών, και πληροφοριών περί containers.

**Δομές Δεδομένων:**

Η κλάση χρησιμοποιεί τις εξής δομές δεδομένων:

List<Map<String, Object>> για την αναπαράσταση των μετρήσεων.
Map<String, Object> για την αναπαράσταση των στατιστικών στοιχείων των containers.
Χρησιμοποιεί επίσης τυπικούς τύπους όπως String, Optional, κλπ.

**Σχέσεις Δεδομένων:**

Η κλάση αλληλεπιδρά με το Spring Framework για τις HTTP αιτήσεις και απαντήσεις. Επιπλέον, αλληλεπιδρά με το Docker μέσω των αντικειμένων του Docker Java API για επικοινωνία με τα containers. Τέλος, αλληλεπιδρά με τη βάση δεδομένων για την ανάκτηση μετρήσεων σε καθορισμένο εύρος ημερομηνιών.

**Χρήση Εξωτερικών Βιβλιοθηκών:**

Η κλάση χρησιμοποιεί το Spring Framework για την υλοποίηση του REST API, καθώς και το Docker Java API για την αλληλεπίδραση με το Docker.


10. **MonitorThread:** Εκτελεί το thread παρακολούθησης.
**Τύποι Δεδομένων:**

Η κλάση MonitorThread χρησιμοποιεί τους εξής τύπους δεδομένων:

DockerClient από το Docker Java API για την αλληλεπίδραση με το Docker.
Λίστες και συλλογές (List, ArrayList, Map, ArrayList) για την αποθήκευση μετρήσεων και άλλων δεδομένων.
Alert από το JavaFX για την εμφάνιση ειδοποιήσεων στο χρήστη.
Άλλοι τύποι όπως String, boolean, Rectangle2D, Stage, Scene για αναπαράσταση διαφόρων δεδομένων.
**Δομές Δεδομένων:**

Η κλάση χρησιμοποιεί διάφορες δομές δεδομένων όπως λίστες για την αποθήκευση μετρήσεων και άλλων στοιχείων. Επίσης, χρησιμοποιεί τον τύπο ContainerMeasurement ως εσωτερική κλάση για την αναπαράσταση μετρήσεων ενός Docker container.

**Σχέσεις Δεδομένων:**

Η κλάση αλληλεπιδρά με τη βιβλιοθήκη Docker Java API για τη λήψη πληροφοριών από τα Docker containers. Επίσης, χρησιμοποιεί τη βιβλιοθήκη JavaFX για τον χειρισμό του γραφικού περιβάλλοντος.

**Χρήση Εξωτερικών Βιβλιοθηκών:**

Η κλάση χρησιμοποιεί τη βιβλιοθήκη Docker Java API για την αλληλεπίδραση με το Docker και το JavaFX για τον χειρισμό του γραφικού περιβάλλοντος. Επίσης, χρησιμοποιεί τη βιβλιοθήκη Spring για τη υλοποίηση του REST API.
   
11. **ExecutorThread:** Εκτελεί το thread εκτέλεσης εντολών.
**Τύποι Δεδομένων:**

Η κλάση ExecutorThread χρησιμοποιεί τους εξής τύπους δεδομένων:

DockerClient από το Docker Java API για την αλληλεπίδραση με το Docker.
DefaultDockerClientConfig για τον παραμετροποιημένο ρυθμίσεις του Docker client.
ExposedPort από το Docker Java API για τον καθορισμό εκτεθειμένων ports του Docker container.
PortBinding από το Docker Java API για τον καθορισμό του port binding του Docker container.
HostConfig από το Docker Java API για τον παραμετροποιημένο ρυθμίσεις του Docker container.
CreateContainerCmd από το Docker Java API για τη δημιουργία ενός Docker container.
CreateContainerResponse από το Docker Java API για την αντιπροσώπευση της απάντησης μετά τη δημιουργία ενός Docker container.

**Δομές Δεδομένων:**

Η κλάση διαχειρίζεται την εκτέλεση, την παύση και τη δημιουργία Docker containers. Για τον καθορισμό των παραμέτρων των containers χρησιμοποιεί δομές δεδομένων όπως οι ExposedPort, PortBinding, HostConfig, CreateContainerCmd, και CreateContainerResponse.

**Σχέσεις Δεδομένων:**

Η κλάση αλληλεπιδρά με το Docker Java API για τη διαχείριση των Docker containers. Επιπλέον, χρησιμοποιεί το JavaFX για την εμφάνιση ειδοποιήσεων στον χρήστη.

12. **Database:** Συνδέεται με τη βάση δεδομένων.
**Τύποι Δεδομένων:**
Η κλάση Database χρησιμοποιεί διάφορους τύπους δεδομένων για τη διαχείριση των αλληλεπιδράσεων με τη βάση δεδομένων:

String: Χρησιμοποιείται για την αποθήκευση πληροφοριών σύνδεσης στη βάση δεδομένων (databaseURL, databaseUser, databasePassword).
Connection: Αναπαριστά τη σύνδεση με τη βάση δεδομένων.
LocalDateTime: Αναπαριστά την τρέχουσα ημερομηνία και ώρα.
int: Αναπαριστά τον τρέχοντα αριθμό μέτρησης.

**Δομές Δεδομένων:**
Η κλάση χρησιμοποιεί Λίστες και Χάρτες για τη διαχείριση των μετρήσεων ενός container και των δεδομένων που ανακτώνται από τη βάση:

List<MonitorThread.ContainerMeasurement>: Αποθηκεύει μετρήσεις container για εισαγωγή στη βάση δεδομένων.
List<Map<String, Object>>: Αποθηκεύει πληροφορίες μέτρησης που ανακτώνται από τη βάση.

**Σχέσεις Δεδομένων:**

Πίνακες Βάσης Δεδομένων:

Η κλάση αλληλεπιδρά με δύο πίνακες της βάσης δεδομένων: ContainerMeasurements και MeasurementData.
Ο πίνακας ContainerMeasurements αποθηκεύει τις μετρήσεις των containers, περιλαμβάνοντας πληροφορίες όπως το id του container, την εικόνα, την κατάσταση, τις θύρες, και την εντολή.
Ο πίνακας MeasurementData αποθηκεύει πρόσθετα δεδομένα σχετικά με τις μετρήσεις, όπως η ημερομηνία της μέτρησης και ο αριθμός της.
Εσωτερικές Δομές Δεδομένων:

Η κλάση χρησιμοποιεί δομές όπως λίστες και χάρτες για τη διαχείριση των μετρήσεων και των δεδομένων που ανακτώνται από τη βάση.
**Ξένες Βιβλιοθήκες:**

JavaFX:

Η κλάση χρησιμοποιεί τη βιβλιοθήκη JavaFX για τη δημιουργία γραφικού περιβάλλοντος χρήστη (GUI) και την επικοινωνία με τον χρήστη. Χρησιμοποιεί στοιχεία όπως το Alert για την προβολή ειδοποιήσεων.
Docker Java API:

Η κλάση αλληλεπιδρά με την εξωτερική βιβλιοθήκη Docker Java API για τη διαχείριση ενεργειών στο Docker daemon. Χρησιμοποιεί κλάσεις όπως DockerClient, CreateContainerCmd, CreateContainerResponse, και ExecCreateCmdResponse για τη δημιουργία container και εκτέλεση εντολών σε αυτά.

13. **AppWithContainer:** Διαχειρίζεται το container που δημιουργείται από την κλάση `AppManager`.

**Τύποι Δεδομένων:**

Η κλάση AppWithContainer χρησιμοποιεί διάφορους τύπους δεδομένων για τη διαχείριση ενός Docker container:
DockerClient: Αντιπροσωπεύει τον Docker client και χρησιμοποιείται για την αλληλεπίδραση με το Docker daemon.
String: Χρησιμοποιείται για το όνομα της εικόνας Docker και το όνομα του container.
List<Container>: Χρησιμοποιείται για τη συλλογή πληροφοριών σχετικά με τα ενεργά containers.

**Δομές Δεδομένων:**
Η κλάση AppWithContainer χρησιμοποιεί διάφορες δομές δεδομένων:
CreateContainerCmd: Χρησιμοποιείται για τη δημιουργία ενός Docker container command.
CreateContainerResponse: Χρησιμοποιείται για την ανάκτηση πληροφοριών μετά τη δημιουργία του container.
ExecCreateCmdResponse: Χρησιμοποιείται για τη δημιουργία ενός Docker exec command.
Frame: Χρησιμοποιείται για την αντιπροσώπευση ενός frame εξόδου από την εκτέλεση ενός Docker exec command.

 **Σχέσεις Δεδομένων:**
Η κλάση AppWithContainer αλληλεπιδρά με την εξωτερική βιβλιοθήκη Docker Java API και διάφορες κλάσεις του JavaFX, όπως:
DockerClient, CreateContainerCmd, CreateContainerResponse, ExecCreateCmdResponse: Χρησιμοποιούνται για τη διαχείριση ενεργειών στο Docker daemon.
Alert, ScrollPane, TextArea: Χρησιμοποιούνται για την επικοινωνία με τον χρήστη και την εμφάνιση πληροφοριών.

 **Χρήση Εξωτερικών Βιβλιοθηκών:**
Η κλάση AppWithContainer χρησιμοποιεί την εξωτερική βιβλιοθήκη Docker Java API για την αλληλεπίδραση με το Docker daemon.
Επίσης, χρησιμοποιείται η κλάση Platform του JavaFX για τον συγχρονισμό με το UI thread κατά την εμφάνιση ενός Alert.


