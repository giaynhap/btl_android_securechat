package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class UserConversation {
    @SerializedName("user_uuid")
    public String userUuid;

    @SerializedName("key")
    public String key;

    @SerializedName("last_seen")
    public Long lastSeen;
}
