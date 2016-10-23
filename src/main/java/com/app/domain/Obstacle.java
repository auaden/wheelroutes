package com.app.domain;

import java.sql.Timestamp;

/**
 * Created by adenau on 20/9/16.
 */
public class Obstacle {

    private String email;
    private Timestamp timestamp;
    private String description;
    private double latitude;
    private double longitude;
    private byte[] image;
    private boolean approved;

    public Obstacle() {}

    public Obstacle(String email,
                    Timestamp timestamp,
                    String description,
                    double latitude,
                    double longitude,
                    byte[] image,
                    boolean approved) {
        this.email = email;
        this.timestamp = timestamp;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.approved = approved;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
