package com.mkandeel.kodsadmin.models;

public class customModel {
    private String cert_num;
    private String comp_num;
    private String comp_name;


    public customModel(){}

    public customModel(String cert_num, String comp_num, String comp_name) {
        this.cert_num = cert_num;
        this.comp_num = comp_num;
        this.comp_name = comp_name;
    }

    public String getCert_num() {
        return cert_num;
    }

    public void setCert_num(String cert_num) {
        this.cert_num = cert_num;
    }

    public String getComp_num() {
        return comp_num;
    }

    public void setComp_num(String comp_num) {
        this.comp_num = comp_num;
    }

    public String getComp_name() {
        return comp_name;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }
}
