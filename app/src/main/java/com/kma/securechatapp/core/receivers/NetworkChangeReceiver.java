package com.kma.securechatapp.core.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.utils.common.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            EventBus.getInstance().pushOnNetworkStateChange(status);
        }
    }
}