package com.kma.securechatapp.core.realm_model;

import com.google.gson.annotations.SerializedName;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.UserInfo;

import java.security.PublicKey;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RUserInfo extends RealmObject implements  ChatRealmObject<UserInfo>  {
    @PrimaryKey
    public String uuid;
    public String userName;
    public String name;
    public String address;
    public Long dob;
    public String publicKey;
    public String phone;
    public boolean online;

    @Override
    public void fromModel(UserInfo con) {
        this.uuid = con.uuid;
        this.name = con.name;
        this.address = con.address;
        this.dob = con.dob;
        this.publicKey = con.publicKey;
        this.phone = con.phone;
        this.online = con.online;
    }

    @Override
    public UserInfo toModel() {
        UserInfo userInfo  = new UserInfo(this.uuid,this.name,this.address,this.dob);
        userInfo.online = this.online;
        userInfo.publicKey = this.publicKey;
        userInfo.phone = this.phone;
        return userInfo;
    }
}
