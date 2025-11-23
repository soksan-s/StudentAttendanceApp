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

    @SerializedName("section")
    private String section;

    @SerializedName("subject") // This must match the JSON key from your API
    private Subject subject; // Add this field


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public Teacher getTeacher() { return teacher; }
    public List<Student> getStudents() { return students; }
}
