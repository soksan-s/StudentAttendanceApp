package com.example.studentmanagementsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentPageAdapter extends RecyclerView.Adapter<StudentPageAdapter.PageViewHolder> {

    private final List<List<Student>> pagedStudents;
    private final List<TakeAttendanceAdapter> pageAdapters = new ArrayList<>();

    public StudentPageAdapter(List<Student> fullStudentList, int pageSize) {
        pagedStudents = new ArrayList<>();

        // Split list into pages of 10
        for (int i = 0; i < fullStudentList.size(); i += pageSize) {
            int end = Math.min(i + pageSize, fullStudentList.size());
            pagedStudents.add(fullStudentList.subList(i, end));
        }
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.page_student_list, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        List<Student> studentPage = pagedStudents.get(position);

        TakeAttendanceAdapter adapter = new TakeAttendanceAdapter(studentPage);
        pageAdapters.add(adapter);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext()));
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return pagedStudents.size();
    }

    // Combine attendance from all pages
    public List<TakeAttendanceAdapter> getPageAdapters() {
        return pageAdapters;
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_student_page);
        }
    }
}



//package com.example.studentmanagementsystem.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.model.Student;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class StudentPagerAdapter extends RecyclerView.Adapter<StudentPagerAdapter.StudentPagerViewHolder> {
//
//    private final List<Student> studentList;
//    // Map to store the attendance status: Key = studentId, Value = "Present" or "Absent"
//    private final Map<String, String> attendanceStatusMap = new HashMap<>();
//
//    public StudentPagerAdapter(List<Student> studentList) {
//        this.studentList = studentList;
//        // Default all students to "Present" when the adapter is first created
//        for (Student student : studentList) {
//            attendanceStatusMap.put(student.getId(), "Present");
//        }
//    }
//
//    @NonNull
//    @Override
//    public StudentPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_pager, parent, false);
//        return new StudentPagerViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull StudentPagerViewHolder holder, int position) {
//        Student student = studentList.get(position);
//        holder.tvStudentId.setText(student.getStudentIdNumber());
//        holder.tvStudentName.setText(student.getFullName());
//
//        // --- Important: Manage RadioButton state ---
//        holder.rgAttendanceStatus.setOnCheckedChangeListener(null); // Clear listener to prevent conflicts
//
//        // Set the checked state based on what's stored in our map
//        String currentStatus = attendanceStatusMap.get(student.getId());
//        if ("Present".equals(currentStatus)) {
//            holder.rbPresent.setChecked(true);
//        } else {
//            holder.rbAbsent.setChecked(true);
//        }
//
//        // Set a new listener to update the map when the user makes a selection
//        holder.rgAttendanceStatus.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.rb_present_pager) {
//                attendanceStatusMap.put(student.getId(), "Present");
//            } else if (checkedId == R.id.rb_absent_pager) {
//                attendanceStatusMap.put(student.getId(), "Absent");
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return studentList.size();
//    }
//
//    // Method for the activity to get all the final attendance data
//    public Map<String, String> getAttendanceStatusMap() {
//        return attendanceStatusMap;
//    }
//
//    static class StudentPagerViewHolder extends RecyclerView.ViewHolder {
//        TextView tvStudentId, tvStudentName;
//        RadioGroup rgAttendanceStatus;
//        RadioButton rbPresent, rbAbsent;
//
//        public StudentPagerViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvStudentId = itemView.findViewById(R.id.tv_student_id_pager);
//            tvStudentName = itemView.findViewById(R.id.tv_student_name_pager);
//            rgAttendanceStatus = itemView.findViewById(R.id.rg_attendance_status_pager);
//            rbPresent = itemView.findViewById(R.id.rb_present_pager);
//            rbAbsent = itemView.findViewById(R.id.rb_absent_pager);
//        }
//    }
//}
