package com.example.studentmanagementsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.CourseAttendance;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private List<CourseAttendance> attendanceList;

    public AttendanceAdapter(List<CourseAttendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        CourseAttendance attendance = attendanceList.get(position);
        holder.tvCourseTitle.setText(attendance.getCourseTitle());
        String ratio = attendance.getAttendedClasses() + "/" + attendance.getTotalClasses();
        holder.tvAttendanceRatio.setText(ratio);
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseTitle, tvAttendanceRatio;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseTitle = itemView.findViewById(R.id.tv_course_title);
            tvAttendanceRatio = itemView.findViewById(R.id.tv_attendance_ratio);
        }
    }
}
