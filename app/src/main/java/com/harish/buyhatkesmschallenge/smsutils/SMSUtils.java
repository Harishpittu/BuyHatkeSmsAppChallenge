package com.harish.buyhatkesmschallenge.smsutils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.harish.buyhatkesmschallenge.models.SMSObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by harish on 12/09/16.
 */
public class SMSUtils {

    private Context context;

    public SMSUtils(Context context) {
        this.context = context;
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
//            Toast.makeText(context, "Message Sent",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public ArrayList<SMSObject> getSMSWithNumber(String number) {

        ArrayList<SMSObject> smsObjectArrayList = new ArrayList<>();
        Uri inboxURI = Uri.parse("content://sms/");

        String[] reqCols = new String[]{"_id", "address", "body", "date", "status", "subject", "person", "reply_path_present"};

        ContentResolver cr = context.getContentResolver();
        String condition = "address = " + number + "||" + "address = " +"+91"+number;

        Cursor cursor = cr.query(inboxURI, reqCols, condition, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("address")).replace("+91", "");
                String msg = cursor.getString(cursor.getColumnIndex("body"));
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                String subject = cursor.getString(cursor.getColumnIndex("subject"));
                String person = cursor.getString(cursor.getColumnIndex("person"));
                String isSend = cursor.getString(cursor.getColumnIndex("reply_path_present"));

                SMSObject smsObject = new SMSObject();
                smsObject.setId(Integer.parseInt(id));
                smsObject.setName(name);
                if (TextUtils.isEmpty(isSend)) {
                    smsObject.setIsSend(1);
                } else {
                    smsObject.setIsSend(0);
                }
                smsObject.setDate(date);
                smsObject.setMsg(msg);

                smsObjectArrayList.add(smsObject);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return smsObjectArrayList;
    }

    public HashMap<String, ArrayList<SMSObject>> getSMS() {

        HashMap<String, ArrayList<SMSObject>> hashMapList = new HashMap<>();
        Uri inboxURI = Uri.parse("content://sms/");

        String[] reqCols = new String[]{"_id", "address", "body", "date", "status", "subject", "person", "reply_path_present"};

        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(inboxURI, reqCols, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("address")).replace("+91", "");
                String msg = cursor.getString(cursor.getColumnIndex("body"));
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                String subject = cursor.getString(cursor.getColumnIndex("subject"));
                String person = cursor.getString(cursor.getColumnIndex("person"));
                String isSend = cursor.getString(cursor.getColumnIndex("reply_path_present"));

                SMSObject smsObject = new SMSObject();
                smsObject.setId(Integer.parseInt(id));
                smsObject.setName(name);
                if (TextUtils.isEmpty(isSend)) {
                    smsObject.setIsSend(1);
                } else {
                    smsObject.setIsSend(0);
                }
                smsObject.setDate(date);
                smsObject.setMsg(msg);

//                Log.d("address - body--->", id +"#####"+reply_path_present+ "###"+person+"###" + address + "###" + date + "###" + status + "###" + subject + "###" + body);

                if (hashMapList.containsKey(name)) {
                    hashMapList.get(name).add(smsObject);
                } else {
                    ArrayList<SMSObject> smsObjectArrayList = new ArrayList<>();
                    smsObjectArrayList.add(smsObject);
                    hashMapList.put(name, smsObjectArrayList);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        return hashMapList;
    }

    public ArrayList<SMSObject> getAllSMSForBackUp() {

        ArrayList<SMSObject> smsObjectArrayList = new ArrayList<>();
        Uri inboxURI = Uri.parse("content://sms/");

        String[] reqCols = new String[]{"_id", "address", "body", "date", "status", "subject", "person", "reply_path_present"};

        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(inboxURI, reqCols, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("address")).replace("+91", "");
                String msg = cursor.getString(cursor.getColumnIndex("body"));
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                String subject = cursor.getString(cursor.getColumnIndex("subject"));
                String person = cursor.getString(cursor.getColumnIndex("person"));
                String isSend = cursor.getString(cursor.getColumnIndex("reply_path_present"));

                SMSObject smsObject = new SMSObject();
                smsObject.setId(Integer.parseInt(id));
                smsObject.setName(name);
                if (TextUtils.isEmpty(isSend)) {
                    smsObject.setIsSend(1);
                } else {
                    smsObject.setIsSend(0);
                }
                smsObject.setDate(date);
                smsObject.setMsg(msg);

//                Log.d("address - body--->", id +"#####"+reply_path_present+ "###"+person+"###" + address + "###" + date + "###" + status + "###" + subject + "###" + body);

                smsObjectArrayList.add(smsObject);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return smsObjectArrayList;
    }


//SMS Query Parameters

    //    0: _id
//    1: thread_id
//    2: address
//    3: person
//    4: date
//    5: protocol
//    6: read
//    7: status
//    8: type
//    9: reply_path_present
//    10: subject
//    11: body
//    12: service_center
//    13: locked
}
