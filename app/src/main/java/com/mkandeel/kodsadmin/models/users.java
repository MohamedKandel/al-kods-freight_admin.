package com.mkandeel.kodsadmin.models;

public class users {
    private String userKey;
    private String deviceID;
    private String mail;
    private String status;
    private String username;

    public users(String userKey, String deviceID, String mail, String status, String username) {
        this.userKey = userKey;
        this.deviceID = deviceID;
        this.mail = mail;
        this.status = status;
        this.username = username;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
