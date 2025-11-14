package com.example.studentmanagementsystem.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.StudentPageAdapter;
import com.example.studentmanagementsystem.adapter.StudentPageAdapter;
import com.example.studentmanagementsystem.adapter.TakeAttendanceAdapter;
import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.ClassApiResponse;
import com.example.studentmanagementsystem.model.Student;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakeAttendanceActivity extends AppCompatActivity {

    // Views from the new layout
    private ViewPager2 viewPager;
    private ProgressBar pbLoading;
    private TextView tvStatusMessage, tvStudentCounter;
    private MaterialButton btnNext, btnPrevious;
    private RelativeLayout navigationControls;
    private FloatingActionButton fabSubmit;

    private StudentPageAdapter pageAdapter;
    private static final int STUDENTS_PER_PAGE = 10;

//    private StudentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        // Initialize Views
        setupToolbar();
        viewPager = findViewById(R.id.view_pager_attendance);
        pbLoading = findViewById(R.id.pb_loading);
        tvStatusMessage = findViewById(R.id.tv_status_message);
        tvStudentCounter = findViewById(R.id.tv_student_counter);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        navigationControls = findViewById(R.id.navigation_controls);
        fabSubmit = findViewById(R.id.fab_submit_attendance);

        // Fetch class data from the API
        fetchClassData();

        // Set up listeners for navigation buttons
        setupNavigationListeners();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_take_attendance);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handle the back arrow click
        return true;
    }

    private void setupNavigationListeners() {
        btnNext.setOnClickListener(v -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        });

        btnPrevious.setOnClickListener(v -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        });

        fabSubmit.setOnClickListener(v -> {
            if (pageAdapter != null) {
                // Here you can get the attendance data and submit it
                Toast.makeText(this, "Submitting attendance...", Toast.LENGTH_SHORT).show();
                // Map<String, String> attendanceData = pagerAdapter.getAttendanceStatusMap();
                // Log.d("AttendanceData", attendanceData.toString());
                // TODO: Add API call to submit data
            }
        });

        // Add a callback to update the counter and button visibility as the user swipes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateUiForPage(position);
            }
        });
    }

    private void fetchClassData() {
        showLoading(true);

        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);
        Call<List<ClassApiResponse>> call = apiService.getClassForAttendance();

        call.enqueue(new Callback<List<ClassApiResponse>>() {
            @Override
            public void onResponse(Call<List<ClassApiResponse>> call, Response<List<ClassApiResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<ClassApiResponse> classList = response.body();
                    if (!classList.isEmpty()) {
                        // Get the first class from the list
                        ClassApiResponse firstClass = classList.get(0);
                        validateAndDisplayData(firstClass);
                    } else {
                        showError("No classes found for this user.");
                    }
                } else {
                    showError("Failed to load class data. Server returned an error.");
                }
            }

            @Override
            public void onFailure(Call<List<ClassApiResponse>> call, Throwable t) {
                showLoading(false);
                showError("Network Error: " + t.getMessage());
            }
        });
    }

    private void validateAndDisplayData(ClassApiResponse classData) {
        List<Student> students = classData.getStudents();
        if (students == null || students.isEmpty()) {
            showError("This class has no students to take attendance for.");
            return;
        }

        // All checks passed, set up the ViewPager
        viewPager.setVisibility(View.VISIBLE);
        navigationControls.setVisibility(View.VISIBLE);
        fabSubmit.setVisibility(View.VISIBLE);

        pageAdapter = new StudentPageAdapter(students, STUDENTS_PER_PAGE);
        viewPager.setAdapter(pageAdapter);

// Update total page count
        updateUiForPage(0);

    }

    private void updateUiForPage(int position) {
        if (pageAdapter == null) return;
        int totalPages = pageAdapter.getItemCount();

        tvStudentCounter.setText("Page " + (position + 1) + " / " + totalPages);

        btnPrevious.setEnabled(position > 0);
        btnNext.setEnabled(position < totalPages - 1);
    }



    private void showLoading(boolean isLoading) {
        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            // Hide everything else while loading
            viewPager.setVisibility(View.GONE);
            tvStatusMessage.setVisibility(View.GONE);
            navigationControls.setVisibility(View.GONE);
            fabSubmit.setVisibility(View.GONE);
        }
    }
    private void showError(String message) {
        // Hide everything and show the error message
        viewPager.setVisibility(View.GONE);
        navigationControls.setVisibility(View.GONE);
        fabSubmit.setOnClickListener(v -> {
            if (pageAdapter != null) {
                Map<String, String> finalAttendance = new HashMap<>();

                for (TakeAttendanceAdapter adapter : pageAdapter.getPageAdapters()) {
                    finalAttendance.putAll(adapter.getAttendanceStatusMap());
                }

                Toast.makeText(this, "Submitting " + finalAttendance.size() + " records", Toast.LENGTH_SHORT).show();

                // TODO: send 'finalAttendance' to API
            }
        });

        pbLoading.setVisibility(View.GONE);

        tvStatusMessage.setVisibility(View.VISIBLE);
        tvStatusMessage.setText(message);
    }


}


//package com.example.studentmanagementsystem.ui;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//// Import SwipeRefreshLayout
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.adapter.TakeAttendanceAdapter;
//import com.example.studentmanagementsystem.api.ApiClient;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.model.ClassApiResponse;
//import com.example.studentmanagementsystem.model.User;
//import com.example.studentmanagementsystem.util.Prefs;
//import com.google.android.material.appbar.MaterialToolbar;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class TakeAttendanceActivity extends AppCompatActivity {
//
//    private RecyclerView rvStudents;
//    private ProgressBar pbLoading;
//    private TextView tvStatusMessage;
//    // Declare the SwipeRefreshLayout variable
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private TakeAttendanceAdapter adapter;
//    private User loggedInTeacher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_take_attendance);
//
//        // Get logged-in teacher's data from Prefs
//        loggedInTeacher = Prefs.getInstance(this).getUser();
//
//        // Initialize Views
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_take_attendance);
//        toolbar.setTitle("Take Attendance");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        rvStudents = findViewById(R.id.rv_students_for_attendance);
//        pbLoading = findViewById(R.id.pb_loading);
//        tvStatusMessage = findViewById(R.id.tv_status_message);
//        // Find the SwipeRefreshLayout by its ID
//        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
//
//        rvStudents.setLayoutManager(new LinearLayoutManager(this));
//
//        // Set up the refresh listener
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            // This code is executed when the user swipes down
//            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
//            fetchClassData();
//        });
//
//        // Configure the refresh indicator color (optional)
//        swipeRefreshLayout.setColorSchemeResources(R.color.purple_500, R.color.purple_700, R.color.teal_200);
//
//        // Fetch the data from the API for the first time
//        fetchClassData();
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed(); // Go back when the top-left back arrow is pressed
//        return true;
//    }
//
//    private void fetchClassData() {
//        // Show the correct loading indicator
//        // If it's a swipe-refresh, the indicator is already showing.
//        // If it's the initial load, show the progress bar.
//        if (!swipeRefreshLayout.isRefreshing()) {
//            showLoading(true);
//        }
//
//        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);
//
//        // The call object is now of type List<ClassApiResponse>
//        Call<List<ClassApiResponse>> call = apiService.getClassForAttendance();
//
//        // The generic type of the Callback must also be updated to match
//        call.enqueue(new Callback<List<ClassApiResponse>>() {
//            @Override
//            public void onResponse(Call<List<ClassApiResponse>> call, Response<List<ClassApiResponse>> response) {
//                showLoading(false);
//                swipeRefreshLayout.setRefreshing(false);
//
//                // The response body is now a LIST
//                if (response.isSuccessful() && response.body() != null) {
//                    List<ClassApiResponse> classList = response.body();
//
//                    // Check if the list is not empty
//                    if (!classList.isEmpty()) {
//                        // Get the FIRST class from the list.
//                        // In a more advanced app, you might show a dialog for the teacher to choose a class.
//                        ClassApiResponse firstClass = classList.get(0);
//                        validateAndDisplayData(firstClass);
//                    } else {
//                        // The API returned an empty list
//                        showError("No classes found for this user.");
//                    }
//
//                } else {
//                    showError("Failed to load class data. Please try again.");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<ClassApiResponse>> call, Throwable t) {
//                showLoading(false);
//                swipeRefreshLayout.setRefreshing(false);
//                // Check for the specific error here as well
//                if (t.getMessage().contains("BEGIN_OBJECT but was BEGIN_ARRAY")) {
//                    showError("API Mismatch Error. Expected an Object but got an Array.");
//                } else {
//                    showError("Network Error: " + t.getMessage());
//                }
//            }
//        });
//    }
//
//    private void validateAndDisplayData(ClassApiResponse classData) {
//        // Validation Check 1: Is the logged-in user the teacher of this class?
//        if (classData.getTeacher() == null) {
//            showError("This class has no assigned teacher.");
//            return;
//        }
//
//        // Validation Check 2: Are there any students in the class?
//        if (classData.getStudents() == null || classData.getStudents().isEmpty()) {
//            showError("There are no students in this class to take attendance for.");
//            return;
//        }
//
//        // Make the RecyclerView visible if it was hidden by an error
//        rvStudents.setVisibility(View.VISIBLE);
//        tvStatusMessage.setVisibility(View.GONE);
//
//        // All checks passed, display the students
//        adapter = new TakeAttendanceAdapter(classData.getStudents());
//        rvStudents.setAdapter(adapter);
//    }
//
//    private void showLoading(boolean isLoading) {
//        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//        if (isLoading) {
//            tvStatusMessage.setVisibility(View.GONE);
//            rvStudents.setVisibility(View.GONE);
//        }
//    }
//
//    private void showError(String message) {
//        rvStudents.setVisibility(View.GONE);
//        tvStatusMessage.setVisibility(View.VISIBLE);
//        tvStatusMessage.setText(message);
//    }
//}


//package com.example.studentmanagementsystem.ui;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.adapter.TakeAttendanceAdapter;
//import com.example.studentmanagementsystem.api.ApiClient;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.api.ApiServices;
//import com.example.studentmanagementsystem.model.ClassApiResponse;
//import com.example.studentmanagementsystem.model.User;
//import com.example.studentmanagementsystem.util.Prefs;
//import com.google.android.material.appbar.MaterialToolbar;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class TakeAttendanceActivity extends AppCompatActivity {
//
//    private RecyclerView rvStudents;
//    private ProgressBar pbLoading;
//    private TextView tvStatusMessage;
//    private TakeAttendanceAdapter adapter;
//    private User loggedInTeacher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_take_attendance);
//
//        // Get logged-in teacher's data from Prefs
//        loggedInTeacher = Prefs.getInstance(this).getUser();
//
//        // Initialize Views
//        MaterialToolbar toolbar = findViewById(R.id.toolbar_take_attendance);
//        toolbar.setTitle("Take Attendance");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        rvStudents = findViewById(R.id.rv_students_for_attendance);
//        pbLoading = findViewById(R.id.pb_loading);
//        tvStatusMessage = findViewById(R.id.tv_status_message);
//
//        rvStudents.setLayoutManager(new LinearLayoutManager(this));
//
//        // Fetch the data from the API
//        fetchClassData();
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed(); // Go back when the top-left back arrow is pressed
//        return true;
//    }
//
//    private void fetchClassData() {
//        showLoading(true);
//
//        ApiServices apiService = ApiClient.getClient(this).create(ApiServices.class);
//        Call<ClassApiResponse> call = apiService.getClassForAttendance();
//
//        call.enqueue(new Callback<ClassApiResponse>() {
//            @Override
//            public void onResponse(Call<ClassApiResponse> call, Response<ClassApiResponse> response) {
//                showLoading(false);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    ClassApiResponse classData = response.body();
//                    validateAndDisplayData(classData);
//                } else {
//                    showError("Failed to load class data. Please try again.");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ClassApiResponse> call, Throwable t) {
//                showLoading(false);
//                showError("Network Error: " + t.getMessage());
//            }
//        });
//    }
//
//    private void validateAndDisplayData(ClassApiResponse classData) {
//        // Validation Check 1: Is the logged-in user the teacher of this class?
//        // NOTE: For a real app, loggedInTeacher.getTeacherId() should be used.
//        // We will use a placeholder check for now.
//        if (classData.getTeacher() == null) {
//            showError("This class has no assigned teacher.");
//            return;
//        }
//
//        // Validation Check 2: Are there any students in the class?
//        if (classData.getStudents() == null || classData.getStudents().isEmpty()) {
//            showError("There are no students in this class to take attendance for.");
//            return;
//        }
//
//        // All checks passed, display the students
//        adapter = new TakeAttendanceAdapter(classData.getStudents());
//        rvStudents.setAdapter(adapter);
//    }
//
//    private void showLoading(boolean isLoading) {
//        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//        if (isLoading) {
//            tvStatusMessage.setVisibility(View.GONE);
//            rvStudents.setVisibility(View.GONE);
//        }
//    }
//
//    private void showError(String message) {
//        rvStudents.setVisibility(View.GONE);
//        tvStatusMessage.setVisibility(View.VISIBLE);
//        tvStatusMessage.setText(message);
//    }
//}
