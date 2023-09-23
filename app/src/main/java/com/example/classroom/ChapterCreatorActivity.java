package com.example.classroom;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.classroom.R;
import com.example.classroom.model.Classroom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChapterCreatorActivity extends AppCompatActivity {

    private Classroom classroom;
    private EditText editTextChapterNumber;
    private EditText editTextChapterName;
    private Button buttonCreateChapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_creator);

        // Initialize UI elements
        editTextChapterNumber = findViewById(R.id.editTextChapterNumber);
        editTextChapterName = findViewById(R.id.editTextChapterName);
        buttonCreateChapter = findViewById(R.id.buttonCreateChapter);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chapter Creator");

        // Firebase Realtime Database setup
        String classroomId = getIntent().getStringExtra("classroomId");
        DatabaseReference classroomRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("classrooms_info").child(classroomId);

        // Handle button click
        buttonCreateChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChapter(classroomRef, classroomId);
            }
        });
    }

    private void createChapter(DatabaseReference classroomRef, String classroomId) {
        String chapterNumber = editTextChapterNumber.getText().toString().trim();
        String chapterName = editTextChapterName.getText().toString().trim();

        if (!chapterNumber.isEmpty() && !chapterName.isEmpty()) {
            // Check if the chapter number is unique
            DatabaseReference chaptersRef = classroomRef.child("chapters");
            chaptersRef.child(chapterNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // Chapter number is unique, create the chapter
                        chaptersRef.child(chapterNumber).child("chapterName").setValue(chapterName);
                        // Optionally, you can perform additional actions here.
                        // For example, navigate back to the previous activity or show a success message.
                        finish(); // Close the ChapterCreatorActivity
                    } else {
                        // Chapter number already exists, show an error message
                        editTextChapterNumber.setError("Chapter number already exists");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        } else {
            // Show an error message if any field is empty
            if (chapterNumber.isEmpty()) {
                editTextChapterNumber.setError("Chapter number is required");
            }
            if (chapterName.isEmpty()) {
                editTextChapterName.setError("Chapter name is required");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
