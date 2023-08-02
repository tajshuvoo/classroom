package com.example.classroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.classroom.model.Classroom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateClassroomActivity extends AppCompatActivity {

    private EditText editTextClassName;
    private EditText editTextSection;
    private EditText editTextRoom;
    private EditText editTextSubject;
    private Button buttonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_classroom);

        //status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Class");


        // Initialize views
        editTextClassName = findViewById(R.id.editTextClassName);
        editTextSection = findViewById(R.id.editTextSection);
        editTextRoom = findViewById(R.id.editTextRoom);
        editTextSubject = findViewById(R.id.editTextSubject);
        buttonCreate = findViewById(R.id.buttonCreate);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered values from EditTexts
                String className = editTextClassName.getText().toString().trim();
                String section = editTextSection.getText().toString().trim();
                String room = editTextRoom.getText().toString().trim();
                String subject = editTextSubject.getText().toString().trim();

                // Check if the classroom name is empty (required field)
                if (className.isEmpty()) {
                    Toast.makeText(CreateClassroomActivity.this, "Classroom Name is required!", Toast.LENGTH_SHORT).show();
                    return;
                } else{

                // Generate a unique ID using Firebase's push() method
                DatabaseReference classroomRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("classrooms");
                DatabaseReference newClassroomRef = classroomRef.push();
                String classroomId = newClassroomRef.getKey();

                //userid
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", null);

                //linked the classroom with the userId
                    DatabaseReference userRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                            .child("linked_classes").child(userId).child("classrooms");

                    userRef.child(classroomId).setValue(classroomId);
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    // Classroom ID added to linked_classes successfully
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    // Handle errors
//                                }
//                            });



                // Create a Classroom object with the information
                Classroom classroom = new Classroom(className, classroomId, room, section, subject, userId);

                // Save the Classroom object to Firebase Realtime Database
                newClassroomRef.setValue(classroom)
                        .addOnSuccessListener(aVoid -> {
                            // Classroom created successfully
                            Toast.makeText(CreateClassroomActivity.this, "Classroom created successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // Failed to create classroom
                            Toast.makeText(CreateClassroomActivity.this, "Failed to create classroom. Please try again.", Toast.LENGTH_SHORT).show();
                        });

                    startActivity(new Intent(CreateClassroomActivity.this, SecondActivity.class));
            }
            }


        });
    }
    // Handle back button press
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
