package com.kma.securechatapp.ui.conversation;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.Message;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.PageResponse;
import com.kma.securechatapp.core.api.model.UserConversation;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.realm_model.RMessage;
import com.kma.securechatapp.core.security.RSAUtil;
import com.kma.securechatapp.core.security.SecureChatSystem;
import com.kma.securechatapp.core.service.CacheService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboxViewModel extends ViewModel {
    private ApiInterface api = ApiUtil.getChatApi();
    private String conversationUuid;
    private Conversation conversation;
    long lastTime;
    public byte[]key;
    int numPage = 1;
    int curenPage = 0;
    private MutableLiveData<List<MessagePlaneText>> listMessage;
    public MutableLiveData<MessagePlaneText> message;
    private MutableLiveData<Conversation> conversationInfo;
    RealmResults<RMessage> rMessages;
    List<MessagePlaneText> cache = null;
    public InboxViewModel() {
        listMessage = new MutableLiveData<>();
        message = new MutableLiveData<>();
        conversationInfo = new MutableLiveData<>();
    }
    public LiveData<MessagePlaneText> getMessage(){
        return message;
    }
    public LiveData<List<MessagePlaneText>> getMessages(){
        return this.listMessage;
    }

    public void trigerNewMessage(MessagePlaneText newMessage){
        if (newMessage.encrypted){
            newMessage.mesage = SecureChatSystem.getInstance().decode(newMessage.mesage,key);
            newMessage.password = key;
            newMessage.encrypted = false;
        }

        message.setValue(newMessage);
        CacheService.getInstance().addNewMessage(newMessage);
       // cache.add(0,newMessage);
    }
    public void trigerLoadMessage(long time){
      /*  api.pageMessage(conversationUuid,time).enqueue(new Callback<ApiResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Message>>> call, Response<ApiResponse<List<Message>>> response) {
                if (response.body() == null){
                    listMessage.setValue(cache);
                    return;
                }
                if (response.body().data == null){
                    listMessage.setValue(cache);
                    return;
                }

                List<MessagePlaneText> messages = SecureChatSystem.getInstance().decoder(response.body().data, key);
                if (cache == null){
                    cache = messages;
                }else{
                    cache.addAll(messages);
                }
                listMessage.setValue(cache);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Message>>> call, Throwable t) {
                listMessage.setValue(cache);
            }
        });
    */

       rMessages = CacheService.getInstance().queryMessage(conversationUuid,time);
      try{
          if ( (time == 0 && rMessages.size() == 0) || (rMessages.size() > 0 && time == 0 &&  rMessages.first().time != null && rMessages.first().time < conversation.lastMessageAt ) ){

              rMessages.addChangeListener(new RealmChangeListener<RealmResults<RMessage>>() {
                  @Override
                  public void onChange(RealmResults<RMessage> rMessages) {
                      setMessages(rMessages);
                      rMessages.removeAllChangeListeners();
                  }
              });
              CacheService.getInstance().fetchMessages(time,conversationUuid,key);
          } else {
              setMessages(rMessages);
          }
      } catch (Exception e){
          rMessages.addChangeListener(new RealmChangeListener<RealmResults<RMessage>>() {
              @Override
              public void onChange(RealmResults<RMessage> rMessages) {
                  setMessages(rMessages);
                  rMessages.removeAllChangeListeners();
              }
          });
          CacheService.getInstance().fetchMessages(time,conversationUuid,key);
          e.printStackTrace();
      }
    }
    private void setMessages( RealmResults<RMessage> rmesgs ){
        List<MessagePlaneText> messages = new ArrayList<>();
        for (RMessage msg : rmesgs){
            MessagePlaneText plaintText = msg.toModel();
            if (plaintText.encrypted){
                String message = SecureChatSystem.getInstance().decode(plaintText.mesage,key);
                if (message != null) {
                    plaintText.mesage = message;
                    plaintText.encrypted = false;
                    plaintText.password = key;
                    CacheService.getInstance().addNewMessage(plaintText);
                }

            }
            messages.add(plaintText);
        }
        if (cache == null){
            cache = messages;
        }else{
            cache.addAll(messages);
        }
        listMessage.setValue(cache);
    }

    public void cleanCache(){
        if (cache!= null) {
            cache.clear();
            cache = null;
        }
    }

    public  void onUpdatedKey(String seckey, Boolean refresh){

        if (refresh) {
            conversation.conversationKey = seckey;
            CacheService.getInstance().updateConversation(conversation);
        }
        conversationInfo.setValue(conversation);
    }
    public void setConversationUuid(String conversationUuid){
        this.conversationUuid = conversationUuid;
        Conversation con =  CacheService.getInstance().getConversationInfo(conversationUuid);
        if (con == null){

        }
        conversation = con;
        String ukey = conversation.getKey(AppData.getInstance().currentUser.uuid);

        if (ukey == null){
             makeKey();
        }else{
            try {
                key = RSAUtil.RSADecryptBuffer(RSAUtil.base64Decode(ukey),AppData.getInstance().getPrivateKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
            onUpdatedKey(null, false);
        }



       /* api.getConversation( this.conversationUuid).enqueue(new Callback<ApiResponse<Conversation>>() {
            @Override
            public void onResponse(Call<ApiResponse<Conversation>> call, Response<ApiResponse<Conversation>> response) {
                if (response.body() != null) {
                    conversation = response.body().data;
                    conversationInfo.setValue(conversation);
                    String ukey = conversation.getKey(AppData.getInstance().currentUser.uuid);
                    if (ukey == null){
                        makeKey();
                    }else{
                        try {
                            key = RSAUtil.RSADecryptBuffer(RSAUtil.base64Decode(ukey),AppData.getInstance().getPrivateKey());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }else{
                    conversationInfo.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Conversation>> call, Throwable t) {
                conversationInfo.setValue(null);
            }
        });
        */

    }
    public String  makeKey(){
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            byte[] buffKey = secretKey.getEncoded();
            String mySeckey = "";
            List<UserConversation> keys = new ArrayList<UserConversation>();
            for (UserInfo u : conversation.users){
                UserConversation uc = new UserConversation();
                if ( u.publicKey != null && !u.publicKey.isEmpty()) {
                    uc.key = RSAUtil.base64Encode(RSAUtil.RSAEncrypt(buffKey, u.getPublicKey()));
                } else {
                    uc.key = RSAUtil.base64Encode(buffKey);
                }
                uc.userUuid = u.uuid;
                keys.add(uc);
                if (u.uuid == AppData.getInstance().userUUID){
                    mySeckey =  uc.key;
                }
            }

            String finalMySeckey = mySeckey;
            api.updateKey(conversation.UUID,keys).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.body() == null || response.body().error != 0){
                        key = null;
                        onUpdatedKey(null, false);
                    } else {
                        onUpdatedKey(finalMySeckey, true);

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    key = null;
                    onUpdatedKey(null, false);
                }
            });

            key = buffKey;
            return   mySeckey;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public void loadMore(){
        //lastTime
        if (cache != null && cache.size() > 0 ){
            lastTime = cache.get(cache.size()-1).time;
        }else{
            lastTime =0;
        }
        trigerLoadMessage(lastTime);
    }

    public LiveData<Conversation> getConversationInfo(){
        return this.conversationInfo;
    }
    public Conversation getConversation(){
        return this.conversation;
    }

    public boolean send(int type,String message,String uuid){
        if (key == null || key.length == 0) {
            return RealtimeServiceConnection.getInstance().send(type, message, uuid, null);
        } else {
            return RealtimeServiceConnection.getInstance().send(type, message, uuid, key);
        }

    }

}
