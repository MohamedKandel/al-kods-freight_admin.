package com.mkandeel.kodsadmin.models;

public class usersModel {
    private String userKey;
    private String userName;
    private String mail;
    private String status;
    private static usersModel model;

    public usersModel(){}

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
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

    public static usersModel getInstance(String userKey,String username,String mail, String status) {
        if (model == null) {
            model = new usersModel(userKey,username,mail,status);
        }
        return model;
    }

    private usersModel(String userKey, String username, String mail, String status) {
        this.userKey = userKey;
        this.userName = username;
        this.mail = mail;
        this.status = status;
    }
}
