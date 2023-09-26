package com.example.classroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.classroom.model.Classroom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class classroomInfo extends AppCompatActivity {

    TextView ClassroomName;
    TextView ClassroomSection;
    TextView ClassroomRoom;
    TextView ClassroomSub;
    TextView ClassroomId;

    Classroom classroom;
    String className;
    String section;
    String room;
    String subject;
    String teacherUid;

    String classroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_info);

        ClassroomName = findViewById(R.id.classroomName);
        ClassroomSection = findViewById(R.id.classroomSection);
        ClassroomRoom = findViewById(R.id.classroomRoom);
        ClassroomSub = findViewById(R.id.classroomSub);
        ClassroomId = findViewById(R.id.textViewDescription);

        Intent intent = getIntent();
        classroomId = intent.getStringExtra("classroomId");

        DatabaseReference classroomRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("classrooms").child(classroomId);
        classroomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    classroom = dataSnapshot.getValue(Classroom.class);

                    className = classroom.getClassName();
                    section = classroom.getSection();
                    room = classroom.getRoom();
                    subject = classroom.getSubject();
                    teacherUid = classroom.getTeacherUid();

                    // Set the text values here
                    ClassroomName.setText(className);
                    ClassroomSection.setText(section);
                    ClassroomRoom.setText(room);
                    ClassroomSub.setText(subject);
                    ClassroomId.setText(classroomId);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}