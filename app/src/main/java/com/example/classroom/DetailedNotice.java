package com.example.classroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class DetailedNotice extends AppCompatActivity {

    private TextView descriptionTextView;
    private RecyclerView recyclerView;
    private FileListAdapter fileListAdapter;
    private ArrayList<String> fileUrls;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_notice);

        // Receive the string
        description = getIntent().getStringExtra("description");
        // Receive the list of strings
        fileUrls = getIntent().getStringArrayListExtra("fileUrls");

        // Initialize description TextView
        descriptionTextView = findViewById(R.id.textViewDescription);
        descriptionTextView.setText(description);

        // Initialize RecyclerView for file list
        recyclerView = findViewById(R.id.recyclerViewFiles);

        // Check if there are no file URLs, hide the RecyclerView and related UI elements
        if (fileUrls == null || fileUrls.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            fileListAdapter = new FileListAdapter(fileUrls);
            recyclerView.setAdapter(fileListAdapter);
        }

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

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notification");
    }

    // Adapter for displaying file list
    private class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
        private ArrayList<String> fileList;

        public FileListAdapter(ArrayList<String> fileList) {
            this.fileList = fileList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.file_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String fileUrl = fileList.get(position);
            holder.bind(fileUrl);
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View itemView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.itemView = itemView;
            }

            public void bind(String fileUrl) {
                // Decode the URL to extract the filename
                String decodedUrl = Uri.decode(fileUrl);

                // Get the last path segment of the decoded URL, which should be the file name
                String[] segments = decodedUrl.split("/");
                String fileName = segments[segments.length - 1];

                // Set file name to the TextView
                TextView fileNameTextView = itemView.findViewById(R.id.textViewFileName);
                fileNameTextView.setText(fileName);

                // Set click listeners for download and open buttons
                itemView.findViewById(R.id.buttonDownload).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Implement the logic to download the file here
                        // You can use DownloadManager for downloading files
                        downloadFile(fileUrl, fileName);
                    }
                });

                itemView.findViewById(R.id.buttonOpen).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Implement the logic to open the file here
                        // You can use Intent to open files with suitable apps
                        openFile(fileUrl, fileName);
                    }
                });
            }
        }
    }

//    // Method to open a file using an appropriate app
//    private void openFile(String fileUrl, String fileName) {
//        try {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse(fileUrl), "application/*");
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//
//
//    // Helper method to map file extensions to MIME types
//    @Nullable
//    private String getMimeType(@NonNull String fileExtension) {
//        String mimeType;
//        if (fileExtension.equalsIgnoreCase("pdf")) {
//            mimeType = "application/pdf";
//        } else if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
//            mimeType = "image/jpeg";
//        } else if (fileExtension.equalsIgnoreCase("png")) {
//            mimeType = "image/png";
//        } else if (fileExtension.equalsIgnoreCase("gif")) {
//            mimeType = "image/gif";
//        } else if (fileExtension.equalsIgnoreCase("doc") || fileExtension.equalsIgnoreCase("docx")) {
//            mimeType = "application/msword";
//        } else if (fileExtension.equalsIgnoreCase("ppt") || fileExtension.equalsIgnoreCase("pptx")) {
//            mimeType = "application/vnd.ms-powerpoint";
//        } else if (fileExtension.equalsIgnoreCase("xls") || fileExtension.equalsIgnoreCase("xlsx")) {
//            mimeType = "application/vnd.ms-excel";
//        } else if (fileExtension.equalsIgnoreCase("txt")) {
//            mimeType = "text/plain";
//        } else if (fileExtension.equalsIgnoreCase("mp3")) {
//            mimeType = "audio/mpeg";
//        } else if (fileExtension.equalsIgnoreCase("mp4")) {
//            mimeType = "video/mp4";
//        } else {
//            mimeType = null; // Unsupported file type
//        }
//        return mimeType;
//    }

    // Method to download a file using DownloadManager
    private void downloadFile(String fileUrl, String fileName) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(fileUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        long downloadId = downloadManager.enqueue(request);
    }

    // Method to open a file using an appropriate app
    private void openFile(String fileUrl, String fileName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(fileUrl), "application/*");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to get MIME type from file name
    @Nullable
    private String getMimeType(String fileName) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        return mimeType;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
