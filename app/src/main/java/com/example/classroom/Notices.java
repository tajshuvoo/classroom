package com.example.classroom;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener; // Add this import
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

public class Notices extends AppCompatActivity implements NoticesAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private NoticesAdapter noticesAdapter;
    private List<Notice> notices = new ArrayList<>();
    private DatabaseReference databaseReference; // Firebase Database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerViewNotices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticesAdapter = new NoticesAdapter(notices);
        noticesAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(noticesAdapter);

        // Initialize Firebase Database reference
        String classroomId = getIntent().getStringExtra("classroomId");
        databaseReference = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("classrooms_info")
                .child(classroomId)
                .child("notices");

        // Set up a ChildEventListener to fetch notices when they are added or changed
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // A new notice has been added to the Firebase Database
                Notice notice = dataSnapshot.getValue(Notice.class);
                if (notice != null) {
                    notices.add(notice);
                    noticesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // A notice has been updated in the Firebase Database
                // You can handle updates here if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // A notice has been removed from the Firebase Database
                // You can handle removals here if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // A notice has been moved within the Firebase Database
                // You can handle moves here if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });


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
        getSupportActionBar().setTitle("Notifications");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemClick(Notice notice) {
        String description=notice.getDescription();
        ArrayList<String> fileUrls=notice.getFileUrls();
        // Handle item click here, e.g., launch DetailedNotice activity
        Intent intent = new Intent(Notices.this, DetailedNotice.class);
        intent.putExtra("description",  description);
        intent.putStringArrayListExtra("fileUrls", (ArrayList<String>) fileUrls);
        // Pass the Notice object as an extra
        startActivity(intent);
    }
}
