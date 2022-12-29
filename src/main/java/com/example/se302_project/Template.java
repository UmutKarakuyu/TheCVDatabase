package com.example.se302_project;

import java.util.ArrayList;

public class Template {
    private String title;
    private ArrayList<String> attributes;


    public Template(String title) {
        this.title = title;
        this.attributes = new ArrayList<String>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }

    public void setAttributes(ArrayList<String> attributes) {
        this.attributes = attributes;
    }

}
