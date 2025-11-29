package com.example.studentmanagementsystem.model;

public class EventItem {
    private String title;
    private String date;
    private String description;
    private int imageResId; // We will use local drawables for now

    public EventItem(String title, String date, String description, int imageResId) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}
