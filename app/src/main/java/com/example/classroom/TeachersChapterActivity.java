package com.example.classroom;
// TeachersChapterActivity.java
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeachersChapterActivity extends AppCompatActivity {

    String classroomId;
    private boolean isFabExpanded = false;
    private FloatingActionButton fabMain;
    private ExtendedFloatingActionButton fabOption1, fabOption2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_chapter);

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
        String chapterName = intent1.getStringExtra("chapterName");
        ChapterItem chapter = new ChapterItem(chapterNum, chapterName);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(chapter.getChapterName());

        // Initialize the FAB buttons
        fabMain = findViewById(R.id.fabChapter);
        fabOption1 = findViewById(R.id.fabCreateUnit);
        fabOption2 = findViewById(R.id.fabPostMaterial);
        fabMain.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fabOption1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fabOption2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

        // Set FAB click listeners
        fabMain.setOnClickListener(view -> {
            if (isFabExpanded) {
                closeFabMenu();
            } else {
                openFabMenu();
            }
        });

        fabOption1.setOnClickListener(view -> {
            Intent intent = new Intent(TeachersChapterActivity.this, PostMaterials.class);
            intent.putExtra("classroomId", classroomId); // Replace classroomId with the actual value
            intent.putExtra("chapterNum", chapterNum);
            startActivity(intent);
            closeFabMenu();
        });

        fabOption2.setOnClickListener(view -> {
            Intent intent = new Intent(TeachersChapterActivity.this, AddUnit.class);
            intent.putExtra("classroomId", classroomId); // Replace classroomId with the actual value
            intent.putExtra("chapterNum", chapterNum);
            startActivity(intent);
            closeFabMenu();
        });


        // Handle RecyclerView for Units
        RecyclerView recyclerViewUnit = findViewById(R.id.recyclerViewUnit);
        recyclerViewUnit.setLayoutManager(new LinearLayoutManager(this));
        final List<UnitItem> unitItemList = new ArrayList<>();
        UnitAdapter unitAdapter = new UnitAdapter(unitItemList);

        DatabaseReference materialsRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("classrooms_info")
                .child(classroomId) // Replace with the actual classroomId
                .child("chapters")
                .child(chapterNum) // Replace with the actual chapterNum
                .child("materials"); // Check for the existence of the "materials" folder

        materialsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If the "materials" folder exists in Firebase, show the "Materials" field
                if (dataSnapshot.exists()) {
                    TextView textViewMaterials = findViewById(R.id.textViewMaterials);
                    textViewMaterials.setVisibility(View.VISIBLE);

                    // Create layout parameters for the RecyclerView
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) recyclerViewUnit.getLayoutParams();
                    int newMarginTopInDp = 136;
                    int newMarginTopInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newMarginTopInDp, getResources().getDisplayMetrics());

                    layoutParams.topMargin = newMarginTopInPx;

                    recyclerViewUnit.setLayoutParams(layoutParams);
                    // Handle click on "Materials" field (e.g., open a new activity)
                    textViewMaterials.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the click action (e.g., open a new activity)
                            // You can replace this with your desired action
                            // For example, open a MaterialsActivity
                            Intent materialsIntent = new Intent(TeachersChapterActivity.this, MaterialsActivity.class);
                            materialsIntent.putExtra("classroomId",classroomId);
                            materialsIntent.putExtra("chapterNum",chapterNum);
                            materialsIntent.putExtra("chapterName", chapterName);
                            startActivity(materialsIntent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });



        // Assuming you have the classroomId and chapterNum variables set
        DatabaseReference unitsRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("classrooms_info")
                .child(classroomId) // Replace classroomId with the actual classroomId
                .child("chapters")
                .child(chapterNum) // Replace chapterNum with the actual chapterNum
                .child("units");

        unitsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unitItemList.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot unitSnapshot : dataSnapshot.getChildren()) {
                    // Assuming your Firebase structure has "unitNum" and "unitName" fields
                    String unitNum = unitSnapshot.getKey();
                    String unitName = unitSnapshot.child("unitName").getValue(String.class);
                    unitItemList.add(new UnitItem(unitNum, unitName));
                }
                // Notify the adapter that the data has changed
                unitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });


        // Set an item click listener for the RecyclerView
        unitAdapter.setOnItemClickListener(new UnitAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click here, e.g., open a new activity or fragment for the selected unit
                UnitItem clickedItem = unitItemList.get(position);
                // Example: Start a new activity with unit details
                Intent unitDetailIntent = new Intent(TeachersChapterActivity.this, UnitDetailActivity.class);
                Intent intent1 = getIntent();
                classroomId = intent1.getStringExtra("classroomId");
                unitDetailIntent.putExtra("classroomId",classroomId);
                unitDetailIntent.putExtra("chapterNum",chapterNum);
                unitDetailIntent.putExtra("unitNum", clickedItem.getUnitNum());
                unitDetailIntent.putExtra("unitName", clickedItem.getUnitName());
                startActivity(unitDetailIntent);
            }
        });

        recyclerViewUnit.setAdapter(unitAdapter);
    }

    private void openFabMenu() {
        isFabExpanded = true;
        fabOption1.show();
        fabOption2.show();
    }

    private void closeFabMenu() {
        isFabExpanded = false;
        fabOption1.hide();
        fabOption2.hide();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
