package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessagePlaneText implements Serializable  {
    @SerializedName("uuid")
    public String uuid;

    @SerializedName("type")
    public int type;

    @SerializedName("device_code")
    public String deviceCode;

    @SerializedName("user_uuid")
    public String userUuid;

    @SerializedName("thread_uuid")
    public String threadUuid;

    @SerializedName("time")
    public Long time;

    @SerializedName("message")
    public String mesage;

    @SerializedName("sender_uuid")
    public String senderUuid;

    @SerializedName("sender")
    public UserInfo sender;

    @SerializedName("thread_name")
    public  String threadName ;

    @SerializedName("encrypted")
    public  boolean encrypted;

    public byte[] password;

}
