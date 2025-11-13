package com.example.studentmanagementsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.AttendanceViewHolder> {

    private final List<Student> studentList;
    // Map to store the attendance status: Key = studentId, Value = status ("Present" or "Absent")
    private final Map<String, String> attendanceStatusMap = new HashMap<>();

    public TakeAttendanceAdapter(List<Student> studentList) {
        this.studentList = studentList;
        // Default all students to "Present"
        for (Student student : studentList) {
            attendanceStatusMap.put(student.getId(), "Present");
        }
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvStudentName.setText(student.getFullName());
        holder.tvStudentId.setText(student.getStudentIdNumber());

        // Set the radio button listener
        holder.rgAttendanceStatus.setOnCheckedChangeListener(null); // Clear previous listener

        // Check the correct radio button based on the map
        String status = attendanceStatusMap.get(student.getId());
        if ("Present".equals(status)) {
            holder.rbPresent.setChecked(true);
        } else {
            holder.rbAbsent.setChecked(true);
        }

        // Set the listener to update the map when a choice is made
        holder.rgAttendanceStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_present) {
                attendanceStatusMap.put(student.getId(), "Present");
            } else if (checkedId == R.id.rb_absent) {
                attendanceStatusMap.put(student.getId(), "Absent");
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    // This method allows the activity to get the final attendance data
    public Map<String, String> getAttendanceStatusMap() {
        return attendanceStatusMap;
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentId;
        RadioGroup rgAttendanceStatus;
        RadioButton rbPresent, rbAbsent;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            rgAttendanceStatus = itemView.findViewById(R.id.rg_attendance_status);
            rbPresent = itemView.findViewById(R.id.rb_present);
            rbAbsent = itemView.findViewById(R.id.rb_absent);
        }
    }
}
