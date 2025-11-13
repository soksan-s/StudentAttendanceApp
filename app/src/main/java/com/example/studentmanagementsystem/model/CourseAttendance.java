package com.example.studentmanagementsystem.model;

public class CourseAttendance {
    private String courseTitle;
    private int attendedClasses;
    private int totalClasses;

    public CourseAttendance(String courseTitle, int attendedClasses, int totalClasses) {
        this.courseTitle = courseTitle;
        this.attendedClasses = attendedClasses;
        this.totalClasses = totalClasses;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public int getTotalClasses() {
        return totalClasses;
    }
}
