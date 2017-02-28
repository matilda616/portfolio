package com.example.user.finder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {
    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "Finder", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table alarm " +
                "(naver_id text, keyword text);");
        db.execSQL("create table found " +
                "(foundImage text, foundText text, " +
                "foundPhone text, foundPlace text);");
        db.execSQL("create table lost " +
                "(foundImage text, foundText text, " +
                "foundPhone text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}