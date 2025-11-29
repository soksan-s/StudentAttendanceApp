package com.example.studentmanagementsystem.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.User;
import com.example.studentmanagementsystem.util.Prefs;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherDashboardActivity extends AppCompatActivity {

    // Drawer Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    // Dashboard Components
    private TextView tvTeacherNameDashboard, tvStatPresent, tvStatAbsent;
    private BarChart dashboardChart;
    private ExtendedFloatingActionButton fabStartAttendance;
    private View btnQuickReport, btnQuickPerformance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        // 1. Initialize Views
        initializeViews();

        // 2. Setup Toolbar & Drawer
        setupDrawer();

        // 3. Load Data (User Info & Stats)
        loadUserData();
        loadDashboardStats();

        // 4. Setup Listeners (Dashboard Clicks & Nav Clicks)
        setupDashboardListeners();
        setupNavigationListener();

        // 5. Handle Back Press
        setupBackPressHandler();
    }

    private void initializeViews() {
        // Drawer Layout
        drawerLayout = findViewById(R.id.drawer_layout_teacher);
        navigationView = findViewById(R.id.navigation_view_teacher);

        // Dashboard Widgets
        tvTeacherNameDashboard = findViewById(R.id.tv_teacher_name_dashboard);
        tvStatPresent = findViewById(R.id.tv_stat_present);
        tvStatAbsent = findViewById(R.id.tv_stat_absent);
        dashboardChart = findViewById(R.id.dashboard_bar_chart);

        fabStartAttendance = findViewById(R.id.fab_start_attendance);
        btnQuickReport = findViewById(R.id.btn_quick_report);
        btnQuickPerformance = findViewById(R.id.btn_quick_performance);
    }

    private void setupDrawer() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_teacher);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadUserData() {
        User loggedInUser = Prefs.getInstance(getApplicationContext()).getUser();

        // Update Side Bar Header
        View headerView = navigationView.getHeaderView(0);
        TextView tvNavName = headerView.findViewById(R.id.tv_teacher_name_header);
        TextView tvNavEmail = headerView.findViewById(R.id.tv_teacher_email_header);
        CircleImageView ivNavImage = headerView.findViewById(R.id.iv_teacher_image);

        if (loggedInUser != null) {
            // Side Bar
            tvNavName.setText(loggedInUser.getName());
            tvNavEmail.setText(loggedInUser.getEmail());
            Glide.with(this).load(loggedInUser.getImage()).placeholder(R.drawable.ic_person_outline).into(ivNavImage);

            // Main Dashboard Welcome
            tvTeacherNameDashboard.setText(loggedInUser.getName());
        }
    }

    private void loadDashboardStats() {
        // --- Mock Data ---
        tvStatPresent.setText("24");
        tvStatAbsent.setText("3");

        // Setup Bar Chart
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 85f)); // Mon
        entries.add(new BarEntry(1, 92f)); // Tue
        entries.add(new BarEntry(2, 78f)); // Wed
        entries.add(new BarEntry(3, 95f)); // Thu
        entries.add(new BarEntry(4, 88f)); // Fri

        BarDataSet dataSet = new BarDataSet(entries, "Attendance %");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        dashboardChart.setData(barData);

        dashboardChart.getDescription().setEnabled(false);
        dashboardChart.getLegend().setEnabled(false);
        dashboardChart.setDrawGridBackground(false);
        dashboardChart.animateY(1000);

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        XAxis xAxis = dashboardChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        dashboardChart.invalidate();
    }

    private void setupDashboardListeners() {
        // FAB: Take Attendance
        fabStartAttendance.setOnClickListener(v ->
                startActivity(new Intent(TeacherDashboardActivity.this, SetupAttendanceActivity.class)));

        // Quick Action: Report
        btnQuickReport.setOnClickListener(v ->
                startActivity(new Intent(TeacherDashboardActivity.this, GetReportActivity.class)));

        // Quick Action: Performance (or Chart Click)
        btnQuickPerformance.setOnClickListener(v ->
                startActivity(new Intent(TeacherDashboardActivity.this, ViewPerformanceActivity.class)));
    }

    private void setupNavigationListener() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_take_attendance) {
                startActivity(new Intent(TeacherDashboardActivity.this, SetupAttendanceActivity.class));
            } else if (itemId == R.id.nav_view_performance) {
                startActivity(new Intent(TeacherDashboardActivity.this, ViewPerformanceActivity.class));
            } else if (itemId == R.id.nav_get_report) {
                startActivity(new Intent(TeacherDashboardActivity.this, GetReportActivity.class));
            } else if (itemId == R.id.nav_logout) {
                logoutUser();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

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







//worked code
//package com.example.studentmanagementsystem.ui;
//
//import android.content.Intent;
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
//public class TeacherDashboardActivity extends AppCompatActivity {
//
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;
//    private ActionBarDrawerToggle toggle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_teacher_dashboard);
//
//        // 1. Initialize Views
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_teacher);
//        setSupportActionBar(toolbar);
//
//        drawerLayout = findViewById(R.id.drawer_layout_teacher);
//        navigationView = findViewById(R.id.navigation_view_teacher);
//
//        // 2. Setup Hamburger Icon for the drawer
//        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
//                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        // 3. Update Header with Teacher's Info
//        updateNavHeader();
//
//        // 4. Handle Navigation Item Clicks
//        setupNavigationListener();
//
//        // 5. Handle Back Press
//        setupBackPressHandler();
//    }
//
//    private void updateNavHeader() {
//        View headerView = navigationView.getHeaderView(0);
//        TextView tvTeacherName = headerView.findViewById(R.id.tv_teacher_name_header);
//        TextView tvTeacherEmail = headerView.findViewById(R.id.tv_teacher_email_header);
//        CircleImageView ivTeacherImage = headerView.findViewById(R.id.iv_teacher_image);
//
//        User loggedInUser = Prefs.getInstance(getApplicationContext()).getUser();
//
//        if (loggedInUser != null) {
//            tvTeacherName.setText(loggedInUser.getName());
//            tvTeacherEmail.setText(loggedInUser.getEmail());
//
//            Glide.with(this)
//                    .load(loggedInUser.getImage())
//                    .placeholder(R.drawable.ic_person_outline)
//                    .into(ivTeacherImage);
//        }
//    }
//
//    private void setupNavigationListener() {
//        navigationView.setNavigationItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_take_attendance) {
//                // --- CHANGE THIS LINE ---
//                // Old (Wrong): startActivity(new Intent(TeacherDashboardActivity.this, TakeAttendanceActivity.class));
//
//                // New (Correct): Go to SetupAttendanceActivity first
//                startActivity(new Intent(TeacherDashboardActivity.this, SetupAttendanceActivity.class));
//
//            } else if (itemId == R.id.nav_view_performance) {
//                // Placeholder for View Performance feature
//                startActivity(new Intent(TeacherDashboardActivity.this, ViewPerformanceActivity.class));
////                Toast.makeText(this, "View Performance Clicked", Toast.LENGTH_SHORT).show();
//            } else if (itemId == R.id.nav_get_report) {
//                // Placeholder for Get Report feature
//                startActivity(new Intent(TeacherDashboardActivity.this, GetReportActivity.class));
////                Toast.makeText(this, "Get Report Clicked", Toast.LENGTH_SHORT).show();
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

//    private void logoutUser() {
//        Prefs.getInstance(getApplicationContext()).clearUserData();
//        Intent intent = new Intent(TeacherDashboardActivity.this, LoginActivity.class);
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
