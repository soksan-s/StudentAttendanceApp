package com.example.studentmanagementsystem.ui;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.StudentPageAdapter;
import com.example.studentmanagementsystem.adapter.TakeAttendanceAdapter;
import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.AttendanceDataItem;
import com.example.studentmanagementsystem.model.AttendanceSubmissionPayload;
import com.example.studentmanagementsystem.model.ClassApiResponse;
import com.example.studentmanagementsystem.model.Student;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakeAttendanceActivity extends AppCompatActivity {
    private static final String TAG = "TakeAttendanceActivity";
    private static final int STUDENTS_PER_PAGE = 10;

    private ViewPager2 viewPager;
    private ProgressBar pbLoading;
    private TextView tvStatusMessage, tvStudentCounter;
    private MaterialButton btnNext, btnPrevious;
    private RelativeLayout navigationControls;
    private FloatingActionButton fabSubmit;

    private StudentPageAdapter pageAdapter;
    private ApiServices apiService;

    private ClassApiResponse currentClassData;
    private String currentUserId;
    private String selectedDate;
    private TextView tvTotalStudents;
    private TextView tvClassNameHeader; // Optional, if you want to set class name
    private TextView textViewDate;

    Calendar calendar = Calendar.getInstance();

    // 1. Add Handler and Runnable for the live clock
    private final Handler timeHandler = new Handler();
    private final Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            updateDateTimeDisplay();
            // Schedule the next update in 1 second (1000ms)
            timeHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        initializeViews();
        setupToolbar();
        startLiveClock();
        updateDateTimeDisplay();
        apiService = ApiClient.getClient(this).create(ApiServices.class);

        // Get data from the Intent passed by SetupAttendanceActivity
        Intent intent = getIntent();
        String classId = intent.getStringExtra("CLASS_ID");
        currentUserId = intent.getStringExtra("USER_ID");
        selectedDate = intent.getStringExtra("SELECTED_DATE");

        if (classId == null || currentUserId == null || selectedDate == null) {
            Toast.makeText(this, "Error: Missing required attendance data.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if data is missing
            return;
        }

        // Fetch the details for the specific class ID
        fetchSpecificClass(classId);

        setupNavigationListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacks(timeRunnable);
    }

    // 4. Helper method to start the clock
    private void startLiveClock() {
        timeHandler.post(timeRunnable);
    }

    // 5. Your existing method (slightly improved formatting)
    private void updateDateTimeDisplay() {
        if (textViewDate == null) return;

        Calendar calendar = Calendar.getInstance();
        // Use a single format string for cleaner output
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss", Locale.getDefault());
        String currentDateTime = dateFormat.format(calendar.getTime());

        textViewDate.setText(currentDateTime);
    }
    private void initializeViews() {
        viewPager = findViewById(R.id.view_pager_attendance);
        pbLoading = findViewById(R.id.pb_loading);
        tvStatusMessage = findViewById(R.id.tv_status_message);
        tvStudentCounter = findViewById(R.id.tv_student_counter);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        navigationControls = findViewById(R.id.navigation_controls);
        fabSubmit = findViewById(R.id.fab_submit_attendance);
        tvTotalStudents = findViewById(R.id.tv_total_students);
        tvClassNameHeader = findViewById(R.id.tv_class_name_header);
        textViewDate = findViewById(R.id.textViewDate);
    }


    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_take_attendance);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupNavigationListeners() {
        btnNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
        btnPrevious.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));
        fabSubmit.setOnClickListener(v -> postAttendanceData());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateUiForPage(position);
            }
        });
    }

    private void fetchSpecificClass(String classId) {
        showLoading(true, "Loading Students...");
        apiService.getStandbyClasses().enqueue(new Callback<List<ClassApiResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ClassApiResponse>> call, @NonNull Response<List<ClassApiResponse>> response) {
                showLoading(false, "");
                if (response.isSuccessful() && response.body() != null) {
                    for (ClassApiResponse cls : response.body()) {
                        if (cls.getId().equals(classId)) {
                            currentClassData = cls; // Found our class
                            break;
                        }
                    }
                    if (currentClassData != null) {
                        // Set the Class Name Header
                        if (tvClassNameHeader != null) {
                            tvClassNameHeader.setText(currentClassData.getName());
                        }

                        // Calculate and Set Total Students
                        List<Student> students = currentClassData.getStudents();
                        if (students != null) {
                            int total = students.size();
                            if (tvTotalStudents != null) {
                                tvTotalStudents.setText("Total Students: " + total);
                            }
                        } else {
                            if (tvTotalStudents != null) {
                                tvTotalStudents.setText("Total Students: 0");
                            }
                        }
                        // --- NEW CODE ENDS HERE ---
                        validateAndDisplayData(currentClassData);
                    } else {
                        showError("Error: Could not find details for the selected class.");
                    }
                } else {
                    showError("Error: Failed to load class details from server.");
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<ClassApiResponse>> call, @NonNull Throwable t) {
                showError("Network Error: " + t.getMessage());
            }
        });
    }

    private void validateAndDisplayData(ClassApiResponse classData) {
        List<Student> students = classData.getStudents();
        if (students == null || students.isEmpty()) {
            showError("This class has no students to display.");
            return;
        }

        viewPager.setVisibility(View.VISIBLE);
        navigationControls.setVisibility(View.VISIBLE);
        fabSubmit.setVisibility(View.VISIBLE);

        pageAdapter = new StudentPageAdapter(students, STUDENTS_PER_PAGE);
        viewPager.setAdapter(pageAdapter);
        updateUiForPage(0);
    }

    private void postAttendanceData() {
        fabSubmit.setEnabled(false);
        if (pageAdapter == null || currentClassData == null || currentUserId == null) {
            Toast.makeText(this, "Error: Cannot submit, data is missing.", Toast.LENGTH_SHORT).show();
            fabSubmit.setEnabled(true);
            return;
        }

        Map<String, String> finalAttendanceMap = new HashMap<>();
        for (TakeAttendanceAdapter adapter : pageAdapter.getPageAdapters()) {
            finalAttendanceMap.putAll(adapter.getAttendanceStatusMap());
        }

        if (finalAttendanceMap.isEmpty()) {
            Toast.makeText(this, "No attendance data was recorded.", Toast.LENGTH_SHORT).show();
            fabSubmit.setEnabled(true);
            return;
        }

        // 1. GET BOTH ID AND NAME (Fixes 400 Error)
        String classId = currentClassData.getId();
        String className = currentClassData.getName();

        List<AttendanceDataItem> dataItems = new ArrayList<>();

        // 2. PREPARE ISO DATE
        String isoDate = selectedDate;
        if (!selectedDate.contains("T")) {
            isoDate = selectedDate + "T00:00:00.000Z";
        }

        for (Map.Entry<String, String> entry : finalAttendanceMap.entrySet()) {
            String status = entry.getValue().toUpperCase();

            // 3. USE 'isoDate' HERE (Fixes potential date format error)
            // Previously you were passing 'selectedDate' here by mistake
            dataItems.add(new AttendanceDataItem(entry.getKey(), isoDate, status));
        }

        // 4. CREATE PAYLOAD WITH BOTH ID AND NAME
        AttendanceSubmissionPayload submissionPayload = new AttendanceSubmissionPayload(classId, className, currentUserId, dataItems);

        // Log to verify JSON
        com.google.gson.Gson gson = new com.google.gson.Gson();
        android.util.Log.d(TAG, "Submitting payload: " + gson.toJson(submissionPayload));

        String sessionCookie = "sessionToken=ADMIN_SESSION_FOREVER";

        apiService.submitAttendance(sessionCookie, submissionPayload).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TakeAttendanceActivity.this, "Attendance Submitted Successfully!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String errorBody = "Unknown Error";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) { e.printStackTrace(); }

                    android.util.Log.e(TAG, "Server Error " + response.code() + ": " + errorBody);
                    Toast.makeText(TakeAttendanceActivity.this, "Failed: " + response.code() + " " + errorBody, Toast.LENGTH_LONG).show();
                    fabSubmit.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(TakeAttendanceActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                fabSubmit.setEnabled(true);
            }
        });

    //worked code
//        String className = currentClassData.getName();
//        List<AttendanceDataItem> dataItems = new ArrayList<>();
//        for (Map.Entry<String, String> entry : finalAttendanceMap.entrySet()) {
//            dataItems.add(new AttendanceDataItem(entry.getKey(), selectedDate, entry.getValue()));
//        }
//
//        AttendanceSubmissionPayload submissionPayload = new AttendanceSubmissionPayload(className, currentUserId, dataItems);
//        Log.d(TAG, "Submitting payload: " + new Gson().toJson(submissionPayload));
//        //added session
//        // We pass this exact string as the Cookie header.
//        String sessionCookie = "sessionToken=ADMIN_SESSION_FOREVER";
//
////        apiService.submitAttendance(submissionPayload).enqueue(new Callback<Void>() {
////            @Override
//        apiService.submitAttendance(sessionCookie, submissionPayload).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(TakeAttendanceActivity.this, "Attendance Submitted Successfully!", Toast.LENGTH_LONG).show();
//                    finish();
//                } else {
//                    Toast.makeText(TakeAttendanceActivity.this, "Submission Failed: " + response.code(), Toast.LENGTH_LONG).show();
//                    fabSubmit.setEnabled(true);
//                }
//            }
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                Toast.makeText(TakeAttendanceActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//                fabSubmit.setEnabled(true);
//            }
//        });
    }

    private void updateUiForPage(int position) {
        if (pageAdapter == null || pageAdapter.getItemCount() == 0) return;
        int totalPages = pageAdapter.getItemCount();
        tvStudentCounter.setText("Page " + (position + 1) + " / " + totalPages);
        btnPrevious.setEnabled(position > 0);
        btnNext.setEnabled(position < totalPages - 1);
    }

    private void showLoading(boolean isLoading, String message) {
        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        tvStatusMessage.setText(message);
        tvStatusMessage.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            viewPager.setVisibility(View.GONE);
            navigationControls.setVisibility(View.GONE);
            fabSubmit.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        showLoading(false, ""); // Hide any active loading indicators
        viewPager.setVisibility(View.GONE);
        navigationControls.setVisibility(View.GONE);
        fabSubmit.setVisibility(View.GONE);
        tvStatusMessage.setText(message);
        tvStatusMessage.setVisibility(View.VISIBLE);
    }
}



//second worked code
//package com.example.studentmanagementsystem.ui;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.adapter.StudentPageAdapter;
//import com.example.studentmanagementsystem.adapter.TakeAttendanceAdapter;
//import com.example.studentmanagementsystem.api.ApiClient;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.model.AttendanceDataItem;
//import com.example.studentmanagementsystem.model.AttendanceSubmissionPayload;
//import com.example.studentmanagementsystem.model.ClassApiResponse;
//import com.example.studentmanagementsystem.model.LoginResponseWrapper;
//import com.example.studentmanagementsystem.model.Student;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.gson.Gson;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class TakeAttendanceActivity extends AppCompatActivity {
//
//    private static final String TAG = "TakeAttendanceActivity";
//    private static final int STUDENTS_PER_PAGE = 10;
//
//    // Views
//    private ViewPager2 viewPager;
//    private ProgressBar pbLoading;
//    private TextView tvStatusMessage, tvStudentCounter;
//    private MaterialButton btnNext, btnPrevious;
//    private RelativeLayout navigationControls;
//    private FloatingActionButton fabSubmit;
//
//    // Adapters and Services
//    private StudentPageAdapter pageAdapter;
//    private ApiServices apiService;
//
//    // Data Holders
//    private ClassApiResponse currentClassData;
//    private String currentUserId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_take_attendance);
//
//        // 1. Initialize API Service
//        apiService = ApiClient.getClient(this).create(ApiServices.class);
//
//        // 2. Initialize Views and Toolbar
//        initializeViews();
//        setupToolbar();
//
//        // 3. Start fetching data from the API
//        fetchClassData();
//
//        // 4. Set up navigation listeners
//        setupNavigationListeners();
//    }
//
//    private void initializeViews() {
//        viewPager = findViewById(R.id.view_pager_attendance);
//        pbLoading = findViewById(R.id.pb_loading);
//        tvStatusMessage = findViewById(R.id.tv_status_message);
//        tvStudentCounter = findViewById(R.id.tv_student_counter);
//        btnNext = findViewById(R.id.btn_next);
//        btnPrevious = findViewById(R.id.btn_previous);
//        navigationControls = findViewById(R.id.navigation_controls);
//        fabSubmit = findViewById(R.id.fab_submit_attendance);
//    }
//
//    private void setupToolbar() {
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_take_attendance);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed(); // Handle the back arrow click
//        return true;
//    }
//
//    private void setupNavigationListeners() {
//        btnNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
//        btnPrevious.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));
//        fabSubmit.setOnClickListener(v -> submitAttendance());
//
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                updateUiForPage(position);
//            }
//        });
//    }
//
//    private void fetchClassData() {
//        showLoading(true);
//        // Call the correct endpoint from your API documentation
//        Call<List<ClassApiResponse>> call = apiService.getStandbyClasses();
//
//        call.enqueue(new Callback<List<ClassApiResponse>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<ClassApiResponse>> call, @NonNull Response<List<ClassApiResponse>> response) {
//                showLoading(false);
//                if (response.isSuccessful() && response.body() != null) {
//                    List<ClassApiResponse> classList = response.body();
//                    if (!classList.isEmpty()) {
//                        // Store the first class from the list (as per logic)
//                        TakeAttendanceActivity.this.currentClassData = classList.get(0);
//                        // Proceed to display its students
//                        validateAndDisplayData(TakeAttendanceActivity.this.currentClassData);
//                    } else {
//                        showError("No standby classes found for this user.");
//                    }
//                } else {
//                    showError("Failed to load class data. Server Error: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<ClassApiResponse>> call, @NonNull Throwable t) {
//                showLoading(false);
//                showError("Network Error: " + t.getMessage());
//            }
//        });
//    }
//
//    private void validateAndDisplayData(ClassApiResponse classData) {
//        List<Student> students = classData.getStudents();
//        if (students == null || students.isEmpty()) {
//            showError("This class has no students to take attendance for.");
//            return;
//        }
//
//        // All checks passed, set up the ViewPager
//        viewPager.setVisibility(View.VISIBLE);
//        navigationControls.setVisibility(View.VISIBLE);
//        fabSubmit.setVisibility(View.VISIBLE);
//
//        pageAdapter = new StudentPageAdapter(students, STUDENTS_PER_PAGE);
//        viewPager.setAdapter(pageAdapter);
//
//        // Update UI for the initial page
//        updateUiForPage(0);
//    }
//
//    private void submitAttendance() {
//        fabSubmit.setEnabled(false); // Disable button to prevent multiple clicks
//
//        // Step 1: Get the current user's ID from /api/me
//        apiService.getMe().enqueue(new Callback<LoginResponseWrapper>() {
//            @Override
//            public void onResponse(@NonNull Call<LoginResponseWrapper> call, @NonNull Response<LoginResponseWrapper> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
//                    // User fetched successfully, get their ID
//                    currentUserId = response.body().getUser().getId();
//                    // Now that we have the userId, proceed to build and post the attendance data
//                    postAttendanceData();
//                } else {
//                    Toast.makeText(TakeAttendanceActivity.this, "Error: Could not verify user.", Toast.LENGTH_SHORT).show();
//                    fabSubmit.setEnabled(true); // Re-enable button on failure
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<LoginResponseWrapper> call, @NonNull Throwable t) {
//                Toast.makeText(TakeAttendanceActivity.this, "Network Error: Cannot get user profile.", Toast.LENGTH_SHORT).show();
//                fabSubmit.setEnabled(true); // Re-enable button on failure
//            }
//        });
//    }
//
//    private void postAttendanceData() {
//        // This method is called only after we have a valid `currentUserId`
//
//        // 1. Guard against null data
//        if (pageAdapter == null || currentClassData == null || currentUserId == null) {
//            Toast.makeText(this, "Error: Data not ready for submission.", Toast.LENGTH_SHORT).show();
//            fabSubmit.setEnabled(true);
//            return;
//        }
//
//        // 2. Combine attendance maps from all pages in the ViewPager
//        Map<String, String> finalAttendanceMap = new HashMap<>();
//        for (TakeAttendanceAdapter adapter : pageAdapter.getPageAdapters()) {
//            finalAttendanceMap.putAll(adapter.getAttendanceStatusMap());
//        }
//
//        if (finalAttendanceMap.isEmpty()) {
//            Toast.makeText(this, "No attendance data to submit.", Toast.LENGTH_SHORT).show();
//            fabSubmit.setEnabled(true);
//            return;
//        }
//
//        // 3. Prepare the data payload matching the API documentation
//        String className = currentClassData.getName();
//        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
//
//        List<AttendanceDataItem> dataItems = new ArrayList<>();
//        for (Map.Entry<String, String> entry : finalAttendanceMap.entrySet()) {
//            // Key = Student's unique ID, Value = Status (e.g., "PRESENT")
//            dataItems.add(new AttendanceDataItem(entry.getKey(), today, entry.getValue()));
//        }
//
//        // This is the final object to send, using the correct model class
//        AttendanceSubmissionPayload submissionPayload = new AttendanceSubmissionPayload(className, currentUserId, dataItems);
//
//        Log.d(TAG, "Submitting payload: " + new Gson().toJson(submissionPayload));
//
//        // 4. Make the final API call with the correct endpoint and payload
//        apiService.submitAttendance(submissionPayload).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                fabSubmit.setEnabled(true); // Re-enable button
//                if (response.isSuccessful()) {
//                    Toast.makeText(TakeAttendanceActivity.this, "Attendance Submitted Successfully!", Toast.LENGTH_LONG).show();
//                    finish(); // Close the activity on success
//                } else {
//                    Toast.makeText(TakeAttendanceActivity.this, "Submission Failed: " + response.code(), Toast.LENGTH_LONG).show();
//                    Log.e(TAG, "Submission failed. Response: " + response.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                fabSubmit.setEnabled(true); // Re-enable button
//                Toast.makeText(TakeAttendanceActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void updateUiForPage(int position) {
//        if (pageAdapter == null || pageAdapter.getItemCount() == 0) return;
//        int totalPages = pageAdapter.getItemCount();
//
//        tvStudentCounter.setText("Page " + (position + 1) + " / " + totalPages);
//
//        btnPrevious.setEnabled(position > 0);
//        btnNext.setEnabled(position < totalPages - 1);
//    }
//
//    private void showLoading(boolean isLoading) {
//        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//        tvStatusMessage.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//        tvStatusMessage.setText(isLoading ? "Loading Classes..." : "");
//
//        if (isLoading) {
//            // Hide main content while loading
//            viewPager.setVisibility(View.GONE);
//            navigationControls.setVisibility(View.GONE);
//            fabSubmit.setVisibility(View.GONE);
//        }
//    }
//
//    private void showError(String message) {
//        // Hide everything and show only the error message
//        viewPager.setVisibility(View.GONE);
//        navigationControls.setVisibility(View.GONE);
//        fabSubmit.setVisibility(View.GONE);
//        pbLoading.setVisibility(View.GONE);
//
//        tvStatusMessage.setVisibility(View.VISIBLE);
//        tvStatusMessage.setText(message);
//    }
//}



//worked code
//package com.example.studentmanagementsystem.ui;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.viewpager2.widget.ViewPager2;
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.adapter.StudentPageAdapter;
//import com.example.studentmanagementsystem.adapter.StudentPageAdapter;
//import com.example.studentmanagementsystem.adapter.TakeAttendanceAdapter;
//import com.example.studentmanagementsystem.api.ApiClient;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.model.AttendanceDataItem;
//import com.example.studentmanagementsystem.model.AttendanceV2Submission;
//import com.example.studentmanagementsystem.model.ClassApiResponse;
//import com.example.studentmanagementsystem.model.Student;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class TakeAttendanceActivity extends AppCompatActivity {
//
//    // Views from the new layout
//    private ViewPager2 viewPager;
//    private ProgressBar pbLoading;
//    private TextView tvStatusMessage, tvStudentCounter;
//    private MaterialButton btnNext, btnPrevious;
//    private RelativeLayout navigationControls;
//    private FloatingActionButton fabSubmit;
//
//    private StudentPageAdapter pageAdapter;
//    private ApiServices apiService; // <-- ADD THIS
//    private ClassApiResponse currentClassData; // <-- ADD THIS
//    private String currentUserId;
//    private static final int STUDENTS_PER_PAGE = 10;
//
////    private StudentPagerAdapter pagerAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_take_attendance);
//
//        apiService = ApiClient.getClient(this).create(ApiServices.class); // <-- ADD THIS
//
//        // Initialize Views
//        setupToolbar();
//        viewPager = findViewById(R.id.view_pager_attendance);
//        pbLoading = findViewById(R.id.pb_loading);
//        tvStatusMessage = findViewById(R.id.tv_status_message);
//        tvStudentCounter = findViewById(R.id.tv_student_counter);
//        btnNext = findViewById(R.id.btn_next);
//        btnPrevious = findViewById(R.id.btn_previous);
//        navigationControls = findViewById(R.id.navigation_controls);
//        fabSubmit = findViewById(R.id.fab_submit_attendance);
//
//        // Fetch class data from the API
//        fetchClassData();
//
//        // Set up listeners for navigation buttons
//        setupNavigationListeners();
//    }
//
//    private void setupToolbar() {
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_take_attendance);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed(); // Handle the back arrow click
//        return true;
//    }
//
//    private void setupNavigationListeners() {
//        btnNext.setOnClickListener(v -> {
//            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
//        });
//
//        btnPrevious.setOnClickListener(v -> {
//            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
//        });
//
//        fabSubmit.setOnClickListener(v -> {
//            if (pageAdapter != null) {
//                // Here you can get the attendance data and submit it
//                submitAttendance();
//                // Map<String, String> attendanceData = pagerAdapter.getAttendanceStatusMap();
//                // Log.d("AttendanceData", attendanceData.toString());
//                // TODO: Add API call to submit data
//            }
//        });
//
//        // Add a callback to update the counter and button visibility as the user swipes
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                updateUiForPage(position);
//            }
//        });
//    }
//
//    private void fetchClassData() {
//        showLoading(true);
//// worked code
////        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);
////        Call<List<ClassApiResponse>> call = apiService.getClassForAttendance();
//        Call<List<ClassApiResponse>> call = apiService.getStandbyClasses();
//
//        call.enqueue(new Callback<List<ClassApiResponse>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<ClassApiResponse>> call, @NonNull Response<List<ClassApiResponse>> response) {
//                showLoading(false);
//                if (response.isSuccessful() && response.body() != null) {
//                    List<ClassApiResponse> classList = response.body();
//                    if (!classList.isEmpty()) {
//                        // Get the first class from the list
//                        // 1. Get the first class and store it in the class field
//                        TakeAttendanceActivity.this.currentClassData = classList.get(0);
//
//                        // 2. Call the next method to display the students
//                        validateAndDisplayData(TakeAttendanceActivity.this.currentClassData);
//
//                        //worked code
////                        ClassApiResponse firstClass = classList.get(0);
////                        validateAndDisplayData(firstClass);
//                    } else {
//                        showError("No classes found for this user.");
//                    }
//                } else {
//                    showError("Failed to load class data. Server returned an error.");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<ClassApiResponse>> call, Throwable t) {
//                showLoading(false);
//                showError("Network Error: " + t.getMessage());
//            }
//        });
//    }
//
//    // DELETE the old submitAttendance method and ADD this new one
//
//    private void submitAttendance() {
//        // 1. Guard against null data
//        if (pageAdapter == null || currentClassData == null || currentClassData.getTeacher() == null) {
//            Toast.makeText(this, "Error: Data not ready for submission.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // 2. Combine attendance maps from all pages into one
//        Map<String, String> finalAttendanceMap = new HashMap<>();
//        for (TakeAttendanceAdapter adapter : pageAdapter.getPageAdapters()) {
//            finalAttendanceMap.putAll(adapter.getAttendanceStatusMap());
//        }
//
//        if (finalAttendanceMap.isEmpty()) {
//            Toast.makeText(this, "No attendance data to submit.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // 3. Prepare the data payload for the API
//        String className = currentClassData.getName();
//        String teacherName = currentClassData.getTeacher().getFullName();
//        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
//
//        List<AttendanceDataItem> dataItems = new ArrayList<>();
//        for (Map.Entry<String, String> entry : finalAttendanceMap.entrySet()) {
//            // The key is the full Student ID (e.g., "STU001")
//            dataItems.add(new AttendanceDataItem(entry.getKey(), today, entry.getValue()));
//        }
//
//        // This is the final object to send to the server
//        AttendanceV2Submission submissionPayload = new AttendanceV2Submission(className, teacherName, dataItems);
//
//        android.util.Log.d("Submission", "Submitting payload: " + new com.google.gson.Gson().toJson(submissionPayload));
//
//        // 4. Make the API call
//        fabSubmit.setEnabled(false); // Disable button to prevent double clicks
//        apiService.submitAttendanceV2(submissionPayload).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                fabSubmit.setEnabled(true); // Re-enable button
//                if (response.isSuccessful()) {
//                    Toast.makeText(TakeAttendanceActivity.this, "Attendance Submitted Successfully!", Toast.LENGTH_LONG).show();
//                    finish(); // Close the activity on success
//                } else {
//                    Toast.makeText(TakeAttendanceActivity.this, "Submission Failed: " + response.code(), Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                fabSubmit.setEnabled(true); // Re-enable button
//                Toast.makeText(TakeAttendanceActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//
//
//    private void validateAndDisplayData(ClassApiResponse classData) {
//        List<Student> students = classData.getStudents();
//        if (students == null || students.isEmpty()) {
//            showError("This class has no students to take attendance for.");
//            return;
//        }
//
//        // All checks passed, set up the ViewPager
//        viewPager.setVisibility(View.VISIBLE);
//        navigationControls.setVisibility(View.VISIBLE);
//        fabSubmit.setVisibility(View.VISIBLE);
//
//        pageAdapter = new StudentPageAdapter(students, STUDENTS_PER_PAGE);
//        viewPager.setAdapter(pageAdapter);
//
//// Update total page count
//        updateUiForPage(0);
//
//    }
//
//    private void updateUiForPage(int position) {
//        if (pageAdapter == null) return;
//        int totalPages = pageAdapter.getItemCount();
//
//        tvStudentCounter.setText("Page " + (position + 1) + " / " + totalPages);
//
//        btnPrevious.setEnabled(position > 0);
//        btnNext.setEnabled(position < totalPages - 1);
//    }
//
//
//    private void showLoading(boolean isLoading) {
//        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//        if (isLoading) {
//            // Hide everything else while loading
//            viewPager.setVisibility(View.GONE);
//            tvStatusMessage.setVisibility(View.GONE);
//            navigationControls.setVisibility(View.GONE);
//            fabSubmit.setVisibility(View.GONE);
//        }
//    }
//
//    private void showError(String message) {
//        // Hide everything and show the error message
//        viewPager.setVisibility(View.GONE);
//        navigationControls.setVisibility(View.GONE);
//        fabSubmit.setOnClickListener(v -> {
//            if (pageAdapter != null) {
//                Map<String, String> finalAttendance = new HashMap<>();
//
//                for (TakeAttendanceAdapter adapter : pageAdapter.getPageAdapters()) {
//                    finalAttendance.putAll(adapter.getAttendanceStatusMap());
//                }
//
//                Toast.makeText(this, "Submitting " + finalAttendance.size() + " records", Toast.LENGTH_SHORT).show();
//
//                // TODO: send 'finalAttendance' to API
//            }
//        });
//
//        pbLoading.setVisibility(View.GONE);
//
//        tvStatusMessage.setVisibility(View.VISIBLE);
//        tvStatusMessage.setText(message);
//    }
//}



//package com.example.studentmanagementsystem.ui;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//// Import SwipeRefreshLayout
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.adapter.TakeAttendanceAdapter;
//import com.example.studentmanagementsystem.api.ApiClient;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.model.ClassApiResponse;
//import com.example.studentmanagementsystem.model.User;
//import com.example.studentmanagementsystem.util.Prefs;
//import com.google.android.material.appbar.MaterialToolbar;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class TakeAttendanceActivity extends AppCompatActivity {
//
//    private RecyclerView rvStudents;
//    private ProgressBar pbLoading;
//    private TextView tvStatusMessage;
//    // Declare the SwipeRefreshLayout variable
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private TakeAttendanceAdapter adapter;
//    private User loggedInTeacher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_take_attendance);
//
//        // Get logged-in teacher's data from Prefs
//        loggedInTeacher = Prefs.getInstance(this).getUser();
//
//        // Initialize Views
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_take_attendance);
//        toolbar.setTitle("Take Attendance");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        rvStudents = findViewById(R.id.rv_students_for_attendance);
//        pbLoading = findViewById(R.id.pb_loading);
//        tvStatusMessage = findViewById(R.id.tv_status_message);
//        // Find the SwipeRefreshLayout by its ID
//        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
//
//        rvStudents.setLayoutManager(new LinearLayoutManager(this));
//
//        // Set up the refresh listener
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            // This code is executed when the user swipes down
//            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
//            fetchClassData();
//        });
//
//        // Configure the refresh indicator color (optional)
//        swipeRefreshLayout.setColorSchemeResources(R.color.purple_500, R.color.purple_700, R.color.teal_200);
//
//        // Fetch the data from the API for the first time
//        fetchClassData();
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed(); // Go back when the top-left back arrow is pressed
//        return true;
//    }
//
//    private void fetchClassData() {
//        // Show the correct loading indicator
//        // If it's a swipe-refresh, the indicator is already showing.
//        // If it's the initial load, show the progress bar.
//        if (!swipeRefreshLayout.isRefreshing()) {
//            showLoading(true);
//        }
//
//        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);
//
//        // The call object is now of type List<ClassApiResponse>
//        Call<List<ClassApiResponse>> call = apiService.getClassForAttendance();
//
//        // The generic type of the Callback must also be updated to match
//        call.enqueue(new Callback<List<ClassApiResponse>>() {
//            @Override
//            public void onResponse(Call<List<ClassApiResponse>> call, Response<List<ClassApiResponse>> response) {
//                showLoading(false);
//                swipeRefreshLayout.setRefreshing(false);
//
//                // The response body is now a LIST
//                if (response.isSuccessful() && response.body() != null) {
//                    List<ClassApiResponse> classList = response.body();
//
//                    // Check if the list is not empty
//                    if (!classList.isEmpty()) {
//                        // Get the FIRST class from the list.
//                        // In a more advanced app, you might show a dialog for the teacher to choose a class.
//                        ClassApiResponse firstClass = classList.get(0);
//                        validateAndDisplayData(firstClass);
//                    } else {
//                        // The API returned an empty list
//                        showError("No classes found for this user.");
//                    }
//
//                } else {
//                    showError("Failed to load class data. Please try again.");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<ClassApiResponse>> call, Throwable t) {
//                showLoading(false);
//                swipeRefreshLayout.setRefreshing(false);
//                // Check for the specific error here as well
//                if (t.getMessage().contains("BEGIN_OBJECT but was BEGIN_ARRAY")) {
//                    showError("API Mismatch Error. Expected an Object but got an Array.");
//                } else {
//                    showError("Network Error: " + t.getMessage());
//                }
//            }
//        });
//    }
//
//    private void validateAndDisplayData(ClassApiResponse classData) {
//        // Validation Check 1: Is the logged-in user the teacher of this class?
//        if (classData.getTeacher() == null) {
//            showError("This class has no assigned teacher.");
//            return;
//        }
//
//        // Validation Check 2: Are there any students in the class?
//        if (classData.getStudents() == null || classData.getStudents().isEmpty()) {
//            showError("There are no students in this class to take attendance for.");
//            return;
//        }
//
//        // Make the RecyclerView visible if it was hidden by an error
//        rvStudents.setVisibility(View.VISIBLE);
//        tvStatusMessage.setVisibility(View.GONE);
//
//        // All checks passed, display the students
//        adapter = new TakeAttendanceAdapter(classData.getStudents());
//        rvStudents.setAdapter(adapter);
//    }
//
//    private void showLoading(boolean isLoading) {
//        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//        if (isLoading) {
//            tvStatusMessage.setVisibility(View.GONE);
//            rvStudents.setVisibility(View.GONE);
//        }
//    }
//
//    private void showError(String message) {
//        rvStudents.setVisibility(View.GONE);
//        tvStatusMessage.setVisibility(View.VISIBLE);
//        tvStatusMessage.setText(message);
//    }
//}


//package com.example.studentmanagementsystem.ui;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.adapter.TakeAttendanceAdapter;
//import com.example.studentmanagementsystem.api.ApiClient;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.model.ClassApiResponse;
//import com.example.studentmanagementsystem.model.User;
//import com.example.studentmanagementsystem.util.Prefs;
//import com.google.android.material.appbar.MaterialToolbar;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class TakeAttendanceActivity extends AppCompatActivity {
//
//    private RecyclerView rvStudents;
//    private ProgressBar pbLoading;
//    private TextView tvStatusMessage;
//    private TakeAttendanceAdapter adapter;
//    private User loggedInTeacher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_take_attendance);
//
//        // Get logged-in teacher's data from Prefs
//        loggedInTeacher = Prefs.getInstance(this).getUser();
//
//        // Initialize Views
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_take_attendance);
//        toolbar.setTitle("Take Attendance");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        rvStudents = findViewById(R.id.rv_students_for_attendance);
//        pbLoading = findViewById(R.id.pb_loading);
//        tvStatusMessage = findViewById(R.id.tv_status_message);
//
//        rvStudents.setLayoutManager(new LinearLayoutManager(this));
//
//        // Fetch the data from the API
//        fetchClassData();
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed(); // Go back when the top-left back arrow is pressed
//        return true;
//    }
//
//    private void fetchClassData() {
//        showLoading(true);
//
//        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);
//        Call<ClassApiResponse> call = apiService.getClassForAttendance();
//
//        call.enqueue(new Callback<ClassApiResponse>() {
//            @Override
//            public void onResponse(Call<ClassApiResponse> call, Response<ClassApiResponse> response) {
//                showLoading(false);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    ClassApiResponse classData = response.body();
//                    validateAndDisplayData(classData);
//                } else {
//                    showError("Failed to load class data. Please try again.");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ClassApiResponse> call, Throwable t) {
//                showLoading(false);
//                showError("Network Error: " + t.getMessage());
//            }
//        });
//    }
//
//    private void validateAndDisplayData(ClassApiResponse classData) {
//        // Validation Check 1: Is the logged-in user the teacher of this class?
//        // NOTE: For a real app, loggedInTeacher.getTeacherId() should be used.
//        // We will use a placeholder check for now.
//        if (classData.getTeacher() == null) {
//            showError("This class has no assigned teacher.");
//            return;
//        }
//
//        // Validation Check 2: Are there any students in the class?
//        if (classData.getStudents() == null || classData.getStudents().isEmpty()) {
//            showError("There are no students in this class to take attendance for.");
//            return;
//        }
//
//        // All checks passed, display the students
//        adapter = new TakeAttendanceAdapter(classData.getStudents());
//        rvStudents.setAdapter(adapter);
//    }
//
//    private void showLoading(boolean isLoading) {
//        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//        if (isLoading) {
//            tvStatusMessage.setVisibility(View.GONE);
//            rvStudents.setVisibility(View.GONE);
//        }
//    }
//
//    private void showError(String message) {
//        rvStudents.setVisibility(View.GONE);
//        tvStatusMessage.setVisibility(View.VISIBLE);
//        tvStatusMessage.setText(message);
//    }
//}
