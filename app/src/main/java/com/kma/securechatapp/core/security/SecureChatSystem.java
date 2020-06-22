package com.kma.securechatapp.core.security;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.model.Message;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.UserCryMessage;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class SecureChatSystem {
    private static SecureChatSystem _instance;
    public static SecureChatSystem getInstance(){
        if (_instance == null){
            _instance = new SecureChatSystem();
        }
        return _instance;
    }
    public List<MessagePlaneText> decoder(List<Message> messages,byte []key){


        List<MessagePlaneText> messagePlaneTexts = new ArrayList<>();
        for (Message msg:messages){
            MessagePlaneText plt = decode(msg,key);
            messagePlaneTexts.add(plt);
        }
        return messagePlaneTexts;
    }

    public MessagePlaneText decode (Message message,byte[]key){
        if (message== null){
            return null;
        }

        MessagePlaneText result = new MessagePlaneText();
        result.threadName = message.threadName;
        result.deviceCode = message.deviceCode;
        if (key != null) {
            result.mesage = decode(message.payload, key);
            result.encrypted = false;
        }else{
            result.encrypted = true;
            result.mesage = message.payload;
        }
        result.sender = message.sender;
        result.senderUuid = message.senderUuid;
        result.threadUuid = message.threadUuid;
        result.time = message.time;
        result.type = message.type;
        result.uuid = message.uuid;
        result.userUuid = message.userUuid;
        result.password = key;

        return result;
    }

    public String decode (byte[] payload){

        return new String(payload);
    }
    public String decode (String payload, byte[] key){

        try {
           return AES.decrypt(RSAUtil.base64Decode(payload),key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encode(String mesage, byte[] key){
        if (mesage == null){
            mesage = "";
        }
        try {
            return RSAUtil.base64Encode( AES.encrypt( mesage, key ) );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Message encode(MessagePlaneText message,byte[]key){
        Message enc = new Message();

        enc.deviceCode = message.deviceCode;
        enc.threadName = message.threadName;
        enc.type = message.type;
        enc.payload = encode(message.mesage,key);
        enc.userUuid = message.userUuid;
        enc.threadUuid = message.threadUuid;
        enc.time = message.time;
        enc.uuid = message.uuid;
        return enc;
    }

}
