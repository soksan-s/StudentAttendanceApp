package com.example.studentmanagementsystem.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.ClassApiResponse;
import com.example.studentmanagementsystem.model.LoginResponseWrapper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetupAttendanceActivity extends AppCompatActivity {

    private static final String TAG = "SetupAttendanceActivity";

    private ProgressBar pbLoading;
    private LinearLayout contentArea;
    private AutoCompleteTextView spinnerClass, spinnerTimeSlot;
    private TextInputEditText tvSubject, etDate;

    private ApiServices apiService;
    private List<ClassApiResponse> teacherClasses = new ArrayList<>();
    private ClassApiResponse selectedClass;
    private String selectedDate;
    private String selectedTimeSlot;
    private String currentUserId;

    private boolean isNavigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_attendance);

        initializeViews();
        setupToolbar();

        apiService = ApiClient.getClient(this).create(ApiServices.class);

        // Start loading data
        fetchUserIdAndClasses();
    }

    private void initializeViews() {
        pbLoading = findViewById(R.id.pb_loading);
        contentArea = findViewById(R.id.content_area);
        spinnerClass = findViewById(R.id.spinner_class);
        spinnerTimeSlot = findViewById(R.id.spinner_time_slot);
        tvSubject = findViewById(R.id.tv_subject);
        etDate = findViewById(R.id.et_date);

        // Disable manual typing in the date field
        etDate.setFocusable(false);
        etDate.setClickable(true);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_setup_attendance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupEventListeners() {
        // 1. Handle Class Selection to show Subject
        spinnerClass.setOnItemClickListener((parent, view, position, id) -> {
            selectedClass = teacherClasses.get(position);

            // Debug log to check what we selected
            Log.d(TAG, "Selected Class: " + selectedClass.getName());

            // logic to show subject
            if (selectedClass != null) {
                // Some APIs return "subject" object, some might return "subjectName" string
                // Adjust this based on your ClassApiResponse model
                if (selectedClass.getSubject() != null) {
                    tvSubject.setText(selectedClass.getSubject().getName());
                } else {
                    tvSubject.setText("No Subject Assigned");
                }
            }
            validateFormAndNavigate();
        });

        // 2. Handle Time Slot
        spinnerTimeSlot.setOnItemClickListener((parent, view, position, id) -> {
            selectedTimeSlot = (String) parent.getItemAtPosition(position);
            validateFormAndNavigate();
        });

        // 3. Handle Date - LOCKED to Today
        etDate.setOnClickListener(v -> showDatePicker());
    }

    private void fetchUserIdAndClasses() {
        pbLoading.setVisibility(View.VISIBLE);
        contentArea.setVisibility(View.GONE);

        apiService.getMe().enqueue(new Callback<LoginResponseWrapper>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseWrapper> call, @NonNull Response<LoginResponseWrapper> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    currentUserId = response.body().getUser().getId();
                    fetchStandbyClasses();
                } else {
                    handleError("Could not verify your account. Please log in again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseWrapper> call, @NonNull Throwable t) {
                handleError("Network Error: Could not get user details.");
            }
        });
    }

    private void fetchStandbyClasses() {
        apiService.getStandbyClasses().enqueue(new Callback<List<ClassApiResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ClassApiResponse>> call, @NonNull Response<List<ClassApiResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teacherClasses = response.body();
                    if (teacherClasses.isEmpty()) {
                        handleError("No classes assigned to you.");
                    } else {
                        populateUi();
                    }
                } else {
                    handleError("Failed to load your classes.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ClassApiResponse>> call, @NonNull Throwable t) {
                handleError("Network Error: " + t.getMessage());
            }
        });
    }

    private void populateUi() {
        // Setup Class Spinner
        List<String> classNames = new ArrayList<>();
        for (ClassApiResponse cls : teacherClasses) {
            // Display Name and Section if available
            String displayName = cls.getName();
            if (cls.getSection() != null && !cls.getSection().isEmpty()) {
                displayName += " - " + cls.getSection();
            }
            classNames.add(displayName);
        }
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
        spinnerClass.setAdapter(classAdapter);

        // Setup Time Slot Spinner
        String[] timeSlots = {"Morning (8:00 - 12:00)", "Afternoon (13:00 - 17:00)"};
        ArrayAdapter<String> timeSlotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, timeSlots);
        spinnerTimeSlot.setAdapter(timeSlotAdapter);

        // Set default date to TODAY and display it
        Calendar today = Calendar.getInstance();
        updateDateLabel(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), false);

        setupEventListeners();

        pbLoading.setVisibility(View.GONE);
        contentArea.setVisibility(View.VISIBLE);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> updateDateLabel(year, month, dayOfMonth, true),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // --- RESTRICT DATE SELECTION ---
        // Set both Min and Max date to current time to lock it to "Today"
        // If you want to allow past dates but not future, remove the setMinDate line.
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void updateDateLabel(int year, int month, int dayOfMonth, boolean shouldValidate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(calendar.getTime());
        etDate.setText(selectedDate);

        if (shouldValidate) {
            validateFormAndNavigate();
        }
    }

    private void validateFormAndNavigate() {
        if (isNavigating) return;

        if (currentUserId == null) {
            Toast.makeText(this, "User ID not loaded. Please wait.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isFormValid = selectedClass != null &&
                selectedDate != null && !selectedDate.isEmpty() &&
                selectedTimeSlot != null && !selectedTimeSlot.isEmpty();

        if (isFormValid) {
            isNavigating = true;
            // Add a small delay so the user sees the selection before screen changes
            new Handler(Looper.getMainLooper()).postDelayed(this::navigateToTakeAttendance, 500);
        }
    }

    private void navigateToTakeAttendance() {
        Intent intent = new Intent(SetupAttendanceActivity.this, TakeAttendanceActivity.class);

        // Pass all required data
        intent.putExtra("CLASS_ID", selectedClass.getId());
        // Pass Name as well since we need it for the Payload 400 Error Fix
        intent.putExtra("CLASS_NAME", selectedClass.getName());
        intent.putExtra("USER_ID", currentUserId);
        intent.putExtra("SELECTED_DATE", selectedDate);

        startActivity(intent);

        // Allow navigation again after 1 second (prevents double clicks)
        new Handler(Looper.getMainLooper()).postDelayed(() -> isNavigating = false, 1000);
    }

    private void handleError(String message) {
        pbLoading.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}




//worked code
//package com.example.studentmanagementsystem.ui;
//
//import android.app.DatePickerDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.api.ApiClient;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.model.ClassApiResponse;
//import com.example.studentmanagementsystem.model.LoginResponseWrapper;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.textfield.TextInputEditText;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class SetupAttendanceActivity extends AppCompatActivity {
//
//    private static final String TAG = "SetupAttendanceActivity";
//
//    private ProgressBar pbLoading;
//    private LinearLayout contentArea;
//    private AutoCompleteTextView spinnerClass, spinnerTimeSlot;
//    private TextInputEditText tvSubject, etDate;
//
//    private ApiServices apiService;
//    private List<ClassApiResponse> teacherClasses = new ArrayList<>();
//    private ClassApiResponse selectedClass;
//    private String selectedDate;
//    private String selectedTimeSlot;
//    private String currentUserId;
//
//    private boolean isNavigating = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setup_attendance);
//
//        initializeViews();
//        setupToolbar();
//
//        apiService = ApiClient.getClient(this).create(ApiServices.class);
//
//        // Start the data fetching chain
//        fetchUserIdAndClasses();
//    }
//
//    private void initializeViews() {
//        pbLoading = findViewById(R.id.pb_loading);
//        contentArea = findViewById(R.id.content_area);
//        spinnerClass = findViewById(R.id.spinner_class);
//        spinnerTimeSlot = findViewById(R.id.spinner_time_slot);
//        tvSubject = findViewById(R.id.tv_subject);
//        etDate = findViewById(R.id.et_date);
//    }
//
//    private void setupToolbar() {
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_setup_attendance);
//        setSupportActionBar(toolbar);
//    }
//
//    // This method is now called only AFTER the UI is populated
//    private void setupEventListeners() {
//        spinnerClass.setOnItemClickListener((parent, view, position, id) -> {
//            selectedClass = teacherClasses.get(position);
//            if (selectedClass != null && selectedClass.getSubject() != null) {
//                tvSubject.setText(selectedClass.getSubject().getName());
//            }
//            validateFormAndNavigate();
//        });
//
//        spinnerTimeSlot.setOnItemClickListener((parent, view, position, id) -> {
//            selectedTimeSlot = (String) parent.getItemAtPosition(position);
//            validateFormAndNavigate();
//        });
//
//        etDate.setOnClickListener(v -> showDatePicker());
//    }
//
//    private void fetchUserIdAndClasses() {
//        pbLoading.setVisibility(View.VISIBLE);
//        contentArea.setVisibility(View.GONE);
//
//        apiService.getMe().enqueue(new Callback<LoginResponseWrapper>() {
//            @Override
//            public void onResponse(@NonNull Call<LoginResponseWrapper> call, @NonNull Response<LoginResponseWrapper> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
//                    currentUserId = response.body().getUser().getId();
//                    Log.d(TAG, "User ID fetched successfully: " + currentUserId);
//                    fetchStandbyClasses();
//                } else {
//                    handleError("Could not verify your account. Please log in again.");
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<LoginResponseWrapper> call, @NonNull Throwable t) {
//                handleError("Network Error: Could not get user details.");
//            }
//        });
//    }
//
//    private void fetchStandbyClasses() {
//        apiService.getStandbyClasses().enqueue(new Callback<List<ClassApiResponse>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<ClassApiResponse>> call, @NonNull Response<List<ClassApiResponse>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    teacherClasses = response.body();
//                    if (teacherClasses.isEmpty()) {
//                        handleError("No classes assigned to you.");
//                    } else {
//                        populateUi();
//                    }
//                } else {
//                    handleError("Failed to load your classes from the server.");
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<ClassApiResponse>> call, @NonNull Throwable t) {
//                handleError("Network Error: " + t.getMessage());
//            }
//        });
//    }
//
//    private void populateUi() {
//        List<String> classNames = new ArrayList<>();
//        for (ClassApiResponse cls : teacherClasses) {
//            classNames.add(cls.getName() + " - " + cls.getSection());
//        }
//        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
//        spinnerClass.setAdapter(classAdapter);
//
//        String[] timeSlots = {"Morning (8:00 - 12:00)", "Afternoon (13:00 - 17:00)"};
//        ArrayAdapter<String> timeSlotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, timeSlots);
//        spinnerTimeSlot.setAdapter(timeSlotAdapter);
//
//        // Set default date to today, but DO NOT call validateFormAndNavigate here.
//        Calendar today = Calendar.getInstance();
//        updateDateLabel(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), false);
//
//        // Enable listeners only after UI is ready
//        setupEventListeners();
//
//        pbLoading.setVisibility(View.GONE);
//        contentArea.setVisibility(View.VISIBLE);
//    }
//
//    private void showDatePicker() {
//        Calendar calendar = Calendar.getInstance();
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                this,
//                (view, year, month, dayOfMonth) -> updateDateLabel(year, month, dayOfMonth, true),
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)
//        );
//        datePickerDialog.show();
//    }
//
//    // Added a boolean flag 'shouldValidate' to prevent auto-navigation during initial setup
//    private void updateDateLabel(int year, int month, int dayOfMonth, boolean shouldValidate) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month, dayOfMonth);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        selectedDate = sdf.format(calendar.getTime());
//        etDate.setText(selectedDate);
//
//        if (shouldValidate) {
//            validateFormAndNavigate();
//        }
//    }
//
//    private void validateFormAndNavigate() {
//        if (isNavigating) return;
//
//        // Strict validation: currentUserId MUST NOT be null
//        if (currentUserId == null) {
//            Log.e(TAG, "Validation failed: User ID is null");
//            Toast.makeText(this, "User ID not loaded yet. Please wait.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        boolean isFormValid = selectedClass != null &&
//                selectedDate != null && !selectedDate.isEmpty() &&
//                selectedTimeSlot != null && !selectedTimeSlot.isEmpty();
//
//        if (isFormValid) {
//            isNavigating = true;
//            new Handler(Looper.getMainLooper()).postDelayed(this::navigateToTakeAttendance, 300);
//        }
//    }
//
//    private void navigateToTakeAttendance() {
//        if (currentUserId == null) {
//            isNavigating = false;
//            Toast.makeText(this, "Critical Error: User ID lost. Please restart.", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        Intent intent = new Intent(SetupAttendanceActivity.this, TakeAttendanceActivity.class);
//
//        intent.putExtra("CLASS_ID", selectedClass.getId());
//        intent.putExtra("USER_ID", currentUserId);
//        intent.putExtra("SELECTED_DATE", selectedDate);
//
//        Log.d(TAG, "Navigating with Data:");
//        Log.d(TAG, "  CLASS_ID: " + selectedClass.getId());
//        Log.d(TAG, "  USER_ID: " + currentUserId);
//        Log.d(TAG, "  SELECTED_DATE: " + selectedDate);
//
//        startActivity(intent);
//
//        new Handler(Looper.getMainLooper()).postDelayed(() -> isNavigating = false, 1000);
//    }
//
//
//    private void handleError(String message) {
//        pbLoading.setVisibility(View.GONE);
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//    }
//}





//package com.example.studentmanagementsystem.ui;
//
//import android.app.DatePickerDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.api.ApiClient;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.model.ClassApiResponse;
//import com.example.studentmanagementsystem.model.LoginResponseWrapper;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.textfield.TextInputEditText;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class SetupAttendanceActivity extends AppCompatActivity {
//
//    private static final String TAG = "SetupAttendanceActivity";
//
//    private ProgressBar pbLoading;
//    private LinearLayout contentArea;
//    private AutoCompleteTextView spinnerClass, spinnerTimeSlot;
//    private TextInputEditText tvSubject, etDate;
//
//    private ApiServices apiService;
//    private List<ClassApiResponse> teacherClasses = new ArrayList<>();
//    private ClassApiResponse selectedClass;
//    private String selectedDate;
//    private String selectedTimeSlot;
//    private String currentUserId;
//
//    private boolean isNavigating = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setup_attendance);
//
//        initializeViews();
//        setupToolbar();
//
//        apiService = ApiClient.getClient(this).create(ApiServices.class);
//
//        // This is now the single starting point
//        fetchUserIdAndClasses();
//    }
//
//    private void initializeViews() {
//        pbLoading = findViewById(R.id.pb_loading);
//        contentArea = findViewById(R.id.content_area);
//        spinnerClass = findViewById(R.id.spinner_class);
//        spinnerTimeSlot = findViewById(R.id.spinner_time_slot);
//        tvSubject = findViewById(R.id.tv_subject);
//        etDate = findViewById(R.id.et_date);
//    }
//
//    private void setupToolbar() {
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_setup_attendance);
//        setSupportActionBar(toolbar);
//    }
//
//    // This method is now called only AFTER the UI is populated
//    private void setupEventListeners() {
//        spinnerClass.setOnItemClickListener((parent, view, position, id) -> {
//            selectedClass = teacherClasses.get(position);
//            if (selectedClass != null && selectedClass.getSubject() != null) {
//                tvSubject.setText(selectedClass.getSubject().getName());
//            }
//            validateFormAndNavigate();
//        });
//
//        spinnerTimeSlot.setOnItemClickListener((parent, view, position, id) -> {
//            selectedTimeSlot = (String) parent.getItemAtPosition(position);
//            validateFormAndNavigate();
//        });
//
//        etDate.setOnClickListener(v -> showDatePicker());
//    }
//
//    private void fetchUserIdAndClasses() {
//        pbLoading.setVisibility(View.VISIBLE);
//        contentArea.setVisibility(View.GONE);
//
//        apiService.getMe().enqueue(new Callback<LoginResponseWrapper>() {
//            @Override
//            public void onResponse(@NonNull Call<LoginResponseWrapper> call, @NonNull Response<LoginResponseWrapper> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
//                    currentUserId = response.body().getUser().getId();
//                    fetchStandbyClasses();
//                } else {
//                    handleError("Could not verify your account. Please log in again.");
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<LoginResponseWrapper> call, @NonNull Throwable t) {
//                handleError("Network Error: Could not get user details.");
//            }
//        });
//    }
//
//    private void fetchStandbyClasses() {
//        apiService.getStandbyClasses().enqueue(new Callback<List<ClassApiResponse>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<ClassApiResponse>> call, @NonNull Response<List<ClassApiResponse>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    teacherClasses = response.body();
//                    if (teacherClasses.isEmpty()) {
//                        handleError("No classes assigned to you.");
//                    } else {
//                        populateUi();
//                    }
//                } else {
//                    handleError("Failed to load your classes from the server.");
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<ClassApiResponse>> call, @NonNull Throwable t) {
//                handleError("Network Error: " + t.getMessage());
//            }
//        });
//    }
//
//    private void populateUi() {
//        List<String> classNames = new ArrayList<>();
//        for (ClassApiResponse cls : teacherClasses) {
//            classNames.add(cls.getName() + " - " + cls.getSection());
//        }
//        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, classNames);
//        spinnerClass.setAdapter(classAdapter);
//
//        String[] timeSlots = {"Morning (8:00 - 12:00)", "Afternoon (13:00 - 17:00)"};
//        ArrayAdapter<String> timeSlotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, timeSlots);
//        spinnerTimeSlot.setAdapter(timeSlotAdapter);
//
//        // Set default date to today, but DO NOT call validateFormAndNavigate here.
//        Calendar today = Calendar.getInstance();
//        updateDateLabel(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
//
//        // ***** THE KEY CHANGE IS HERE *****
//        // We only start listening for user input AFTER the entire UI is ready.
//        setupEventListeners();
//
//        pbLoading.setVisibility(View.GONE);
//        contentArea.setVisibility(View.VISIBLE);
//    }
//
//    private void showDatePicker() {
//        Calendar calendar = Calendar.getInstance();
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                this,
//                (view, year, month, dayOfMonth) -> updateDateLabel(year, month, dayOfMonth),
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)
//        );
//        datePickerDialog.show();
//    }
//
//    private void updateDateLabel(int year, int month, int dayOfMonth) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month, dayOfMonth);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        selectedDate = sdf.format(calendar.getTime());
//        etDate.setText(selectedDate);
//
//        // Now this call is safe because it only happens on explicit user interaction
//        validateFormAndNavigate();
//    }
//
//    private void validateFormAndNavigate() {
//        if (isNavigating) return;
//
//        boolean isFormValid = selectedClass != null &&
//                selectedDate != null && !selectedDate.isEmpty() &&
//                selectedTimeSlot != null && !selectedTimeSlot.isEmpty();
//
//        if (isFormValid) {
//            isNavigating = true;
//            new Handler(Looper.getMainLooper()).postDelayed(this::navigateToTakeAttendance, 300);
//        }
//    }
//
//    private void navigateToTakeAttendance() {
//        Intent intent = new Intent(SetupAttendanceActivity.this, TakeAttendanceActivity.class);
//
//        intent.putExtra("CLASS_ID", selectedClass.getId());
//        intent.putExtra("USER_ID", currentUserId);
//        intent.putExtra("SELECTED_DATE", selectedDate);
//
//        Log.d(TAG, "Navigating with Data:");
//        Log.d(TAG, "  CLASS_ID: " + selectedClass.getId());
//        Log.d(TAG, "  USER_ID: " + currentUserId);
//        Log.d(TAG, "  SELECTED_DATE: " + selectedDate);
//
//        startActivity(intent);
//
//        new Handler(Looper.getMainLooper()).postDelayed(() -> isNavigating = false, 1000);
//    }
//
//
//    private void handleError(String message) {
//        pbLoading.setVisibility(View.GONE);
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//    }
//}
