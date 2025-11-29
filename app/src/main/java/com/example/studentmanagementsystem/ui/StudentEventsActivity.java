package com.example.studentmanagementsystem.ui;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.EventAdapter;
import com.example.studentmanagementsystem.model.EventItem;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class StudentEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_events);

        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_events);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_all_events);

        // Set to Vertical List
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load Data
        List<EventItem> events = getMockEvents();
        adapter = new EventAdapter(events);
        recyclerView.setAdapter(adapter);
    }

    private List<EventItem> getMockEvents() {
        List<EventItem> list = new ArrayList<>();

        // 1. Sports Day
        list.add(new EventItem(
                "School Sports Day",
                "Dec 12, 2025",
                "Join us at the main stadium for the annual sports meet.",
                R.drawable.sport_pic // Replace with your actual image name
        ));

        // 2. Exams
        list.add(new EventItem(
                "Final Exams Week",
                "Jan 15, 2026",
                "Prepare for the semester finals. Check your schedule.",
                R.drawable.exam_pic // Replace with your actual image name
        ));

        // 3. Science Fair
        list.add(new EventItem(
                "Science Fair",
                "Feb 20, 2026",
                "Showcase your innovative projects in the main hall.",
                R.drawable.scient_pic // Replace with your actual image name
        ));

        // 4. Parent Meeting
        list.add(new EventItem(
                "Parent-Teacher Meeting",
                "Mar 05, 2026",
                "Discuss student progress with teachers.",
                R.drawable.parent_meeting // Replace with your actual image name
        ));

        // 5. Art Exhibition
        list.add(new EventItem(
                "Art Exhibition",
                "Apr 10, 2026",
                "Displaying student artwork from all grades.",
                R.drawable.art_pic // Replace with your actual image name
        ));

        return list;
    }
}
