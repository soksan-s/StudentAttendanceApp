package com.example.studentmanagementsystem.ui; // Or your relevant package

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.StudentAttendanceAdapter;
import com.example.studentmanagementsystem.api.ApiClient;
import com.example.studentmanagementsystem.api.ApiServices;
import com.example.studentmanagementsystem.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakeAttendanceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnSubmitAttendance;
    private ProgressBar progressBar;
    private StudentAttendanceAdapter adapter;
    private List<User> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Take Attendance");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Add back button
        }

        recyclerView = findViewById(R.id.rv_students);
        btnSubmitAttendance = findViewById(R.id.btn_submit_attendance);
        progressBar = findViewById(R.id.progress_bar);

        setupRecyclerView();
//        fetchStudents();

        btnSubmitAttendance.setOnClickListener(v -> submitAttendance());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAttendanceAdapter(this, studentList);
        recyclerView.setAdapter(adapter);
    }

//    private void fetchStudents() {
//        progressBar.setVisibility(View.VISIBLE);
//        ApiServices apiService = ApiClient.getSimpleClient().create(ApiServices.class);
//
//        // We assume your 'getAllUsers' fetches all users.
//        // You'll want a more specific endpoint like 'getStudentsByCourse' in a real app.
//        apiService.getAllUsers("your_auth_cookie_if_needed").enqueue(new Callback<List<User>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
//                progressBar.setVisibility(View.GONE);
//                if (response.isSuccessful() && response.body() != null) {
//                    studentList.clear();
//                    // Filter out only students
//                    List<User> allUsers = response.body();
//                    for (User user : allUsers) {
//                        if ("STUDENT".equals(user.getRole())) {
//                            studentList.add(user);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(TakeAttendanceActivity.this, "Failed to fetch students.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(TakeAttendanceActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void submitAttendance() {
        List<User> finalAttendanceList = adapter.getAttendanceList();
        // Here, you would create a proper request body for your API
        // For example, a list of objects with { studentId, status, date }

        Toast.makeText(this, "Submitting attendance...", Toast.LENGTH_SHORT).show();

        // Example: Log the data to be sent
        for (User student : finalAttendanceList) {
            System.out.println("Student: " + student.getName() + ", Present: " + student.isPresent());
        }

        // TODO: Create a Retrofit call to your 'POST /api/attendance' endpoint here.
        // apiService.submitAttendance(attendancePayload).enqueue(...)
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Go back to the previous activity
        return true;
    }
}
