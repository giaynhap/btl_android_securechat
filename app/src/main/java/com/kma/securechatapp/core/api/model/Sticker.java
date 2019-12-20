package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

public class Sticker {
    @SerializedName("name")
    public String name;
    @SerializedName("model")
    public String model;
    @SerializedName("col")
    public int col;
    @SerializedName("row")
    public int row;
    @SerializedName("width")
    public int width;
    @SerializedName("height")
    public int height;
    @SerializedName("num")
    public int num;
}
