package com.example.se302_project;

import java.io.File;
import java.util.ArrayList;

public class Resume {
    private String name;
    private String date;
    private File file;
    private ArrayList<String> tags;
    private Template template; // içerisindeki tüm attributelar template'ten geleceği için ekstra variable
                               // oluşturmadım.

    public Resume(String name, String date, File file, ArrayList<String> tags) {
        this.name = name;
        this.date = date;
        this.file = file;
        this.tags = tags;
    }

    public Resume(String name, String date, File file, ArrayList<String> tags, Template template) {
        this.name = name;
        this.date = date;
        this.file = file;
        this.tags = tags;
        this.template = template;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}
