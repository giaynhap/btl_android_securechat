package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;
import com.kma.securechatapp.core.MessageCommand;

import java.io.Serializable;

public class SocketLoginTranferData   implements Serializable
{
        @SerializedName("token")
        private String token;
        @SerializedName("refresh")
        private String refresh;
        @SerializedName("key")
        private String key;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRefresh() {
            return refresh;
        }

        public void setRefresh(String refresh) {
            this.refresh = refresh;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
}
