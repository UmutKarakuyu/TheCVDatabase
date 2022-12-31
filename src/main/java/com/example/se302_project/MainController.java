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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

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
        private HBox resumeHBox, searchHBox, templateHBox, modalHBox;
        @FXML
        private Pane modalPane;
        @FXML
        private ListView modalListView;
        @FXML
        private TextField modalResumeName;
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
        private TextField templateName;
        @FXML
        private Button newTemplateButton;
        @FXML
        private GridPane templateList;
        @FXML
        private VBox templateNameVBox;

        public void initialize() throws SQLException, IOException {
                tagSearchField.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                                addTag();
                        }
                });

                modalHBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                        if (modalHBox.getWidth() < 1250)
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
                        String filepath = "src/main/resources/com/example/se302_project/images/pdfs/"
                                        + resume.getfileName()
                                        + ".png";
                        File file = new File(filepath);

                        originalResume.setImage(null);
                        file.delete();

                        DBConnection.getInstance().deleteResume(resumeName);
                        initialize();

                } else {
                        String fileName = resume.getfileName().replace(".pdf", "_1");
                        String path1 = "images/pdfs/" + fileName + ".png";
                        Image image = new Image(getClass().getResource(path1).toExternalForm());
                        originalResume.setImage(image);

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
        public void showModal() throws SQLException {
                modalHBox.setVisible(true);
                for (int i = 0; i < DBConnection.getInstance().getTemplates().size(); i++) {
                        modalListView.getItems().add(DBConnection.getInstance().getTemplates().get(i));
                }
        }

        @FXML
        public void addResumeScreen() throws SQLException {
                resumeHBox.setVisible(true);
                searchHBox.setVisible(false);
                templateHBox.setVisible(false);
                showModal();
        }

        @FXML
        public void saveResume() throws SQLException, IOException {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a file");
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(null);

                String name = modalResumeName.getText();
                LocalDateTime date = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedDate = date.format(formatter);

                Template template = DBConnection.getInstance()
                                .getTemplateObject(modalListView.getSelectionModel().getSelectedItem().toString());

                Resume resume = new Resume(name, formattedDate,
                                file.getName(),
                                template);
                DBConnection.getInstance().addResume(resume);
                try {
                        String destinationDir = "src/main/resources/com/example/se302_project/images/pdfs/";
                        File destinationFile = new File(destinationDir);
                        if (!destinationFile.exists()) {
                                destinationFile.mkdir();
                                System.out.println("Folder Created -> " + destinationFile.getAbsolutePath());
                        }
                        if (file.exists()) {
                                System.out.println("Images copied to Folder Location: "
                                                + destinationFile.getAbsolutePath());
                                PDDocument document = PDDocument.load(file);
                                PDFRenderer pdfRenderer = new PDFRenderer(document);

                                int numberOfPages = document.getNumberOfPages();
                                System.out.println("Total files to be converting -> " + numberOfPages);

                                String fileName = file.getName().replace(".pdf", "");
                                String fileExtension = "png";
                                /*
                                 * 600 dpi give good image clarity but size of each image is 2x times of 300
                                 * dpi.
                                 * Ex: 1. For 300dpi 04-Request-Headers_2.png expected size is 797 KB
                                 * 2. For 600dpi 04-Request-Headers_2.png expected size is 2.42 MB
                                 */
                                int dpi = 300;// use less dpi for to save more space in harddisk.
                                // For professional usage
                                // you can use more than 300dpi

                                for (int i = 0; i < numberOfPages; ++i) {
                                        File outPutFile = new File(destinationDir + fileName + "_" + (i + 1) + "."
                                                        + fileExtension);
                                        BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
                                        ImageIO.write(bImage, fileExtension, outPutFile);
                                }

                                document.close();
                                System.out.println("Converted Images are saved at -> "
                                                + destinationFile.getAbsolutePath());
                        } else {
                                System.err.println(file.getName() + " File not exists");
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }

                fillTableViews();
                closeModal();

        }

        @FXML
        private void closeModal() {
                modalHBox.setVisible(false);
                modalResumeName.clear();
                modalListView.getSelectionModel().clearSelection();
                modalListView.getItems().clear();
        }

        @FXML
        private void longDrawer() {
                drawerStackPane.setPrefWidth(350);
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
                        if (child instanceof Button) {
                                button = child;
                                break;
                        }
                }
                if (button != null) {
                        children.remove(button);
                }

                TextField textField = new TextField();
                textField.setMaxWidth(250);
                GridPane.setRowIndex(textField, GridPane.getRowIndex(button));
                children.add(textField);

                templateList.addRow(templateList.getRowCount() + 1, newTemplateButton);

        }

        public class Triple {
                Node n;
                int row;
                int col;

                public Triple(Node n, int row, int col) {
                        this.row = row;
                        this.col = col;
                        this.n = n;
                }
        }

        public static Node getItem(List<Triple> l, int findRow, int findCol) {

                for (int i = 0; i < l.size(); i++) {
                        if (l.get(i).row == findRow && l.get(i).col == findCol) {
                                return l.get(i).n;
                        }
                }
                return null;
        }

        public void saveTemplate() throws SQLException, IOException {
                List<Triple> list = new ArrayList<>();
                for (int i = 0; i < templateList.getChildren().size(); i++) {
                        list.add(new Triple(templateList.getChildren().get(i),
                                        GridPane.getRowIndex(templateList.getChildren().get(i)),
                                        GridPane.getColumnIndex(templateList.getChildren().get(i))));

                        TextField tempText = (TextField) getItem(list, 1, 1);
                        assert tempText != null;

                        // for(int i= 1; i< templateList.getColumnCount(); i++){
                        // templateList.get
                        // }

                        Template template = new Template(templateName.getText());
                        DBConnection.getInstance().addTemplate(template);
                        initialize();

                }
        }

}
