package com.example.classroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // User is already logged in, navigate to the next activity
            startActivity(new Intent(SplashScreenActivity.this, SecondActivity.class));
        } else {
            // User is not logged in, navigate to the login activity
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        }

        finish();
    }
}
