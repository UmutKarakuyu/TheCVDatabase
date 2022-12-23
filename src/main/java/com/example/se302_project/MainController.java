package com.example.se302_project;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainController {
        @FXML
        private TextField searchField;
        @FXML
        private TextField filterSearchField;
        @FXML
        private TextField templateName;
        @FXML
        private Button addResumeButton;
        @FXML
        private Button addTemplateButton;
        @FXML
        private Button leftBarButton;
        @FXML
        private Button filterOptionButton;
        @FXML
        private Button newTemplateButton;
        @FXML
        private ComboBox<String> tags;
        @FXML
        private ComboBox<String> templates;
        @FXML
        private TableView searchTableView;
        @FXML
        private VBox templateAttributeView;
        @FXML
        private ListView<String> resumeListView, templateListView;

        public void initialize() throws SQLException {

                for (int i = 0; i < DBConnection.getInstance().getResumes().size(); i++) {
                        resumeListView.getItems().add(DBConnection.getInstance().getResumes().get(i));
                }

                for (int i = 0; i < DBConnection.getInstance().getTemplates().size(); i++) {
                        templateListView.getItems().add(DBConnection.getInstance().getTemplates().get(i));
                }
        }
}