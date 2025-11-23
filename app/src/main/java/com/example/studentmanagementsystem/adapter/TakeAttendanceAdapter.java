package com.example.studentmanagementsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagementsystem.R;
import com.example.studentmanagementsystem.model.Student;
import com.google.android.material.chip.ChipGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.AttendanceViewHolder> {

    private final List<Student> studentList;
    // Map to store the attendance status: Key = studentId, Value = FULL STATUS STRING
    private final Map<String, String> attendanceStatusMap = new HashMap<>();

    public TakeAttendanceAdapter(List<Student> studentList) {
        this.studentList = studentList;
        // Default all students to "PRESENT" initially
        for (Student student : studentList) {
            attendanceStatusMap.put(student.getStudentIdNumber(), "PRESENT");
            //worked code
//            attendanceStatusMap.put(student.getId(), "PRESENT");
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

        // Ensure we handle potential nulls in names/IDs
        String fullName = (student.getFullName() != null) ? student.getFullName() : "Unknown Name";
        String studentIdDisplay = (student.getStudentIdNumber() != null) ? student.getStudentIdNumber() : "N/A";

        holder.tvStudentName.setText(fullName);
        holder.tvStudentId.setText("ID: " + studentIdDisplay);

        // 1. Remove listener to prevent infinite loops while setting state
        holder.cgAttendanceStatus.setOnCheckedStateChangeListener(null);

        // 2. Get the current status from the map
        String status = attendanceStatusMap.get(student.getId());

        // 3. Update UI based on data
        if (status != null) {
            switch (status) {
                case "EXCUSED":
                    holder.cgAttendanceStatus.check(R.id.chip_permission);
                    break;
                case "LATE":
                    holder.cgAttendanceStatus.check(R.id.chip_late);
                    break;
                case "ABSENT":
                    holder.cgAttendanceStatus.check(R.id.chip_absent);
                    break;
                case "PRESENT":
                default:
                    holder.cgAttendanceStatus.check(R.id.chip_present);
                    break;
            }
        } else {
            // Fallback default
            holder.cgAttendanceStatus.check(R.id.chip_present);
        }

        // 4. Re-attach listener using 'setOnCheckedStateChangeListener' (Newer Material API)
        // OR use 'setOnCheckedChangeListener' (Older API).
        // Since your previous code used setOnCheckedChangeListener, we stick to that for compatibility.

        holder.cgAttendanceStatus.setOnCheckedChangeListener((group, checkedId) -> {
//            String studentDbId = student.getId();
            String studentCode = student.getStudentIdNumber();

            // --- CRITICAL FIX HERE: SAVE FULL UPPERCASE WORDS ---
            if (checkedId == R.id.chip_present) {
                attendanceStatusMap.put(studentCode, "PRESENT"); // Was "PS"
            } else if (checkedId == R.id.chip_permission) {
                attendanceStatusMap.put(studentCode, "EXCUSED"); // Was "PM"
            } else if (checkedId == R.id.chip_late) {
                attendanceStatusMap.put(studentCode, "LATE"); // Was "LA"
            } else if (checkedId == R.id.chip_absent) {
                attendanceStatusMap.put(studentCode, "ABSENT"); // Was "AB"
            } else {
                // If user unselects (which shouldn't happen with singleSelection=true), default to PRESENT
                attendanceStatusMap.put(studentCode, "PRESENT");
                // Force visual update back to Present
                group.check(R.id.chip_present);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public Map<String, String> getAttendanceStatusMap() {
        return attendanceStatusMap;
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentId;
        ChipGroup cgAttendanceStatus;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            cgAttendanceStatus = itemView.findViewById(R.id.cg_attendance_status);
        }
    }
}




//Second worked code
//package com.example.studentmanagementsystem.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.model.Student;
//import com.google.android.material.chip.ChipGroup; // <-- IMPORTANT: Import ChipGroup
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//// The class name and ViewHolder should be updated for clarity
//public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.AttendanceViewHolder> {
//
//    private final List<Student> studentList;
//    // Map to store the attendance status: Key = studentId, Value = status string
//    private final Map<String, String> attendanceStatusMap = new HashMap<>();
//
//    public TakeAttendanceAdapter(List<Student> studentList) {
//        this.studentList = studentList;
//        // Default all students to "PRESENT"
//        for (Student student : studentList) {
//            attendanceStatusMap.put(student.getId(), "PRESENT");
//        }
//    }
//
//    @NonNull
//    @Override
//    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        // Use the updated layout file
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
//        return new AttendanceViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
//        Student student = studentList.get(position);
//        holder.tvStudentName.setText(student.getFullName());
//        holder.tvStudentId.setText(student.getStudentIdNumber());
//
//        // This prevents the listener from firing while we programmatically set the checked state
//        holder.cgAttendanceStatus.setOnCheckedChangeListener(null);
//
//        // Get the current status from our map
//        String status = attendanceStatusMap.get(student.getId());
//
//        // Set the initial checked chip based on the map
//        if (status != null) {
//            switch (status) {
//                case "PERMISSION":
//                    holder.cgAttendanceStatus.check(R.id.chip_permission);
//                    break;
//                case "LATE":
//                    holder.cgAttendanceStatus.check(R.id.chip_late);
//                    break;
//                case "ABSENT":
//                    holder.cgAttendanceStatus.check(R.id.chip_absent);
//                    break;
//                case "PRESENT":
//                default:
//                    holder.cgAttendanceStatus.check(R.id.chip_present);
//                    break;
//            }
//        } else {
//            // Default to Present if something goes wrong
//            holder.cgAttendanceStatus.check(R.id.chip_present);
//        }
//
//        // Re-attach the listener to handle user interaction
//        holder.cgAttendanceStatus.setOnCheckedChangeListener((group, checkedId) -> {
//            String studentDbId = student.getId();
//            if (checkedId == R.id.chip_present) {
//                attendanceStatusMap.put(studentDbId, "PS");
//            } else if (checkedId == R.id.chip_permission) {
//                attendanceStatusMap.put(studentDbId, "PM");
//            } else if (checkedId == R.id.chip_late) {
//                attendanceStatusMap.put(studentDbId, "LA");
//            } else if (checkedId == R.id.chip_absent) {
//                attendanceStatusMap.put(studentDbId, "AB");
//            } else {
//                // If the user un-checks everything, default back to present
//                attendanceStatusMap.put(studentDbId, "PS");
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return studentList.size();
//    }
//
//    // This method allows the activity to get the final attendance data
//    public Map<String, String> getAttendanceStatusMap() {
//        return attendanceStatusMap;
//    }
//
//    // The ViewHolder now references the ChipGroup, not RadioGroup
//    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
//        TextView tvStudentName, tvStudentId;
//        ChipGroup cgAttendanceStatus; // <-- IMPORTANT: Change from RadioGroup
//
//        public AttendanceViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvStudentName = itemView.findViewById(R.id.tv_student_name);
//            tvStudentId = itemView.findViewById(R.id.tv_student_id);
//            // Reference the new ChipGroup from the layout
//            cgAttendanceStatus = itemView.findViewById(R.id.cg_attendance_status);
//        }
//    }
//}








//worked code
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
//
//import com.example.studentmanagementsystem.R;
//import com.example.studentmanagementsystem.model.Student;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.AttendanceViewHolder> {
//
//    private final List<Student> studentList;
//    // Map to store the attendance status: Key = studentId, Value = status ("Present" or "Absent")
//    private final Map<String, String> attendanceStatusMap = new HashMap<>();
//
//    public TakeAttendanceAdapter(List<Student> studentList) {
//        this.studentList = studentList;
//        // Default all students to "Present"
//        for (Student student : studentList) {
//            attendanceStatusMap.put(student.getId(), "Present");
//        }
//    }
//
//    @NonNull
//    @Override
//    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
//        return new AttendanceViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
//        Student student = studentList.get(position);
//        holder.tvStudentName.setText(student.getFullName());
//        holder.tvStudentId.setText(student.getStudentIdNumber());
//
//        // Set the radio button listener
//        holder.rgAttendanceStatus.setOnCheckedChangeListener(null); // Clear previous listener
//
//        // Check the correct radio button based on the map
//        String status = attendanceStatusMap.get(student.getId());
//
//        // Set the initial checked chip based on the map
//        //worked code
//        if ("Present".equals(status)) {
//            holder.rbPresent.setChecked(true);
//        } else {
//            holder.rbAbsent.setChecked(true);
//        }
//
//        // Set the listener to update the map when a choice is made
//        holder.rgAttendanceStatus.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.rb_present) {
//                attendanceStatusMap.put(student.getId(), "Present");
//            } else if (checkedId == R.id.rb_absent) {
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
//    // This method allows the activity to get the final attendance data
//    public Map<String, String> getAttendanceStatusMap() {
//        return attendanceStatusMap;
//    }
//
//    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
//        TextView tvStudentName, tvStudentId;
//        RadioGroup rgAttendanceStatus;
//        RadioButton rbPresent, rbAbsent;
//
//        public AttendanceViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvStudentName = itemView.findViewById(R.id.tv_student_name);
//            tvStudentId = itemView.findViewById(R.id.tv_student_id);
//            rgAttendanceStatus = itemView.findViewById(R.id.rg_attendance_status);
//            rbPresent = itemView.findViewById(R.id.rb_present);
//            rbAbsent = itemView.findViewById(R.id.rb_absent);
//        }
//    }
//}
