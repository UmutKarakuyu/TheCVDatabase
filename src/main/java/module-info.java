module com.example.se302_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.se302_project to javafx.fxml;
    exports com.example.se302_project;
}