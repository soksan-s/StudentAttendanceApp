package com.example.studentmanagementsystem.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClassApiResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("teacher")
    private Teacher teacher;

    @SerializedName("students")
    private List<Student> students;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public Teacher getTeacher() { return teacher; }
    public List<Student> getStudents() { return students; }
}
