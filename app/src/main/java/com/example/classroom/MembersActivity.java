package com.example.classroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class MembersActivity extends AppCompatActivity {

    private List<Users> teachersList = new ArrayList<>();
    private List<Users> studentsList = new ArrayList<>();
    private RecyclerView teachersRecyclerView, studentsRecyclerView;
    private MembersAdapter teachersAdapter, studentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);


        String classroomId = getIntent().getStringExtra("classroomId");
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
        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("members");

        // Initialize RecyclerViews
        teachersRecyclerView = findViewById(R.id.teacherRecyclerView);
        studentsRecyclerView = findViewById(R.id.studentRecyclerView);

        // Initialize Adapters
        teachersAdapter = new MembersAdapter(teachersList);
        studentsAdapter = new MembersAdapter(studentsList);

        // Set up RecyclerViews with LayoutManagers and Adapters
        teachersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teachersRecyclerView.setAdapter(teachersAdapter);

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsRecyclerView.setAdapter(studentsAdapter);

        Log.d("MembersActivity", "classroomId: " + classroomId);

        DatabaseReference classroomRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("classrooms_info")
                .child(classroomId);

        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");

        classroomRef.child("teachers").get().addOnSuccessListener(teachersSnapshot -> {
            if (teachersSnapshot.exists()) {
                for (DataSnapshot teacherSnapshot : teachersSnapshot.getChildren()) {
                    String teacherId = teacherSnapshot.getValue(String.class);

                    Log.d("MembersActivity", "teacherId: " + teacherId);
                    // Fetch teacher's data from Users node
                    usersRef.child(teacherId).get()
                            .addOnSuccessListener(teacherUserSnapshot -> {
                                if (teacherUserSnapshot.exists()) {
                                    Users teacher = teacherUserSnapshot.getValue(Users.class);
                                    teachersList.add(teacher);
                                    teachersAdapter.notifyDataSetChanged();
                                }
                            });
                }
            }
        });

        classroomRef.child("students").get().addOnSuccessListener(studentsSnapshot -> {
            if (studentsSnapshot.exists()) {
                for (DataSnapshot studentSnapshot : studentsSnapshot.getChildren()) {
                    String studentId = studentSnapshot.getValue(String.class);

                    Log.d("MembersActivity", "studentId: " + studentId);
                    // Fetch student's data from Users node
                    usersRef.child(studentId).get()
                            .addOnSuccessListener(studentUserSnapshot -> {
                                if (studentUserSnapshot.exists()) {
                                    Users student = studentUserSnapshot.getValue(Users.class);
                                    studentsList.add(student);
                                    studentsAdapter.notifyDataSetChanged();
                                }
                            });
                }
            }
        });

    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
