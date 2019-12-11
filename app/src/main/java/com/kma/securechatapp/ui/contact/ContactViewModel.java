package com.kma.securechatapp.ui.contact;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ContactViewModel extends ViewModel {


    ApiInterface api = ApiUtil.getChatApi();

    boolean loadError = false;
    int numPage = 1;
    int curenPage = 0;
    private MutableLiveData<List<Contact>> listContact;
    List<Contact> cache = null;
     class ResponsePageContact implements Callback<ApiResponse<PageResponse<Contact>>> {

        @Override
        public void onResponse(Call<ApiResponse<PageResponse<Contact>>> call, Response<ApiResponse<PageResponse<Contact>>> response) {

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
            listContact.setValue(cache);
        }

        @Override
        public void onFailure(Call<ApiResponse<PageResponse<Contact>>> call, Throwable t) {
            loadError = true;
            listContact.setValue(null);
        }
    }
    public ContactViewModel() {
        listContact = new MutableLiveData<>();
       // mText.setValue("This is home fragment");

    }

    public LiveData<List<Contact>> getContact(int page ) {
        api.pageContact(page).enqueue(new  ResponsePageContact());
        return listContact;
    }
    public void trigerLoadData(int page){
        api.pageContact(page).enqueue(new  ResponsePageContact());
    }
}