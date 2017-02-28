package com.example.user.seoulapp;

/**
 * Created by LG on 2016-05-31.
 */
public class MyItem {
    // public int _id;
    public String name_eng;
    public String type; // pharmacy 약국 hospital 병원
    public String address;
    public String tel;
    public String language;
    public String street_lat;
    public String street_lon;
    public String ko_name;
    public String mainkey;
    public MyItem(String mainkey, String name_eng, String type, String address, String tel, String language, String street_lat, String street_lon, String ko_name) {
        this.mainkey = mainkey;
        this.name_eng = name_eng;
        this.type = type;
        this.address = address;
        this.tel = tel;
        this.language = language;
        this.street_lat = street_lat;
        this.street_lon = street_lon;
        this.ko_name = ko_name;
    }

    @Override
    public String toString() {
        return "MyItem{" +
                "mainkey='"+mainkey+'\''+
                ",name_eng='" + name_eng + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", tel='" + tel + '\'' +
                ", language='" + language + '\'' +
                ", street_lat= '" + street_lat + '\''+
                ", street_lon= '" + street_lon + '\''+
                ", ko_name= '" + ko_name + '\''+
                '}';
    }
}
