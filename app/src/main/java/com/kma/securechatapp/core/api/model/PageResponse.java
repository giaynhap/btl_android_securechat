package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageResponse<T> {
  @SerializedName("content")
  public List<T> content;
  @SerializedName("totalElements")
  public int totalElements;
  @SerializedName("totalPages")
  public int totalPages;
  @SerializedName("last")
  public boolean last;
  @SerializedName("size")
  public int size;
  @SerializedName("number")
  public int number;
  @SerializedName("first")
  public boolean first;
  @SerializedName("empty")
  public boolean empty;


}
