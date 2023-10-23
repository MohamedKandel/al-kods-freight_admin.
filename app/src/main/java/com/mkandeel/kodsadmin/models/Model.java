package com.mkandeel.kodsadmin.models;

public class Model {

    private String txt;
    private int img;

    public Model(String txt, int img) {
        this.txt = txt;
        this.img = img;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
