package ResumeNLP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.lucene.queryparser.classic.ParseException;

public class App {
    public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException {
        // Resume Parser Test Code
        /*
        Path fileName = Path.of("src/main/resources/resume1.txt");
        String resume_text = Files.readString(fileName);

        ResumeParser resparser = new ResumeParser("src/main/resources/linkedin_skills.txt",
                                                  "src/main/resources/titles_combined.txt",
                                                  "src/main/resources/stopwords.txt");
        List<String> tokens = resparser.extractTokens(resume_text);
        resparser.match(tokens, "TITLE");
        resparser.match(tokens, "SKILL"); */
        
        // Index Searcher Test Code
        Index index = new Index(10);
        String resume1_text = Files.readString(Path.of("src/main/resources/resume1.txt"));
        ArrayList<String> tag1 = new ArrayList<String>();
        tag1.add("Python");
        index.addDoc("resume1", resume1_text, tag1);

        String resume2_text = Files.readString(Path.of("src/main/resources/resume2.txt"));
        ArrayList<String> tag2 = new ArrayList<String>();
        tag2.add("Java");
        tag2.add("Django");
        index.addDoc("resume2", resume2_text, tag2);

        String resume3_text = Files.readString(Path.of("src/main/resources/resume3.txt"));
        ArrayList<String> tag3 = new ArrayList<String>();
        tag3.add("Java");
        tag3.add("Django");
        tag3.add("SQLite");
        index.addDoc("resume3", resume3_text, tag3);

        index.query("lucrabot", new ArrayList<String>());
        index.query("izmir University of economics", new ArrayList<String>());
        index.query("izmir", new ArrayList<String>());
        index.query("python", new ArrayList<String>());
        index.query("tps", new ArrayList<String>());

        ArrayList<String> tagFilters = new ArrayList<String>();
        tagFilters.add("SQLite");
        index.query("tps", tagFilters);
        
        index.close();
    }
}
