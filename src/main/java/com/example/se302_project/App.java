package com.example.se302_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException, ParseException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("CvDatabase.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setMinHeight(700);
        stage.setMinWidth(1200);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();

        /*
        Path fileName = Path.of("src/main/resources/com/example/se302_project/nlp/resume1.txt");
        String resume_text = Files.readString(fileName);
        ResumeParser resparser = new ResumeParser("src/main/resources/com/example/se302_project/nlp/skills_t100.txt",
                                                  "src/main/resources/com/example/se302_project/nlp/titles/titles_combined.txt",
                                                  "src/main/resources/com/example/se302_project/nlp/stopwords.txt");
        List<String> tokens = resparser.extractTokensFromResume(resume_text);
        System.out.println("TITLES");
        resparser.match(tokens, "TITLE");
        System.out.println("===");
        System.out.println("SKILLS");
        System.out.println("===");
        resparser.match(tokens, "SKILL");
        */
    }

    public static void main(String[] args) {
        launch();
    }
}