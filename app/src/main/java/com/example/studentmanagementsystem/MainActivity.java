package com.example.studentmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.LoginResponse;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.ui.StudentDashboardActivity;
import com.example.studentmanagementsystem.ui.TeacherDashboardActivity;
import com.example.studentmanagementsystem.util.Prefs;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Handle login click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });
    }
//    private void handleLogin() {
//        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
//        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
//
//        if (TextUtils.isEmpty(username)) {
//            etUsername.setError("Username required");
//            etUsername.requestFocus();
//            return;
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            etPassword.setError("Password required");
//            etPassword.requestFocus();
//            return;
//        }
//
//        // Initialize API
//        ApiServices api = ApiClient.getClient(this, "https://yourwebsite.com/") // 🔧 change your base URL
//                .create(ApiServices.class);
//
//        // Show progress
//        btnLogin.setEnabled(false);
//
//        api.login(username, password).enqueue(new retrofit2.Callback<LoginResponse>() {
//            @Override
//            public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
//                btnLogin.setEnabled(true);
//                if (response.isSuccessful() && response.body() != null) {
//                    LoginResponse res = response.body();
//                    if (res.success) {
//                        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
//
//                        // Save token & userType
//                        Prefs.saveToken(MainActivity.this, res.sessionToken);
//                        Prefs.saveUserType(MainActivity.this, res.userType);
//
//                        // Navigate based on userType
//                        if ("teacher".equalsIgnoreCase(res.userType)) {
//                            startActivity(new Intent(MainActivity.this, TeacherDashboardActivity.class));
//                        } else if ("student".equalsIgnoreCase(res.userType)) {
//                            startActivity(new Intent(MainActivity.this, StudentDashboardActivity.class));
//                        } else {
//                            Toast.makeText(MainActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
//                        }
//
//                        finish();
//                    } else {
//                        Toast.makeText(MainActivity.this, res.message != null ? res.message : "Login failed", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
//                btnLogin.setEnabled(true);
//                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void handleLogin() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username (email) required");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        ApiServices api = ApiClient.getClient(this, "https://student-attendance-system-f7ly.onrender.com/")
                .create(ApiServices.class);

        api.getAllUsers().enqueue(new retrofit2.Callback<List<User>>() {
            @Override
            public void onResponse(retrofit2.Call<List<User>> call, retrofit2.Response<List<User>> response) {
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();

                    User foundUser = null;
                    for (User user : users) {
                        if (user.email.equalsIgnoreCase(username)) {
                            foundUser = user;
                            break;
                        }
                    }

                    if (foundUser == null) {
                        Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ⚠️ WARNING: The passwords from API are hashed (bcrypt)
                    // So you cannot directly compare plain-text passwords.
                    // For demo, you can simulate:
                    if (password.equals("1234")) { // Replace with your own simple rule
                        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        Prefs.saveUserType(MainActivity.this, foundUser.role);

                        if ("TEACHER".equalsIgnoreCase(foundUser.role)) {
                            startActivity(new Intent(MainActivity.this, TeacherDashboardActivity.class));
                        } else if ("STUDENT".equalsIgnoreCase(foundUser.role)) {
                            startActivity(new Intent(MainActivity.this, StudentDashboardActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                        }

                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<User>> call, Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}