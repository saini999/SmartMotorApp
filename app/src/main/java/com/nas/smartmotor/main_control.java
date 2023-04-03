package com.nas.smartmotor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class main_control extends AppCompatActivity {
    String number = null;
    SharedPreferences deviceData;
    SharedPreferences.Editor deviceDataEditor;
    TextView showNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);
        showNum = (TextView) findViewById(R.id.viewNumber);
        if(deviceData == null) {
            deviceData = getSharedPreferences("LoginData", MODE_PRIVATE);
            number = deviceData.getString("phonenum", "");
            showNum.setText(number);
        }
    }
}