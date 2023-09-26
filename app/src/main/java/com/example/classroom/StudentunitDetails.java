package com.example.classroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class StudentunitDetails extends AppCompatActivity implements MaterialsAdapter.OnItemClickListener {

    String classroomId;
    private RecyclerView recyclerView;
    private MaterialsAdapter materialsAdapter;
    private List<Materials> materialsList = new ArrayList<>();
    private DatabaseReference databaseReference; // Firebase Database reference
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentunit_details);
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

        Intent intent1 = getIntent();
        classroomId = intent1.getStringExtra("classroomId");
        String chapterNum = intent1.getStringExtra("chapterNum");
        String unitNum = intent1.getStringExtra("unitNum");
        String unitName = intent1.getStringExtra("unitName");

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(unitName);

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerViewMaterials);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        materialsAdapter = new MaterialsAdapter(materialsList);
        materialsAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(materialsAdapter);

        databaseReference = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("classrooms_info")
                .child(classroomId)
                .child("chapters")
                .child(chapterNum)
                .child("units")
                .child(unitNum)
                .child("materials");

        // Set up a ChildEventListener to fetch materials when they are added or changed
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // A new material has been added to the Firebase Database
                Materials material = dataSnapshot.getValue(Materials.class);
                if (material != null) {
                    materialsList.add(material);
                    materialsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            // Handle other ChildEventListener methods as needed

        });

    }

    @Override
    public void onItemClick(Materials material) {
        String description=material.getDescription();
        ArrayList<String> fileUrls=material.getFileUrls();
        // Handle item click here, e.g., launch DetailedNotice activity
        Intent intent = new Intent(StudentunitDetails.this, DetailedMaterial.class);
        intent.putExtra("description",  description);
        intent.putStringArrayListExtra("fileUrls", (ArrayList<String>) fileUrls);
        // Pass the Notice object as an extra
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
