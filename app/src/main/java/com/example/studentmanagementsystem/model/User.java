package com.example.studentmanagementsystem.model;

import com.google.gson.annotations.SerializedName;

public class User {

    // The @SerializedName annotation tells Gson exactly which JSON key to map to this field.
    // This is the most reliable way to prevent mapping errors.

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role; // This is the critical field

    @SerializedName("password")
    private String password;

    @SerializedName("image")
    private String image;

    @SerializedName("address")
    private String address;

    @SerializedName("birthDate")
    private String birthDate;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("isPresent") // Optional but good practice
    private boolean isPresent = true; // Default to true

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    // --- Getters and Setters ---
    // Make sure you have a getter for the role field.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role; // Your LoginActivity calls this method.
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}


