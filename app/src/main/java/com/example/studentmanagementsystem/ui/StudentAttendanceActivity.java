package com.example.studentmanagementsystem.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar; // Added ProgressBar import
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.StudentAttendanceAdapter;
import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.AttendanceDataItem;
import com.example.studentmanagementsystem.model.StudentAttendanceResponse; // Import new model
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentAttendanceActivity extends AppCompatActivity {

    private TextView tvCountPresent, tvCountAbsent, tvCountLate, tvCountExcused;
    private RecyclerView recyclerView;
    private StudentAttendanceAdapter adapter;


    // Add a ProgressBar to your XML (optional but recommended) or just show/hide RecyclerView
    // private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        initializeViews();
        setupToolbar();

        // Call the API
        fetchAttendanceData();
    }

    private void initializeViews() {
        tvCountPresent = findViewById(R.id.tv_count_present);
        tvCountAbsent = findViewById(R.id.tv_count_absent);
        tvCountLate = findViewById(R.id.tv_count_late);
        recyclerView = findViewById(R.id.rv_attendance_history);
        // progressBar = findViewById(R.id.progressBar); // If you add one to XML
        tvCountExcused = findViewById(R.id.tv_count_excused); // Added this line

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_student_attendance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void fetchAttendanceData() {
        // 1. Get the FULL cookie string from Prefs
        // Because your Prefs.saveSessionToken adds "sessionToken=", this string is already correct.
        String cookieHeader = com.example.studentmanagementsystem.util.Prefs.getInstance(this).getSessionToken();

        // Debugging: Check what is actually being sent
        android.util.Log.d("API_DEBUG", "Sending Cookie: " + cookieHeader);

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Make the API Call (Pass cookieHeader directly)
        com.example.studentmanagementsystem.api.ApiServices apiService =
                com.example.studentmanagementsystem.api.ApiClient.getClient(this).create(com.example.studentmanagementsystem.api.ApiServices.class);

        retrofit2.Call<com.example.studentmanagementsystem.model.StudentAttendanceResponse> call = apiService.getStudentAttendance(cookieHeader);

        call.enqueue(new retrofit2.Callback<com.example.studentmanagementsystem.model.StudentAttendanceResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.studentmanagementsystem.model.StudentAttendanceResponse> call, retrofit2.Response<com.example.studentmanagementsystem.model.StudentAttendanceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // ... (Your existing success logic remains the same) ...
                    com.example.studentmanagementsystem.model.StudentAttendanceResponse data = response.body();

                    // Update Stats
                    if (data.getStatistics() != null) {
                        tvCountPresent.setText(String.valueOf(data.getStatistics().getPresentCount()));
                        tvCountAbsent.setText(String.valueOf(data.getStatistics().getAbsentCount()));
                        tvCountLate.setText(String.valueOf(data.getStatistics().getLateCount()));
                        tvCountExcused.setText(String.valueOf(data.getStatistics().getExcusedCount()));
                    }


                    // Update List
                    if (data.getAttendanceRecords() != null) {
                        java.util.List<com.example.studentmanagementsystem.model.AttendanceDataItem> displayList = new java.util.ArrayList<>();
                        for (com.example.studentmanagementsystem.model.StudentAttendanceResponse.AttendanceRecord record : data.getAttendanceRecords()) {
                            displayList.add(new com.example.studentmanagementsystem.model.AttendanceDataItem(
                                    record.getClassName(),
                                    record.getDate(),
                                    record.getStatus()
                            ));
                        }
                        adapter = new com.example.studentmanagementsystem.adapter.StudentAttendanceAdapter(displayList);
                        recyclerView.setAdapter(adapter);
                    }

                } else {
                    if (response.code() == 401) {
                        // Log the error body to see why the server rejected it
                        try {
                            android.util.Log.e("API_ERROR", "401 Error Body: " + response.errorBody().string());
                        } catch (Exception e) {}
                        Toast.makeText(StudentAttendanceActivity.this, "Session expired. Login again.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StudentAttendanceActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.studentmanagementsystem.model.StudentAttendanceResponse> call, Throwable t) {
                Toast.makeText(StudentAttendanceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}




//worked code
//package com.example.studentmanagementsystem.ui;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.adapter.StudentAttendanceAdapter;
//import com.example.studentmanagementsystem.model.AttendanceDataItem;
//import com.google.android.material.appbar.MaterialToolbar;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StudentAttendanceActivity extends AppCompatActivity {
//
//    private TextView tvCountPresent, tvCountAbsent, tvCountLate;
//    private RecyclerView recyclerView;
//    private StudentAttendanceAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_student_attendance);
//
//        initializeViews();
//        setupToolbar();
//
//        // Load Data (Eventually replace this with API Call)
//        fetchAttendanceData();
//    }
//
//    private void initializeViews() {
//        tvCountPresent = findViewById(R.id.tv_count_present);
//        tvCountAbsent = findViewById(R.id.tv_count_absent);
//        tvCountLate = findViewById(R.id.tv_count_late);
//        recyclerView = findViewById(R.id.rv_attendance_history);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }
//
//    private void setupToolbar() {
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_student_attendance);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(v -> onBackPressed());
//    }
//
//    private void fetchAttendanceData() {
//        // --- MOCK DATA: Replace this block with your API Call ---
//        List<AttendanceDataItem> data = new ArrayList<>();
//
//        // Note: We use "You" or the student ID as placeholder since the list is for one student
//        data.add(new AttendanceDataItem("S001", "2023-11-24", "PRESENT"));
//        data.add(new AttendanceDataItem("S001", "2023-11-23", "PRESENT"));
//        data.add(new AttendanceDataItem("S001", "2023-11-22", "LATE"));
//        data.add(new AttendanceDataItem("S001", "2023-11-21", "ABSENT"));
//        data.add(new AttendanceDataItem("S001", "2023-11-20", "PERMISSION")); // Excused
//        data.add(new AttendanceDataItem("S001", "2023-11-19", "PRESENT"));
//        // -------------------------------------------------------
//
//        // 1. Setup Adapter
//        adapter = new StudentAttendanceAdapter(data);
//        recyclerView.setAdapter(adapter);
//
//        // 2. Calculate Stats for the Header
//        calculateStats(data);
//    }
//
//    private void calculateStats(List<AttendanceDataItem> data) {
//        int present = 0;
//        int absent = 0;
//        int late = 0;
//
//        for (AttendanceDataItem item : data) {
//            String status = item.getStatus().toUpperCase();
//            if (status.equals("PRESENT")) present++;
//            else if (status.equals("ABSENT")) absent++;
//            else if (status.equals("LATE")) late++;
//            // Permission/Excused isn't counted in these 3 boxes, or could be grouped with Present depending on school rules
//        }
//
//        tvCountPresent.setText(String.valueOf(present));
//        tvCountAbsent.setText(String.valueOf(absent));
//        tvCountLate.setText(String.valueOf(late));
//    }
//}
