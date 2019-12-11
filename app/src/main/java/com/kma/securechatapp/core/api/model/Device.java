package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName( "device_code" )
    public  String deviceCode;
    @SerializedName( "device_os" )
    public String deviceOs;

}
