package com.kma.securechatapp.core;

public enum SocketLoginCommand{
    PREHANDSHAKE(0),
    HANDSHAKE(1),
    TRANSFER(2);

    private final int value;
    private SocketLoginCommand(int value) {
        this.value = value;
    }
}