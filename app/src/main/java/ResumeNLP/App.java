package ResumeNLP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        Path fileName = Path.of("/resume.txt");
        String resume_text = Files.readString(fileName);

        ResumeParser resparser = new ResumeParser("/linkedin_skills.txt",
                                                  "/titles_combined.txt",
                                                  "/stopwords.txt");
        List<String> tokens = resparser.extractTokens(resume_text);
        resparser.match(tokens, "TITLE");
        resparser.match(tokens, "SKILL");
    }
}
