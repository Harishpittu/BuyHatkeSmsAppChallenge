package com.harish.buyhatkesmschallenge.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harish.buyhatkesmschallenge.R;
import com.harish.buyhatkesmschallenge.models.SMSObject;

import java.util.List;

/**
 * Created by harish on 13/09/16.
 */
public class SearchMessagesRecyclerAdapter extends RecyclerView.Adapter<SearchMessagesRecyclerAdapter.MyViewHolder> {

    private List<SMSObject> smsObjectList;
    private Activity activity;

    public SearchMessagesRecyclerAdapter(Activity activity,List<SMSObject> smsObjectList) {
        this.smsObjectList = smsObjectList;
        this.activity = activity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvMessage;
        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.name);
            tvMessage = (TextView) view.findViewById(R.id.msg);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_sms_recycler_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvName.setText(smsObjectList.get(position).getName());
        holder.tvMessage.setText(smsObjectList.get(position).getMsg());
    }

    @Override
    public int getItemCount() {
        return smsObjectList.size();
    }
}
