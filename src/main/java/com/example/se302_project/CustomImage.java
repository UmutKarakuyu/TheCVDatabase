package com.example.se302_project;

import javafx.scene.image.ImageView;

public class CustomImage {

    private String name;
    private ImageView image;

    public CustomImage(String resumeName, ImageView image) {
        this.name = resumeName;
        this.image = image;
    }

    public void setImage(ImageView value) {
        image = value;
    }

    public ImageView getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void setName(String resumeName) {
        this.name = resumeName;
    }
}
