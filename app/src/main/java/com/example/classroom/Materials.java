package com.example.classroom;

import java.util.ArrayList;

public class Materials {
    private String description;
    private ArrayList<String> fileUrls;

    public Materials() {
        // Default constructor required for Firebase
    }

    public Materials(String description, ArrayList<String> fileUrls) {
        this.description = description;
        this.fileUrls = fileUrls;
    }

    public Materials(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getFileUrls() {
        return fileUrls;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFileUrls(ArrayList<String> fileUrls) {
        this.fileUrls = fileUrls;
    }
}
