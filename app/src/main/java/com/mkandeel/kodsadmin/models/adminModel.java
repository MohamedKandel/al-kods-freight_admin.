package com.mkandeel.kodsadmin.models;

public class adminModel {
    private String mail;
    private String pass;
    private String name;
    private String UUID;
    private static adminModel model;

    public static adminModel getInstance(String mail,String pass,String name,String UUID) {
        if (model == null) {
            model = new adminModel(mail,pass,name,UUID);
        }
        return model;
    }

    private adminModel(String mail, String pass, String name, String UUID) {
        this.mail = mail;
        this.pass = pass;
        this.name = name;
        this.UUID = UUID;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
