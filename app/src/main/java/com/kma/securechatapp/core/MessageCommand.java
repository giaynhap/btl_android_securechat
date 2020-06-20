package com.kma.securechatapp.core;

public enum MessageCommand{
    MESSAGE(0),
    READ(1),
    TYPING(2);

    private final int value;
    private MessageCommand(int value) {
        this.value = value;
    }
}


