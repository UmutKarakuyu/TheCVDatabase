package com.example.se302_project;

import java.util.ArrayList;
import java.util.HashMap;

public class Resume {
    private String name;
    private String date;
    private String fileName;
    private ArrayList<String> tags;
    private Template template;
    private HashMap<String, String> attributes;

    public Resume(String name, String date, String fileName, Template template) {
        this.name = name;
        this.date = date;
        this.fileName = fileName;
        this.tags = new ArrayList<>();
        this.template = template;
        this.attributes = new HashMap<String, String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getfileName() {
        return fileName;
    }

    public void setfileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getTag(int position) {
        String output = tags.get(position);
        return output;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

}
