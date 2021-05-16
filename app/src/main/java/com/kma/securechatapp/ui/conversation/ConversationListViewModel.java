package com.kma.securechatapp.ui.conversation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.realm_model.RConversation;
import com.kma.securechatapp.core.service.CacheService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationListViewModel extends ViewModel {

    ApiInterface api = ApiUtil.getChatApi();

    boolean loadError = false;
    int numPage = 1;
    int curenPage = 0;
    private MutableLiveData<List<Conversation>> listConversation;
    private MutableLiveData<List<Contact>> listOnline;
    List<Conversation> cache = null;
    RealmResults <RConversation> conversations;

    public ConversationListViewModel() {
        listConversation = new MutableLiveData<>();
        listOnline = new MutableLiveData<>();



    }
    public void registEvent(){

        conversations =  CacheService.getInstance().queryConversation();
        try {
            conversations.addChangeListener(new RealmChangeListener<RealmResults<RConversation>>() {
                @Override
                public void onChange(RealmResults<RConversation> rConversations) {
                    ArrayList<Conversation> conns = new ArrayList<>();
                    for (RConversation rlmodel : conversations) {
                        conns.add(rlmodel.toModel());
                    }
                    setConvetsation(conns);
                }
            });



            ArrayList<Conversation> conns = new ArrayList<>();
            for (RConversation rlmodel : conversations) {
                conns.add(rlmodel.toModel());
            }
            setConvetsation(conns);
        } catch (Exception e){
            ArrayList<Conversation> conns = new ArrayList<>();
            setConvetsation(conns);
            e.printStackTrace();
        }
    }
    public void setConvetsation(List<Conversation> conns){

        listConversation.setValue(conns);
    }
    public LiveData<List<Conversation>> getConversations( ) {

        return listConversation;
    }

    public LiveData<List<Contact>> getListOnline(){
        return listOnline;
    }
    public void trigerLoadOnline(){
        api.getListOnline().enqueue(new Callback<ApiResponse<List<Contact>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Contact>>> call, Response<ApiResponse<List<Contact>>> response) {

                if (response.body() == null){
                    listOnline.setValue(null);
                    return;
                }
                listOnline.setValue(response.body().data);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Contact>>> call, Throwable t) {
                listOnline.setValue(null);
            }
        });
    }


    public void trigerLoadData(int page){
        CacheService.getInstance().fetchConversation(page);

        return;
        /*
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
        });*/
    }
}