package com.example.studentmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentmanagementsystem.MainActivity;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.LoginRequest; // Import the new model
import com.example.studentmanagementsystem.model.LoginResponseWrapper;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.util.Prefs;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText etEmail, etPassword; // Renamed for clarity
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etUsername); // Still uses the same ID from your XML
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoginInProgress(true);

        LoginRequest loginRequest = new LoginRequest(email, password);
        ApiServices apiService = ApiClient.getSimpleClient().create(ApiServices.class);

        // === STEP 1: Call the login endpoint ===
        apiService.login(loginRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Login was successful, now fetch user data
                    fetchUserData(apiService);
                } else {
                    // Handle login failure (e.g., invalid credentials)
                    Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                    setLoginInProgress(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Handle network failure for the login call
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                setLoginInProgress(false);
            }
        });
    }

    // === STEP 2: Fetch user data after successful login ===
    private void fetchUserData(ApiServices apiService) {
        apiService.getMe().enqueue(new Callback<LoginResponseWrapper>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseWrapper> call, @NonNull Response<LoginResponseWrapper> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    User loggedInUser = response.body().getUser();

                    // --- Your existing navigation logic ---
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Prefs.getInstance(getApplicationContext()).saveUser(loggedInUser);

                    String sanitizedRole = loggedInUser.getRole() != null ? loggedInUser.getRole().trim().toUpperCase() : "";

                    Intent intent;
                    if ("TEACHER".equals(sanitizedRole)) {
                        intent = new Intent(LoginActivity.this, TeacherDashboardActivity.class);
                    } else if ("STUDENT".equals(sanitizedRole)) {
                        intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
                    } else {
                        Toast.makeText(LoginActivity.this, "User role is not supported.", Toast.LENGTH_LONG).show();
                        setLoginInProgress(false);
                        return;
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    // Handle failure to get user data
                    Toast.makeText(LoginActivity.this, "Failed to retrieve user profile.", Toast.LENGTH_SHORT).show();
                    setLoginInProgress(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseWrapper> call, @NonNull Throwable t) {
                // Handle network failure for the /me call
                Toast.makeText(LoginActivity.this, "Network Error fetching profile: " + t.getMessage(), Toast.LENGTH_LONG).show();
                setLoginInProgress(false);
            }
        });
    }

    private void setLoginInProgress(boolean inProgress) {
        btnLogin.setEnabled(!inProgress);
        btnLogin.setText(inProgress ? "Logging In..." : "Login");
    }
}
