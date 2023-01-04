package com.example.se302_project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class ResumeParser {
    private final int MAX_GROUP_LENGTH = 4;

    private List<String> stopwords;
    private LinkedHashMap<String, String> skillMatches;
    private LinkedHashMap<String, String> titleMatches;

    public ResumeParser(String skills_path, String titles_path, String stopwords_path) throws IOException {
        List<String> stopwords = read_file(stopwords_path);
        this.stopwords = stopwords;

        this.skillMatches = this.createMatchList(skills_path);
        this.titleMatches = this.createMatchList(titles_path);
    }

    private List<String> read_file(String path){
        BufferedReader reader;
        List<String> data = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(path), "UTF-8"));
			for (String line; (line = reader.readLine()) != null;) {
                data.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
            path = "src/main/resources/" + path;
            try {
                data = Files.readAllLines(Paths.get(path));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return data;
    }

    private LinkedHashMap<String, String> createMatchList(String path) throws IOException {
        List<String> list = read_file(path);
        LinkedHashMap<String, String> matchList = new LinkedHashMap<>();
        for (String original_text : list) {
            String text = this.lowercaseText(original_text);
            List<String> text_tokens = this.splitTokens(text);
            List<String> refined_text_tokens = this.removePunctuationMark(text_tokens);
            String refined_text = "";
            for (String refined_skill_token : refined_text_tokens) {
                refined_text += refined_skill_token + " ";
            }
            refined_text = refined_text.trim();
            refined_text = refined_text.replaceAll(" ", "_");

            matchList.put(refined_text, original_text);
        }

        return matchList;
    }

    private String lowercaseText(String text) {
        return text.toLowerCase(Locale.forLanguageTag("en"));
    }

    private String capitalizeText(String text) {
        char[] chars = this.lowercaseText(text).toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
          if (!found && Character.isLetter(chars[i])) {
            chars[i] = Character.toUpperCase(chars[i]);
            found = true;
          } else if (Character.isWhitespace(chars[i])) {
            found = false;
          }
        }
        return String.valueOf(chars);
      }

    private List<String> removePunctuationMark(List<String> tokens) {
        List<String> refined_tokens = new LinkedList<>();
        for (String token : tokens) {
            token = this.cleanToken(token);
            if (!Pattern.matches("\\p{Punct}", token)) {
                if (!token.equals("")) {
                    refined_tokens.add(token);
                }
            }
        }
        return refined_tokens;
    }

    private List<String> splitTokens(String text) {
        StringTokenizer st = new StringTokenizer(text, " .\n?!()'/" + '"');
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        return tokens;
    }

    private String cleanToken(String token) {
        token = token.replace(",", "");
        token = token.replace(".", "");
        token = token.replace("\n", "").replace("\r", "");
        token = token.trim();
        return token;
    }

    private List<String> removeStopWords(List<String> tokens) {
        List<String> refined_tokens = new LinkedList<>();
        for (String token : tokens) {
            boolean isStopWord = false;
            for (String stopword : this.stopwords) {
                if (token.equals(stopword)) {
                    isStopWord = true;
                    break;
                }
            }

            if (!isStopWord) {
                refined_tokens.add(token);
            }
        }
        return refined_tokens;
    }

    public List<String> extractTokensFromResume(String text) {
        text = this.lowercaseText(text);
        List<String> tokens = this.splitTokens(text);
        tokens = this.removeStopWords(tokens);
        List<String> refined_tokens = this.removePunctuationMark(tokens);
        return refined_tokens;
    }

    public List<String> match(List<String> tokens, String type) throws IllegalArgumentException {
        HashMap<String, String> matchList;
        if (type.equals("SKILL")) {
            matchList = this.skillMatches;
        } else if (type.equals("TITLE")) {
            matchList = this.titleMatches;
        } else {
            throw new IllegalArgumentException("Type value only can be 'SKILL' or 'TITLE'.");
        }

        List<String> matches = new LinkedList<>();
        for (int i = 0; i < tokens.size(); i++) {
            for (int j = 1; j <= this.MAX_GROUP_LENGTH; j++) {
                try {
                    String token_group = "";
                    for (int t = 0; t < j; t++) {
                        token_group += tokens.get(i + t) + " ";
                    }
                    token_group = token_group.trim();
                    token_group = token_group.replaceAll(" ", "_");

                    String orig_text = matchList.get(token_group);
                    if (orig_text != null) {
                        matches.add(orig_text);
                    }

                } catch (Exception e) {
                    continue;
                }
            }
        }

        ArrayList<String> ordered_matches = new ArrayList<>();
        for (String orig_text : matchList.values()) {
            for (String match : matches) {
                if (orig_text.equals(match)) {
                    String capitalized_text = this.capitalizeText(orig_text);
                    if(type.equals("TITLE")){
                        capitalized_text = "["+capitalized_text+"]";
                    }
                    if (!ordered_matches.contains(capitalized_text)) {
                        ordered_matches.add(capitalized_text);
                    }
                    break;
                }
            }
        }

        return ordered_matches;
    }
}