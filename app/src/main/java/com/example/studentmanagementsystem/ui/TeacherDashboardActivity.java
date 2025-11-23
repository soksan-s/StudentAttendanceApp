package com.example.studentmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.util.Prefs;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        // 1. Initialize Views
        MaterialToolbar toolbar = findViewById(R.id.toolbar_teacher);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_teacher);
        navigationView = findViewById(R.id.navigation_view_teacher);

        // 2. Setup Hamburger Icon for the drawer
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 3. Update Header with Teacher's Info
        updateNavHeader();

        // 4. Handle Navigation Item Clicks
        setupNavigationListener();

        // 5. Handle Back Press
        setupBackPressHandler();
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvTeacherName = headerView.findViewById(R.id.tv_teacher_name_header);
        TextView tvTeacherEmail = headerView.findViewById(R.id.tv_teacher_email_header);
        CircleImageView ivTeacherImage = headerView.findViewById(R.id.iv_teacher_image);

        User loggedInUser = Prefs.getInstance(getApplicationContext()).getUser();

        if (loggedInUser != null) {
            tvTeacherName.setText(loggedInUser.getName());
            tvTeacherEmail.setText(loggedInUser.getEmail());

            Glide.with(this)
                    .load(loggedInUser.getImage())
                    .placeholder(R.drawable.ic_person_outline)
                    .into(ivTeacherImage);
        }
    }

    private void setupNavigationListener() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_take_attendance) {
                // --- CHANGE THIS LINE ---
                // Old (Wrong): startActivity(new Intent(TeacherDashboardActivity.this, TakeAttendanceActivity.class));

                // New (Correct): Go to SetupAttendanceActivity first
                startActivity(new Intent(TeacherDashboardActivity.this, SetupAttendanceActivity.class));

            } else if (itemId == R.id.nav_view_performance) {
                // Placeholder for View Performance feature
                Toast.makeText(this, "View Performance Clicked", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_get_report) {
                // Placeholder for Get Report feature
                Toast.makeText(this, "Get Report Clicked", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_logout) {
                // Handle Logout
                logoutUser();
            }

            // Close the drawer after an item is tapped.
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }



//worked code
//    private void setupNavigationListener() {
//        navigationView.setNavigationItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_take_attendance) {
//                // Launch TakeAttendanceActivity
//                startActivity(new Intent(TeacherDashboardActivity.this, TakeAttendanceActivity.class));
//            } else if (itemId == R.id.nav_view_performance) {
//                // Placeholder for View Performance feature
//                Toast.makeText(this, "View Performance Clicked", Toast.LENGTH_SHORT).show();
//            } else if (itemId == R.id.nav_get_report) {
//                // Placeholder for Get Report feature
//                Toast.makeText(this, "Get Report Clicked", Toast.LENGTH_SHORT).show();
//            } else if (itemId == R.id.nav_logout) {
//                // Handle Logout
//                logoutUser();
//            }
//
//            // Close the drawer after an item is tapped.
//            drawerLayout.closeDrawer(GravityCompat.START);
//            return true;
//        });
//    }

    private void logoutUser() {
        Prefs.getInstance(getApplicationContext()).clearUserData();
        Intent intent = new Intent(TeacherDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }
}


//package com.example.studentmanagementsystem.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.studentmanagementsystem.R;
//import com.google.android.material.card.MaterialCardView;
//
//public class TeacherDashboardActivity extends AppCompatActivity {
//
//    private MaterialCardView cardAttendance;
//    private MaterialCardView cardPerformance;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // This line is the critical link between your Java and XML.
//        // It tells the activity to load the layout you created.
//        setContentView(R.layout.activity_teacher_dashboard);
//
//        // Set the title in the action bar to match the layout's title.
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("Teacher Dashboard");
//        }
//
//        // Find the views using the IDs from your XML file.
//        cardAttendance = findViewById(R.id.cardAttendance);
//        cardPerformance = findViewById(R.id.cardPerformance);
//
//        // Set the click listener for the "Take Attendance" card.
//        cardAttendance.setOnClickListener(v -> {
//            // This will open the screen to take attendance.
//            Intent intent = new Intent(TeacherDashboardActivity.this, TakeAttendanceActivity.class);
//            startActivity(intent);
//        });
//
//        // Set the click listener for the "View Performance" card.
//        cardPerformance.setOnClickListener(v -> {
//            // A placeholder message for now.
//            Toast.makeText(TeacherDashboardActivity.this, "View Performance coming soon!", Toast.LENGTH_SHORT).show();
//        });
//    }
//}
