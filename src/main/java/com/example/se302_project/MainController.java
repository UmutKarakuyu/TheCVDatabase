package com.example.se302_project;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.scene.effect.*;
import javafx.stage.Stage;

public class MainController {
        @FXML
        private Label searchField;
        @FXML
        private TextField filterSearchField;
        @FXML
        private Button addResumeButton;
        @FXML
        private Button addTemplateButton;
        @FXML
        private Button leftBarButton;
        @FXML
        private Button filterOptionButton;
        @FXML
        private TextField tagSearchField;
        @FXML
        private ComboBox<String> tags;
        @FXML
        private ComboBox<String> templates;
        @FXML
        private TableView searchTableView;
        @FXML
        private TableColumn searchTResumeNameColumn, searchTResumeDateColumn;
        @FXML
        private TableView resumeTableView, templateTableView;
        @FXML
        private TableColumn resumeNameColumn, resumeTrashColumn, templateNameColumn, templateTrashColumn;
        @FXML
        private HBox resumeHBox, searchHBox, templateHBox;
        @FXML
        private ImageView pdfUploadView, originalResume;
        @FXML
        private VBox drawerShort, drawerLong;
        @FXML
        private StackPane drawerStackPane;
        @FXML
        private ListView templateCarouselList;
        @FXML
        private VBox generatedResumeBox;
        @FXML
        private ImageView ellipse1, ellipse2;
        @FXML
        private HBox firstEllipses;
        @FXML
        private HBox secondEllipses;
        @FXML
        private HBox thirdEllipses;
        @FXML
        private VBox templateAttributeView;
        @FXML
        private TextField templateName1;
        @FXML
        private Button newTemplateButton;
        @FXML
        private GridPane templateList;
        @FXML
        private VBox templateNameVBox;
        @FXML
        private GridPane generateResume;
        @FXML
        private VBox originalResumeVBox;
        @FXML
        private TextField resumeTitle;
        @FXML
        private ChoiceBox<String> tagDropDown;
        @FXML
        private TextField tagTextField;
        @FXML
        private ChoiceBox<String> myTagsDropDown;
        @FXML
        private HBox modalHBox;
        @FXML
        private HBox allHbox;

        public void initialize() throws SQLException, IOException {

                tagSearchField.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                                addTag();
                        }
                });

                firstEllipses.widthProperty().addListener((obs, oldVal, newVal) -> {
                        if (firstEllipses.getWidth() < 1400)
                                shortDrawer();
                        else
                                longDrawer();
                });

                generatedResumeBox.heightProperty().addListener((obs, oldVal, newVal) -> {
                        ellipse2.setFitHeight(generatedResumeBox.getHeight());
                        ellipse2.setFitWidth(generatedResumeBox.getWidth() / 2);
                });

                generatedResumeBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                        ellipse2.setFitHeight(generatedResumeBox.getHeight());
                        ellipse2.setFitWidth(generatedResumeBox.getWidth() / 2);
                });

                originalResumeVBox.heightProperty().addListener((obs, oldVal, newVal) -> {
                        originalResume.setFitWidth(originalResumeVBox.getWidth());
                });

                originalResumeVBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                        originalResume.setFitWidth(originalResumeVBox.getWidth());
                });

                fillTableViews();

                /*
                 * Template template = new Template("Student");
                 * DBConnection.getInstance().addTemplate(template);
                 * 
                 * Resume resume1 = new Resume("Emre Dülek", "01.01.2023",
                 * "C://EmreDulek/Desktop/EmreDulek.pdf", template);
                 * HashMap<String, String> resume1_attributes = new HashMap<>();
                 * resume1_attributes.put("introduction",
                 * "I am a software developer who has experience with application and web development.\n"
                 * +
                 * "I have experience working for startups, thus I have developed adequate teamwork,\n"
                 * +
                 * "communication, and problem solving abilities. I’m currently studying two undergraduate\n"
                 * +
                 * "program to learn new concepts in science. Even though I didn't have enough knowledge\n"
                 * +
                 * "of some concepts, I considered myself as a ‘fast learner’.");
                 * resume1_attributes.put("education", "2020-Present\n" +
                 * "Izmir University of Economics\n" +
                 * "Bachelor of Science : Computer Engineering (GPA : 3.63)\n" +
                 * "Izmir University of Economics\n" +
                 * "2021-Present\n" +
                 * "Bachelor of Science : Electrical and Electronics Engineering (GPA : 3.80)");
                 * resume1.setAttributes(resume1_attributes);
                 * ArrayList<String> tags1 = new ArrayList<String>();
                 * tags1.add("Java");
                 * tags1.add("JavaFX");
                 * tags1.add("SQLite");
                 * tags1.add("Django");
                 * resume1.setTags(tags1);
                 * 
                 * Resume resume2 = new Resume("Umut Karakuyu", "03.01.2023",
                 * "C://UmutKarakuyu/Desktop/UmutKarakuyu.pdf",
                 * template);
                 * HashMap<String, String> resume2_attributes = new HashMap<>();
                 * resume2_attributes.put("introduction",
                 * "As a software developer, I have 2+ of expertise planning and implementing\n"
                 * +
                 * "reliable software. I consider my communication skills are exceptional and\n"
                 * +
                 * "have worked on both academic and startup initiatives. I'm putting in an\n" +
                 * "application as a software developer to broaden my skill set and get\n" +
                 * "valuable experience for my line of work.");
                 * resume2_attributes.put("education", "Izmir University of Economics");
                 * resume2.setAttributes(resume2_attributes);
                 * ArrayList<String> tags2 = new ArrayList<String>();
                 * tags2.add("Testing");
                 * tags2.add("JavaFX");
                 * tags2.add("MySQL");
                 * tags2.add("Django");
                 * resume2.setTags(tags2);
                 * 
                 * DBConnection.getInstance().addResume(resume1);
                 * DBConnection.getInstance().addResume(resume2);
                 */
        }

        @FXML
        public void selectFromResumeTable() throws SQLException, IOException {
                openResumeScreen();
                int index = resumeTableView.getSelectionModel().getSelectedIndex();
                String resumeName = (String) resumeNameColumn.getCellData(index);
                Resume resume = DBConnection.getInstance().getResumeObject(resumeName);

                ObservableList<TablePosition> selectedCells = resumeTableView.getSelectionModel().getSelectedCells();

                if (selectedCells.get(0).getTableColumn().equals(resumeTrashColumn)) {
                        File file = new File(resume.getfileName() + "_1.png");
                        originalResume.setImage(null);
                        file.delete();
                        DBConnection.getInstance().deleteResume(resumeName);
                        clearResumeContents();
                        fillTableViews();

                } else {
                        String path1 = resume.getfileName() + "_1.png";
                        Image image = new Image(getClass().getResource(path1).toExternalForm());
                        originalResume.setImage(image);
                        double ratio;
                        if (image.getWidth() > originalResumeVBox.getWidth())
                                ratio = image.getWidth() / originalResumeVBox.getWidth();
                        else
                                ratio = originalResumeVBox.getWidth() / image.getWidth();
                        originalResume.setFitWidth(originalResumeVBox.getWidth());
                        originalResume.setFitHeight(originalResumeVBox.getHeight() * ratio);
                        resumeTitle.setText(resume.getName());
                        showGeneratedResume(resume);
                        fillAllTags();
                }
        }

        ArrayList<TextField> arr = new ArrayList<>();

        public void generateResume(Template template) {
                generateResume.getChildren().clear();
                Label l1 = new Label("Attributes");
                l1.setStyle("-fx-font-size: 20;");
                Label l2 = new Label("Values");
                l2.setStyle("-fx-font-size: 20;");

                generateResume.addRow(0, l1, l2);

                ArrayList<String> templateAttributes = template.getAttributes();
                for (int i = 0; i < templateAttributes.size(); i++) {
                        Label label = new Label(templateAttributes.get(i));
                        TextField textField = new TextField();
                        arr.add(textField);
                        generateResume.addRow(generateResume.getRowCount() + 1, label, textField);
                }

        }

        @FXML
        public void selectFromTemplateTable() throws SQLException, IOException {
                if (templateTableView.getSelectionModel().getSelectedCells() == null ||
                                templateTableView.getSelectionModel().getSelectedIndex() == -1) {
                        return;
                }

                int index = templateTableView.getSelectionModel().getSelectedIndex();
                String templateName = (String) templateNameColumn.getCellData(index);
                ObservableList<TablePosition> selectedCells = templateTableView.getSelectionModel().getSelectedCells();

                templateName1.setText(templateName);
                if (selectedCells.get(0).getTableColumn().equals(templateTrashColumn)) {
                        DBConnection.getInstance().deleteTemplate(templateName);
                        System.out.println(templateName);
                        fillTableViews();
                } else {
                        openTemplateScreen();
                        Template template = DBConnection.getInstance().getTemplateObject(templateName);
                        ObservableList<Node> children = templateList.getChildren();
                        children.clear();

                        for (String attribute : template.getAttributes()) {
                                TextField textField = new TextField(attribute);
                                textField.setMaxWidth(250);
                                textField.setPromptText("Enter an attribute");

                                String path = "images/trash.png";
                                Image image = new Image(getClass().getResource(path).toExternalForm());

                                Button deleteButton = new Button();
                                deleteButton.setGraphic(new ImageView(image));
                                deleteButton.setStyle("-fx-background-color: transparent;");

                                deleteButton.setOnAction(event -> {
                                        deleteButton.setMaxHeight(50);
                                        children.removeAll(textField, deleteButton);
                                });

                                templateList.addRow(templateList.getRowCount(), textField, deleteButton);
                        }
                        templateList.addRow(templateList.getRowCount() + 1, newTemplateButton);
                }
        }

        @FXML
        public void openSearchScreen() {
                resumeHBox.setVisible(false);
                searchHBox.setVisible(true);
                templateHBox.setVisible(false);

                firstEllipses.setVisible(false);
                secondEllipses.setVisible(true);
                thirdEllipses.setVisible(false);
        }

        @FXML
        public void openResumeScreen() {
                resumeHBox.setVisible(true);
                searchHBox.setVisible(false);
                templateHBox.setVisible(false);

                firstEllipses.setVisible(true);
                secondEllipses.setVisible(false);
                thirdEllipses.setVisible(false);
        }

        @FXML
        public void openTemplateScreen() {
                resumeHBox.setVisible(false);
                searchHBox.setVisible(false);
                templateHBox.setVisible(true);

                firstEllipses.setVisible(false);
                secondEllipses.setVisible(false);
                thirdEllipses.setVisible(true);

        }

        @FXML
        public void search() {
                String query = filterSearchField.getText();
                if (query.equals("")) {
                        return;
                }

                ArrayList<String> tagFilters = new ArrayList<String>();
                // tagFilters.add("SQLite");
                Index index = DBConnection.getInstance().getIndex();
                HashMap<String, String> findings = null;
                try {
                        findings = index.query(query, tagFilters);
                } catch (IOException | ParseException | ClassNotFoundException e) {
                        e.printStackTrace();
                }

                ObservableList<SearchResult> query_results = FXCollections.observableArrayList();
                searchTResumeNameColumn.setCellValueFactory(new PropertyValueFactory<SearchResult, String>("name"));
                searchTResumeDateColumn.setCellValueFactory(new PropertyValueFactory<SearchResult, String>("date"));

                for (String resumeName : findings.keySet()) {
                        query_results.add(new SearchResult(resumeName, findings.get(resumeName)));
                }
                searchTableView.setItems(query_results);
        }

        private void addTag() {
                // Search for tag query if it's found, added to combobox
                String tag_query = tagSearchField.getText();
                if (tag_query.equals("")) {
                        return;
                }

                ArrayList<String> available_tags = new ArrayList<>();
                try {
                        available_tags = DBConnection.getInstance().getTags();
                } catch (SQLException e) {
                        e.printStackTrace();
                }

                boolean match_found = false;
                ObservableList<String> combobox_tags = tags.getItems();
                for (String available_tag : available_tags) {
                        if (tag_query.toLowerCase(Locale.forLanguageTag("en")).equals(
                                        available_tag.toLowerCase(Locale.forLanguageTag("en")))) {
                                combobox_tags.add(available_tag);
                                match_found = true;
                                break;
                        }
                }

                if (match_found) {
                        tags.setItems(combobox_tags);
                        tagSearchField.clear();
                }
        }

        @FXML
        public void addResumeScreen() throws SQLException, IOException {
                resumeHBox.setVisible(true);
                searchHBox.setVisible(false);
                templateHBox.setVisible(false);
                saveResume();
        }

        String path = "";

        @FXML
        public void saveResume() throws SQLException, IOException {
                clearResumeContents();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a file");
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(null);

                // Create a task to perform the file conversion in the background
                Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                                try {
                                        String destinationDir = "src/main/resources/com/example/se302_project/images/pdfs/";
                                        File destinationFile = new File(destinationDir);
                                        if (!destinationFile.exists()) {
                                                destinationFile.mkdir();
                                        }
                                        if (file.exists()) {
                                                PDDocument document = PDDocument.load(file);
                                                PDFRenderer pdfRenderer = new PDFRenderer(document);

                                                int numberOfPages = document.getNumberOfPages();

                                                String fileName = file.getName().replace(".pdf", "");
                                                String fileExtension = "png";
                                                int dpi = 300;

                                                for (int i = 0; i < numberOfPages; ++i) {
                                                        File outPutFile = new File(destinationDir + fileName + "_"
                                                                        + (i + 1) + "." + fileExtension);
                                                        BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi,
                                                                        ImageType.RGB);
                                                        ImageIO.write(bImage, fileExtension, outPutFile);
                                                }

                                                document.close();

                                                path = "images/pdfs/" + fileName;
                                        } else {
                                                System.err.println(file.getName() + " File not exists");
                                        }
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                                return null;
                        }
                };

                // Set the image in the "originalResume" ImageView when the task is complete
                task.setOnSucceeded(event -> setOriginalResumeImage());

                // Start the task
                new Thread(task).start();
        }

        private void setOriginalResumeImage() {
                String path1 = "file:src/main/resources/com/example/se302_project/" + path + "_1" + ".png";
                Image image = new Image(path1);
                originalResume.setImage(image);
                double ratio;
                if (image.getWidth() > originalResumeVBox.getWidth()) {
                        ratio = image.getWidth() / originalResumeVBox.getWidth();
                } else {
                        ratio = originalResumeVBox.getWidth() / image.getWidth();
                }
                originalResume.setFitWidth(originalResumeVBox.getWidth());
                originalResume.setFitHeight(originalResumeVBox.getHeight() * ratio);
        }

        @FXML
        private void longDrawer() {
                drawerStackPane.setPrefWidth(275);
                drawerShort.setVisible(false);
                drawerLong.setVisible(true);
        }

        @FXML
        private void shortDrawer() {
                drawerStackPane.setPrefWidth(70);
                drawerShort.setVisible(true);
                drawerLong.setVisible(false);
        }

        @FXML
        private void toggleDrawer() {
                if (drawerShort.isVisible()) {
                        longDrawer();
                } else
                        shortDrawer();
        }

        @FXML
        public void fillTableViews() throws SQLException {
                String path = "images/trash.png";
                Image image = new Image(getClass().getResource(path).toExternalForm());

                ObservableList<CustomImage> resumeList = FXCollections
                                .observableArrayList();
                resumeNameColumn.setCellValueFactory(new PropertyValueFactory<CustomImage, String>("name"));
                resumeTrashColumn.setCellValueFactory(new PropertyValueFactory<CustomImage, ImageView>("image"));

                for (int i = 0; i < DBConnection.getInstance().getResumes().size(); i++) {
                        resumeList.add(new CustomImage(DBConnection.getInstance().getResumes().get(i),
                                        new ImageView(image)));
                }
                resumeTableView.setItems(resumeList);

                ObservableList<CustomImage> templateList = FXCollections
                                .observableArrayList();
                templateNameColumn.setCellValueFactory(new PropertyValueFactory<CustomImage, String>("name"));
                templateTrashColumn.setCellValueFactory(new PropertyValueFactory<CustomImage, ImageView>("image"));

                for (int i = 0; i < DBConnection.getInstance().getTemplates().size(); i++) {
                        templateList.add(new CustomImage(DBConnection.getInstance().getTemplates().get(i),
                                        new ImageView(image)));
                }

                templateTableView.setItems(templateList);
                templateCarouselList
                                .setItems(FXCollections.observableArrayList(DBConnection.getInstance().getTemplates()));
        }

        public void buttonAdd() {

                ObservableList<Node> children = templateList.getChildren();

                Node button = null;
                for (Node child : children) {
                        if (child == newTemplateButton) {
                                button = child;
                                break;
                        }
                }

                if (button != null) {
                        children.remove(button);
                }

                TextField textField = new TextField();
                textField.setMaxWidth(250);
                textField.setPromptText("Enter an attribute");

                String path = "images/trash.png";
                Image image = new Image(getClass().getResource(path).toExternalForm());

                Button deleteButton = new Button();
                deleteButton.setGraphic(new ImageView(image));
                deleteButton.setStyle("-fx-background-color: transparent;");

                deleteButton.setOnAction(event -> {
                        deleteButton.setMaxHeight(50);

                        children.removeAll(textField, deleteButton);
                });

                templateList.addRow(templateList.getRowCount(), textField, deleteButton);

                templateList.addRow(templateList.getRowCount() + 1, newTemplateButton);

        }

        public void saveTemplate() throws SQLException, IOException {
                // Get the list of attributes from the text fields in the templateList
                List<String> textFieldData = new ArrayList<>();
                for (Node node : templateList.getChildren()) {
                        if (node instanceof TextField) {
                                textFieldData.add(((TextField) node).getText());
                        }
                }

                // Get the template name from the templateName1 text field
                String templateName = templateName1.getText();

                // Check if the template already exists in the database
                if (DBConnection.getInstance().templateExists(templateName)) {
                        // If the template already exists, update the attributes in the database
                        DBConnection.getInstance().updateTemplateAttributes(templateName, textFieldData);
                } else {
                        // If the template does not exist, create a new template with the given
                        // attributes and add it to the database
                        Template template = new Template(templateName);
                        for (int i = 0; i < textFieldData.size(); i++) {
                                template.addAttribute(textFieldData.get(i));
                        }
                        DBConnection.getInstance().addTemplate(template);
                }
                // Clear the templateName1 text field and templateList, and add the
                // newTemplateButton back to the templateList
                templateName1.clear();
                templateList.getChildren().clear();
                templateList.addRow(0, newTemplateButton);

                // Refresh the templateTableView and resumeTableView
                fillTableViews();
        }

        @FXML
        private void helpMenu() throws IOException {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HelpScreen.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root, 800, 600);
                stage.setTitle("Help");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.showAndWait();
        }

        @FXML
        private Template courselSelect() throws SQLException {

                Template template = DBConnection.getInstance().getTemplateObject(
                                (String) templateCarouselList.getSelectionModel().getSelectedItem());
                generateResume(template);
                return template;
        }

        @FXML
        private void newSubmitButton() {
                try {
                        LocalDateTime date = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String formattedDate = date.format(formatter);
                        Resume resume = new Resume(resumeTitle.getText(), formattedDate, path, courselSelect());
                        HashMap<String, String> attributes = new HashMap<>();

                        ObservableList<Node> childrens = generateResume.getChildren();

                        String key = null;
                        String value = null;
                        int i = 0;

                        childrens.remove(0); // Removing the Attributes and Values strings.
                        childrens.remove(0);

                        for (Node node : childrens) {
                                if (generateResume.getColumnIndex(node) == 0) {
                                        Label label = (Label) node;
                                        key = label.getText();
                                }
                                if (generateResume.getColumnIndex(node) == 1) {
                                        if (node instanceof Label) {
                                                Label label = (Label) node;
                                                value = label.getText();
                                        }
                                        if (node instanceof TextField) {
                                                TextField textFiel = arr.get(i);
                                                value = textFiel.textProperty().get();
                                                i++;
                                        }
                                }
                                if (key != null && value != null) {
                                        attributes.put(key, value);
                                        key = null;
                                        value = null;
                                }
                        }

                        arr.clear();
                        generateResume.getChildren().clear();
                        for (TextField t : arr) {
                                System.out.println(t.getText());

                        }

                        resume.setAttributes(attributes);
                        DBConnection.getInstance().addResume(resume);
                        fillTableViews();
                        clearResumeContents();

                } catch (Exception e) {
                }
        }

        @FXML
        private void showGeneratedResume(Resume resume) {

                generateResume.getChildren().clear();
                Label l1 = new Label("Attributes");
                l1.setStyle("-fx-font-size: 20;");
                Label l2 = new Label("Values");
                l2.setStyle("-fx-font-size: 20;");

                generateResume.addRow(0, l1, l2);

                ArrayList<String> templateAttributes = resume.getTemplate().getAttributes();
                for (int i = 0; i < templateAttributes.size(); i++) {
                        Label key = new Label(templateAttributes.get(i));
                        Label value = new Label(resume.getAttributes().get(templateAttributes.get(i)));
                        generateResume.addRow(generateResume.getRowCount() + 1, key, value);
                }
                Label l3 = new Label("Tags");
                l3.setStyle("-fx-font-size: 20;");

                Label l4 = new Label("");
                generateResume.addRow(generateResume.getRowCount(), l4);
                generateResume.addRow(generateResume.getRowCount(), l3);

                for (int i = 0; i < resume.getTags().size() - 1; i += 2) {
                        Label label = new Label(resume.getTag(i));
                        Label label2 = new Label(resume.getTag(i + 1));
                        generateResume.addRow(generateResume.getRowCount(), label, label2);
                }
                if (resume.getTags().size() % 2 != 0) {
                        Label label = new Label(resume.getTag(resume.getTags().size() - 1));
                        generateResume.addRow(generateResume.getRowCount(), label);
                }
                Button button = new Button();
                button.setPrefWidth(80);
                button.setText("Edit");
                button.setStyle("-fx-background-color: white; -fx-border-color: grey; -fx-border-radius: 10; -fx-");

                button.setOnAction(e -> {
                        try {
                                openTags();
                        } catch (SQLException e1) {
                                e1.printStackTrace();
                        }
                });
                generateResume.addRow(generateResume.getRowCount(), new Label(""));
                generateResume.addRow(generateResume.getRowCount(), button);
        }

        private void clearResumeContents() {
                generateResume.getChildren().clear();
                Label l1 = new Label("Attributes");
                l1.setStyle("-fx-font-size: 20;");
                Label l2 = new Label("Values");
                l2.setStyle("-fx-font-size: 20;");
                generateResume.addRow(0, l1, l2);
                resumeTitle.setText(null);
                originalResume.setImage(null);
        }

        @FXML
        private void addTagDrop() throws SQLException {

                String tag = (String) tagDropDown.getSelectionModel().getSelectedItem();
                CustomImage image = (CustomImage) resumeTableView.getSelectionModel().getSelectedItem();
                Resume resume = DBConnection.getInstance().getResumeObject(image.getName());
                DBConnection.getInstance().addTag(resume, tag);
                tagDropDown.getSelectionModel().clearSelection();
                tagDropDown.setValue(null);

        }

        @FXML
        private void addTagText() throws SQLException {
                String tag = (String) tagTextField.getText();
                CustomImage image = (CustomImage) resumeTableView.getSelectionModel().getSelectedItem();
                Resume resume = DBConnection.getInstance().getResumeObject(image.getName());
                DBConnection.getInstance().addTag(resume, tag);
                tagDropDown.getItems().add(tag);
                tagTextField.clear();
                fillMyTags(resume);
        }

        @FXML
        private void fillMyTags(Resume resume) throws SQLException {
                myTagsDropDown.getItems().clear();
                for (String s : DBConnection.getInstance().getResumeTags(resume.getName())) {
                        myTagsDropDown.getItems().add(s);
                }
        }

        @FXML
        private void openTags() throws SQLException {
                modalHBox.setVisible(true);
                CustomImage image = (CustomImage) resumeTableView.getSelectionModel().getSelectedItem();
                Resume resume = DBConnection.getInstance().getResumeObject(image.getName());
                fillMyTags(resume);

                // create a gaussian blur effect
                GaussianBlur gaussian_blur = new GaussianBlur();

                // set radius for gaussian blur
                gaussian_blur.setRadius(20.0f);

                // set effect
                firstEllipses.setEffect(gaussian_blur);
                secondEllipses.setEffect(gaussian_blur);
                thirdEllipses.setEffect(gaussian_blur);
                allHbox.setEffect(gaussian_blur);
        }

        @FXML
        private void closeTags() throws SQLException {
                CustomImage image = (CustomImage) resumeTableView.getSelectionModel().getSelectedItem();
                Resume resume = DBConnection.getInstance().getResumeObject(image.getName());
                modalHBox.setVisible(false);
                firstEllipses.setEffect(null);
                secondEllipses.setEffect(null);
                thirdEllipses.setEffect(null);
                allHbox.setEffect(null);
                showGeneratedResume(resume);
        }

        @FXML
        private void deleteTag() throws SQLException {
                CustomImage image = (CustomImage) resumeTableView.getSelectionModel().getSelectedItem();
                Resume resume = DBConnection.getInstance().getResumeObject(image.getName());
                String tag = myTagsDropDown.getSelectionModel().getSelectedItem();
                DBConnection.getInstance().deleteTag(resume, tag);
                myTagsDropDown.getItems().remove(tag);
                myTagsDropDown.getSelectionModel().clearSelection();
                myTagsDropDown.setValue(null);
                fillAllTags();

        }

        @FXML
        private void fillAllTags() throws SQLException {
                tagDropDown.getItems().clear();
                for (String s : DBConnection.getInstance().getTags()) {
                        tagDropDown.getItems().add(s);
                }
        }
}
