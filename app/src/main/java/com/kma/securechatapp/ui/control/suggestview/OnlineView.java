package com.kma.securechatapp.ui.control.suggestview;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.OnlineViewAdapter;
import com.kma.securechatapp.adapter.SuggestViewAdapter;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.ui.conversation.InboxActivity;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.misc.RecyclerItemClickListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnlineView {
    RecyclerView recyclerView;
    OnlineViewAdapter adapter;
    ApiInterface api = ApiUtil.getChatApi();

    public OnlineView(Activity activity, View layout){

        recyclerView = (RecyclerView)layout.findViewById(R.id.online_list_recyclerview);
        adapter = new OnlineViewAdapter();

        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(activity, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Contact userInfo = adapter.getUsers().get(position);
                        api.getConversationByUser(userInfo.contactUuid).enqueue(new Callback<ApiResponse<Conversation>>() {
                            @Override
                            public void onResponse(Call<ApiResponse<Conversation>> call, Response<ApiResponse<Conversation>> response) {
                                if (response.body() != null && response.body().data != null){
                                    Intent intent1 = new Intent(activity, InboxActivity.class);
                                    intent1.putExtra("uuid", response.body().data.UUID);
                                    activity.startActivity(intent1);
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<Conversation>> call, Throwable t) {
                            }
                        });
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Contact userInfo = adapter.getUsers().get(position);
                        Intent intent1 = new Intent(activity, UserProfileActivity.class);
                        intent1.putExtra("uuid",userInfo.contactUuid);
                        intent1.setAction("view_user");
                        activity.startActivity(intent1);
                    }
                })
        );

    }

    public void upDateList(List<Contact> users){
        adapter.setUsers(users);
        adapter.notifyDataSetChanged();
    }
}
