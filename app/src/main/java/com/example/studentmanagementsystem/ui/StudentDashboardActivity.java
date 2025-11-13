package com.example.studentmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.util.Prefs;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavController navController;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // 1. Initialize Views
        MaterialToolbar toolbar = findViewById(R.id.toolbar_student);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_student);
        navigationView = findViewById(R.id.navigation_view_student);

        // 2. Set up NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_student);
        navController = navHostFragment.getNavController();

        // 3. Connect UI components with NavigationUI
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 4. Setup Hamburger Icon
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 5. Update Navigation Header with User Info
        updateNavHeader();

        // 6. Set up Logout Listener
        setupLogoutListener();

        // 7. Set up the modern back press handler
        setupBackPressHandler();

    }
    // --- START: NEW METHOD FOR BACK PRESS HANDLING ---
    private void setupBackPressHandler() {
        // This is the modern way to handle the back button.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // The same logic you had in onBackPressed() goes here.
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // If you want the default back press behavior (exit the app or go to the previous screen),
                    // you must disable this callback and call the method again.
                    setEnabled(false); // Disable this callback
                    getOnBackPressedDispatcher().onBackPressed(); // Trigger the default behavior
                    setEnabled(true); // Re-enable it for the next time
                }
            }
        };
        // Add the callback to the dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    // --- END: NEW METHOD FOR BACK PRESS HANDLING ---
    private void updateNavHeader() {
        // Get the header view from the NavigationView
        View headerView = navigationView.getHeaderView(0);
        TextView tvStudentName = headerView.findViewById(R.id.tv_student_name_header);
        TextView tvStudentEmail = headerView.findViewById(R.id.tv_student_email_header);
        CircleImageView ivStudentImage = headerView.findViewById(R.id.iv_student_image);

        // Retrieve the logged-in user from SharedPreferences
        User loggedInUser = Prefs.getInstance(getApplicationContext()).getUser();

        if (loggedInUser != null) {
            tvStudentName.setText(loggedInUser.getName());
            tvStudentEmail.setText(loggedInUser.getEmail());

            // Use Glide to load the user's image, if available
            Glide.with(this)
                    .load(loggedInUser.getImage())
                    .placeholder(R.drawable.ic_person_outline)
                    .error(R.drawable.ic_person_outline)
                    .into(ivStudentImage);
        }
    }

    private void setupLogoutListener() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_logout) {
                // Handle logout
                logoutUser();
                return true; // Mark as handled
            }

            // Let NavigationUI handle other menu items (like Home, Profile)
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                // Close the drawer if a navigation item was clicked
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return handled;
        });
    }

    private void logoutUser() {
        // Clear user data from SharedPreferences
        Prefs.getInstance(getApplicationContext()).clearUserData();

        // Navigate back to LoginActivity
        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close this activity
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp();
    }

}
