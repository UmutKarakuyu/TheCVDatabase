package ResumeNLP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        // Resume Parser Test Code
        Path fileName = Path.of("src/main/resources/resume.txt");
        String resume_text = Files.readString(fileName);

        ResumeParser resparser = new ResumeParser("src/main/resources/linkedin_skills.txt",
                                                  "src/main/resources/titles_combined.txt",
                                                  "src/main/resources/stopwords.txt");
        List<String> tokens = resparser.extractTokens(resume_text);
        resparser.match(tokens, "TITLE");
        resparser.match(tokens, "SKILL");
        
        // Index Searcher Test Code
        Index index = new Index(10);
        String resume1_text = Files.readString(Path.of("src/main/resources/resume1.txt"));
        index.addDoc("resume1", resume1_text, new ArrayList<String>());
        String resume2_text = Files.readString(Path.of("src/main/resources/resume2.txt"));
        index.addDoc("resume2", resume2_text, new ArrayList<String>());
        String resume3_text = Files.readString(Path.of("src/main/resources/resume3.txt"));
        index.addDoc("resume3", resume3_text, new ArrayList<String>());

        index.query("lucrabot", "text");
        index.query("izmir University of economics", "text");
        index.query("izmir", "text");
        index.query("tps", "text");
        
        index.close();
    }
}
