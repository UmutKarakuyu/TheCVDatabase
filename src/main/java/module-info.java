module com.example.se302_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.apache.lucene.queryparser;
    requires org.apache.lucene.core;

    opens com.example.se302_project to javafx.fxml;

    exports com.example.se302_project;
}