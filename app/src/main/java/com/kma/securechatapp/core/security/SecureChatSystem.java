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
    public List<MessagePlaneText> decoder(List<Message> messages){


        List<MessagePlaneText> messagePlaneTexts = new ArrayList<>();
        for (Message msg:messages){
            MessagePlaneText plt = decode(msg);
            messagePlaneTexts.add(plt);
        }
        return messagePlaneTexts;
    }

    public MessagePlaneText decode (Message message){
        if (message== null){
            return null;
        }
        MessagePlaneText result = new MessagePlaneText();
        result.conversation = message.conversation;
        result.deviceCode = message.deviceCode;
        result.mesage = decode(message.payload);
        result.sender = message.sender;
        result.senderUuid = message.senderUuid;
        result.threadUuid = message.threadUuid;
        result.time = message.time;
        result.type = message.type;
        result.uuid = message.uuid;
        result.userUuid = message.userUuid;

        return result;
    }

    public String decode (byte[] payload){

        return new String(payload);
    }
    public String decode (String payload){
        List<UserCryMessage> msgs = new Gson().fromJson(payload,new TypeToken<List<UserCryMessage>>(){}.getType() );
        String msg = null;
        for (UserCryMessage m : msgs ){
           if ( m.uuid.equals(AppData.getInstance().currentUser.uuid) ){
               msg = m.message;
           }
        }
        try {
            return RSAUtil.RSADecrypt(RSAUtil.base64Decode(msg), AppData.getInstance().getPrivateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encode(String mesage, PublicKey publicKey){
        if (mesage == null){
            mesage = "";
        }
        try {
            return RSAUtil.base64Encode( RSAUtil.RSAEncrypt( mesage, publicKey ) );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Message encode(MessagePlaneText message){
        Message enc = new Message();

        enc.deviceCode = message.deviceCode;
        enc.conversation = message.conversation;
        enc.type = message.type;
        enc.userUuid = message.userUuid;
        enc.threadUuid = message.threadUuid;
        enc.time = message.time;
        enc.uuid = message.uuid;
        return enc;
    }

}
