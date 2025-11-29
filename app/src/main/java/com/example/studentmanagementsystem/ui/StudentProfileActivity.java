package com.example.studentmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.StudentAttendanceResponse;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.util.Prefs;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentProfileActivity extends AppCompatActivity {

    private CircleImageView ivProfileImage;
    private TextView tvNameHeader, tvRole, tvStudentId, tvEmail, tvClass;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        initializeViews();
        setupToolbar();

        // 1. Load basic info from Cache (Prefs) first for immediate display
        loadCachedData();

        // 2. Fetch detailed info (ID, Class) from API
        fetchProfileData();

        setupListeners();
    }

    private void initializeViews() {
        ivProfileImage = findViewById(R.id.iv_profile_image);
        tvNameHeader = findViewById(R.id.tv_profile_name_header);
        tvRole = findViewById(R.id.tv_profile_role);

        tvStudentId = findViewById(R.id.tv_profile_student_id);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvClass = findViewById(R.id.tv_profile_class);

        btnLogout = findViewById(R.id.btn_profile_logout);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadCachedData() {
        // Show what we have immediately while waiting for API
        User user = Prefs.getInstance(this).getUser();
        if (user != null) {
            tvNameHeader.setText(user.getName());
            tvEmail.setText(user.getEmail());
            Glide.with(this)
                    .load(user.getImage())
                    .placeholder(R.drawable.ic_person_outline)
                    .into(ivProfileImage);
        }
    }

    private void fetchProfileData() {
        // Get Cookie
        String cookieHeader = Prefs.getInstance(this).getSessionToken();
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return; // Silent fail or show login prompt
        }

        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);
        Call<StudentAttendanceResponse> call = apiService.getStudentAttendance(cookieHeader);

        call.enqueue(new Callback<StudentAttendanceResponse>() {
            @Override
            public void onResponse(Call<StudentAttendanceResponse> call, Response<StudentAttendanceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StudentAttendanceResponse.StudentInfo student = response.body().getStudent();

                    if (student != null) {
                        // Update UI with fresh data from API
                        tvNameHeader.setText(student.getFullName());
                        tvEmail.setText(student.getEmail());

                        // These are the fields we really needed the API for:
                        tvStudentId.setText(student.getStudentId() != null ? student.getStudentId() : "N/A");
                        tvClass.setText(student.getStandbyClass() != null ? student.getStandbyClass() : "N/A");
                    }
                } else {
                    Log.e("Profile", "Failed to fetch details: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StudentAttendanceResponse> call, Throwable t) {
                Log.e("Profile", "Network error: " + t.getMessage());
            }
        });
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> {
            // Clear Session
            Prefs.getInstance(this).clearUserData();

            // Go to Login
            Intent intent = new Intent(StudentProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}


//package com.example.studentmanagementsystem.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.model.User;
//import com.example.studentmanagementsystem.util.Prefs;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.button.MaterialButton;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class StudentProfileActivity extends AppCompatActivity {
//
//    private CircleImageView ivProfileImage;
//    private TextView tvNameHeader, tvStudentId, tvEmail, tvClass;
//    private MaterialButton btnLogout;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_student_profile);
//
//        initializeViews();
//        setupToolbar();
//        loadUserProfile();
//        setupListeners();
//    }
//
//    private void initializeViews() {
//        ivProfileImage = findViewById(R.id.iv_profile_image);
//        tvNameHeader = findViewById(R.id.tv_profile_name_header);
//        tvStudentId = findViewById(R.id.tv_profile_student_id);
//        tvEmail = findViewById(R.id.tv_profile_email);
//        tvClass = findViewById(R.id.tv_profile_class);
//        btnLogout = findViewById(R.id.btn_profile_logout);
//    }
//
//    private void setupToolbar() {
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_profile);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(v -> onBackPressed());
//    }
//
//    private void loadUserProfile() {
//        // 1. Get user data stored locally in Prefs
//        User user = Prefs.getInstance(this).getUser();
//
//        if (user != null) {
//            tvNameHeader.setText(user.getName());
//            tvEmail.setText(user.getEmail());
//
//            // If your User model has these fields, set them:
//            // If not, you might need to fetch them from the API again or save them during login
//
//            // Example placeholders if data is missing in standard User model
//            tvStudentId.setText(user.getId() != null ? user.getId() : "STU-001");
//            tvClass.setText("Class A"); // You might need to add 'className' to your User model
//
//            // Load Image
//            Glide.with(this)
//                    .load(user.getImage())
//                    .placeholder(R.drawable.ic_person_outline) // Make sure this drawable exists
//                    .into(ivProfileImage);
//        } else {
//            Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void setupListeners() {
//        btnLogout.setOnClickListener(v -> {
//            // Clear Data
//            Prefs.getInstance(this).clearUserData();
//
//            // Redirect to Login
//            Intent intent = new Intent(StudentProfileActivity.this, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        });
//    }
//}
