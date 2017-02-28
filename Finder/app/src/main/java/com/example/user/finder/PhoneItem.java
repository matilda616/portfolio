package com.example.user.finder;

public class PhoneItem {
    public String phone;

    public PhoneItem(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return phone;
    }
}