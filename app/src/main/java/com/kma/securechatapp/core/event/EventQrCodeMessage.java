package com.kma.securechatapp.core.event;

import com.kma.securechatapp.core.SocketLoginCommand;
import com.kma.securechatapp.core.api.model.SocketLoginDTO;

public interface EventQrCodeMessage {
    SocketLoginDTO onMessage(SocketLoginDTO data);
}
