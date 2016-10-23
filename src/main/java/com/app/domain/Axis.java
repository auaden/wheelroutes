package com.app.domain;

import java.sql.Timestamp;

/**
 * Created by adenau on 6/9/16.
 */
public class Axis {
    private int userId;
    private Timestamp timestamp;
    private double xAxis;
    private double yAxis;
    private double zAxis;

    public Axis(){}

    public Axis(int userId, Timestamp timestamp, double xAxis, double yAxis, double zAxis) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zAxis = zAxis;
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

    public double getxAxis() {
        return xAxis;
    }

    public void setxAxis(double xAxis) {
        this.xAxis = xAxis;
    }

    public double getyAxis() {
        return yAxis;
    }

    public void setyAxis(double yAxis) {
        this.yAxis = yAxis;
    }

    public double getzAxis() {
        return zAxis;
    }

    public void setzAxis(double zAxis) {
        this.zAxis = zAxis;
    }

    @Override
    public String toString() {
        return "userId: " + this.userId +
                "timestamp: " + this.timestamp +
                "x: " + this.xAxis +
                "y: " + this.yAxis +
                "z: " + this.zAxis;
    }
}
