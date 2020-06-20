package com.kma.securechatapp.core.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
    Boolean b = false;
    String token;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();
            token=messageBody.replaceAll("[^0-9]","");

            if(b==true) {
                mListener.messageReceived(token);
            }
            else
            {

            }
        }
    }
    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}