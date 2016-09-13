package com.harish.buyhatkesmschallenge.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.harish.buyhatkesmschallenge.R;
import com.harish.buyhatkesmschallenge.adapters.SearchMessagesRecyclerAdapter;
import com.harish.buyhatkesmschallenge.models.SMSObject;
import com.harish.buyhatkesmschallenge.smsutils.SMSUtils;

import java.util.ArrayList;
import java.util.List;

public class MessagesSearchActivity extends AppCompatActivity {

    private SMSUtils smsUtils;
    private RecyclerView recyclerView;
    private EditText etSearchText;
    private SearchMessagesRecyclerAdapter searchMessagesRecyclerAdapter;
private List<SMSObject> smsObjectList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_messages);
        init();

    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        etSearchText = (EditText) findViewById(R.id.searchtext);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        smsUtils = new SMSUtils(getApplicationContext());
        smsObjectList = smsUtils.getAllSMSForBackUp();


        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)) {
                    initOrUpdateRecyclerView(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initOrUpdateRecyclerView(String searchQuery) {
        List<SMSObject> smsObjectArrayList = new ArrayList<>();


        for (SMSObject smsObject:
        smsObjectList  ) {

            if(smsObject.getMsg().toLowerCase().contains(searchQuery.toLowerCase()))
            {
                smsObjectArrayList.add(smsObject);
            }

        }

        searchMessagesRecyclerAdapter = new SearchMessagesRecyclerAdapter(this, smsObjectArrayList);
        recyclerView.setAdapter(searchMessagesRecyclerAdapter);

    }


}
