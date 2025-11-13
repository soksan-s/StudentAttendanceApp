package com.example.studentmanagementsystem; // Or your package name

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.ui.LoginActivity;
import com.example.studentmanagementsystem.ui.StudentDashboardActivity;
import com.example.studentmanagementsystem.ui.TeacherDashboardActivity;
import com.example.studentmanagementsystem.util.Prefs;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    ImageView runnerGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        runnerGif = findViewById(R.id.runnerGif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.gojo) // your GIF name (runner.gif)
               .into(runnerGif);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Get the Prefs instance
            Prefs prefs = Prefs.getInstance(getApplicationContext());

            // Use the new method to check for a valid session
            if (prefs.isUserLoggedIn()) {
                // Session is valid, get the user and navigate to the correct dashboard
                User user = prefs.getUser();
                if (user != null) {
                    navigateToDashboard(user.getRole());
                } else {
                    // This is unlikely, but as a fallback, go to login
                    navigateToLogin();
                }
            } else {
                // No valid session, user needs to log in
                navigateToLogin();
            }
        }, SPLASH_DELAY);
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        String sanitizedRole = (role != null) ? role.trim().toUpperCase() : "";

        if ("TEACHER".equals(sanitizedRole)) {
            intent = new Intent(SplashActivity.this, TeacherDashboardActivity.class);
        } else if ("STUDENT".equals(sanitizedRole)) {
            intent = new Intent(SplashActivity.this, StudentDashboardActivity.class);
        } else {
            // Role is unknown, send to login as a safeguard
            navigateToLogin();
            return;
        }

        startActivity(intent);
        finish(); // Close the splash activity
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the splash activity
    }
}


//package com.example.studentmanagementsystem;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.bumptech.glide.Glide;
//import com.example.studentmanagementsystem.ui.LoginActivity;
//
//public class SplashActivity extends AppCompatActivity {
//    private static final int SPLASH_TIME_OUT = 4000; // 4 seconds
//    ImageView runnerGif;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_splash);
//
//        // Optional edge-to-edge layout handling
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Initialize the ImageView
//        runnerGif = findViewById(R.id.runnerGif);
//
//        // Load and loop GIF using Glide
//        Glide.with(this)
//                .asGif()
//                .load(R.drawable.gojo) // your GIF name (runner.gif)
//                .into(runnerGif);
//
//        // Delay before switching to MainActivity
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                finish(); // close splash activity
//            }
//        }, SPLASH_TIME_OUT);
//    }
//}
