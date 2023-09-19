package com.example.classroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class DetailedNotice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_notice);

        // Receive the string
        String description = getIntent().getStringExtra("description");

// Receive the list of strings
        ArrayList<String> fileUrls = getIntent().getStringArrayListExtra("fileUrls");
    }
}