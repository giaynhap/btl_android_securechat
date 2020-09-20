package com.kma.securechatapp.core.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.utils.common.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {
    Activity parent;

    public NetworkChangeReceiver(Activity a) {
        // TODO Auto-generated constructor stub
        parent = a;
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            EventBus.getInstance().pushOnNetworkStateChange(status);
        }
        Toast.makeText(context, "Network change", Toast.LENGTH_LONG).show();
    }
}