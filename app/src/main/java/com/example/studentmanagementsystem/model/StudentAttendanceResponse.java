package com.example.studentmanagementsystem.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StudentAttendanceResponse {

    @SerializedName("student")
    private StudentInfo student;

    @SerializedName("statistics")
    private AttendanceStats statistics;

    @SerializedName("attendanceRecords")
    private List<AttendanceRecord> attendanceRecords;

    // Getters
    public StudentInfo getStudent() { return student; }
    public AttendanceStats getStatistics() { return statistics; }
    public List<AttendanceRecord> getAttendanceRecords() { return attendanceRecords; }

    // --- Nested Classes matching your JSON ---

//    public static class StudentInfo {
//        @SerializedName("fullName")
//        private String fullName;
//        // Add other fields if needed
//        public String getFullName() { return fullName; }
//    }

    public static class AttendanceStats {
        @SerializedName("totalRecords")
        private int totalRecords;
        @SerializedName("presentCount")
        private int presentCount;
        @SerializedName("absentCount")
        private int absentCount;
        @SerializedName("lateCount")
        private int lateCount;
        @SerializedName("excusedCount")
        private int excusedCount;

        public int getPresentCount() { return presentCount; }
        public int getAbsentCount() { return absentCount; }
        public int getLateCount() { return lateCount; }
        public int getExcusedCount() { return excusedCount; }
    }

    public static class AttendanceRecord {
        @SerializedName("id")
        private String id;
        @SerializedName("date")
        private String date;
        @SerializedName("class")
        private String className;
        @SerializedName("teacher")
        private String teacher;
        @SerializedName("status")
        private String status;

        public String getDate() { return date; }
        public String getStatus() { return status; }
        public String getClassName() { return className; }
    }

    public static class StudentInfo {
        @SerializedName("id")
        private String id;

        @SerializedName("fullName")
        private String fullName;

        @SerializedName("studentId")
        private String studentId;        // NEW

        @SerializedName("standbyClass")
        private String standbyClass;     // NEW

        @SerializedName("email")
        private String email;            // NEW

        // Getters
        public String getId() { return id; }
        public String getFullName() { return fullName; }
        public String getStudentId() { return studentId; }
        public String getStandbyClass() { return standbyClass; }
        public String getEmail() { return email; }
    }
}

