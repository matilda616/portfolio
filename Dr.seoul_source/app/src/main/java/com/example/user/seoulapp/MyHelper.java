package com.example.user.seoulapp;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LG on 2016-05-31.
 */

public class MyHelper extends SQLiteOpenHelper {
    public static final String  COL_NAME = "name";
    Context context;


    public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table Bookmark_pha ( _id integer primary key autoincrement"
                + ", mainkey text, name_eng_pha text, type_pha text, address_pha text,tel_pha text, language_pha text, street_lat text, street_lon text, ko_name text);";
        String sql2 = "create table Bookmark_hos (_id integer primary key autoincrement"
                + ", mainkey text, name_eng_hos text, type_hos text, address_hos text, language_hos text, street_lat text, street_lon text, ko_name text);";
        try {
            db.execSQL(sql);
            db.execSQL(sql2);
        }catch (SQLException e){
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
