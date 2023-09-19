package com.example.classroom;

import java.util.ArrayList;
import java.util.List;

public class Notice {
    private String description;
    private ArrayList<String> fileUrls;

    public Notice() {
        // Default constructor required by Firebase
    }
    public Notice(String description) {
        this.description = description;
    }
    public Notice(String description, ArrayList<String> fileUrls) {
        this.description = description;
        this.fileUrls = fileUrls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(ArrayList<String> fileUrls) {
        this.fileUrls = fileUrls;
    }
}
