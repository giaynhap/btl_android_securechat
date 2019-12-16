package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message  implements Serializable {
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

    @SerializedName("encrypt")
    public Boolean encrypt;

    @SerializedName("payload")
    public String payload;

    @SerializedName("sender_uuid")
    public String senderUuid;

    @SerializedName("sender")
    public UserInfo sender;

    @SerializedName("thread_name")
    public  String threadName ;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getThreadUuid() {
        return threadUuid;
    }

    public void setThreadUuid(String threadUuid) {
        this.threadUuid = threadUuid;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(Boolean encrypt) {
        this.encrypt = encrypt;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSenderUuid() {
        return senderUuid;
    }

    public void setSenderUuid(String senderUuid) {
        this.senderUuid = senderUuid;
    }

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

}
