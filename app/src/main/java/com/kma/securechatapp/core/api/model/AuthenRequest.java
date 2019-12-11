package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

public class AuthenRequest{


    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;

    @SerializedName("token")
    public String token;

    @SerializedName("device")
    public Device device;

    public AuthenRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}