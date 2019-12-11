package com.kma.securechatapp.core.service;

public class CacheService {

    private static CacheService instance;
    public static  CacheService getInstance(){
        if (instance == null){
            instance = new CacheService();
        }
        return instance;
    }
    public void getUserPublicKey(String uuid){

    }
}

