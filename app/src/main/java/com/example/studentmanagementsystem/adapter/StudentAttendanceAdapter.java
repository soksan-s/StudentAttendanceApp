package com.example.studentmanagementsystem.adapter; // Or your relevant package

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.User; // Make sure your User model is here

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.StudentViewHolder> {

    private final Context context;
    private final List<User> studentList;

    public StudentAttendanceAdapter(Context context, List<User> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_attendance, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        User student = studentList.get(position);
        holder.studentName.setText(student.getName());

        // Use Glide to load the student's image
        Glide.with(context)
                .load(student.getImage())
                .placeholder(R.drawable.ic_person_outline) // Default image
                .error(R.drawable.ic_person_outline)       // Image on error
                .into(holder.studentImage);

        // Keep track of the checked state for each student
        // Here we're using a simple boolean in the User model.
        // Make sure to add `private boolean isPresent = true;` to your User model.
        holder.presentCheckBox.setChecked(student.isPresent());
        holder.presentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            student.setPresent(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    // Helper method to get the list of students with their attendance status
    public List<User> getAttendanceList() {
        return studentList;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView studentImage;
        TextView studentName;
        CheckBox presentCheckBox;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentImage = itemView.findViewById(R.id.iv_student_image);
            studentName = itemView.findViewById(R.id.tv_student_name);
            presentCheckBox = itemView.findViewById(R.id.cb_present);
        }
    }
}
