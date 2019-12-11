package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Conversation {
    @SerializedName( "uuid" )
    public String UUID;

    @SerializedName( "user_uuid" )
    public  String user_uuid;

    @SerializedName( "thread_name" )
    public  String name;

    @SerializedName( "create_at" )
    public Long createAt;

    @SerializedName ("last_msg_at")
    public Long lastMessageAt;

    @SerializedName( "users" )
    public List<UserInfo> users;

    @SerializedName("last_message")
    public String lastMessage;

    public Conversation(String UUID, String user_uuid, String name) {
        this.UUID = UUID;
        this.user_uuid = user_uuid;
        this.name = name;
    }
}
