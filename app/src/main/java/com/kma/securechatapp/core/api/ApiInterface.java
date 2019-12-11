package com.kma.securechatapp.core.api;

import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.AuthenRequest;
import com.kma.securechatapp.core.api.model.AuthenResponse;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.Message;
import com.kma.securechatapp.core.api.model.PageResponse;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.api.model.UserKey;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @POST("/authenticate")
    Call<ApiResponse<AuthenResponse>> login( @Body  AuthenRequest request);

    @GET("/users/info")
    Call<ApiResponse<UserInfo>> getCurrenUserInfo();

    @GET("/users/exist/{username}")
    Call<ApiResponse<UserInfo>> userExist(@Path("username") String username);

    @GET("/users/info/{uuid}")
    Call<ApiResponse<UserInfo>> userInfo(@Path("uuid") String uuid);

    @GET("/users/find/{name}")
    Call<ApiResponse<List<UserInfo>>> searchUser(@Path("name") String userName);


    @GET("/contact/page/{page}")
    Call<ApiResponse<PageResponse<Contact>>> pageContact(@Path("page") int page);


    @POST("/contact/add")
    Call<ApiResponse<UserInfo>> addContact(@Body Contact contact);

    @GET("/contact/delete/{uuid}")
    Call<ApiResponse<Boolean>> deleteContact(@Path("uuid") String uuid);

    @GET("/contact/exist/{uuid}")
    Call<ApiResponse<Contact>> existContact(@Path("uuid") String uuid);


    @GET("/conversation/{uuid}")
    Call<ApiResponse<Conversation>> getConversation(@Path("uuid") String uuid);

    @GET("/conversation/u/{uuid}")
    Call<ApiResponse<Conversation>> getConversationByUser(@Path("uuid") String uuid);

    @GET("/conversation/page/{page}")
    Call<ApiResponse<PageResponse<Conversation>>> pageConversation(@Path("page") int page);

    @GET("/conversation/messages/{uuid}")
    Call<ApiResponse<List<Message>>> pageMessage(@Path("uuid") String uuid, @Query("time")long time);


    @GET("/users/prelogin/{device}/{username}")
    Call<ApiResponse<UserInfo>> preLogin(@Path("username") String username, @Path("device")String device);


    @GET("/keyprovider/public/{uuid}")
    Call<ApiResponse<String>> getPublicKey(@Path("uuid") String uuid);

    @GET("/keyprovider/private")
    Call<ApiResponse<String>> getPrivateKey();

    @POST("/keyprovider/update")
    Call<ApiResponse<String>> updateKey(@Body UserKey key);

}
