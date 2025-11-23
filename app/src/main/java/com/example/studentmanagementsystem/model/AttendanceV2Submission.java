package com.example.studentmanagementsystem.model;

import java.util.List;

// This class represents the entire JSON object for the POST request
public class AttendanceV2Submission {

    private String className;
    private String teacherName;
    private List<AttendanceDataItem> attendanceData;

    // Constructor
    public AttendanceV2Submission(String className, String teacherName, List<AttendanceDataItem> attendanceData) {
        this.className = className;
        this.teacherName = teacherName;
        this.attendanceData = attendanceData;
    }

    // Getters and Setters (optional)
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<AttendanceDataItem> getAttendanceData() {
        return attendanceData;
    }

    public void setAttendanceData(List<AttendanceDataItem> attendanceData) {
        this.attendanceData = attendanceData;
    }
}
