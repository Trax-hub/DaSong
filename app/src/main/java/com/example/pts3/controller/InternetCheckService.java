package com.example.pts3.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.pts3.model.NetworkUtil;

public class InternetCheckService extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
       if (!NetworkUtil.isConnectedToInternet(context)){
           context.startActivity(new Intent(context, NoConnectionActivity.class));
       }
    }
}
