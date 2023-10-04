package com.example.classroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddUnit extends AppCompatActivity {

    private EditText editTextUnitNumber;
    private EditText editTextUnitName;
    private Button buttonCreateUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);

        // Initialize UI elements
        editTextUnitNumber = findViewById(R.id.editTextUnitNumber);
        editTextUnitName = findViewById(R.id.editTextUnitName);
        buttonCreateUnit = findViewById(R.id.buttonCreateUnit);

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
        getSupportActionBar().setTitle("Add Lesson");

        // Firebase Realtime Database setup
        String classroomId = getIntent().getStringExtra("classroomId");
        String chapterNum = getIntent().getStringExtra("chapterNum");
        DatabaseReference unitsRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("classrooms_info").child(classroomId).child("chapters").child(chapterNum).child("units");

        // Handle button click
        buttonCreateUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUnit(unitsRef);
            }
        });
    }

    private void createUnit(DatabaseReference unitsRef) {
        String unitNumber = editTextUnitNumber.getText().toString().trim();
        String unitName = editTextUnitName.getText().toString().trim();

        if (!unitNumber.isEmpty() && !unitName.isEmpty()) {
            // Check if the unit number is unique
            unitsRef.child(unitNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Unit number already exists, show an error message
                        editTextUnitNumber.setError("Lesson number already exists");
                    } else {
                        // Unit number is unique, create the unit
                        unitsRef.child(unitNumber).child("unitName").setValue(unitName);
                        // Optionally, you can perform additional actions here.
                        // For example, navigate back to the previous activity or show a success message.
                        finish(); // Close the AddUnit activity
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });
        } else {
            // Show an error message if any field is empty
            if (unitNumber.isEmpty()) {
                editTextUnitNumber.setError("Lesson number is required");
            }
            if (unitName.isEmpty()) {
                editTextUnitName.setError("Lesson name is required");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
