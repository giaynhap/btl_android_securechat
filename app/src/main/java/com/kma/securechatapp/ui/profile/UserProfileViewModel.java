package com.kma.securechatapp.ui.profile;

import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil; 
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileViewModel extends ViewModel {
    public  interface UploadAvatarListener{
        public void onSuccess(String url);
        public void onFalse(int code,String message);
    }
    ApiInterface api = ApiUtil.getChatApi();
    private MutableLiveData<UserInfo> userInfo  = new MutableLiveData<UserInfo> ();
    private MutableLiveData<Boolean> hasContact =  new MutableLiveData<Boolean>();
    private MutableLiveData<Conversation> conversation ;

    public LiveData<Conversation> getConversion(){
            if (conversation == null){
                conversation = new  MutableLiveData<com.kma.securechatapp.core.api.model.Conversation>();
            }
            return conversation;
    }
    public void trigerConversation(String userUuid){
        api.getConversationByUser(userUuid).enqueue(new Callback<ApiResponse<Conversation>>() {
            @Override
            public void onResponse(Call<ApiResponse<Conversation>> call, Response<ApiResponse<Conversation>> response) {
                if (response.body()== null || response.body().data == null){
                    conversation.setValue(null);
                }else
                {
                    conversation.setValue(response.body().data);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Conversation>> call, Throwable t) {
                conversation.setValue(null);
            }
        });
    }

    public LiveData <UserInfo> getUsrInfo(){
        return userInfo;
    }
    public void trigerUserInfo(String uuid){
        api.userInfo(uuid).enqueue(new Callback<ApiResponse<UserInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserInfo>> call, Response<ApiResponse<UserInfo>> response) {

                if (response.body()!=null)
                    userInfo.setValue(response.body().data);
                else
                    userInfo.setValue(null);
            }

            @Override
            public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {
                userInfo.setValue(null);
            }
        });
    }
    public LiveData<Boolean> getHasContact(){
        return hasContact;
    }
    public void trigerCheckHasContact(String uuid){
        api.existContact(uuid).enqueue(new Callback<ApiResponse<Contact>>() {
            @Override
            public void onResponse(Call<ApiResponse<Contact>> call, Response<ApiResponse<Contact>> response) {
                    if (response.body() == null || response.body().error != 0 || response.body().data == null){
                        hasContact.setValue(false);

                    }else
                    {
                        hasContact.setValue(true);
                    }
            }

            @Override
            public void onFailure(Call<ApiResponse<Contact>> call, Throwable t) {
                hasContact.setValue(false);
            }
        });
    }

    void deleteContact(String uuid){
            api.deleteContact(uuid).enqueue(new Callback<ApiResponse<Boolean>>() {
                @Override
                public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                    trigerCheckHasContact(uuid);
                }

                @Override
                public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {

                }
            });
    }

    void addContact(String uuid){

        api.addContact( new Contact(uuid,0,null)).enqueue(new Callback<ApiResponse<UserInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserInfo>> call, Response<ApiResponse<UserInfo>> response) {
                trigerCheckHasContact(uuid);
            }

            @Override
            public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {

            }
        });
    }
    public void uploadAvatar(MultipartBody.Part part, UploadAvatarListener listenr){

        api.uploadAvatar(part).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.body() == null || response.body().data == null){
                    listenr.onFalse(1,"Upload image error!!");
                    return;
                }
                listenr.onSuccess(response.body().data);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                listenr.onFalse(1,"Upload image error!!");
            }
        });

    }

    public Call<ApiResponse<UserInfo>> quickChangeName(String name){
        return api.quickChangeName(name);
    }

}
