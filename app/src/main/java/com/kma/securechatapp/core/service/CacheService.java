package com.kma.securechatapp.core.service;

import android.content.Context;

import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.Message;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.PageResponse;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.realm_model.RConversation;
import com.kma.securechatapp.core.realm_model.RMessage;
import com.kma.securechatapp.core.realm_model.RUserInfo;
import com.kma.securechatapp.core.security.AES;
import com.kma.securechatapp.core.security.RSAUtil;
import com.kma.securechatapp.core.security.SecureChatSystem;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
      try {
          Realm.init(context);
          RealmConfiguration config = new RealmConfiguration.Builder()
                  .deleteRealmIfMigrationNeeded()
                  .name(db)
                  .encryptionKey(RSAUtil.getSHA512(password))
                  .build();
          rdb = Realm.getInstance(config);
      }catch ( Exception e){
          e.printStackTrace();
      }
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
        return "x-encr-db-"+key+"-realm.gn";
    }

    public void addConveration(Conversation con){
        RConversation realmCon = new RConversation();
        realmCon.fromModel(con);
        rdb.beginTransaction();
        rdb.insertOrUpdate(realmCon);
        rdb.commitTransaction();
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

    public void fetchMessages(long time, String conversationUuid, byte [] key){

        api.pageMessage(conversationUuid,time).enqueue(new Callback<ApiResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Message>>> call, Response<ApiResponse<List<Message>>> response) {
                if (response.body() == null){
                    return;
                }
                if (response.body().data == null){
                    return;
                }

                List<MessagePlaneText> messages = SecureChatSystem.getInstance().decoder(response.body().data, key);

                rdb.beginTransaction();
                for (MessagePlaneText msg : messages) {
                    RMessage rmsg = new RMessage();
                    rmsg.fromModel(msg);
                    rdb.insertOrUpdate(rmsg);
                }
                rdb.commitTransaction();

            }

            @Override
            public void onFailure(Call<ApiResponse<List<Message>>> call, Throwable t) {

            }
        });

    }

    public RealmResults<RConversation>  queryConversation(){
        try {
            return rdb.where(RConversation.class).sort("lastMessageAt", Sort.DESCENDING).findAll();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void saveUser(UserInfo userInfo,String userName){
        rdb.beginTransaction();
        RUserInfo info = new RUserInfo();
        info.fromModel(userInfo);
        info.userName = userName;
        rdb.insertOrUpdate(info);
        rdb.commitTransaction();
    }

    public UserInfo getUser(String uuid){
        if ( rdb == null){
            return null;
        }
        RUserInfo user =  rdb.where(RUserInfo.class).equalTo("uuid",uuid).findFirst();
        if ( user == null ){
            return null;
        }
       return user.toModel();
    }

    public Conversation getConversationInfo(String uuid) {
        try {
            return rdb.where(RConversation.class).equalTo("UUID", uuid).findFirst().toModel();

        } catch (Exception e){
            return null;
        }
    }

    public void updateConversation(Conversation con){
        rdb.beginTransaction();
        RConversation rCon = new RConversation();
        rCon.fromModel(con);
        rdb.insertOrUpdate(rCon);
        rdb.commitTransaction();
    }

    public void addNewMessage(MessagePlaneText message){
        RMessage rmesg = new RMessage();
        rmesg.fromModel(message);
        rdb.beginTransaction();
        rdb.insertOrUpdate(rmesg);
        rdb.commitTransaction();
    }
    public RealmResults<RMessage> queryMessage(String conversation, Long time ){
        if (time  > 0 )
            return rdb.where(RMessage.class).equalTo("threadUuid",conversation).lessThan("time",time).sort("time",Sort.DESCENDING).findAll();
        else
            return rdb.where(RMessage.class).equalTo("threadUuid",conversation).sort("time",Sort.DESCENDING).findAll();

    }


}

