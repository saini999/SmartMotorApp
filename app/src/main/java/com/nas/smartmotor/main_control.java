package com.nas.smartmotor;

import static com.nas.smartmotor.R.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Random;

public class main_control extends AppCompatActivity {
    SharedPreferences deviceData;
    SharedPreferences.Editor deviceDataEditor;
    boolean isMotorBtn, isRefreshBtn;
    TextView header, RYText, YBText, BRText, RYValue, YBValue, BRValue, phaseText, phaseValue, statusText, statusValue;
    Button motorControl, refreshData;

    String requestIDSv,requestID,deviceNum;

    String motorState, powerState, RY, YB, BR, phase, alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main_control);
        header = (TextView) findViewById(id.pwrText);
        RYText = (TextView) findViewById(id.pwrRYv);
        YBText = (TextView) findViewById(id.pwrYBv);
        BRText = (TextView) findViewById(id.pwrBRv);
        RYValue = (TextView) findViewById(id.pwrRYvalue);
        YBValue = (TextView) findViewById(id.pwrYBvalue);
        BRValue = (TextView) findViewById(id.pwrBRvalue);
        phaseText = (TextView) findViewById(id.pwrPhaseSeq);
        phaseValue = (TextView) findViewById(id.pwrPhaseSeqValue);
        statusText = (TextView) findViewById(id.pwrStatus);
        statusValue = (TextView) findViewById(id.pwrStatusValue);
        motorControl = (Button) findViewById(id.pwrBtnMotorControl);
        refreshData = (Button) findViewById(id.pwrBtnRefresh);


        Random random = new Random();
        requestID = String.format("%04d", random.nextInt(10000));
        requestIDSv = requestID;
        deviceData = getSharedPreferences("LoginData", MODE_PRIVATE);
        deviceDataEditor = deviceData.edit();
        deviceNum = deviceData.getString("phonenum", "");
        String msg;
        msg = "@state" + "#" + requestIDSv + "&getData";
        motorControl.setBackgroundColor(getColor(color.pwr_btnControlMotorDisabled));
        refreshData.setBackgroundColor(getColor(color.pwr_refreshPwrDisabled));
        isMotorBtn = false;  isRefreshBtn = false;
        motorControl.setOnClickListener(view -> {
            if(isMotorBtn){
                if(alarm.contains("AOK")){
                    Toast.makeText(this, "CLICK!! Motor", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(main_control.this, motorControlActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, "SYSTEM ALARM ACTIVE!", Toast.LENGTH_LONG).show();
                }
            }
        });
        refreshData.setOnClickListener(view -> {
            if(isRefreshBtn){
                Toast.makeText(this, "Connecting...", Toast.LENGTH_LONG).show();
                SmsManager sms = SmsManager.getDefault();
                requestID = String.format("%04d", random.nextInt(1000));
                requestIDSv = requestID;
                String msgtemp;
                msgtemp = "@state" + "#" + requestIDSv + "&getData";
                sms.sendTextMessage(deviceNum, null, msgtemp, null, null);
                Toast.makeText(getApplicationContext(), "Connecting to Device. \n Request Sent!", Toast.LENGTH_LONG).show();
                getData();
            } else {
                Toast.makeText(this, "Already Querying", Toast.LENGTH_LONG).show();
            }
        });
        if(deviceNum != null) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(deviceNum, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Connecting to Device. \n Request Sent!", Toast.LENGTH_LONG).show();
            getData();
        }


    }


    Handler handler = new Handler();
    Runnable runnable;
    final int delay = 1000;
    private void getData() {
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, delay);
            motorControl.setBackgroundColor(getColor(color.pwr_btnControlMotorDisabled));
            refreshData.setBackgroundColor(getColor(color.pwr_refreshPwrDisabled));
            isMotorBtn = false;  isRefreshBtn = false;
            Toast.makeText(this, "Waiting for Response...", Toast.LENGTH_LONG).show();

            checkSms getSms = new checkSms();
            String smsData = null;
            smsData = getSms.getSms(this, requestIDSv, deviceNum);
            if(smsData != null) {
                if(smsData.contains("@STATE")){
                    String tempRid = null;
                    tempRid = smsData.substring(smsData.indexOf("#RID") + 4, smsData.indexOf("#RID") + 8);
                    if(tempRid.contains(requestIDSv)){
                        Toast.makeText(this, "Response OK, Loading..", Toast.LENGTH_LONG).show();
                        if(smsData.contains("&DATA=")) {
                            if (smsData.contains("%MOTORSTATE:")) {
                                motorState = smsData.substring(smsData.indexOf("%MOTORSTATE:") + 12, smsData.indexOf("%MOTORSTATE:") + 15);
                            }
                            if (smsData.contains("%PWRSTATE:")) {
                                powerState = smsData.substring(smsData.indexOf("%PWRSTATE:") + 10, smsData.indexOf("%PWRSTATE:") + 13);
                            }
                            if (smsData.contains("%RY:")) {
                                RY = smsData.substring(smsData.indexOf("%RY:") + 4, smsData.indexOf("%RY:") + 7);
                            }
                            if (smsData.contains("%YB:")) {
                                YB = smsData.substring(smsData.indexOf("%YB:") + 4, smsData.indexOf("%YB:") + 7);
                            }
                            if (smsData.contains("%BR:")) {
                                BR = smsData.substring(smsData.indexOf("%BR:") + 4, smsData.indexOf("%BR:") + 7);
                            }
                            if (smsData.contains("%PHASE:")) {
                                phase = smsData.substring(smsData.indexOf("%PHASE:") + 7, smsData.indexOf("%PHASE:") + 10);
                            }
                            if (smsData.contains("%ALARM:")) {
                                alarm = smsData.substring(smsData.indexOf("%ALARM:") + 7, smsData.indexOf("%ALARM:") + 10);
                            }
                        }
                        if(powerState != null) {
                            if(powerState.equals("NPR")){
                                header.setText(string.pwr_noPowerText);
                                header.setTextColor(getColor(color.pwr_noPowerText));
                            } else if (powerState.equals("PWR")) {
                                header.setText(string.pwr_activePowerText);
                                header.setTextColor(getColor(color.pwr_activePowerText));
                            } else {
                                Toast.makeText(this, "ERR_U_PWRST:" + powerState, Toast.LENGTH_LONG).show();
                            }
                        }
                        if(RY != null){
                            RYValue.setText(RY);
                            if(Integer.parseInt(RY) < 350) {
                                RYValue.setTextColor(getColor(color.pwr_error));
                            } else if (Integer.parseInt(RY) > 350 && Integer.parseInt(RY) < 470) {
                                RYValue.setTextColor(getColor(color.pwr_ok));
                            } else if (Integer.parseInt(RY) > 470) {
                                RYValue.setTextColor(getColor(color.pwr_error));
                            } else {
                                Toast.makeText(this, "ERR_U_RY:" + RY, Toast.LENGTH_LONG).show();
                            }
                        }
                        if(YB != null){
                            YBValue.setText(YB);
                            if(Integer.parseInt(YB) < 350) {
                                YBValue.setTextColor(getColor(color.pwr_error));
                            } else if (Integer.parseInt(YB) > 350 && Integer.parseInt(YB) < 470) {
                                YBValue.setTextColor(getColor(color.pwr_ok));
                            } else if (Integer.parseInt(YB) > 470) {
                                YBValue.setTextColor(getColor(color.pwr_error));
                            } else {
                                Toast.makeText(this, "ERR_U_YB:" + YB, Toast.LENGTH_LONG).show();
                            }
                        }
                        if(BR != null){
                            BRValue.setText(BR);
                            if(Integer.parseInt(BR) < 350) {
                                BRValue.setTextColor(getColor(color.pwr_error));
                            } else if (Integer.parseInt(BR) > 350 && Integer.parseInt(BR) < 470) {
                                BRValue.setTextColor(getColor(color.pwr_ok));
                            } else if (Integer.parseInt(BR) > 470) {
                                BRValue.setTextColor(getColor(color.pwr_error));
                            } else {
                                Toast.makeText(this, "ERR_U_BR:" + BR, Toast.LENGTH_LONG).show();
                            }
                        }
                        if(phase != null){
                            if(phase.contains("RYB")){
                                phaseValue.setText(string.pwr_phsqRYB);
                                phaseValue.setTextColor(getColor(color.pwr_ok));
                            } else if (phase.contains("RBY")){
                                phaseValue.setText(string.pwr_phsqRBY);
                                phaseValue.setTextColor(getColor(color.pwr_error));
                            } else {
                                Toast.makeText(this, "ERR_U_PHASE:" + phase, Toast.LENGTH_LONG).show();
                            }
                        }
                        if(motorState != null){
                            if(motorState.contains("RUN")){
                                statusValue.setText(string.pwr_statusRunning);
                                statusValue.setTextColor(getColor(color.pwr_ok));
                            } else if (motorState.contains("RDY")){
                                statusValue.setText(string.pwr_statusReady);
                                statusValue.setTextColor(getColor(color.pwr_ok));
                            } else if (motorState.contains("ALM")){
                                statusValue.setText(string.pwr_error);
                                statusValue.setTextColor(getColor(color.pwr_error));
                            } else {
                                Toast.makeText(this, "ERR_U_MSTATE:" + motorState, Toast.LENGTH_LONG).show();
                            }
                        }
                        motorControl.setBackgroundColor(getColor(color.pwr_btnControlMotorEnabled));
                        refreshData.setBackgroundColor(getColor(color.pwr_refreshPwrEnabled));
                        isMotorBtn = true;  isRefreshBtn = true;
                        handler.removeCallbacks(runnable);
                    }
                }
            }
        },delay);
    }
}