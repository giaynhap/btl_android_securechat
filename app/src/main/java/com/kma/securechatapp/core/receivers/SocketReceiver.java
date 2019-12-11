package com.kma.securechatapp.core.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.service.ServiceAction;

public class SocketReceiver extends BroadcastReceiver {
    private OnSocketMessageListener listener;
    public interface OnSocketMessageListener {

        void onNewMessage(MessagePlaneText message);

        void onTyping(String conversationId, String userUuid, boolean typing);

        void onRead(String conversationId, String userUuid);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case ServiceAction.REVC_MESSAGE:
                handle_message(intent);
                break;
            case ServiceAction.REVC_TYPING:
                handle_typing(intent);
                break;
            case ServiceAction.REVC_READ:
                handle_read(intent);
                break;
            default:
                break;
        }

    }
    public void handle_read(Intent intent){
        String uuid = intent.getStringExtra("uuid");
        String userUuid = intent.getStringExtra("useruuid");

        if (listener != null){
            listener.onRead(uuid,userUuid);
        }
    }
    public void handle_typing(Intent intent){
        String uuid = intent.getStringExtra("uuid");
        String userUuid = intent.getStringExtra("useruuid");
        int type = intent.getIntExtra("data",0);
        if (listener != null){
            listener.onTyping(uuid,userUuid,type == 1);
        }
    }
    public void handle_message(Intent intent){
        String message =  intent.getStringExtra("data");
        MessagePlaneText plt = new Gson().fromJson(message,MessagePlaneText.class);

        if (listener != null){
            listener.onNewMessage(plt);
        }
    }


    public OnSocketMessageListener getListener() {
        return listener;
    }

    public void setListener(OnSocketMessageListener listener) {
        this.listener = listener;
    }
}
