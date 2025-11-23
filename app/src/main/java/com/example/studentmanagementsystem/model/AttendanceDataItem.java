package com.example.studentmanagementsystem.model;

import com.google.gson.annotations.SerializedName;

public class AttendanceDataItem {

        @SerializedName("studentId")    // ✔ MUST MATCH API EXACTLY
        private String studentId;

        @SerializedName("date")
        private String date;            // format: 2025-11-16

        @SerializedName("status")
        private String status;          // PRESENT or ABSENT

        public AttendanceDataItem(String studentId, String date, String status) {
            this.studentId = studentId;
            this.date = date;
            this.status = status != null ? status.toUpperCase() : "ABSENT";
        }


//    @SerializedName("studentId")
//    private String studentId;
//
//    @SerializedName("date")
//    private String date;
//
//    @SerializedName("status")
//    private String status;
//
//    public AttendanceDataItem(String studentId, String date, String status) {
//        this.studentId = studentId;
//        this.date = date;
//        // Ensure status is UPPERCASE because your Postman response shows "PRESENT"
//        this.status = status.toUpperCase();
//    }
}

//public class AttendanceDataItem {
//
//    @SerializedName("studentId")
//    private String studentId;
//
//    @SerializedName("date")
//    private String date;
//
//    @SerializedName("status")
//    private String status;
//
//    public AttendanceDataItem(String studentId, String date, String status) {
//        this.studentId = studentId;
//        this.date = date;
//        this.status = status;
//    }
//}


//package com.example.studentmanagementsystem.model;
//
//public class AttendanceDataItem {
//
//    private String studentId;
//    private String date;
//    private String status;
//
//    // Constructor
//    public AttendanceDataItem(String studentId, String date, String status) {
//        this.studentId = studentId;
//        this.date = date;
//        this.status = status;
//    }
//
//    // Getters and Setters (optional, but good practice)
//    public String getStudentId() {
//        return studentId;
//    }
//
//    public void setStudentId(String studentId) {
//        this.studentId = studentId;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//}
