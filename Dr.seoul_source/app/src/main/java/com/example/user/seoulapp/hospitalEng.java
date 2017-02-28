package com.example.user.seoulapp;

/**
 * Created by swu19 on 2016-09-24.
 */
public class hospitalEng {
    String H_ENG_CITY;
    String NAME_ENG;
    String H_ENG_GU;
    String H_ENG_DONG;
    String MAIN_KEY;
    Double lat;
    Double lon;
    String lan;
    String Type;
    String ko_name;

    public hospitalEng(String h_ENG_CITY, String NAME_ENG, String h_ENG_GU, String h_ENG_DONG, String MAIN_KEY, Double lat, Double lon,  String type) {
        H_ENG_CITY = h_ENG_CITY;
        this.NAME_ENG = NAME_ENG;
        H_ENG_GU = h_ENG_GU;
        H_ENG_DONG = h_ENG_DONG;
        this.MAIN_KEY = MAIN_KEY;
        this.lat = lat;
        this.lon = lon;
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setH_ENG_CITY(String h_ENG_CITY) {
        H_ENG_CITY = h_ENG_CITY;
    }

    public void setNAME_ENG(String NAME_ENG) {
        this.NAME_ENG = NAME_ENG;
    }

    public void setH_ENG_GU(String h_ENG_GU) {
        H_ENG_GU = h_ENG_GU;
    }

    public void setH_ENG_DONG(String h_ENG_DONG) {
        H_ENG_DONG = h_ENG_DONG;
    }

    public void setMAIN_KEY(String MAIN_KEY) {
        this.MAIN_KEY = MAIN_KEY;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getH_ENG_CITY() {
        return H_ENG_CITY;
    }

    public String getNAME_ENG() {
        return NAME_ENG;
    }

    public String getH_ENG_GU() {
        return H_ENG_GU;
    }

    public String getH_ENG_DONG() {
        return H_ENG_DONG;
    }

    public String getMAIN_KEY() {
        return MAIN_KEY;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getLan() {
        return lan;
    }

    public String getKo_name() {
        return ko_name;
    }

    public void setKo_name(String ko_name) {
        this.ko_name = ko_name;
    }
}
