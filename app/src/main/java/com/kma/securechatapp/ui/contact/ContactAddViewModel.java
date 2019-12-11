package com.kma.securechatapp.ui.contact;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.UserInfo;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactAddViewModel extends ViewModel {
    private MutableLiveData<List<UserInfo>> listUser;
    ApiInterface api = ApiUtil.getChatApi();
    public ContactAddViewModel(){
        listUser = new MutableLiveData<>();
    }

    public LiveData<List<UserInfo>> getSearchUser(){
        return listUser;
    }

    public void trigerSearch(String userName){
        api.searchUser(userName).enqueue(new Callback<ApiResponse<List<UserInfo>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserInfo>>> call, Response<ApiResponse<List<UserInfo>>> response) {

                if (response.body()!= null)
                {
                    listUser.setValue(response.body().data);
                }else
                {
                    listUser.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<UserInfo>>> call, Throwable t) {
                listUser.setValue(null);
            }
        });
    }
}
