package com.example.studentmanagementsystem.ui;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView; // Import ImageView
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import java.io.OutputStream;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.ReportAdapter;
import com.example.studentmanagementsystem.model.AttendanceDataItem;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GetReportActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerClass;
    private Button btnGenerate;
    private ImageView btnExportCsv; // Variable for download button
    private RecyclerView recyclerView;
    private ProgressBar pbLoading;
    private ReportAdapter adapter;

    // Keep a local copy of the data to export
    private List<AttendanceDataItem> currentReportList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_report);

        initializeViews();
        setupToolbar();
        setupClassSpinner();

        btnGenerate.setOnClickListener(v -> fetchReportData());

        // Listener for CSV Export
        btnExportCsv.setOnClickListener(v -> exportToCSV());
    }

    private void initializeViews() {
        spinnerClass = findViewById(R.id.spinner_report_class);
        btnGenerate = findViewById(R.id.btn_generate_report);
        recyclerView = findViewById(R.id.rv_report_list);
        pbLoading = findViewById(R.id.pb_loading_report);
        // Initialize export button (Make sure ID matches XML)
        btnExportCsv = findViewById(R.id.btn_export_csv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_report);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupClassSpinner() {
        // Mock Data (Replace with API later)
        String[] classes = {"Class 10A", "Class 11B", "Class 12C"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classes);
        spinnerClass.setAdapter(adapter);
    }

    private void fetchReportData() {
        String selectedClass = spinnerClass.getText().toString();
        if (selectedClass.isEmpty()) {
            Toast.makeText(this, "Please select a class first", Toast.LENGTH_SHORT).show();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // MOCK API CALL
        new Handler().postDelayed(() -> {
            currentReportList = new ArrayList<>(); // Reset list

            // Add fake data
            currentReportList.add(new AttendanceDataItem("STU001", "2023-11-23", "PRESENT"));
            currentReportList.add(new AttendanceDataItem("STU002", "2023-11-23", "ABSENT"));
            currentReportList.add(new AttendanceDataItem("STU003", "2023-11-23", "LATE"));
            currentReportList.add(new AttendanceDataItem("STU001", "2023-11-22", "PRESENT"));
            currentReportList.add(new AttendanceDataItem("STU002", "2023-11-22", "PRESENT"));

            adapter = new ReportAdapter(currentReportList);
            recyclerView.setAdapter(adapter);

            pbLoading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Report Generated", Toast.LENGTH_SHORT).show();
        }, 1500);
    }

    // --- NEW CSV EXPORT FUNCTION ---
    private void exportToCSV() {
        if (currentReportList == null || currentReportList.isEmpty()) {
            Toast.makeText(this, "No data to export. Generate report first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "AttendanceReport_" + System.currentTimeMillis() + ".csv";

        // 1. Build CSV String
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Student ID,Date,Status\n"); // Header

        for (AttendanceDataItem item : currentReportList) {
            csvBuilder.append(item.getStudentId()).append(",");
            csvBuilder.append(item.getDate()).append(",");
            csvBuilder.append(item.getStatus()).append("\n");
        }

        try {
            // 2. Save file using MediaStore (Works on Android 10+)
            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (uri == null) {
                    throw new Exception("Failed to create file URI");
                }
                fos = getContentResolver().openOutputStream(uri);
            } else {
                // For older Android versions (Below Android 10), simpler path
                // Note: Would need WRITE_EXTERNAL_STORAGE permission
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                java.io.File file = new java.io.File(path, fileName);
                fos = new java.io.FileOutputStream(file);
            }

            if (fos != null) {
                fos.write(csvBuilder.toString().getBytes());
                fos.close();
                Toast.makeText(this, "Saved to Downloads: " + fileName, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}



//worked code
//package com.example.studentmanagementsystem.ui;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.adapter.ReportAdapter;
//import com.example.studentmanagementsystem.model.AttendanceDataItem;
//import com.google.android.material.appbar.MaterialToolbar;
//
//import java.util.ArrayList;
//import java.util.List;
//import android.content.ContentValues;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.widget.ImageView;
//import java.io.OutputStream;
//
//public class GetReportActivity extends AppCompatActivity {
//
//    private AutoCompleteTextView spinnerClass;
//    private Button btnGenerate;
//    private RecyclerView recyclerView;
//    private ProgressBar pbLoading;
//    private ReportAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_get_report);
//
//        initializeViews();
//        setupToolbar();
//        setupClassSpinner();
//
//        btnGenerate.setOnClickListener(v -> fetchReportData());
//    }
//
//    private void initializeViews() {
//        spinnerClass = findViewById(R.id.spinner_report_class);
//        btnGenerate = findViewById(R.id.btn_generate_report);
//        recyclerView = findViewById(R.id.rv_report_list);
//        pbLoading = findViewById(R.id.pb_loading_report);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }
//
//    private void setupToolbar() {
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_report);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//        toolbar.setNavigationOnClickListener(v -> onBackPressed());
//    }
//
//    private void setupClassSpinner() {
//        // MOCK DATA: Replace with apiService.getStandbyClasses() later
//        String[] classes = {"Class 10A", "Class 11B", "Class 12C"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classes);
//        spinnerClass.setAdapter(adapter);
//    }
//
//    private void fetchReportData() {
//        String selectedClass = spinnerClass.getText().toString();
//        if (selectedClass.isEmpty()) {
//            Toast.makeText(this, "Please select a class first", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        pbLoading.setVisibility(View.VISIBLE);
//        recyclerView.setVisibility(View.GONE);
//
//        // MOCK API CALL
//        new Handler().postDelayed(() -> {
//            List<AttendanceDataItem> mockList = new ArrayList<>();
//
//            // Add fake data
//            mockList.add(new AttendanceDataItem("STU001", "2023-11-23", "PRESENT"));
//            mockList.add(new AttendanceDataItem("STU002", "2023-11-23", "ABSENT"));
//            mockList.add(new AttendanceDataItem("STU003", "2023-11-23", "LATE"));
//            mockList.add(new AttendanceDataItem("STU001", "2023-11-22", "PRESENT"));
//            mockList.add(new AttendanceDataItem("STU002", "2023-11-22", "PRESENT"));
//
//            adapter = new ReportAdapter(mockList);
//            recyclerView.setAdapter(adapter);
//
//            pbLoading.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//        }, 1500);
//    }
//}
