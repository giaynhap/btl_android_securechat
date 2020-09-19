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
    public RUserInfo sender;
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
        this.sender = new RUserInfo();
        this.sender.fromModel(con.sender);
    }

    @Override
    public MessagePlaneText toModel() {
        MessagePlaneText messagePlaneText = new MessagePlaneText();
        messagePlaneText.threadName = this.threadName;
        messagePlaneText.deviceCode = this.deviceCode;
        messagePlaneText.encrypted = this.encrypted;
        messagePlaneText.mesage = this.mesage;
        messagePlaneText.senderUuid = this.senderUuid;
        messagePlaneText.userUuid = this.userUuid;
        messagePlaneText.time = this.time;
        messagePlaneText.type = this.type;
        messagePlaneText.uuid = this.uuid;
        if (this.sender != null) {
            messagePlaneText.sender = this.sender.toModel();
        }

        return messagePlaneText;
    }

}
