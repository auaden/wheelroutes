package com.app.domain;

/**
 * Created by adenau on 8/7/16.
 */
public class User {
    private String email;
    private String password;
    private boolean expPain;
    private boolean haveBalance;
    private int sensitivity;

    public User(){
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public boolean isExpPain() {
        return expPain;
    }

    public boolean isHaveBalance() {
        return haveBalance;
    }

    public void setExpPain(boolean expPain) {
        this.expPain = expPain;
    }

    public void setHaveBalance(boolean haveBalance) {
        this.haveBalance = haveBalance;
    }
    
}
