package com.kma.securechatapp.core.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DataService {
    private static final String SHARED_PREFERENCES_NAME = "store_msdnid";
    private static final String ACCESSTOKEN_KEY = "ACCESSTOKEN";
    private static final String REFRESHTOKEN_KEY = "REFRESHTOKEN";
    private static final String USER_KEY = "USERTOKEN";
    private static final String PRIVATE_KEY = "PRIVATE_KEY";
    private SharedPreferences sharedPreferences;
    private static DataService instance = null;
    SharedPreferences.Editor editor = null;
    public static DataService getInstance(Context context ){
        if (instance == null){
            instance = new DataService();

            instance.init (context);

        }
        return instance;
    }
    void init(Context conn){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(conn);
        editor = sharedPreferences.edit();
    }

    public void storeToken(String accessToken,String refreshToken){

        editor.putString(ACCESSTOKEN_KEY,accessToken);
        editor.putString(REFRESHTOKEN_KEY,refreshToken);

    }
    public void storeUserUuid(String userUuid){

        editor.putString(USER_KEY,userUuid);

    }

    public void storePrivateKey(String uuid,String privateKey,String password){
        editor.putString(PRIVATE_KEY+uuid,privateKey);
    }
    public void save(){
        editor.apply();
    }
    public String getToken(){
        try {
            return sharedPreferences.getString(ACCESSTOKEN_KEY, null);
        }catch (Exception e){

        }
        return null;
    }

    public String getRefreshtoken(){
        try {
            return sharedPreferences.getString(REFRESHTOKEN_KEY, null);
        }catch (Exception e){

        }
        return null;
    }

    public String getPrivateKey(String uuid,String password){
        try {
            return sharedPreferences.getString(PRIVATE_KEY+uuid, null);
        }catch (Exception e){

        }
        return null;
    }
    public String getUserUuid(){
        try {
            return sharedPreferences.getString(USER_KEY, null);
        }catch (Exception e){

        }
        return null;
    }
}
