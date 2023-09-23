package com.example.classroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class TeachersChapterActivity extends AppCompatActivity {


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
        String chapterNum = intent1.getStringExtra("chapterNum");
        String chapterName = intent1.getStringExtra("chapterName");
        ChapterItem chapter = new ChapterItem(chapterNum,chapterName);

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
                startActivity(intent);
            closeFabMenu();
        });

        fabOption2.setOnClickListener(view -> {
                Intent intent = new Intent(TeachersChapterActivity.this, AddUnit.class);
                startActivity(intent);
            closeFabMenu();
        });

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