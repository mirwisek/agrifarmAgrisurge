package com.fyp.agrifarm.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NetworkChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(NetworkHelper.CHECK_INTERNET);
        in.putExtra(NetworkHelper.CHECK_INTERNET,
                NetworkHelper.isInternetConnected(context));
        LocalBroadcastManager.getInstance(context).sendBroadcast(in);
    }
}
