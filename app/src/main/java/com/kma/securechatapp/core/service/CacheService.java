package com.kma.securechatapp.core.service;

import android.content.Context;

import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.PageResponse;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.realm_model.RConversation;
import com.kma.securechatapp.core.realm_model.RUserInfo;
import com.kma.securechatapp.core.security.AES;
import com.kma.securechatapp.core.security.RSAUtil;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CacheService {

    private static CacheService instance;
    public Realm rdb;
    ApiInterface api = ApiUtil.getChatApi();
    public static  CacheService getInstance(){
        if (instance == null){
            instance = new CacheService();
        }
        return instance;
    }
    public void getUserPublicKey(String uuid){

    }

    public void init(Context context, String db ,String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name(db)
                .encryptionKey( RSAUtil.getSHA512(password))
                .build();
        rdb =   Realm.getInstance( config) ;
    }

    public String accountToDbName(String account){
        String key = account;
        try {
            key = RSAUtil.bytesToHex( RSAUtil.getSHA(account) );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "encr-db-"+key+"-realm.gn";
    }


    public void fetchConversation(int page){
        api.pageConversation(page).enqueue(new Callback<ApiResponse<PageResponse<Conversation>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Conversation>>> call, Response<ApiResponse<PageResponse<Conversation>>> response) {
               // response.body().data.content;
                rdb.beginTransaction();
                for (Conversation conversation : response.body().data.content) {
                    RConversation realmCon = new RConversation();
                    realmCon.fromModel(conversation);
                    rdb.insertOrUpdate(realmCon);
                }
                rdb.commitTransaction();
            }
            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Conversation>>> call, Throwable t) {
                EventBus.getInstance().noticShow("Lỗi kết nối","Có lỗi xảy ra");
            }
        });
    }

    public RealmResults<RConversation>  queryConversation(){
       return rdb.where(RConversation.class).sort("lastMessageAt", Sort.DESCENDING).findAll();
    }

    public void saveUser(UserInfo userInfo,String userName){
        rdb.beginTransaction();
        RUserInfo info = new RUserInfo();
        info.fromModel(userInfo);
        info.userName = userName;
        rdb.insertOrUpdate(info);
        rdb.commitTransaction();
    }

    public UserInfo getUser(String userName){
        if ( rdb == null){
            return null;
        }
        RUserInfo user =  rdb.where(RUserInfo.class).equalTo("userName",userName).findFirst();
        if ( user == null ){
            return null;
        }
       return user.toModel();
    }


}

