package com.kma.securechatapp.core;

import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.api.model.UserKey;
import com.kma.securechatapp.core.security.RSAUtil;
import com.kma.securechatapp.core.service.DataService;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

public class AppData {
     private String token = null;
     private String refreshToken;
     public String account = null;
     public String deviceId = null;
     public UserKey userKey = null;
     public String password = "md5";
     private PrivateKey privateKey = null;


     public UserInfo currentUser;
     private static AppData _instance = null;
     private AppData(){

     }
     public static AppData getInstance(){
            if (_instance == null)
                _instance = new AppData();
            return _instance;
     }

     public String getToken(){
         return token;
     }
     public void setToken(String token){
         this.token = token;
     }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public PrivateKey getPrivateKey(){

         if ( privateKey == null){
             try {
                 privateKey = RSAUtil.stringToPrivateKey(userKey.privateKey);
             } catch (NoSuchAlgorithmException e) {
                 e.printStackTrace();
             } catch (InvalidKeySpecException e) {
                 e.printStackTrace();
             }
         }
         return privateKey;
    }
    public void clean(){
        this.userKey = null;
        this.token = null;
        this.privateKey = null;
        this.currentUser = null;
        this.account = null;
        this.refreshToken = null;
    }
}
