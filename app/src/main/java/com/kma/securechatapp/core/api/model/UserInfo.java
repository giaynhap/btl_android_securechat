package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;
import com.kma.securechatapp.core.security.RSAUtil;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class UserInfo {
  @SerializedName("uuid")
  public String uuid;
  @SerializedName("name")
  public String name;
  @SerializedName("address")
  public String address;
  @SerializedName("dob")
  public Long dob;
  @SerializedName("publickey")
  public String publicKey;

  @SerializedName("online")
  public boolean online;


  private PublicKey _publicKey= null;

  public UserInfo(String uuid, String name, String address, Long dob) {
    this.uuid = uuid;
    this.name = name;
    this.address = address;
    this.dob = dob;
  }

  public PublicKey getPublicKey(){
    if (_publicKey == null){
      try {
        _publicKey = RSAUtil.stringToPublicKey(publicKey);
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      } catch (InvalidKeySpecException e) {
        e.printStackTrace();
      }
    }
    return _publicKey;
  }
  @Override
  public boolean equals(Object o){
    try{
      UserInfo c = (UserInfo)o;
      return c.uuid.equals(uuid);
    }catch (Exception e){
      return false;
    }
  }
}
