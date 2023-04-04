package com.nas.smartmotor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class main_control extends AppCompatActivity {
    String number = null;
    SharedPreferences deviceData;
    SharedPreferences.Editor deviceDataEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);

    }
}