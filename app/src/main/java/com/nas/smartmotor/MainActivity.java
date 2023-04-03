package com.nas.smartmotor;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.telephony.SmsManager;


import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class MainActivity extends AppCompatActivity implements MessageListener {
    private static final int PERMISSION_RQST_SEND = 0;
    private static final int READ_SMS_PERMISSION_CODE = 445566;
    EditText devicenum, phonenum, devicepin;
    Button register, help, buy;
    String msg,to;
    SharedPreferences deviceData;
    SharedPreferences.Editor deviceDataEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "Permission Granted!", Toast.LENGTH_LONG).show();
        } else{
            String[] permissionRequested={Manifest.permission.READ_SMS};
            requestPermissions(permissionRequested, READ_SMS_PERMISSION_CODE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MessageReceiver.bindListener(this);
        checkLogin();
        devicenum = (EditText) findViewById(R.id.loginDevicePhone);
        phonenum = (EditText) findViewById(R.id.loginPhone);
        devicepin = (EditText) findViewById(R.id.loginDevicePass);
        register = (Button) findViewById(R.id.loginRegisterBtn);
        help = (Button) findViewById(R.id.loginHelpBtn);
        buy = (Button) findViewById(R.id.loginBuyBtn);

        register.setOnClickListener(view -> {

            //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            //PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
            msg = "@LK" + devicepin.getText().toString() + "&" + phonenum.getText().toString();
            to = devicenum.getText().toString();

            checkPerms();
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(to, null, msg, null, null);
                deviceData = getSharedPreferences("LoginData", MODE_PRIVATE);
                deviceDataEditor = deviceData.edit();
                deviceDataEditor.putString("phonenum", phonenum.getText().toString());
                deviceDataEditor.commit();
                Intent i = new Intent(MainActivity.this,main_control.class);
                startActivity(i);
                finish();
                Toast.makeText(getApplicationContext(), "Msg Sent. Loading...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Missing Permissions.", Toast.LENGTH_LONG).show();
            }
            /*SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(to,null,msg,null,null);
            Toast.makeText(getApplicationContext(), "Message Sent! Waiting for Response!",
                    Toast.LENGTH_LONG).show();
            */
        });
        }
            protected void checkPerms() {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)) {
                    //do_Nothing
                    }
                    else { ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_RQST_SEND);
                    }
                }
            }
            @Override
            public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                switch (requestCode) {
                    case PERMISSION_RQST_SEND: {
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext(), "Permission Granted!, Sending Message.", Toast.LENGTH_LONG).show();
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(to, null, msg, null, null);
                            deviceData = getSharedPreferences("LoginData", MODE_PRIVATE);
                            deviceDataEditor = deviceData.edit();
                            deviceDataEditor.putString("phonenum", phonenum.getText().toString());
                            deviceDataEditor.commit();
                            Intent i = new Intent(MainActivity.this,main_control.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Missing Permission to Send SMS!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            @Override
            public void messageReceived(String message) {
                Toast.makeText(this, "New Message Received: " + message, Toast.LENGTH_SHORT).show();
            }
            public void checkLogin() {
                String phone1 = null;
                if(deviceData == null) {
                    deviceData = getSharedPreferences("LoginData", MODE_PRIVATE);
                    phone1 = deviceData.getString("phonenum", "");
                }
                if (phone1 != null && !phone1.equals("")) {
                    Toast.makeText(getApplicationContext(),"Welcome " + phone1, Toast.LENGTH_LONG).show();
                       Intent i = new Intent(MainActivity.this, main_control.class);
                       startActivity(i);
                       finish();
                    }
                }

}
