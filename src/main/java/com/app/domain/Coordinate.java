package com.app.domain;

import com.app.Utility.GpsUtility;

import java.sql.Timestamp;

/**
 * Created by adenau on 19/7/16.
 */
public class Coordinate {
    private int userId;
    private Timestamp timestamp;
    private double latitude;
    private double longitude;
    private int numSat;
    private int rating;

    public Coordinate(){}

    public Coordinate(int userId, Timestamp timestamp, double latitude, double longitude, int numSat) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numSat = numSat;
    }

    public Coordinate(int userId, Timestamp timestamp, double latitude, double longitude, int numSat, int rating) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numSat = numSat;
        this.rating = rating;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getNumSat() {
        return numSat;
    }

    public void setNumSat(int numSat) {
        this.numSat = numSat;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double lattitude) {
        this.latitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double distanceFrom(Coordinate coordinate) {
        return GpsUtility.calculateDistance(this.latitude, coordinate.getLatitude(), this.longitude, coordinate.getLongitude());
    }

    public long timeFromCurrent(Coordinate coordinate) {
        return (coordinate.getTimestamp().getTime() - this.timestamp.getTime())/1000;
    }

    @Override
    public String toString() {
        return "userId - " + this.userId +
                " timestamp - " + this.timestamp +
                " lat - " + this.latitude +
                " lng - " + this.longitude +
                " numSat - " + this.numSat;
    }
}
