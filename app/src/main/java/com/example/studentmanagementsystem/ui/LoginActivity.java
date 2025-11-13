package com.example.studentmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.LoginRequest;
import com.example.studentmanagementsystem.model.LoginResponseWrapper;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.util.Prefs;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Set layout first!

        // 1. Initialize views immediately after setting the content view.
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 2. Now that views are initialized, check for an existing session.
        if (Prefs.getInstance(this).isUserLoggedIn()) {
            Log.d(TAG, "User already logged in. Navigating to dashboard.");
            navigateToDashboard(Prefs.getInstance(this).getUser());
            return; // Stop further execution in onCreate
        }

        // 3. Set the click listener for manual login.
        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoginInProgress(true);

        LoginRequest loginRequest = new LoginRequest(email, password);
        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);

        // The login call should expect a Void response body but will have headers
        apiService.login(loginRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Extract and save the session token from the response headers
                    Headers headers = response.headers();
                    String setCookieHeader = headers.get("Set-Cookie");
                    if (setCookieHeader != null) {
                        String[] cookieParts = setCookieHeader.split(";");
                        if (cookieParts.length > 0 && cookieParts[0].startsWith("sessionToken=")) {
                            String sessionTokenValue = cookieParts[0].substring("sessionToken=".length());
                            Prefs.getInstance(getApplicationContext()).saveSessionToken(sessionTokenValue);
                            Log.d(TAG, "Session token saved successfully.");
                        }
                    }

                    // Proceed to fetch user details with the now-active session
                    fetchUserData();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    setLoginInProgress(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                setLoginInProgress(false);
            }
        });
    }

    private void fetchUserData() {
        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);
        apiService.getMe().enqueue(new Callback<LoginResponseWrapper>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseWrapper> call, @NonNull Response<LoginResponseWrapper> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    User loggedInUser = response.body().getUser();
                    // Save the user profile (which also saves the session expiration)
                    Prefs.getInstance(getApplicationContext()).saveUser(loggedInUser);
                    Log.d(TAG, "User data fetched and saved. Role: " + loggedInUser.getRole());

                    // Navigate to the correct dashboard
                    navigateToDashboard(loggedInUser);
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to retrieve user profile after login.", Toast.LENGTH_SHORT).show();
                    setLoginInProgress(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseWrapper> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error fetching profile: " + t.getMessage(), Toast.LENGTH_LONG).show();
                setLoginInProgress(false);
            }
        });
    }

    private void navigateToDashboard(User user) {
        if (user == null || user.getRole() == null) {
            Toast.makeText(this, "User data is invalid. Cannot proceed.", Toast.LENGTH_LONG).show();
            setLoginInProgress(false);
            return;
        }

        String sanitizedRole = user.getRole().trim().toUpperCase();
        Intent intent;

        if ("TEACHER".equals(sanitizedRole)) {
            intent = new Intent(this, TeacherDashboardActivity.class);
        } else if ("STUDENT".equals(sanitizedRole)) {
            intent = new Intent(this, StudentDashboardActivity.class);
        } else {
            Toast.makeText(this, "Login successful, but role '" + sanitizedRole + "' is not supported.", Toast.LENGTH_LONG).show();
            setLoginInProgress(false);
            // Clear bad login data so the user doesn't get stuck in a loop
            Prefs.getInstance(this).clearUserData();
            return;
        }

        Toast.makeText(this, "Welcome " + user.getName() + "!", Toast.LENGTH_SHORT).show();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish LoginActivity so the user cannot go back to it
    }

    private void setLoginInProgress(boolean inProgress) {
        btnLogin.setEnabled(!inProgress);
        btnLogin.setText(inProgress ? "Logging In..." : "Login");
    }
}


//package com.example.studentmanagementsystem.ui;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.studentmanagementsystem.MainActivity;
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.api.ApiClient;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.model.LoginRequest; // Import the new model
//import com.example.studentmanagementsystem.model.LoginResponseWrapper;
//import com.example.studentmanagementsystem.model.User;
//import com.example.studentmanagementsystem.util.Prefs;
//import com.google.android.material.textfield.TextInputEditText;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class LoginActivity extends AppCompatActivity {
//    private static final String TAG = "LoginActivity";
//    private TextInputEditText etEmail, etPassword; // Renamed for clarity
//    private Button btnLogin;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        etEmail = findViewById(R.id.etUsername); // Still uses the same ID from your XML
//        etPassword = findViewById(R.id.etPassword);
//        btnLogin = findViewById(R.id.btnLogin);
//
//        btnLogin.setOnClickListener(v -> loginUser());
//    }
//
//    private void loginUser() {
//        String email = etEmail.getText().toString().trim();
//        String password = etPassword.getText().toString().trim();
//
//        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
//            Toast.makeText(LoginActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        setLoginInProgress(true);
//
//        LoginRequest loginRequest = new LoginRequest(email, password);
//        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);
//
//        // === STEP 1: Call the login endpoint ===
//        apiService.login(loginRequest).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                if (response.isSuccessful()) {
//                    // Login was successful, now fetch user data
//                    fetchUserData(apiService);
//                } else {
//                    // Handle login failure (e.g., invalid credentials)
//                    Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
//                    setLoginInProgress(false);
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                // Handle network failure for the login call
//                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//                setLoginInProgress(false);
//            }
//        });
//    }
//
//    // === STEP 2: Fetch user data after successful login ===
//    // === STEP 2: Fetch user data after successful login ===
//    // ... inside the fetchUserData method ...
//
//    private void fetchUserData(ApiServices apiService) {
//        apiService.getMe().enqueue(new Callback<LoginResponseWrapper>() {
//            @Override
//            public void onResponse(@NonNull Call<LoginResponseWrapper> call, @NonNull Response<LoginResponseWrapper> response) {
//                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
//                    User loggedInUser = response.body().getUser();
//
//                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
//                    Prefs.getInstance(getApplicationContext()).saveUser(loggedInUser);
//
//                    // Sanitize the role to prevent null pointer exceptions and handle whitespace/case issues.
//                    String sanitizedRole = loggedInUser.getRole() != null ? loggedInUser.getRole().trim().toUpperCase() : "";
//
////                    Intent intent;
////
////                    // === FIX IS HERE: REMOVE THE "ADMIN" CHECK ===
////                    if ("TEACHER".equals(sanitizedRole)) {
////                        intent = new Intent(LoginActivity.this, TeacherDashboardActivity.class);
////                    } else if ("STUDENT".equals(sanitizedRole)) {
////                        intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
////                    } else {
////                        // This block now correctly handles unsupported roles like "ADMIN", "GUEST", etc.
////                        Toast.makeText(LoginActivity.this, "Login successful, but this app only supports Teacher and Student roles.", Toast.LENGTH_LONG).show();
////                        setLoginInProgress(false);
////                        return; // Stop execution
////                    }
////
////                    // This code runs only if a role was matched
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    startActivity(intent);
////                    finish();
//
//                    // ... inside the fetchUserData method's onResponse ...
//
//                    Intent intent;
//
//                    if ("TEACHER".equals(sanitizedRole)) {
//                        intent = new Intent(LoginActivity.this, TeacherDashboardActivity.class);
//                    } else if ("STUDENT".equals(sanitizedRole)) {
//                        intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
//                    } else {
//                        // === THIS IS THE MODIFIED BLOCK ===
//
//                        // Original Toast:
//                        // Toast.makeText(LoginActivity.this, "Login successful, but this app only supports Teacher and Student roles.", Toast.LENGTH_LONG).show();
//
//                        // New, More Informative Toast:
//                        Toast.makeText(LoginActivity.this, "Login successful, but role received was: '" + sanitizedRole + "'", Toast.LENGTH_LONG).show();
//
//                        setLoginInProgress(false);
//                        return; // Stop execution
//                    }
//
//                    // This code runs only if a role was matched
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
//// ...
//
//
//                } else {
//                    // Handle failure to get user data
//                    Toast.makeText(LoginActivity.this, "Failed to retrieve user profile.", Toast.LENGTH_SHORT).show();
//                    setLoginInProgress(false);
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<LoginResponseWrapper> call, @NonNull Throwable t) {
//                // Handle network failure for the /me call
//                Toast.makeText(LoginActivity.this, "Network Error fetching profile: " + t.getMessage(), Toast.LENGTH_LONG).show();
//                setLoginInProgress(false);
//            }
//        });
//    }
//
//
//
//    private void setLoginInProgress(boolean inProgress) {
//        btnLogin.setEnabled(!inProgress);
//        btnLogin.setText(inProgress ? "Logging In..." : "Login");
//    }
//}

