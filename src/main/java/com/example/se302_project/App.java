package com.example.se302_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException, ParseException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("CvDatabase.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        String path = "images/paintedLogo.png";
        Image image = new Image(getClass().getResource(path).toExternalForm());
        stage.getIcons().add(image);
        stage.setTitle("CV Database");
        stage.setMinHeight(800);
        stage.setMinWidth(1300);
        stage.setScene(scene);
        stage.show();

        /*
         * Path fileName =
         * Path.of("src/main/resources/com/example/se302_project/nlp/resume1.txt");
         * String resume_text = Files.readString(fileName);
         * ResumeParser resparser = new ResumeParser(
         * "src/main/resources/com/example/se302_project/nlp/skills_t100.txt",
         * "src/main/resources/com/example/se302_project/nlp/titles/titles_combined.txt",
         * "src/main/resources/com/example/se302_project/nlp/stopwords.txt");
         * List<String> tokens = resparser.extractTokensFromResume(resume_text);
         * System.out.println("TITLES");
         * resparser.match(tokens, "TITLE");
         * System.out.println("===");
         * System.out.println("SKILLS");
         * System.out.println("===");
         * resparser.match(tokens, "SKILL");
         * 
         * 
         * Index index = new Index("index2", 10);
         * String resume1_text = Files.readString(Path.of(
         * "src/main/resources/com/example/se302_project/nlp/resume1.txt"));
         * ArrayList<String> tag1 = new ArrayList<String>();
         * tag1.add("Python");
         * index.addDoc("resume1", "1", resume1_text, tag1);
         * String resume2_text = Files.readString(Path.of(
         * "src/main/resources/com/example/se302_project/nlp/resume2.txt"));
         * ArrayList<String> tag2 = new ArrayList<String>();
         * tag2.add("Java");
         * tag2.add("Django");
         * index.addDoc("resume2", "2", resume2_text, tag2);
         * String resume3_text = Files.readString(Path.of(
         * "src/main/resources/com/example/se302_project/nlp/resume3.txt"));
         * ArrayList<String> tag3 = new ArrayList<String>();
         * tag3.add("Java");
         * tag3.add("Django");
         * tag3.add("SQLite");
         * index.addDoc("resume3", "3", resume3_text, tag3);
         * index.query("lucrabot", new ArrayList<String>());
         * index.query("izmir University of economics", new ArrayList<String>());
         * index.query("izmir", new ArrayList<String>());
         * 
         * index.deleteDoc("resume1");
         * index.deleteDoc("resume2");
         * index.deleteDoc("resume3");
         * index.query("python", new ArrayList<String>());
         * index.query("tps", new ArrayList<String>());
         * ArrayList<String> tagFilters = new ArrayList<String>();
         * tagFilters.add("SQLite");
         * index.query("tps", tagFilters);
         * 
         * index.close();
         */
    }

    public static void main(String[] args) {
        launch();
    }
}