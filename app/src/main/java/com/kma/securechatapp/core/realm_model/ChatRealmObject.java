package com.kma.securechatapp.core.realm_model;

import com.kma.securechatapp.core.api.model.Conversation;

public interface ChatRealmObject<T> {
    public  void fromModel(T con) ;
    public T toModel();
}
