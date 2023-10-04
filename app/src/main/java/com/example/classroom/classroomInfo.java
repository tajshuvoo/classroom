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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classroom.model.Classroom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class classroomInfo extends AppCompatActivity {

    TextView ClassroomName;
    TextView ClassroomSection;
    TextView ClassroomRoom;
    TextView ClassroomSub;
    TextView ClassroomId;
    Button deleteButton;
    RelativeLayout deleteConfirmationLayout;
    Button confirmButton;
    Button cancelButton;

    Classroom classroom;
    String className;
    String section;
    String room;
    String subject;
    String classroomId;
    String userId;
    ArrayList<String> teacherUids = new ArrayList<>();
    ArrayList<String> studentUids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_info);

        // Initialize UI elements
        ClassroomName = findViewById(R.id.classroomName);
        ClassroomSection = findViewById(R.id.classroomSection);
        ClassroomRoom = findViewById(R.id.classroomRoom);
        ClassroomSub = findViewById(R.id.classroomSub);
        ClassroomId = findViewById(R.id.textViewDescription);
        deleteButton = findViewById(R.id.button);
        deleteConfirmationLayout = findViewById(R.id.deleteConfirmationLayout);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        // Get classroomId from intent
        Intent intent = getIntent();
        classroomId = intent.getStringExtra("classroomId");

        // Initialize Firebase references
        DatabaseReference classroomRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("classrooms").child(classroomId);

        DatabaseReference teachersRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("classrooms_info").child(classroomId).child("teachers");

        DatabaseReference studentsRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("classrooms_info").child(classroomId).child("students");
        // Retrieve classroom information
        classroomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    classroom = dataSnapshot.getValue(Classroom.class);
                    className = classroom.getClassName();
                    section = classroom.getSection();
                    room = classroom.getRoom();
                    subject = classroom.getSubject();

                    // Set the text values
                    ClassroomName.setText(className);
                    ClassroomSection.setText(section);
                    ClassroomRoom.setText(room);
                    ClassroomSub.setText(subject);
                    ClassroomId.setText(classroomId);

                    // Retrieve all teacher UIDs for this classroom
                    teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                                    String teacherId = teacherSnapshot.getKey();
                                    teacherUids.add(teacherId);
                                }

                                // Check if the logged-in user is a teacher for this classroom
                                if (teacherUids.contains(userId)) {
                                    deleteButton.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                        String studentId = teacherSnapshot.getKey();
                       studentUids.add(studentId);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });


        // Status bar color
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
        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Classroom information");

        // Delete button click listener
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the delete confirmation layout
                deleteConfirmationLayout.setVisibility(View.VISIBLE);
            }
        });

        // Confirm button click listener
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the deletion operation

                // Delete the classroom from classrooms/classroomId
                classroomRef.removeValue();

                // Delete it from classrooms_info/classroomId
                DatabaseReference classroomsInfoRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                        .getReference().child("classrooms_info").child(classroomId);
                classroomsInfoRef.removeValue();

                // Loop through all teacher UIDs and remove the classroomId from their linked classes
                for (String teacherId : teacherUids) {
                    DatabaseReference teacherLinkedClassesRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference().child("linked_classes").child(teacherId).child("classrooms");
                    teacherLinkedClassesRef.child(classroomId).removeValue();
                }
                // Loop through all teacher UIDs and remove the classroomId from their linked classes
                for (String studentId : studentUids) {
                    DatabaseReference teacherLinkedClassesRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference().child("linked_classes").child(studentId).child("classrooms");
                    teacherLinkedClassesRef.child(classroomId).removeValue();
                }

                // Hide the delete confirmation layout
                deleteConfirmationLayout.setVisibility(View.GONE);

                // Optionally, navigate back to the previous activity or perform any other necessary actions
            }
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the delete confirmation layout
                deleteConfirmationLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
