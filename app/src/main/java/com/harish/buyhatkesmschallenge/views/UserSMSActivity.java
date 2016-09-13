package com.harish.buyhatkesmschallenge.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.harish.buyhatkesmschallenge.R;
import com.harish.buyhatkesmschallenge.adapters.MessagesRecyclerAdapter;
import com.harish.buyhatkesmschallenge.models.SMSObject;
import com.harish.buyhatkesmschallenge.smsutils.SMSUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSMSActivity extends AppCompatActivity {

    private SMSUtils smsUtils;
    private RecyclerView recyclerView;
    private EditText etSMSText;
    private TextView tvName;
    private EditText etNumber;
    private String mobileNo;
    private List<SMSObject> smsObjectList = new ArrayList<>();
    private MessagesRecyclerAdapter messagesRecyclerAdapter;
    private boolean isEnteredNewMobileNumber = Boolean.FALSE;
    private  RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sms);
        init();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        etSMSText = (EditText) findViewById(R.id.smsText);
        etNumber = (EditText) findViewById(R.id.number);
        tvName = (TextView) findViewById(R.id.name);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        smsUtils = new SMSUtils(getApplicationContext());


        mobileNo = getIntent().getStringExtra(AppConstants.MOBILE_NUMBER);
        smsObjectList = (ArrayList<SMSObject>) getIntent().getSerializableExtra(AppConstants.MESSAGES);


        if(smsObjectList==null)
        {
            smsObjectList = new ArrayList<>();
        }

        if(TextUtils.isEmpty(mobileNo))
        {
            tvName.setVisibility(View.INVISIBLE);
            etNumber.setVisibility(View.VISIBLE);
            isEnteredNewMobileNumber = Boolean.TRUE;
        }
        else {
            tvName.setText(mobileNo);
            etNumber.setVisibility(View.GONE);
        }
        Collections.reverse(smsObjectList);

        initOrUpdateMessages();

    }

    private void initOrUpdateMessages() {


        if (messagesRecyclerAdapter != null) {
            messagesRecyclerAdapter.notifyDataSetChanged();
        } else {
            messagesRecyclerAdapter = new MessagesRecyclerAdapter(this, smsObjectList);
            recyclerView.setAdapter(messagesRecyclerAdapter);
        }

        mLayoutManager.scrollToPosition(smsObjectList.size()-1);
    }

    public void sendSMS(View v) {

        if(isEnteredNewMobileNumber)
        {
            mobileNo = etNumber.getText().toString();
        }

        if (!TextUtils.isEmpty(etSMSText.getText().toString())&&!TextUtils.isEmpty(mobileNo)) {
            smsUtils.sendSMS(mobileNo, etSMSText.getText().toString());
        }


        SMSObject smsObject = new SMSObject();
        smsObject.setName(mobileNo);
        smsObject.setIsSend(1);
        smsObject.setMsg(etSMSText.getText().toString());

        smsObjectList.add(smsObject);
        initOrUpdateMessages();
        etSMSText.setText("");


    }

    @Override
    protected void onResume() {

        // Register reciever if activity is in front
         IntentFilter filter = new IntentFilter("sms.action.receivedsms");
        this.registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {

        // Unregister reciever if activity is not in front
        this.unregisterReceiver(receiver);
        super.onPause();
    }


    private BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context c, Intent i) {

              if(i.getStringExtra("name").toLowerCase().replace("+91","").matches(mobileNo))
              {
                  SMSObject smsObject = new SMSObject();
                  smsObject.setName(mobileNo);
                  smsObject.setMsg(i.getStringExtra("msg"));
                  smsObject.setIsSend(0);
                  smsObjectList.add(smsObject);
                  initOrUpdateMessages();
              }

        }
    };
}
