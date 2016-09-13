package com.harish.buyhatkesmschallenge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harish.buyhatkesmschallenge.views.AppConstants;
import com.harish.buyhatkesmschallenge.R;
import com.harish.buyhatkesmschallenge.views.UserSMSActivity;
import com.harish.buyhatkesmschallenge.models.SMSObject;
import com.harish.buyhatkesmschallenge.models.SMSUserObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by harish on 13/09/16.
 */
public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.MyViewHolder> {

    private List<SMSUserObject> smsUserObjectList;
    private HashMap<String, ArrayList<SMSObject>> listHashMap;
    private Activity activity;

    public UsersRecyclerAdapter(Activity activity, List<SMSUserObject> smsUserObjectList, HashMap<String, ArrayList<SMSObject>> listHashMap) {
        this.smsUserObjectList = smsUserObjectList;
        this.listHashMap = listHashMap;
        this.activity = activity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvMessage;
        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.name);
            tvMessage = (TextView) view.findViewById(R.id.msg);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, UserSMSActivity.class);
                    intent.putExtra(AppConstants.MOBILE_NUMBER, smsUserObjectList.get(getAdapterPosition()).getName());
                    intent.putExtra(AppConstants.MESSAGES, listHashMap.get(smsUserObjectList.get(getAdapterPosition()).getName()));
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_user_recycler_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvName.setText(smsUserObjectList.get(position).getName());
        holder.tvMessage.setText(smsUserObjectList.get(position).getLastMsg());
    }

    @Override
    public int getItemCount() {
        return smsUserObjectList.size();
    }
}
