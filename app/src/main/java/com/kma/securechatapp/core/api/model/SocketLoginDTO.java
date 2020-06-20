package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;
import com.kma.securechatapp.core.MessageCommand;
import com.kma.securechatapp.core.SocketLoginCommand;

public class SocketLoginDTO {

    @SerializedName("command")
    SocketLoginCommand command;
    @SerializedName("data")
    private String data;

    public SocketLoginCommand getCommand() {
        return command;
    }

    public void setCommand(SocketLoginCommand command) {
        this.command = command;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
