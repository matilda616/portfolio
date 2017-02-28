package com.example.user.finder;

public class MyListItem {
    public String text;

    public MyListItem(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "MyListItem{" +
                "text='" + text + '\'' +
                '}';
    }
}