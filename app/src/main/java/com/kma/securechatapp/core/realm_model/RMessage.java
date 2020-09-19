package com.kma.securechatapp.core.realm_model;

import com.google.gson.annotations.SerializedName;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.UserInfo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RMessage  extends RealmObject implements  ChatRealmObject<MessagePlaneText> {

    @PrimaryKey
    public String uuid;
    public int type;
    public String deviceCode;
    public String userUuid;
    public String threadUuid;
    public Long time;
    public String mesage;
    public String senderUuid;
    public UserInfo sender;
    public  String threadName ;
    public  boolean encrypted;


    @Override
    public void fromModel(MessagePlaneText con) {
        this.uuid = con.uuid;
        this.type = con.type;
        this.deviceCode = con.deviceCode;
        this.userUuid = con.userUuid;
        this.threadUuid =con.threadUuid;
        this.time = con.time;
        this.mesage = con.mesage;
        this.senderUuid = con.senderUuid;
        this.threadName = con.threadName;
        this.encrypted = con.encrypted;
    }

    @Override
    public MessagePlaneText toModel() {
        return null;
    }

}
