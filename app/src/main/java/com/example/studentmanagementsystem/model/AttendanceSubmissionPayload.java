package com.example.studentmanagementsystem.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AttendanceSubmissionPayload {

    @SerializedName("classId")
    private String classId;

    // --- ADD THIS BACK ---
    @SerializedName("className")
    private String className;

    @SerializedName("userId")
    private String userId;

    @SerializedName("attendanceData")
    private List<AttendanceDataItem> attendanceData;

    // Update constructor to accept BOTH ID and Name
    public AttendanceSubmissionPayload(String classId, String className, String userId, List<AttendanceDataItem> attendanceData) {
        this.classId = classId;
        this.className = className;
        this.userId = userId;
        this.attendanceData = attendanceData;
    }
}





//worked code
//package com.example.studentmanagementsystem.model;
//
//import com.google.gson.annotations.SerializedName;
//import java.util.List;
//
//public class AttendanceSubmissionPayload {
//
//        @SerializedName("className")
//        private String className;
//
//        @SerializedName("userId")
//        private String userId;
//
//        @SerializedName("attendanceData")   // FIXED
//        private List<AttendanceDataItem> attendanceData;
//
//        public AttendanceSubmissionPayload(String className, String userId, List<AttendanceDataItem> attendanceData) {
//            this.className = className;
//            this.userId = userId;
//            this.attendanceData = attendanceData;
//        }


//    @SerializedName("className")
//    private String className;
//
//    @SerializedName("userId")
//    private String userId;
//
//    @SerializedName("attendanceData")   // ✔️ FIXED KEY NAME
//    private List<AttendanceDataItem> attendanceData;
//
//    public AttendanceSubmissionPayload(String className, String userId, List<AttendanceDataItem> attendanceData) {
//        this.className = className;
//        this.userId = userId;
//        this.attendanceData = attendanceData;
//    }
//}



//package com.example.studentmanagementsystem.model;
//
//import com.google.gson.annotations.SerializedName;
//import java.util.List;
//
//public class AttendanceSubmissionPayload {
//
//    @SerializedName("className")
//    private String className;
//
//    @SerializedName("userId")
//    private String userId;
//
//    @SerializedName("attendanceData")
//    private List<AttendanceDataItem> attendanceData;
//
//    public AttendanceSubmissionPayload(String className, String userId, List<AttendanceDataItem> attendanceData) {
//        this.className = className;
//        this.userId = userId;
//        this.attendanceData = attendanceData;
//    }
//}


//package com.example.studentmanagementsystem.model;
//
//import java.util.List;
//
//// This class matches the new API documentation for POSTing data
//public class AttendanceSubmissionPayload {
//
//    private String className;
//    private String userId; // This is the ID of the logged-in user/teacher
//    private List<AttendanceDataItem> attendanceData;
//
//
//    public String getClassName() {
//        return className;
//    }
//
//    public void setClassName(String className) {
//        this.className = className;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public List<AttendanceDataItem> getAttendanceData() {
//        return attendanceData;
//    }
//
//    public void setAttendanceData(List<AttendanceDataItem> attendanceData) {
//        this.attendanceData = attendanceData;
//    }
//
//    public AttendanceSubmissionPayload(String className, String userId, List<AttendanceDataItem> attendanceData) {
//        this.className = className;
//        this.userId = userId;
//        this.attendanceData = attendanceData;
//    }
//
//    // Getters can be added if needed
//}
