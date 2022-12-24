package com.example.se302_project;

public class SearchResult {

    private String name;
    private String date;

    public SearchResult(String resumeName, String date) {
        this.name = resumeName;
        this.date = date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String resumeName) {
        this.name = resumeName;
    }
}
