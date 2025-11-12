package com.example.studentmanagementsystem.model;

import com.google.gson.annotations.SerializedName;

// This class matches the top-level JSON object: { "user": { ... } }
public class LoginResponseWrapper {

    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
