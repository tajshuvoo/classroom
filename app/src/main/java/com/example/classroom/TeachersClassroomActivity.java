package com.example.classroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.classroom.model.Classroom;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeachersClassroomActivity extends AppCompatActivity {

    Classroom classroom;
    String className;
    String section;
    String room;
    String subject;
    String teacherUid;


    //fab
    private boolean isFabExpanded = false;
    private FloatingActionButton fabMain;
    private ExtendedFloatingActionButton fabOption1, fabOption2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_classroom);

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
        String classroomId = intent.getStringExtra("classroomId");

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
                Intent intent =new Intent(TeachersClassroomActivity.this,Notices.class);
                intent.putExtra("classroomId", classroomId);
                startActivity(intent);

            }
        });

        //fab
        fabMain = findViewById(R.id.fabTeacher);
        fabOption1 = findViewById(R.id.fabTeacher1);
        fabOption2 = findViewById(R.id.fabTeacher2);
        fabMain.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fabOption1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fabOption2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

        //fab
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFabExpanded) {
                    closeFabMenu();
                } else {
                    openFabMenu();
                }
            }
        });

        fabOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // Handle Option 1 click
//                startActivity(new Intent(SecondActivity.this,JoinClassroomActivity.class));
                closeFabMenu();
            }
        });

        fabOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(TeachersClassroomActivity.this,SendNoticeActivity.class);
                intent.putExtra("classroomId", classroomId);
                startActivity(intent);
                closeFabMenu();
            }
        });


        //fab done

    }


    //fab
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

    private void openMenu(View v,String classroomId) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenu().add(Menu.NONE, Menu.FIRST, Menu.NONE, "Members");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == Menu.FIRST) {
                    Intent intent = new Intent(TeachersClassroomActivity.this, MembersActivity.class);
                    intent.putExtra("classroomId", classroomId);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}