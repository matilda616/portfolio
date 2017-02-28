package com.example.user.finder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AppInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo);
        setTitle("Application Information");
    }
}