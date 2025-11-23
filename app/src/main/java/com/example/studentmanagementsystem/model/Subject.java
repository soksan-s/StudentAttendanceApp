package com.example.studentmanagementsystem.model;

import com.google.gson.annotations.SerializedName;

public class Subject {

    @SerializedName("name") // Use the exact name from your API's JSON response
    private String name;

    // --- Getter ---
    public String getName() {
        return name;
    }

    // --- Setter (optional but good practice) ---
    public void setName(String name) {
        this.name = name;
    }
}
