package com.mkandeel.kodsadmin.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Cert implements Parcelable {
    private String cert_num;
    private String cert_date;
    private String comp_name;
    private String comp_num;
    private String country;
    private String trans;
    private String offers;
    private boolean model_13;
    private boolean chk_fact;

    public Cert(){}


    public Cert(String cert_num, String cert_date, String comp_name,
                String comp_num, String country, String trans,
                String offers, boolean model_13, boolean chk_fact) {
        this.cert_num = cert_num;
        this.cert_date = cert_date;
        this.comp_name = comp_name;
        this.comp_num = comp_num;
        this.country = country;
        this.trans = trans;
        this.offers = offers;
        this.model_13 = model_13;
        this.chk_fact = chk_fact;
    }

    protected Cert(Parcel in) {
        cert_num = in.readString();
        cert_date = in.readString();
        comp_name = in.readString();
        comp_num = in.readString();
        country = in.readString();
        trans = in.readString();
        offers = in.readString();
        model_13 = in.readByte() != 0;
        chk_fact = in.readByte() != 0;
    }

    public String getCert_num() {
        return cert_num;
    }

    public void setCert_num(String cert_num) {
        this.cert_num = cert_num;
    }

    public String getCert_date() {
        return cert_date;
    }

    public void setCert_date(String cert_date) {
        this.cert_date = cert_date;
    }

    public String getComp_name() {
        return comp_name;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public String getComp_num() {
        return comp_num;
    }

    public void setComp_num(String comp_num) {
        this.comp_num = comp_num;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getOffers() {
        return offers;
    }

    public void setOffers(String offers) {
        this.offers = offers;
    }

    public boolean isModel_13() {
        return model_13;
    }

    public void setModel_13(boolean model_13) {
        this.model_13 = model_13;
    }

    public boolean isChk_fact() {
        return chk_fact;
    }

    public void setChk_fact(boolean chk_fact) {
        this.chk_fact = chk_fact;
    }

    public static final Creator<Cert> CREATOR = new Creator<Cert>() {
        @Override
        public Cert createFromParcel(Parcel in) {
            return new Cert(in);
        }

        @Override
        public Cert[] newArray(int size) {
            return new Cert[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(cert_num);
        dest.writeString(cert_date);
        dest.writeString(comp_name);
        dest.writeString(comp_num);
        dest.writeString(country);
        dest.writeString(trans);
        dest.writeString(offers);
        dest.writeByte((byte) (model_13 ? 1 : 0));
        dest.writeByte((byte) (chk_fact ? 1 : 0));
    }
}
