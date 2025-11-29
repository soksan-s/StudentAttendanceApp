package com.example.studentmanagementsystem.model;

public class ScheduleItem {
    private String subjectName;
    private String startTime;
    private String endTime;
    private String teacherName;
    private String roomNumber;
    private String status; // e.g., "Upcoming", "In Progress", "Finished"

    public ScheduleItem(String subjectName, String startTime, String endTime, String teacherName, String roomNumber, String status) {
        this.subjectName = subjectName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teacherName = teacherName;
        this.roomNumber = roomNumber;
        this.status = status;
    }

    public String getSubjectName() { return subjectName; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getTeacherName() { return teacherName; }
    public String getRoomNumber() { return roomNumber; }
    public String getStatus() { return status; }
}
