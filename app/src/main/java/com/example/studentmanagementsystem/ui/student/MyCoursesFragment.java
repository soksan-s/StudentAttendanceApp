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
// You will need to create these adapter and model classes
// import com.example.studentmanagementsystem.adapter.CourseAdapter;
// import com.example.studentmanagementsystem.model.Course;

import java.util.ArrayList;

public class MyCoursesFragment extends Fragment {

    private RecyclerView recyclerView;
    // private CourseAdapter courseAdapter;
    // private ArrayList<Course> courseList;

    public MyCoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_my_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- Placeholder ---
        // In the future, you will fetch real data from your API.
        // For now, the fragment will just display the layout.
        // Once you create the Course model and CourseAdapter, you can uncomment
        // the following lines to populate the list with sample data.

        /*
        courseList = new ArrayList<>();
        // Add sample data
        courseList.add(new Course("Introduction to Android", "CS-101"));
        courseList.add(new Course("Advanced Java Programming", "CS-202"));
        courseList.add(new Course("Database Systems", "DB-303"));

        courseAdapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(courseAdapter);
        */

        // For now, you can add a simple toast to confirm the fragment is working.
        // Toast.makeText(getContext(), "MyCoursesFragment Loaded", Toast.LENGTH_SHORT).show();
    }
}
