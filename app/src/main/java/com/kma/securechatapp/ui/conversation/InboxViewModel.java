package com.kma.securechatapp.ui.conversation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.Message;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.PageResponse;
import com.kma.securechatapp.core.security.SecureChatSystem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboxViewModel extends ViewModel {
    private ApiInterface api = ApiUtil.getChatApi();
    private String conversationUuid;
    private Conversation conversation;
    long lastTime;

    int numPage = 1;
    int curenPage = 0;
    private MutableLiveData<List<MessagePlaneText>> listMessage;
    public MutableLiveData<MessagePlaneText> message;
    private MutableLiveData<Conversation> conversationInfo;
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
        message.setValue(newMessage);
       // cache.add(0,newMessage);
    }
    public void trigerLoadMessage(long time){
        api.pageMessage(conversationUuid,time).enqueue(new Callback<ApiResponse<List<Message>>>() {
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

                List<MessagePlaneText> messages = SecureChatSystem.getInstance().decoder(response.body().data);
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
    }
    public void cleanCache(){
        if (cache!= null) {
            cache.clear();
            cache = null;
        }
    }
    public void setConversationUuid(String conversationUuid){
        this.conversationUuid = conversationUuid;
        api.getConversation( this.conversationUuid).enqueue(new Callback<ApiResponse<Conversation>>() {
            @Override
            public void onResponse(Call<ApiResponse<Conversation>> call, Response<ApiResponse<Conversation>> response) {
                if (response.body() != null) {
                    conversation = response.body().data;
                    conversationInfo.setValue(conversation);

                }else{
                    conversationInfo.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Conversation>> call, Throwable t) {
                conversationInfo.setValue(null);
            }
        });
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

}
