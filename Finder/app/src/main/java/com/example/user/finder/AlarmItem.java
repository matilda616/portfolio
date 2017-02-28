package com.example.user.finder;

public class AlarmItem {
    public String keyword;

    public AlarmItem(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "AlarmItem{" +
                "keyword='" + keyword + '\'' +
                '}';
    }
}