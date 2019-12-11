package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

public class AuthenResponse {
    @SerializedName("refreshToken")
    public String refreshToken;
    @SerializedName("token")
    public String token;
    @SerializedName("duringTime")
    public long duringTime;

    public AuthenResponse(String refreshToken, String token, long duringTime) {
        this.refreshToken = refreshToken;
        this.token = token;
        this.duringTime = duringTime;
    }
}

