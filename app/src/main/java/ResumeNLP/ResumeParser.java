package ResumeNLP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class ResumeParser{
    private final int MAX_GROUP_LENGTH = 4;

    private StanfordCoreNLP pipeline;
    private List<String> stopwords;
    private HashMap<String, String> skillMatches;
    private HashMap<String, String> titleMatches;
    
    public ResumeParser(String skills_path, String titles_path, String stopwords_path) throws IOException{
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,pos,depparse,lemma,ner,kbp"); // tokenize,lemma,ner,parse,depparse,coref,kbp
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        this.pipeline = pipeline;

        List<String> stopwords = Files.readAllLines(Paths.get(stopwords_path));
        this.stopwords = stopwords;

        this.skillMatches = this.createMatchList(skills_path);
        this.titleMatches = this.createMatchList(titles_path);
    }

    private HashMap<String, String> createMatchList(String path) throws IOException{
        List<String> list = Files.readAllLines(Paths.get(path));
        HashMap<String, String> matchList = new HashMap<>();
        for(String original_text: list){
            String text = this.lowercaseText(original_text);

            CoreDocument text_doc = new CoreDocument(text);
            this.pipeline.annotate(text_doc);
            List<CoreLabel> text_tokens = text_doc.tokens();
            List<String> refined_text_tokens = this.removePunctuationMark(text_tokens);
            String refined_text = "";
            for(String refined_skill_token: refined_text_tokens){
                refined_text += refined_skill_token + " ";
            }
            refined_text = refined_text.trim();
            refined_text = refined_text.replaceAll(" ", "_");

            matchList.put(refined_text, original_text);
        }

        return matchList;
    }

    private String lowercaseText(String text){
        return text.toLowerCase(Locale.forLanguageTag("en"));
    }

    private List<String> removePunctuationMark(List<CoreLabel> tokens){
        List<String> refined_tokens = new LinkedList<>();
        for(CoreLabel token: tokens){
            if(!Pattern.matches("\\p{Punct}", token.lemma())){
                refined_tokens.add(token.lemma());
            }
        }
        return refined_tokens;
    }

    private List<CoreLabel> removeStopWords(List<CoreLabel> tokens){
        List<CoreLabel> refined_tokens = new LinkedList<>();
        for(CoreLabel token: tokens){
            boolean isStopWord = false;
            for(String stopword: this.stopwords){
                if(token.originalText().equals(stopword)){
                    isStopWord = true;
                    break;
                }
            }

            if(!isStopWord){
                refined_tokens.add(token);
            }
        }
        return refined_tokens;
    }

    public List<String> extractTokens(String text){
        text = this.lowercaseText(text);

        CoreDocument document = new CoreDocument(text);
        this.pipeline.annotate(document);
        List<CoreLabel> tokens = document.tokens();

        tokens = this.removeStopWords(tokens);
        List<String> refined_tokens = this.removePunctuationMark(tokens);

        return refined_tokens;
    }

    public List<String> match(List<String> tokens, String type) throws IllegalArgumentException{
        HashMap<String, String> matchList;
        if(type.equals("SKILL")){
            matchList = this.skillMatches;
        } else if(type.equals("TITLE")){
            matchList = this.titleMatches;
        } else{
            throw new IllegalArgumentException("Type value only can be 'SKILL' or 'TITLE'.");
        }

        List<String> matches = new LinkedList<>();
        for(int i=0; i<tokens.size(); i++){
            for(int j=1; j<=this.MAX_GROUP_LENGTH; j++){
                try{
                    String token_group = "";
                    for(int t=0; t<j; t++){
                        token_group += tokens.get(i+t) + " ";
                    }
                    token_group = token_group.trim();
                    token_group = token_group.replaceAll(" ", "_");
                    
                    String orig_text = matchList.get(token_group);
                    if(orig_text != null){
                        System.out.println(orig_text);
                        matches.add(orig_text);
                    }
                    
                } catch(Exception e){
                    continue;
                }
            }
        }

        return matches;
    }
}