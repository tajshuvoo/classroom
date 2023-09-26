package com.example.classroom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class PostMaterials extends AppCompatActivity {

    private EditText materialsDescriptionEditText;
    private ImageView attachFileButton;
    private Button postMaterialsButton;
    private TextView selectedFilesTextView;

    private FirebaseStorage storage;
    private List<Uri> selectedFileUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_materials);

        storage = FirebaseStorage.getInstance();

        materialsDescriptionEditText = findViewById(R.id.materialsDescriptionEditText);
        attachFileButton = findViewById(R.id.attachFileButton);
        postMaterialsButton = findViewById(R.id.postMaterialsButton);
        selectedFilesTextView = findViewById(R.id.selectedFilesTextView);

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
        getSupportActionBar().setTitle("Post Materials");

        attachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        postMaterialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMaterials();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Files"), PICK_FILE_REQUEST_CODE);
    }

    private void postMaterials() {
        String description = materialsDescriptionEditText.getText().toString();
        if (TextUtils.isEmpty(description.trim())) {
            Toast.makeText(this, "Please enter a valid description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFileUris.isEmpty()) {
            // No files selected
            uploadMaterials(description, null);
        } else {
            // Upload selected files to Firebase Storage
            uploadFiles(description);
        }
    }

    private void uploadFiles(final String description) {
        // Upload each selected file and get the download URLs
        ArrayList<String> fileUrls = new ArrayList<>();
        String classroomId = getIntent().getStringExtra("classroomId");
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        int chapterNum = getIntent().getIntExtra("chapterNum", -1); // Replace with the actual method of getting chapter number
        for (Uri uri : selectedFileUris) {
            String filename;
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            filename = cursor.getString(nameIndex);
                        } else {
                            filename = uri.getLastPathSegment();
                        }
                    } else {
                        filename = uri.getLastPathSegment();
                    }
                } catch (Exception e) {
                    filename = uri.getLastPathSegment();
                }
            } else {
                filename = uri.getLastPathSegment();
            }

            StorageReference storageRef = storage.getReference()
                    .child("classrooms")
                    .child(classroomId)
                    .child(userId)
                    .child("materials")
                    .child(filename);

            UploadTask uploadTask = storageRef.putFile(uri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    String fileUrl = downloadUri.toString();
                    fileUrls.add(fileUrl);

                    if (fileUrls.size() == selectedFileUris.size()) {
                        uploadMaterials(description, fileUrls);
                    }
                });
            });
        }
    }

    private void uploadMaterials(String description, ArrayList<String> fileUrls) {
        Intent intent = getIntent();
        String classroomId = intent.getStringExtra("classroomId");
        String chapterNum = intent.getStringExtra("chapterNum");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        DatabaseReference materialsRef = databaseReference
                .child("classrooms_info")
                .child(classroomId)
                .child("chapters")
                .child(String.valueOf(chapterNum))
                .child("materials");

        // Create a Materials object with description and file URLs
        Materials materials = new Materials(description, fileUrls);

        // Push the materials to the materials node
        materialsRef.push().setValue(materials);

        Toast.makeText(this, "Materials posted successfully", Toast.LENGTH_SHORT).show();

        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    selectedFileUris.add(uri);
                }
                updateSelectedFilesTextView();
            } else if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                selectedFileUris.add(uri);
                updateSelectedFilesTextView();
            }
        }
    }

    private void updateSelectedFilesTextView() {
        int selectedFileCount = selectedFileUris.size();
        if (selectedFileCount > 0) {
            selectedFilesTextView.setText(selectedFileCount + " files selected");
        } else {
            selectedFilesTextView.setText("No files selected");
        }
    }

    private static final int PICK_FILE_REQUEST_CODE = 123;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
