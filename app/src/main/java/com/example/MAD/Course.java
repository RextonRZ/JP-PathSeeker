package com.example.MAD;

import java.io.Serializable;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    private String courseName;
    private String level;
    private String duration;
    private String contentDetails;
    private String description;
    private double rating;
    private int imageResId;
    private String url;

    public Course(String courseName, String level, String duration, String contentDetails, String description, double rating, int imageResId, String url) {
        this.courseName = courseName;
        this.level = level;
        this.duration = duration;
        this.contentDetails = contentDetails;
        this.description = description;
        this.rating = rating;
        this.imageResId = imageResId;
        this.url = url;
    }

    public String getCourseName() { return courseName; }
    public String getLevel() { return level; }
    public String getDuration() { return duration; }
    public String getContentDetails() { return contentDetails; }
    public String getDescription() { return description; }
    public double getRating() { return rating; }
    public int getImageResId() { return imageResId; }
    public String getUrl() { return url; }

}
