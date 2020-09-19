package com.kma.securechatapp.core.realm_model;

import com.google.gson.annotations.SerializedName;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.UserConversation;
import com.kma.securechatapp.core.api.model.UserInfo;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RConversation extends RealmObject implements  ChatRealmObject<Conversation> {
    @PrimaryKey
    public String UUID;

    public  String user_uuid;

    public  String name;

    public Long createAt;

    public Long lastMessageAt;

    public String lastMessage;

    public Integer unRead;

    public byte[] password;

    @Override
    public  void fromModel(Conversation con){
        this.UUID = con.UUID;
        this.unRead = con.unRead;
        this.createAt = con.createAt;
        this.lastMessage = con.lastMessage;
        this.lastMessageAt = con.lastMessageAt;
        this.name = con.name;
        this.user_uuid = con.user_uuid;
    }

    @Override
    public Conversation toModel(){
        Conversation con = new Conversation(this.UUID,this.user_uuid,this.name);
        con.createAt = this.createAt;
        con.lastMessage = this.lastMessage;
        con.lastMessageAt = this.lastMessageAt;
        con.unRead = this.unRead;
        return con;
    }

}
