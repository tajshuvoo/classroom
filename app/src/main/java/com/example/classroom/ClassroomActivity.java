package com.example.classroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.classroom.model.Classroom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClassroomActivity extends AppCompatActivity implements ChapterAdapter.OnItemClickListener {

    Classroom classroom;
    String className;
    String section;
    String room;
    String subject;
    String teacherUid;
    String classroomId;

    private RecyclerView recyclerView;
    private ChapterAdapter chapterAdapter;
    private List<ChapterItem> chapterList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

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


        Intent intent = getIntent();
        classroomId = intent.getStringExtra("classroomId");

        DatabaseReference classroomRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("classrooms").child(classroomId);
        classroomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    classroom = dataSnapshot.getValue(Classroom.class);

                    // Access classroom properties and update the UI here
                    className = classroom.getClassName();
                    section = classroom.getSection();
                    room = classroom.getRoom();
                    subject=classroom.getSubject();
                    teacherUid=classroom.getTeacherUid();

                    // Toolbar setup
                    Toolbar toolbar = findViewById(R.id.toolbar1);
                    toolbar.setContentInsetsAbsolute(0, 0);
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    getSupportActionBar().setTitle(className);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });



        ImageView threeDotMenu = findViewById(R.id.toolbar_menu);
        threeDotMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu(v,classroomId);
            }
        });

        ImageView notification = findViewById(R.id.toolbar_notification);

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ClassroomActivity.this,Notices.class);
                intent.putExtra("classroomId", classroomId);
                startActivity(intent);

            }
        });

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerViewClassrooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch chapters from Firebase and populate the RecyclerView
        DatabaseReference chaptersRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("classrooms_info")
                .child(classroomId)
                .child("chapters");

        chaptersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterList.clear();
                for (DataSnapshot chapterSnapshot : dataSnapshot.getChildren()) {
                    // Assuming chapterSnapshot.getKey() is the chapter number
                    String chapterNum = chapterSnapshot.getKey();

                    // Get the chapter name under this chapter number
                    String chapterName = chapterSnapshot.child("chapterName").getValue(String.class);


                    Log.d("ChapterData", "Chapter Num: " + chapterNum + ", Chapter Name: " + chapterName);
                    // Create a ChapterItem and add it to the list
                    ChapterItem chapter = new ChapterItem(chapterNum, chapterName);
                    chapterList.add(chapter);
                }
                chapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });


        // Initialize and set the adapter for the RecyclerView
        chapterAdapter = new ChapterAdapter(chapterList , this); // Pass 'this' as the listener
        recyclerView.setAdapter(chapterAdapter);
    }

    private void openMenu(View v, String classroomId) {
        PopupMenu popupMenu = new PopupMenu(this, v);

        // Add "Classroom Info" option
        popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Classroom Info");

        // Add "Members" option
        popupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Members");


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1: // Classroom Info
                        // Open the Classroom Info activity with the classroomId
                        Intent infoIntent = new Intent(ClassroomActivity.this, classroomInfo.class);
                        infoIntent.putExtra("classroomId", classroomId);
                        startActivity(infoIntent);
                        return true;

                    case 2: // Members
                        // Open the Members activity with the classroomId
                        Intent membersIntent = new Intent(ClassroomActivity.this, MembersActivity.class);
                        membersIntent.putExtra("classroomId", classroomId);
                        startActivity(membersIntent);
                        return true;


                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    @Override
    public void onItemClick(int position) {
        // Get the clicked chapter item
        ChapterItem clickedChapter = chapterList.get(position);

        // Create an Intent to start TeachersChapterActivity
        Intent intent = new Intent(ClassroomActivity.this, ChapterActivity.class);

        // Pass relevant data to the next activity, for example, chapter number and name
        intent.putExtra("classroomId", classroomId);
        intent.putExtra("chapterNum", clickedChapter.getChapterNum());
        intent.putExtra("chapterName", clickedChapter.getChapterName());

        // Start the activity
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}