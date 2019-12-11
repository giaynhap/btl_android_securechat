package com.kma.securechatapp.ui.conversation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationListViewModel extends ViewModel {

    ApiInterface api = ApiUtil.getChatApi();

    boolean loadError = false;
    int numPage = 1;
    int curenPage = 0;
    private MutableLiveData<List<Conversation>> listConversation;
    List<Conversation> cache = null;
    public ConversationListViewModel() {
        listConversation = new MutableLiveData<>();
    }
    public LiveData<List<Conversation>> getConversations( ) {

        return listConversation;
    }

    public void trigerLoadData(int page){
        api.pageConversation(page).enqueue(new Callback<ApiResponse<PageResponse<Conversation>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Conversation>>> call, Response<ApiResponse<PageResponse<Conversation>>> response) {
                if (response.body()== null || response.body().data == null)
                {
                    return;
                }
                loadError = false;
                numPage = response.body().data.totalPages;
                curenPage= response.body().data.number;


                if (cache== null|| curenPage == 0){
                    cache = response.body().data.content;
                }
                else if ( response.body().data.content != null){
                    cache.addAll(response.body().data.content);
                }
                listConversation.setValue(cache);
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Conversation>>> call, Throwable t) {

            }
        });
    }
}