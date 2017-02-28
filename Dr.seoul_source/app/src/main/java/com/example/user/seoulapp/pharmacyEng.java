package com.example.user.seoulapp;

/**
 * Created by USER on 2016-08-12.
 */
public class pharmacyEng {
    String H_ENG_CITY;
    String NAME_ENG;
    String TEL;
    String H_ENG_GU;
    String H_ENG_DONG;
    String MAIN_KEY;
    Double lat;
    Double lon;
    String lan;
    String ko_name;

    public pharmacyEng(String NAME_ENG, String TEL,String h_ENG_CITY, String h_ENG_GU, String h_ENG_DONG, String MAIN_KEY, Double lat, Double lon) {
        H_ENG_CITY = h_ENG_CITY;
        this.NAME_ENG = NAME_ENG;
        this.TEL = TEL;
        H_ENG_GU = h_ENG_GU;
        H_ENG_DONG = h_ENG_DONG;
        this.MAIN_KEY = MAIN_KEY;
        this.lat = lat;
        this.lon = lon;

    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setH_ENG_CITY(String h_ENG_CITY) {
        H_ENG_CITY = h_ENG_CITY;
    }

    public void setNAME_ENG(String NAME_ENG) {
        this.NAME_ENG = NAME_ENG;
    }

    public void setTEL(String TEL) {
        this.TEL = TEL;
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

    public String getH_ENG_CITY() {
        return H_ENG_CITY;
    }

    public String getNAME_ENG() {
        return NAME_ENG;
    }

    public String getTEL() {
        return TEL;
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

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getKo_name() {
        return ko_name;
    }

    public void setKo_name(String ko_name) {
        this.ko_name = ko_name;
    }
}
