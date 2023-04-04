package com.nas.smartmotor;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

public class checkSms {
    String getSms(Context context, String rid, String devicenum) {
        if (devicenum != null) {
            Cursor c = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                c = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null);
            }
            assert c != null;
            c.moveToFirst();
            String tempnum = null;
            String tempbody = null;
            tempnum = c.getString(c.getColumnIndex("address"));
            tempbody = c.getString(c.getColumnIndex("body"));
            if (devicenum != null) {
                if (tempnum.contains(devicenum)) {
                    if (rid != null) {
                        if (tempbody.contains(rid)) {
                            return tempbody;
                        } else {
                            return "RequestIDMismatch";
                        }
                    } else {
                        return "NoNumber";
                    }
                } else {
                    return "NumberMismatch";
                }
            } else {
                return "NumberMismatch";
            }
        } else {
            return null;
        }
    }
}
