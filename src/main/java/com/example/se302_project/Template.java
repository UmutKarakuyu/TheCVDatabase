package com.example.se302_project;

import java.util.HashMap;

public class Template {
    private String title;
    private HashMap attributes; // Hem String hem de ImageView gibi componentleri tutabileceği için type
                                // vermedim.

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap attributes) {
        this.attributes = attributes;
    }

    public Template(String title, HashMap attributes) {
        this.title = title;
        this.attributes = attributes;
    }
}
