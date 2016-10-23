/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.domain;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Lewis
 */
public class AxisTimeFrame {
    private List<Axis> axes;
    private Timestamp startTime;
    private int accessibilityRate; //0-10
    private int userId;

    public AxisTimeFrame(int userId, List<Axis> axes, Timestamp startTime) {
        this.userId = userId;
        this.axes = axes;
        this.startTime = startTime;
        this.accessibilityRate = 0;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAxes(List<Axis> axes) {
        this.axes = axes;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public List<Axis> getAxes() {
        return axes;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public int getAccessibilityRate() {
        return accessibilityRate;
    }

    public void setAccessibilityRate(int accessibilityRate) {
        this.accessibilityRate = accessibilityRate;
    }

    @Override
    public String toString() {
        return "userId:" + userId + " timestamp:" + " size of AL:" + axes.size() + " rating:" + accessibilityRate;
    }
}
