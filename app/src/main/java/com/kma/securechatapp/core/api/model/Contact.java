package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

public class Contact {
  @SerializedName("id")
  public String id;
  @SerializedName("contact_uuid")
  public String contactUuid;
  @SerializedName("created_at")
  public long createdAt;
  @SerializedName("level")
  public int level;
  @SerializedName("contact_name")
  public String contactName;

  @SerializedName("online")
  public boolean online;

  public Contact(String contactUuid, int level, String contactName) {
    this.contactUuid = contactUuid;
    this.level = level;
    this.contactName = contactName;
  }

  @Override
  public boolean equals(Object o){
    try{
      Contact c = (Contact)o;
      return c.id.equals(id);
    }catch (Exception e){
      return false;
    }
  }

}
