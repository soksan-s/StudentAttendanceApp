package com.example.studentmanagementsystem.ui; // Your package name

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.studentmanagementsystem.R;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

public class StudentDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    // A constant for your SharedPreferences file
    private static final String PREFS_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // --- Setup Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar); // Make sure you have a Toolbar with this ID
        setSupportActionBar(toolbar);

        // --- Setup Navigation ---
        drawerLayout = findViewById(R.id.studentDrawerLayout);
        navigationView = findViewById(R.id.studentNavView);
        // In your Activity's onCreate()
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        // Now you can use the navController, for example, to set up a toolbar
        NavigationUI.setupActionBarWithNavController(this, navController);

        // Define top-level destinations for the AppBarConfiguration
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.myCoursesFragment, R.id.myGradesFragment, R.id.studentProfileFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // --- Populate Navigation Header and Handle Logout ---
        setupNavigationDrawer();
    }

    private void setupNavigationDrawer() {
        // Get the header view from the NavigationView
        View headerView = navigationView.getHeaderView(0);
        ImageView imgStudentProfile = headerView.findViewById(R.id.imgStudentProfile);
        TextView tvStudentName = headerView.findViewById(R.id.tvStudentName);
        TextView tvStudentEmail = headerView.findViewById(R.id.tvStudentEmail);

        // --- Fetch and Display User Data ---
        // Assuming you store user data in SharedPreferences after login
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String studentName = prefs.getString("STUDENT_NAME", "Student Name");
        String studentEmail = prefs.getString("STUDENT_EMAIL", "student.email@example.com");
        String profileImageUrl = prefs.getString("PROFILE_IMAGE_URL", null);

        tvStudentName.setText(studentName);
        tvStudentEmail.setText(studentEmail);

        // Use Glide to load the profile image
        Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_person_outline) // Placeholder image
                .error(R.drawable.ic_person_outline)       // Error image if URL is invalid
                .into(imgStudentProfile);

        // --- Handle Menu Item Clicks (including Logout) ---
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.btnLogout) {
                // Handle logout
                performLogout();
                return true; // Mark as handled
            }

            // Let NavigationUI handle the other menu items
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                // Close the drawer if a navigation event occurred
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return handled;
        });
    }

    private void performLogout() {
        // 1. Clear user session data (e.g., from SharedPreferences)
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        // 2. Navigate to the Login/Splash screen
        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class); // Replace with your Login activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close the dashboard activity
    }

    @Override
    public boolean onSupportNavigateUp() {
        // This ensures the hamburger icon and back arrow work correctly
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // Close the drawer if it's open, otherwise perform default back action
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}



//package com.example.studentmanagementsystem.ui;
//
//import android.os.Bundle;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.navigation.NavController;
//import androidx.navigation.fragment.NavHostFragment;
//import androidx.navigation.ui.NavigationUI;
//import com.example.studentmanagementsystem.R;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.navigation.NavigationView;
//
//public class StudentDashboardActivity extends AppCompatActivity {
//
//    private DrawerLayout drawerLayout;
//    private NavController navController;
//    private ActionBarDrawerToggle toggle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // The EdgeToEdge code can be removed when using a standard Toolbar/Drawer setup
//        setContentView(R.layout.activity_student_dashboard);
//
//        // 1. Initialize Views using the unique IDs
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_student);
//        setSupportActionBar(toolbar);
//
//        drawerLayout = findViewById(R.id.drawer_layout_student);
//        NavigationView navigationView = findViewById(R.id.navigation_view_student);
//
//        // 2. Set up NavController
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.nav_host_fragment_student);
//        navController = navHostFragment.getNavController();
//
//        // 3. Connect UI components with NavigationUI
//        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
//        NavigationUI.setupWithNavController(navigationView, navController);
//
//        // This is the hamburger icon that opens/closes the drawer
//        toggle = new ActionBarDrawerToggle(
//                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//    }
//
//    // Handle the Up button (back arrow in toolbar)
//    @Override
//    public boolean onSupportNavigateUp() {
//        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp();
//    }
//
//    // Handle the system Back button press
//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//}
