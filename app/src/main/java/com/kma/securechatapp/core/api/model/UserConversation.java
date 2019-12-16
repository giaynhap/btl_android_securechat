package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class UserConversation {
    @SerializedName("userUuid")
    public String userUuid;

    @SerializedName("key")
    public String key;

    @SerializedName("lastSeen")
    public Long lastSeen;
}
