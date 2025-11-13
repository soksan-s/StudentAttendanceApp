package com.example.studentmanagementsystem.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.adapter.AttendanceAdapter;
import com.example.studentmanagementsystem.model.CourseAttendance;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyAttendanceFragment extends Fragment {

    private TextView tvAttendancePercentage;
    private RecyclerView rvAttendanceDetails;
    private AttendanceAdapter attendanceAdapter;
    private List<CourseAttendance> attendanceList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_attendance, container, false);

        tvAttendancePercentage = view.findViewById(R.id.tv_attendance_percentage);
        rvAttendanceDetails = view.findViewById(R.id.rv_attendance_details);
        rvAttendanceDetails.setLayoutManager(new LinearLayoutManager(getContext()));

        loadAttendanceData(); // Load dummy data

        attendanceAdapter = new AttendanceAdapter(attendanceList);
        rvAttendanceDetails.setAdapter(attendanceAdapter);

        updateOverallPercentage();

        return view;
    }

    private void loadAttendanceData() {
        // This is placeholder data. Later, you will replace this with an API call.
        attendanceList = new ArrayList<>();
        attendanceList.add(new CourseAttendance("Software Engineering", 38, 40));
        attendanceList.add(new CourseAttendance("Mobile Application Development", 35, 40));
        attendanceList.add(new CourseAttendance("Database Systems", 39, 40));
        attendanceList.add(new CourseAttendance("Project Management", 32, 40));
    }

    private void updateOverallPercentage() {
        if (attendanceList == null || attendanceList.isEmpty()) {
            tvAttendancePercentage.setText("N/A");
            return;
        }

        int totalAttended = 0;
        int totalClasses = 0;
        for (CourseAttendance item : attendanceList) {
            totalAttended += item.getAttendedClasses();
            totalClasses += item.getTotalClasses();
        }

        if (totalClasses == 0) {
            tvAttendancePercentage.setText("0%");
        } else {
            double percentage = ((double) totalAttended / totalClasses) * 100;
            tvAttendancePercentage.setText(String.format(Locale.getDefault(), "%.0f%%", percentage));
        }
    }
}
