package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;
import com.kma.securechatapp.core.AppData;

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

    @SerializedName( "userConversations" )
    public List<UserConversation> userConversations;

    @SerializedName("last_message")
    public String lastMessage;

    @SerializedName("un_read")
    public Integer unRead;

    public Conversation(String UUID, String user_uuid, String name) {
        this.UUID = UUID;
        this.user_uuid = user_uuid;
        this.name = name;
    }

    public boolean isOnline(){
        if ( AppData.getInstance().currentUser == null || users == null || users.size()<1)
            return false;

        String uuid = AppData.getInstance().currentUser.uuid;
        for (UserInfo u : users){
            if (!u.uuid.equals(uuid) && u.online ){
                return true;
            }
        }

        return false;
    }
    @Override
    public boolean equals(Object o){
       try{
           Conversation c = (Conversation)o;
           return c.UUID.equals(UUID);
       }catch (Exception e){
           return false;
       }
    }
    public String getKey(String uuid){
        if (userConversations == null)
            return null;
        for (UserConversation u : userConversations){
            if (u.userUuid.equals(uuid)){
                if (u.key == null || u.key.isEmpty()){
                    return null;
                }
                return u.key;
            }
        }
        return null;
    }
}

