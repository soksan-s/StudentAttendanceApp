package com.example.studentmanagementsystem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagementsystem.R;
import com.google.android.material.card.MaterialCardView;

public class TeacherDashboardActivity extends AppCompatActivity {

    private MaterialCardView cardAttendance;
    private MaterialCardView cardPerformance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This line is the critical link between your Java and XML.
        // It tells the activity to load the layout you created.
        setContentView(R.layout.activity_teacher_dashboard);

        // Set the title in the action bar to match the layout's title.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Teacher Dashboard");
        }

        // Find the views using the IDs from your XML file.
        cardAttendance = findViewById(R.id.cardAttendance);
        cardPerformance = findViewById(R.id.cardPerformance);

        // Set the click listener for the "Take Attendance" card.
        cardAttendance.setOnClickListener(v -> {
            // This will open the screen to take attendance.
            Intent intent = new Intent(TeacherDashboardActivity.this, TakeAttendanceActivity.class);
            startActivity(intent);
        });

        // Set the click listener for the "View Performance" card.
        cardPerformance.setOnClickListener(v -> {
            // A placeholder message for now.
            Toast.makeText(TeacherDashboardActivity.this, "View Performance coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
}
