package com.mkandeel.kodsadmin.models;

import android.net.Uri;

public class FilesModel {
    private String name;
    private Uri uri;

    public FilesModel(){}

    public FilesModel(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
