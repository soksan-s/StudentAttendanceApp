package com.example.studentmanagementsystem.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.ScheduleAdapter;
import com.example.studentmanagementsystem.model.ScheduleItem;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class StudentScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private TextView tvDayLabel; // e.g., "Today, Monday"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_schedule); // Define layout below

        setupToolbar();

        tvDayLabel = findViewById(R.id.tv_schedule_day_label);
        recyclerView = findViewById(R.id.rv_schedule_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadScheduleData();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_schedule);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadScheduleData() {
        // MOCK DATA - Eventually replace with API
        List<ScheduleItem> list = new ArrayList<>();

        // Mocking a typical school day
        list.add(new ScheduleItem("Mathematics", "08:00", "09:30", "Mr. Smith", "Room 101", "Finished"));
        list.add(new ScheduleItem("Physics", "09:45", "11:15", "Mrs. Johnson", "Lab 3", "In Progress"));
        list.add(new ScheduleItem("Lunch Break", "11:15", "12:00", "-", "Canteen", "Upcoming"));
        list.add(new ScheduleItem("History", "12:00", "13:30", "Mr. Brown", "Room 204", "Upcoming"));
        list.add(new ScheduleItem("English Literature", "13:45", "15:15", "Ms. Davis", "Room 105", "Upcoming"));

        adapter = new ScheduleAdapter(list);
        recyclerView.setAdapter(adapter);

        tvDayLabel.setText("Today's Classes");
    }
}
