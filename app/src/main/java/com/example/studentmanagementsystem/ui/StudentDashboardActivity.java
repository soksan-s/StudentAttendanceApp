package com.example.studentmanagementsystem.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler; // Import Handler
import android.os.Looper;  // Import Looper
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.EventAdapter;
import com.example.studentmanagementsystem.model.EventItem;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.util.Prefs;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    // Content
    private TextView tvStudentName;
    private RecyclerView rvEvents;
    private EventAdapter eventAdapter;
    private View cardMyAttendance, cardMyProfile;
    private TextView tvSeeAllEvents;

    // --- Auto Slide Variables ---
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private int currentPosition = 0;
    // ----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Load Theme Preference BEFORE setContentView
        SharedPreferences prefs = getSharedPreferences("AppSetting", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_student_dashboard);

        initializeViews();
        setupDrawer();
        loadUserData();
        setupEventsSlider();
        setupClickListeners();
        setupBackPressHandler();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout_student);
        navigationView = findViewById(R.id.navigation_view_student);

        tvStudentName = findViewById(R.id.tv_student_name);
        rvEvents = findViewById(R.id.rv_student_events);
        cardMyAttendance = findViewById(R.id.card_my_attendance);
        cardMyProfile = findViewById(R.id.card_my_profile);
        tvSeeAllEvents = findViewById(R.id.tv_see_all_events);
    }

    private void setupDrawer() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_student);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

        navigationView.setNavigationItemSelectedListener(this);

        // --- DARK MODE LOGIC START ---
        // Find the menu item
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_dark_mode);
        // Find the switch inside the layout
        SwitchCompat themeSwitch = (SwitchCompat) menuItem.getActionView().findViewById(R.id.switch_dark_mode);

        // Set current state based on current mode
        SharedPreferences prefs = getSharedPreferences("AppSetting", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        themeSwitch.setChecked(isDarkMode);

        // Handle Click
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("AppSetting", MODE_PRIVATE).edit();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("dark_mode", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("dark_mode", false);
            }
            editor.apply();
        });
        // --- DARK MODE LOGIC END ---
    }

    private void loadUserData() {
        User user = Prefs.getInstance(this).getUser();

        View headerView = navigationView.getHeaderView(0);
        TextView tvNavName = headerView.findViewById(R.id.tv_student_name_header);
        TextView tvNavEmail = headerView.findViewById(R.id.tv_student_email_header);
        CircleImageView ivNavImage = headerView.findViewById(R.id.iv_student_image_header);

        if (user != null) {
            tvStudentName.setText(user.getName());
            tvNavName.setText(user.getName());
            tvNavEmail.setText(user.getEmail());
            Glide.with(this).load(user.getImage()).placeholder(R.drawable.ic_person_outline).into(ivNavImage);
        } else {
            SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            String name = prefs.getString("username", "Student");
            tvStudentName.setText(name);
            tvNavName.setText(name);
        }
    }

    private void setupEventsSlider() {
        List<EventItem> events = new ArrayList<>();
        events.add(new EventItem("School Sports Day", "Dec 12", "Join us at the main stadium.", R.drawable.sport_pic));
        events.add(new EventItem("Final Exams", "Jan 15", "Prepare for the semester finals.", R.drawable.exam_pic));
        events.add(new EventItem("Science Fair", "Feb 20", "Showcase your projects.", R.drawable.scient_pic));

        eventAdapter = new EventAdapter(events);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvEvents.setLayoutManager(layoutManager);
        rvEvents.setAdapter(eventAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvEvents);

        // --- Auto Slide Logic ---
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (events.size() == 0) return;

                // Calculate next position
                currentPosition++;
                if (currentPosition >= events.size()) {
                    currentPosition = 0; // Loop back to start
                }

                // Smooth scroll to the position
                rvEvents.smoothScrollToPosition(currentPosition);

                // Schedule next slide after 3 seconds (3000ms)
                sliderHandler.postDelayed(this, 3000);
            }
        };

        // Add a scroll listener to update currentPosition if user swipes manually
        rvEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int foundPos = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if(foundPos != RecyclerView.NO_POSITION) {
                        currentPosition = foundPos;
                    }
                }
            }
        });
    }

    // --- Lifecycle Methods to Start/Stop Auto Slide ---

    @Override
    protected void onResume() {
        super.onResume();
        // Start sliding when app is open
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop sliding when app is minimized to save battery/resources
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    // ---------------------------------------------------

    private void setupClickListeners() {
        cardMyAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentAttendanceActivity.class);
            startActivity(intent);
        });

        if (tvSeeAllEvents != null) {
            tvSeeAllEvents.setOnClickListener(v -> {
                startActivity(new Intent(StudentDashboardActivity.this, StudentEventsActivity.class));
            });
        }

        // Note: Clicking the RecyclerView itself usually doesn't work well for items.
        // Typically, the OnClickListener is inside the Adapter's ViewHolder.

        cardMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_student_home) {
            // Already here
        } else if (id == R.id.nav_student_attendance) {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentAttendanceActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_student_events) {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentEventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_student_profile) {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_student_schedule) {
        startActivity(new Intent(StudentDashboardActivity.this, StudentScheduleActivity.class));
        }
        else if (id == R.id.nav_student_logout) {
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        Prefs.getInstance(getApplicationContext()).clearUserData();
        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
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




//worked code
//package com.example.studentmanagementsystem.ui;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.OnBackPressedCallback;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.PagerSnapHelper;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.SnapHelper;
//
//import com.bumptech.glide.Glide;
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.adapter.EventAdapter;
//import com.example.studentmanagementsystem.model.EventItem;
//import com.example.studentmanagementsystem.model.User;
//import com.example.studentmanagementsystem.util.Prefs;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.navigation.NavigationView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class StudentDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//
//    // Drawer
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;
//    private ActionBarDrawerToggle toggle;
//
//    // Content
//    private TextView tvStudentName;
//    private RecyclerView rvEvents;
//    private EventAdapter eventAdapter;
//    private View cardMyAttendance, cardMyProfile;
//    private TextView tvSeeAllEvents;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_student_dashboard);
//
//        initializeViews();
//        setupDrawer();
//        loadUserData();
//        setupEventsSlider();
//        setupClickListeners();
//        setupBackPressHandler();
//    }
//
//    private void initializeViews() {
//        drawerLayout = findViewById(R.id.drawer_layout_student);
//        navigationView = findViewById(R.id.navigation_view_student);
//
//        tvStudentName = findViewById(R.id.tv_student_name);
//        rvEvents = findViewById(R.id.rv_student_events);
//        cardMyAttendance = findViewById(R.id.card_my_attendance);
//        cardMyProfile = findViewById(R.id.card_my_profile);
//        tvSeeAllEvents = findViewById(R.id.tv_see_all_events);
//
//    }
//
//    private void setupDrawer() {
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_student);
//        setSupportActionBar(toolbar);
//
//        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
//                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black)); // Make hamburger black
//
//        navigationView.setNavigationItemSelectedListener(this);
//    }
//
//    private void loadUserData() {
//        User user = Prefs.getInstance(this).getUser();
//
//        // Header Views
//        View headerView = navigationView.getHeaderView(0);
//        TextView tvNavName = headerView.findViewById(R.id.tv_student_name_header);
//        TextView tvNavEmail = headerView.findViewById(R.id.tv_student_email_header);
//        CircleImageView ivNavImage = headerView.findViewById(R.id.iv_student_image_header);
//
//        if (user != null) {
//            tvStudentName.setText(user.getName());
//            tvNavName.setText(user.getName());
//            tvNavEmail.setText(user.getEmail());
//            Glide.with(this).load(user.getImage()).placeholder(R.drawable.ic_person_outline).into(ivNavImage);
//        } else {
//            // Fallback
//            SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
//            String name = prefs.getString("username", "Student");
//            tvStudentName.setText(name);
//            tvNavName.setText(name);
//        }
//    }
//
//    private void setupEventsSlider() {
//        List<EventItem> events = new ArrayList<>();
//        events.add(new EventItem("School Sports Day", "Dec 12", "Join us at the main stadium.", android.R.drawable.ic_menu_camera));
//        events.add(new EventItem("Final Exams", "Jan 15", "Prepare for the semester finals.", android.R.drawable.ic_menu_edit));
//        events.add(new EventItem("Science Fair", "Feb 20", "Showcase your projects.", android.R.drawable.ic_menu_view));
//
//        eventAdapter = new EventAdapter(events);
//        rvEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        rvEvents.setAdapter(eventAdapter);
//
//        SnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(rvEvents);
//    }
//
//    private void setupClickListeners() {
//        cardMyAttendance.setOnClickListener(v -> {
////            Toast.makeText(this, "Opening Attendance...", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(StudentDashboardActivity.this, StudentAttendanceActivity.class);
//                startActivity(intent);
//
//        });
//        // In setupClickListeners()
//        if (tvSeeAllEvents != null) {
//            tvSeeAllEvents.setOnClickListener(v -> {
//                startActivity(new Intent(StudentDashboardActivity.this, StudentEventsActivity.class));
//            });
//        }
//
//        rvEvents.setOnClickListener(v -> {
//            Intent intent = new Intent(StudentDashboardActivity.this, StudentEventsActivity.class);
//            startActivity(intent);
//        });
//
//        cardMyProfile.setOnClickListener(v -> {
//            Intent intent = new Intent(StudentDashboardActivity.this, StudentProfileActivity.class);
//            startActivity(intent);
////            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_student_home) {
//            // Already here
//        } else if (id == R.id.nav_student_attendance) {
//            Intent intent = new Intent(StudentDashboardActivity.this, StudentAttendanceActivity.class);
//            startActivity(intent);
////            Toast.makeText(this, "My Attendance Clicked", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_student_events) {
//            Intent intent = new Intent(StudentDashboardActivity.this, StudentEventsActivity.class);
//            startActivity(intent);
////            Toast.makeText(this, "Events Clicked", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_student_profile) {
//            Intent intent = new Intent(StudentDashboardActivity.this, StudentProfileActivity.class);
//            startActivity(intent);
////            Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_student_logout) {
//            logoutUser();
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    private void logoutUser() {
//        Prefs.getInstance(getApplicationContext()).clearUserData();
//        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
//
//    private void setupBackPressHandler() {
//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    setEnabled(false);
//                    getOnBackPressedDispatcher().onBackPressed();
//                    setEnabled(true);
//                }
//            }
//        });
//    }
//}





//worked code
//package com.example.studentmanagementsystem.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.OnBackPressedCallback;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.navigation.NavController;
//import androidx.navigation.fragment.NavHostFragment;
//import androidx.navigation.ui.NavigationUI;
//
//import com.bumptech.glide.Glide;
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.model.User;
//import com.example.studentmanagementsystem.util.Prefs;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.navigation.NavigationView;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class StudentDashboardActivity extends AppCompatActivity {
//
//    private DrawerLayout drawerLayout;
//    private NavController navController;
//    private ActionBarDrawerToggle toggle;
//    private NavigationView navigationView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_student_dashboard);
//
//        // 1. Initialize Views
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_student);
//        setSupportActionBar(toolbar);
//
//        drawerLayout = findViewById(R.id.drawer_layout_student);
//        navigationView = findViewById(R.id.navigation_view_student);
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
//        // 4. Setup Hamburger Icon
//        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        // 5. Update Navigation Header with User Info
//        updateNavHeader();
//
//        // 6. Set up Logout Listener
//        setupLogoutListener();
//
//        // 7. Set up the modern back press handler
//        setupBackPressHandler();
//
//    }
//    // --- START: NEW METHOD FOR BACK PRESS HANDLING ---
//    private void setupBackPressHandler() {
//        // This is the modern way to handle the back button.
//        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                // The same logic you had in onBackPressed() goes here.
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    // If you want the default back press behavior (exit the app or go to the previous screen),
//                    // you must disable this callback and call the method again.
//                    setEnabled(false); // Disable this callback
//                    getOnBackPressedDispatcher().onBackPressed(); // Trigger the default behavior
//                    setEnabled(true); // Re-enable it for the next time
//                }
//            }
//        };
//        // Add the callback to the dispatcher
//        getOnBackPressedDispatcher().addCallback(this, callback);
//    }
//    // --- END: NEW METHOD FOR BACK PRESS HANDLING ---
//    private void updateNavHeader() {
//        // Get the header view from the NavigationView
//        View headerView = navigationView.getHeaderView(0);
//        TextView tvStudentName = headerView.findViewById(R.id.tv_student_name_header);
//        TextView tvStudentEmail = headerView.findViewById(R.id.tv_student_email_header);
//        CircleImageView ivStudentImage = headerView.findViewById(R.id.iv_student_image);
//
//        // Retrieve the logged-in user from SharedPreferences
//        User loggedInUser = Prefs.getInstance(getApplicationContext()).getUser();
//
//        if (loggedInUser != null) {
//            tvStudentName.setText(loggedInUser.getName());
//            tvStudentEmail.setText(loggedInUser.getEmail());
//
//            // Use Glide to load the user's image, if available
//            Glide.with(this)
//                    .load(loggedInUser.getImage())
//                    .placeholder(R.drawable.ic_person_outline)
//                    .error(R.drawable.ic_person_outline)
//                    .into(ivStudentImage);
//        }
//    }
//
//    private void setupLogoutListener() {
//        navigationView.setNavigationItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_logout) {
//                // Handle logout
//                logoutUser();
//                return true; // Mark as handled
//            }
//
//            // Let NavigationUI handle other menu items (like Home, Profile)
//            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
//            if (handled) {
//                // Close the drawer if a navigation item was clicked
//                drawerLayout.closeDrawer(GravityCompat.START);
//            }
//            return handled;
//        });
//    }
//
//    private void logoutUser() {
//        // Clear user data from SharedPreferences
//        Prefs.getInstance(getApplicationContext()).clearUserData();
//
//        // Navigate back to LoginActivity
//        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish(); // Close this activity
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp();
//    }
//
//}
