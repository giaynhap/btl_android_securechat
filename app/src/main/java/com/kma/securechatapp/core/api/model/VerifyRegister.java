package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VerifyRegister implements Serializable {

    @SerializedName( "username")
    public String username;
    @SerializedName( "password")
    public String password;

    @SerializedName( "uuid")
    public String uuid;

    @SerializedName( "opt")
    public String opt;

    @SerializedName("device_code")
    public String deviceCode;


}
