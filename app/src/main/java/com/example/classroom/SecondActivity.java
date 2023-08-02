package com.example.classroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.classroom.adapter.ClassroomAdapter;
import com.example.classroom.model.Classroom;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    TextView name,email;
    Button signOut;



    //hamburger
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    //fab
    private boolean isFabExpanded = false;
    private FloatingActionButton fabMain;
    private ExtendedFloatingActionButton fabOption1, fabOption2;

    //recyclerview
    private RecyclerView recyclerView;
    private ClassroomAdapter adapter;
    private List<Classroom> classroomList;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

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



        //hamburger
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_profile) {
                    // Handle Profile click (e.g., open ProfileActivity)
                    // startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
                } else if (itemId == R.id.menu_settings) {
                    // Handle Settings click (e.g., open SettingsActivity)
                    // startActivity(new Intent(SecondActivity.this, SettingsActivity.class));
                } else if (itemId == R.id.menu_sign_out) {
                    // Handle Sign Out click
                  signOUT();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });




        // recycler view work
        recyclerView = findViewById(R.id.recyclerViewClassrooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        classroomList = new ArrayList<>();
        adapter = new ClassroomAdapter(classroomList);
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);


        if (userId != null) {
            // Reference to the user's linked_classes node in Firebase
            DatabaseReference linkedClassesRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference().child("linked_classes").child(userId).child("classrooms");
//            Toast.makeText(SecondActivity.this,"hi",Toast.LENGTH_LONG).show();
            // Fetch the linked classroom IDs for the user
            linkedClassesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    classroomList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String classroomId = snapshot.getKey();
//                        Toast.makeText(SecondActivity.this,classroomId,Toast.LENGTH_LONG).show();
                        // Reference to the classroom with this ID
                        DatabaseReference classroomRef = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference().child("classrooms").child(classroomId);

                        classroomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){

                                    if(task.getResult().exists()){

                                        DataSnapshot dataSnapshot =task.getResult();
                                        String className = dataSnapshot.child("className").getValue(String.class);
                                        String classroomId = dataSnapshot.child("classroomId").getValue(String.class);
                                        String room = dataSnapshot.child("room").getValue(String.class);
                                        String section = dataSnapshot.child("section").getValue(String.class);
                                        String subject = dataSnapshot.child("subject").getValue(String.class);
                                        String teacherUid = dataSnapshot.child("teacherUid").getValue(String.class);


                                    // Create a new Classroom object with retrieved data
                                    Classroom classroom = new Classroom(className, classroomId, room, section, subject, teacherUid);

                                    // Add the Classroom object to the list and notify the adapter
                                    classroomList.add(classroom);
                                    adapter.notifyDataSetChanged();
                                    } else {
                                    // Handle the case where the dataSnapshot does not exist
                                    Log.e("ClassroomAdapter", "DataSnapshot does not exist");
                                }

                                }
                            }
                        });


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }




        //fab
        fabMain = findViewById(R.id.fabMain);
        fabOption1 = findViewById(R.id.fabOption1);
        fabOption2 = findViewById(R.id.fabOption2);
        fabMain.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fabOption1.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fabOption2.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));





        googleSignInOptions = new GoogleSignInOptions.Builder(googleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        GoogleSignInAccount acct= GoogleSignIn.getLastSignedInAccount(this);





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
                // Handle Option 1 click
                closeFabMenu();
            }
        });

        fabOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this,CreateClassroomActivity.class));
                closeFabMenu();
            }
        });


        //fab done
    }

    private void signOUT() {
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //clear session data and update login status

               //clear the user UID from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("userId"); // Remove the "uid" key to indicate the user is not logged in
                editor.apply();

                startActivity(new Intent(SecondActivity.this,MainActivity.class));
                finishAffinity();
            }
        });
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
}