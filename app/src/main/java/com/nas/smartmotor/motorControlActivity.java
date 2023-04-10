package com.nas.smartmotor;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import java.util.Random;

public class motorControlActivity extends AppCompatActivity {

    SharedPreferences deviceData;
    SharedPreferences.Editor deviceDataEditor;
    boolean isMotorOnBtn, isMotorOffBtn, isRefreshBtn, isUpdateTimeBtn;

    String requestIDSv,requestID,deviceNum;
    String motorState, powerState, RY, YB, BR, phase, current, alarm, setTime, timeLeft;
    Button motorOnBtn, motorOffBtn, refreshDataBtn, updateTimeBtn;
    TextView motorStatusValue, timeLeftText;
    int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_control);
        deviceData = getSharedPreferences("LoginData", MODE_PRIVATE);
        deviceDataEditor = deviceData.edit();
        deviceNum = deviceData.getString("phonenum", "");
        motorOnBtn = findViewById(R.id.mtrMotorOnBtn);
        motorOffBtn = findViewById(R.id.mtrMotorOffBtn);
        refreshDataBtn = findViewById(R.id.mtrRefreshDataBtn);
        updateTimeBtn = findViewById(R.id.mtrUpdateTimeBtn);
        motorStatusValue = findViewById(R.id.mtrStatusValue);
        timeLeftText = findViewById(R.id.mtrTimeLeftText);
        //false = GetData, True = Send Command

        motorOnBtn.setOnClickListener(view -> {
            if(isMotorOnBtn){
                if(alarm.contains("AOK")){
                    Toast.makeText(this, "Starting Motor", Toast.LENGTH_LONG).show();
                    sendRequest(true, "%MOTORSTATE:RUN");
                    getData(true);
                } else {
                    Toast.makeText(this, "SYSTEM ALARM ACTIVE!", Toast.LENGTH_LONG).show();
                }
            }
        });
        motorOffBtn.setOnClickListener(view -> {
            if(isMotorOffBtn){
                Toast.makeText(this, "Stopping Motor", Toast.LENGTH_LONG).show();
                sendRequest(true, "%MOTORSTATE:STP");
                getData(true);
            }
        });
        refreshDataBtn.setOnClickListener(view -> {
            if(isRefreshBtn){
                sendRequest(false, null);
                getData(false);
            } else {
                Toast.makeText(this, "Already Querying", Toast.LENGTH_LONG).show();

            }
        });

        updateTimeBtn.setOnClickListener(view -> {
            if(isUpdateTimeBtn) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, selectedHour, selectedMinute) -> {
                    hour = selectedHour;
                    minute = selectedMinute;
                    String text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute) + " Set, Click to Change";
                    updateTimeBtn.setText(text);
                    setTime = String.format(Locale.getDefault(),"%02d:%02d", hour, minute);
                    sendRequest(true, "%MOTORSTATE:RUN%TIME:" + setTime);
                    getData(true);
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hour, minute, true);

                timePickerDialog.setTitle("Select Motor Off Time");
                timePickerDialog.show();
            } else {
                Toast.makeText(this, "Button Disabled", Toast.LENGTH_LONG).show();
            }
        });

        if(deviceNum != null) {
            sendRequest(false, null);
            getData(false);
        }

    }
    private void setButtonState(String button, boolean value){
        if(value) {
            if (button.contains("refresh")) {
                refreshDataBtn.setBackgroundColor(getColor(R.color.mtr_RefreshBtnEnabled));
                isRefreshBtn = true;
            }
            if (button.contains("motoron")) {
                motorOnBtn.setBackgroundColor(getColor(R.color.mtr_MotorOnBtnEnabled));
                isMotorOnBtn = true;
            }
            if (button.contains("motoroff")) {
                motorOffBtn.setBackgroundColor(getColor(R.color.mtr_MotorOffBtnEnabled));
                isMotorOffBtn = true;
            }
            if (button.contains("timer")) {
                updateTimeBtn.setBackgroundColor(getColor(R.color.mtr_SetTimeBtnEnabled));
                isUpdateTimeBtn = true;
            }
            if (button.contains("all")){
                refreshDataBtn.setBackgroundColor(getColor(R.color.mtr_RefreshBtnEnabled));
                isRefreshBtn = true;
                motorOnBtn.setBackgroundColor(getColor(R.color.mtr_MotorOnBtnEnabled));
                isMotorOnBtn = true;
                motorOffBtn.setBackgroundColor(getColor(R.color.mtr_MotorOffBtnEnabled));
                isMotorOffBtn = true;
                updateTimeBtn.setBackgroundColor(getColor(R.color.mtr_SetTimeBtnEnabled));
                isUpdateTimeBtn = true;
            }

        } else {
            if (button.contains("refresh")) {
                refreshDataBtn.setBackgroundColor(getColor(R.color.mtr_RefreshBtnDisabled));
                isRefreshBtn = false;
            }
            if (button.contains("motoron")) {
                motorOnBtn.setBackgroundColor(getColor(R.color.mtr_MotorOnBtnDisabled));
                isMotorOnBtn = false;
            }
            if (button.contains("motoroff")) {
                motorOffBtn.setBackgroundColor(getColor(R.color.mtr_MotorOffBtnDisabled));
                isMotorOffBtn = false;
            }
            if (button.contains("timer")) {
                updateTimeBtn.setBackgroundColor(getColor(R.color.mtr_SetTimeBtnDisabled));
                isUpdateTimeBtn = false;
            }
            if (button.contains("all")){
                refreshDataBtn.setBackgroundColor(getColor(R.color.mtr_RefreshBtnDisabled));
                isRefreshBtn = false;
                motorOnBtn.setBackgroundColor(getColor(R.color.mtr_MotorOnBtnDisabled));
                isMotorOnBtn = false;
                motorOffBtn.setBackgroundColor(getColor(R.color.mtr_MotorOffBtnDisabled));
                isMotorOffBtn = false;
                updateTimeBtn.setBackgroundColor(getColor(R.color.mtr_SetTimeBtnDisabled));
                isUpdateTimeBtn = false;
            }
        }

    }

    private void sendRequest(boolean mode, String request){
        Random random = new Random();
        Toast.makeText(this, "Connecting...", Toast.LENGTH_LONG).show();
        SmsManager sms = SmsManager.getDefault();
        requestID = String.format(Locale.getDefault(),"%04d", random.nextInt(1000));
        requestIDSv = requestID;
        String msgtemp;
        if(!mode) {
            msgtemp = "@STATE" + "#RID" + requestIDSv + "&GETDATA";
        } else {
            msgtemp = "@STATE" + "#RID" + requestIDSv + "&SETDATA=" + request;
        }
        sms.sendTextMessage(deviceNum, null, msgtemp, null, null);
        Toast.makeText(getApplicationContext(), "Connecting to Device. \n Request Sent!", Toast.LENGTH_LONG).show();
    }

    Handler handler = new Handler();
    Runnable runnable;
    final int delay = 1000;
    private void getData(boolean mode/*Get Data Request: False, Set Data Response: True*/) {
        setButtonState("all", false);
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, delay);

            Toast t = Toast.makeText(this, "Waiting for Response...", Toast.LENGTH_LONG);
            t.show();

            checkSms getSms = new checkSms();
            String smsData;
            smsData = getSms.getSms(this, requestIDSv, deviceNum);
            if(smsData != null) {
                if(smsData.contains("@STATE")){
                    String tempRid;
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
                            if (smsData.contains("%TIME:")) {
                                timeLeft = "Time Left:" + smsData.substring(smsData.indexOf("%TIME:") +6, smsData.indexOf("%TIME:") + 8) + " Hr(s), "
                                        + smsData.substring(smsData.indexOf("%TIME:") + 9, smsData.indexOf("%TIME:") + 11) + " Min(s).";
                                timeLeftText.setText(timeLeft);
                                timeLeftText.setVisibility(View.VISIBLE);
                            } else if (!smsData.contains("%TIME:")){
                                timeLeftText.setVisibility(View.INVISIBLE);
                                updateTimeBtn.setText(getString(R.string.mtr_SetTimeBtn));
                            }
                            if (mode) {
                                if(smsData.contains("REQOK")) {
                                    Toast.makeText(this, "Reqeust Successfull", Toast.LENGTH_LONG).show();
                                }
                            }
                            if(motorState != null){
                                if(motorState.contains("RDY")){
                                    motorStatusValue.setText(getString(R.string.mtr_idle));
                                    motorStatusValue.setTextColor(getColor(R.color.mtr_idle));
                                    setButtonState("motoron,refresh,timer", true);
                                } else if(motorState.contains("RUN")){
                                    motorStatusValue.setText(getString(R.string.mtr_running));
                                    motorStatusValue.setTextColor(getColor(R.color.mtr_ok));
                                    setButtonState("motoroff,refresh,timer", true);
                                } else if(motorState.contains("ALM")){
                                    motorStatusValue.setText(getString(R.string.mtr_alarm));
                                    motorStatusValue.setTextColor(getColor(R.color.mtr_error));
                                    setButtonState("refresh", true);
                                }
                            }
                            t.cancel();

                        }

                        handler.removeCallbacks(runnable);
                    }
                }
            }
        },delay);
    }
}

