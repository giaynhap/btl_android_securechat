package com.kma.securechatapp.core.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kma.securechatapp.core.security.AES;
import com.kma.securechatapp.core.security.RSAUtil;

public class DataService {
    private static final String SHARED_PREFERENCES_NAME = "store_msdnid";
    private static final String ACCESSTOKEN_KEY = "ACCESSTOKEN";
    private static final String REFRESHTOKEN_KEY = "REFRESHTOKEN";

    private static final String USER_KEY = "USER_UUID";
    private static final String USER_ACCOUNT= "USER_ACCOUNT";
    private static final String PRIVATE_KEY = "PRIVATE_KEY";
    private static final String SAVE_FINGER = "SAVE_FINGER";

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
    public void storeUserAccount(String account){

        editor.putString(USER_ACCOUNT,account);

    }

    public void storePrivateKey(String uuid,String privateKey,String password){
        byte[] encryptPrivateKey = AES.encrypt(privateKey, password);
        editor.putString(PRIVATE_KEY+uuid, RSAUtil.base64Encode(encryptPrivateKey));
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
           String privateKeyHex = sharedPreferences.getString(PRIVATE_KEY+uuid, null);
           byte[] privateKeyEncrypt =  RSAUtil.base64Decode(privateKeyHex);

           return AES.decrypt(privateKeyEncrypt, password);
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
    public String getUserAccount(){
        try {
            return sharedPreferences.getString(USER_ACCOUNT, null);
        }catch (Exception e){

        }
        return null;
    }

    public String getFingerSaved(String uuid){
        try {
            return sharedPreferences.getString(SAVE_FINGER+uuid, null);
        }catch (Exception e){

        }
        return null;
    }
    public void storeFingerPassword(String uuid, String password){
        editor.putString(SAVE_FINGER+uuid,password);
    }

}
