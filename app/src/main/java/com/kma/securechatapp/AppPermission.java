package com.kma.securechatapp;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECEIVE_SMS;

public class AppPermission {
    public static void requireAll(Activity context){
        requestSmsPermission(context);
        requestFileStorePermission(context);
    }
    public static void requestSmsPermission(Activity context) {
        String permission = RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(context.getApplicationContext(), permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(context, permission_list, 1);
        }
    }
    public static  void requestFileStorePermission(Activity context){
        if (
                ContextCompat.checkSelfPermission(context.getApplicationContext(), READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        ||
                        ContextCompat.checkSelfPermission(context.getApplicationContext(), INTERNET)
                                != PackageManager.PERMISSION_GRANTED
                        ||
                        ContextCompat.checkSelfPermission(context.getApplicationContext(), RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED
                        ||
                        ContextCompat.checkSelfPermission(context.getApplicationContext(), CAMERA)
                                != PackageManager.PERMISSION_GRANTED

        ) {
            ActivityCompat.requestPermissions(context, new String[]
                    {
                            CAMERA,
                            INTERNET,
                            RECORD_AUDIO,
                            Context.NETWORK_STATS_SERVICE,
                            READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE

                    }, 1);

        }
    }



}
