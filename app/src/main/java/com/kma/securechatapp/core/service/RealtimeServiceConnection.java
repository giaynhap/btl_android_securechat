package com.kma.securechatapp.core.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.kma.securechatapp.core.MessageCommand;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.event.EventQrCodeMessage;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class RealtimeServiceConnection {
    ServiceConnection mSocketServiceConnection;
    RealtimeService mService;
    boolean mBound = false;
    boolean callRestart = false;
    boolean callConnect = false;
    private static RealtimeServiceConnection instance;

    public static RealtimeServiceConnection getInstance() {
        if (instance == null) instance = new RealtimeServiceConnection();
        return instance;
    }
    public void bindService(Context context){
        try {
            Intent intent = new Intent(context, RealtimeService.class);
            context.bindService(intent, mSocketServiceConnection, Context.BIND_AUTO_CREATE);
        }catch (Exception e){

        }
    }
    public RealtimeServiceConnection() {

        mSocketServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                RealtimeService.LocalBinder binder  = (RealtimeService.LocalBinder) iBinder;
                RealtimeServiceConnection.this.mService = binder.getService();
                RealtimeServiceConnection.this.mBound = true;
                if (RealtimeServiceConnection.this.callConnect ){
                    RealtimeServiceConnection.this.mService.connectSocket();
                }
                if (RealtimeServiceConnection.this.callRestart ){
                    RealtimeServiceConnection.this.mService.resStart();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mService.disconnectSocket();

                mBound = false;
                mService = null;
            }
        };


    }

    public void onlyDisconnectSocket() {
        if (mService != null) {
            mService.disconnectSocket();
        }
    }


    public void onlyConnectSocket() {
        if (mService != null) {
            mService.connectSocket();
        }
    }
    public void connectBeforLogin(String hash,String code,EventQrCodeMessage e){
        mService.connectBeforLogin(hash,code,e);
    }

    public Disposable  subscribeLoginQRTopic(String hash, EventQrCodeMessage e){
       return  mService.subscribeLoginQRTopic(hash,e);
    }

    public void unsubscribeLoginQrTopic(Disposable disposable){
          mService.unsubcribeLoginQRTopic(disposable);
    }

    public void disconnectSocketService(Context context) {
        if (mSocketServiceConnection != null) {
            context.unbindService(mSocketServiceConnection);
        }
    }

    public boolean isConnected() {
        return mBound && mService != null;
    }
    public void restart(){
        callRestart = true;
        if (this.mService != null) {
            this.mService.resStart();

        }
    }
    public void startService(){
        callConnect = true;
        if (this.mService != null) {
            this.mService.connectSocket();
        }
    }
    public boolean sendStatus(MessageCommand command, int type, String thread)
    {
        if (this.mService != null) {
            return mService.sendStatusMessage(command,type, thread);
        }
        return false;
    }
    public boolean send(int type,String message, String thread, byte[]key ){
        if (this.mService != null) {
            return mService.sendMessage(type, message, thread, null, key);
        }
        return false;
    }


    public void registThreadToSoft(String uuid){
        mService.registThreadToSoft(uuid);
    }
    public void unRegistThreadToSoft(String uuid){
        mService.unRegistThreadToSoft(uuid);
    }
    public void clearRegistTrheadToSoft(){
        mService.clearRegistTrheadToSoft();
    }
}
