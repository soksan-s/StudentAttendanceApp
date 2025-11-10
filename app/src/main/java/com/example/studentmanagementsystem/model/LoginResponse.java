package com.example.studentmanagementsystem.model;

public class LoginResponse {
    public boolean success;
    public String message;
    public String sessionToken;
    public String userType; // "teacher" or "student"
    public String userId;   // optional
}
