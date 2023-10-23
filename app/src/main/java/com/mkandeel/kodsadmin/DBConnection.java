package com.mkandeel.kodsadmin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBConnection extends SQLiteOpenHelper {
    private static final String DBname = "mydbAdmin.db";
    private static final int DBVersion = 1;

    private static DBConnection connection;

    private DBConnection(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBname, factory, DBVersion);
    }

    public static synchronized DBConnection getInstance(Context context) {
        if (connection == null) {
            connection = new DBConnection(context,DBname,null,DBVersion);
        }
        return connection;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS = "create table if not exists users (" +
                "userKey     int primary key," +
                "username    text," +
                "pass        text," +
                "email       text);";

        db.execSQL(CREATE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");
        onCreate(db);
    }

    public void insertIntoUsers(String userKey,String mail,String pass,String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userKey",userKey);
        cv.put("username",username);
        cv.put("pass",pass);
        cv.put("email",mail);
        db.insert("users",null,cv);
    }

    @SuppressLint("Range")
    public String getUserID() {
        String UUID = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select userKey from users ",null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            UUID = cursor.getString(cursor.getColumnIndex("userKey"));
            cursor.moveToNext();
        }
        return UUID;
    }

    @SuppressLint("Range")
    public String getMail() {
        String UUID = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select email from users ",null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            UUID = cursor.getString(cursor.getColumnIndex("email"));
            cursor.moveToNext();
        }
        return UUID;
    }

    public void deleteUser(String userKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("delete from users where userKey = ?",new String[]{userKey});
    }
}
