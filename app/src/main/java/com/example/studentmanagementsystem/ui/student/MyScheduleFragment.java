package com.example.studentmanagementsystem.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.ScheduleAdapter;
import com.example.studentmanagementsystem.model.Schedule;
import java.util.ArrayList;
import java.util.List;

public class MyScheduleFragment extends Fragment {

    private RecyclerView rvSchedule;
    private ScheduleAdapter scheduleAdapter;
    private List<Schedule> scheduleList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_schedule, container, false);

        rvSchedule = view.findViewById(R.id.rv_my_schedule);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));

        loadScheduleData(); // Load dummy data

        scheduleAdapter = new ScheduleAdapter(scheduleList);
        rvSchedule.setAdapter(scheduleAdapter);

        return view;
    }

    private void loadScheduleData() {
        // This is placeholder data. Later, you will replace this with an API call.
        scheduleList = new ArrayList<>();
        scheduleList.add(new Schedule("08:00 AM", "Software Engineering", "Prof. John Doe"));
        scheduleList.add(new Schedule("10:00 AM", "Mobile Application Development", "Prof. Jane Smith"));
        scheduleList.add(new Schedule("01:00 PM", "Database Systems", "Prof. Robert Paulson"));
        scheduleList.add(new Schedule("03:00 PM", "Project Management", "Prof. Emily White"));
    }
}
