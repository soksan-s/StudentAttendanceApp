package com.example.studentmanagementsystem.model;

import com.google.gson.annotations.SerializedName;

public class Teacher {
    @SerializedName("id")
    private String id;

    @SerializedName("fullName")
    private String fullName;

    // Getters
    public String getId() { return id; }
    public String getFullName() { return fullName; }
}
