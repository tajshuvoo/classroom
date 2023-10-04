package com.example.classroom;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // Set the dimensions of the sign-in button.
    SignInButton signInButton ;

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    FirebaseAuth auth;
    FirebaseDatabase database;


    Users users = new Users();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        // Check if the user ID is stored in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        if (userId== null) {
            // User is not logged in, navigate to the next activity

            database = FirebaseDatabase.getInstance(" https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app");


            String s = "893931592162-v2ko2ee01mc9lokbdh1r4s4vnvnop19s.apps.googleusercontent.com";

            googleSignInOptions = new GoogleSignInOptions.Builder(googleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(s)
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }


       else {    startNextActivity();
        }
    }

    private void signIn() {
        Intent signInIntent =googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,40);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==40){
            Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),"something went wrong!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();

                    users.setUserId(user.getUid());
                    users.setName(user.getDisplayName());
                    users.setProfile(user.getPhotoUrl().toString());
                    users.setMail(user.getEmail());

                    database.getReference().child("Users").child(user.getUid()).setValue(users);
                    startNextActivity();
                }
            }
        });
    }

    // After successful login, save the user's UID to SharedPreferences
    private void saveUserIdToSharedPreferences(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }


    private void startNextActivity() {
        String userId = auth.getCurrentUser().getUid();
        saveUserIdToSharedPreferences(userId);
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
        finish();
    }

    // ...


}