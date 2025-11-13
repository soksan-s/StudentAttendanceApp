package com.example.studentmanagementsystem.model;

public class Schedule {
    private String classTime;
    private String courseName;
    private String teacherName;

    public Schedule(String classTime, String courseName, String teacherName) {
        this.classTime = classTime;
        this.courseName = courseName;
        this.teacherName = teacherName;
    }

    public String getClassTime() {
        return classTime;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }
}
