package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;
import com.kma.securechatapp.core.MessageCommand;

import java.io.Serializable;

public class SocketMessageCommand implements Serializable {
    @SerializedName("command")
    private MessageCommand command;
    private Message data;

    public MessageCommand  getCommand() {
        return command;
    }

    public void setCommand(MessageCommand command) {
        this.command = command;
    }

    public Message getData() {
        return data;
    }

    public void setData(Message data) {
        this.data = data;
    }
}
