package com.kma.securechatapp.core.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.MessageCommand;
import com.kma.securechatapp.core.api.model.Message;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.SocketMessageCommand;
import com.kma.securechatapp.core.api.model.UserCryMessage;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.security.SecureChatSystem;
import com.kma.securechatapp.ui.conversation.InboxActivity;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;


public class RealtimeService extends Service {

    private StompClient client;

    String TAG = "RealtimeService";
    private String token ;
    private String deviceId;
    private boolean regist = false;
    private List<String> listThreadRegist = new ArrayList<>();

    public class LocalBinder extends Binder {
        public RealtimeService getService() {
            return RealtimeService.this;
        }

    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        deviceId =  Settings.Secure.getString(this.getApplication().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        initSocket();

    }
    private boolean closeSocket = false;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void resStart(){
        if (client != null){
            disconnectSocket();
        }
        initSocket();
        connectSocket();
    }
    @SuppressLint("CheckResult")
    void initSocket() {
        token = DataService.getInstance(this).getToken();
        if (client != null){
            disconnectSocket();
        }

        regist = false;
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, BuildConfig.WS_FULL_PATH);
        client.withClientHeartbeat(1000).withServerHeartbeat(1000);

        client.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "initSocket: OPENED" + lifecycleEvent.getMessage());
                            break;
                        case ERROR:
                            Log.d(TAG, "initSocket: ERROR" + lifecycleEvent.getException());
                            if (!closeSocket) {
                                initSocket();
                            }
                            break;
                        case CLOSED:
                            Log.d(TAG, "initSocket: CLOSED" + lifecycleEvent.getMessage());
                            if (!closeSocket) {
                                initSocket();
                            }
                            break;
                    }
                });

    }

    @SuppressLint("CheckResult")
    public void connectSocket() {
        if (!client.isConnected() && !closeSocket) {

            String username =DataService.getInstance(this.getApplication()).getUserUuid();
            String channel = "/topic/" + username;
            if (!regist) {
                client.topic(channel, getHeaders())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            String jsonString = response.getPayload();
                            Gson gson = new Gson();
                            SocketMessageCommand message = gson.fromJson(jsonString, SocketMessageCommand.class);
                            Log.d("Test", jsonString);
                            switch (message.getCommand()) {
                                case MESSAGE:
                                    MessagePlaneText planeMessage = SecureChatSystem.getInstance().decode(message.getData());
                                    if (checkThreadToSoft(planeMessage.threadUuid)) {
                                        sendBroadcastNewMessage(planeMessage);
                                    } else {
                                        createNotification(planeMessage);
                                    }
                                    break;
                                case READ:
                                    sendBroadcastStatus(ServiceAction.REVC_READ, message.getData().uuid, message.getData().senderUuid, 1);
                                    break;
                                case TYPING:
                                    sendBroadcastStatus(ServiceAction.REVC_TYPING, message.getData().uuid, message.getData().senderUuid, message.getData().type);
                                    break;
                            }

                        });
                regist = true;
            }
            client.connect(getHeaders());

        }
    }
    public  List<StompHeader> getHeaders(){
        token = DataService.getInstance(this).getToken();

        List<StompHeader> headers = new ArrayList<>();
        headers.add( new StompHeader("X-Authorization",token));
        return headers;
    }

    public void disconnectSocket() {
        if (client.isConnected()){
            client.disconnect();

        }
    }
    void sendBroadcastNewMessage(MessagePlaneText message) {
        Intent intent = new Intent();
        intent.setAction(ServiceAction.REVC_MESSAGE);
        intent.putExtra("data", new Gson().toJson(message));
        sendBroadcast(intent);
    }
    void sendBroadcastStatus(String type,String uuid,String useruuid, int status) {
        Intent intent = new Intent();
        intent.setAction(type);
        intent.putExtra("data", status);
        intent.putExtra("uuid", uuid);
        intent.putExtra("useruuid", useruuid);
        sendBroadcast(intent);
    }
    void createNotification(MessagePlaneText message){
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);



        Intent notificationIntent = new Intent(this, InboxActivity.class);
        notificationIntent
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent
                .getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New message from "+message.conversation.name)
                        .setContentText(message.mesage)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setPriority(6)
                        .setVibrate(new long[]{1000,1000,1000})
                        .setContentIntent(contentIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "chanel_001";
            CharSequence name = "security_chat";
            String Description = "security notification chanel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
            builder.setChannelId(CHANNEL_ID);
        }
        notificationManager.notify(1, builder.build());
    }
    public boolean sendStatusMessage(MessageCommand status,int type,String thread){
        if (client == null || !client.isConnected())
        {
            resStart();
            return false;
        }
        Message plt = new Message();
        plt.type = type;
        plt.threadUuid = thread;
        plt.deviceCode = deviceId;


        List<StompHeader> headers = getHeaders();
        headers.add(new StompHeader(StompHeader.DESTINATION, "/queue/send"));

        SocketMessageCommand  command = new SocketMessageCommand();

        command.setCommand(status);
        command.setData(plt);
        String jsonMessage = new Gson().toJson(command);

        StompMessage stompMessage = new StompMessage(
                StompCommand.SEND,headers,jsonMessage);

        client.send(stompMessage)
                .compose(applySchedulers())
                .subscribe(
                        () -> Log.d(TAG, "Sent data!"),
                        error -> Log.e(TAG, "Error", error)
                );

        return true;
    }

    @SuppressLint("CheckResult")
    public boolean sendMessage(String message, String thread, String deviceCode, List<UserInfo> users){
        if (deviceCode == null){
            deviceCode = deviceId;
        }
        if (client == null || !client.isConnected())
        {
          client.reconnect();
            return false;
        }
        MessagePlaneText plt = new MessagePlaneText();
        plt.type = 0;
        plt.threadUuid = thread;
        plt.deviceCode = deviceCode;
        plt.mesage = message;
        List<UserCryMessage> msgs = new ArrayList<>();
        // ma ho voi tung user
        for (UserInfo u : users){
            String encryptTxt = SecureChatSystem.getInstance().encode(message, u.getPublicKey() );
            UserCryMessage um = new UserCryMessage();
            um.message = encryptTxt;
            um.uuid = u.uuid;
            msgs.add(um);
        }

        Message encMessage =  SecureChatSystem.getInstance().encode(plt);
        encMessage.payload = new Gson().toJson(msgs);

        SocketMessageCommand  command = new SocketMessageCommand();
        command.setCommand(MessageCommand.MESSAGE);
        command.setData(encMessage);
        String jsonMessage = new Gson().toJson(command);

        List<StompHeader> headers = getHeaders();
        headers.add(new StompHeader(StompHeader.DESTINATION, "/queue/send"));
        StompMessage stompMessage = new StompMessage(
                StompCommand.SEND,headers,jsonMessage);

        client.send(stompMessage)
                .compose(applySchedulers())
                .subscribe(
                    () -> Log.d(TAG, "Sent data!"),
                    error -> Log.e(TAG, "Error", error)
                );
        return true;
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void registThreadToSoft(String uuid){
        listThreadRegist.add(uuid);
    }
    public void unRegistThreadToSoft(String uuid){
        listThreadRegist.remove(uuid);
    }
    public void clearRegistTrheadToSoft(){
        listThreadRegist.clear();
    }
    public  boolean checkThreadToSoft(String uuid){
        for ( String m : listThreadRegist){
            if (m.equals(uuid)){
                return true;
            }
        }
        return false;

    }

}
