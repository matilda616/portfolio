package com.example.user.finder;

public class PlaceItem {
    public String text;

    public PlaceItem(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}