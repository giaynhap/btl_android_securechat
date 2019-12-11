package com.kma.securechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.UserViewHolder;
import com.kma.securechatapp.core.api.model.UserInfo;

import java.util.List;

public class UserInfoAdapter extends   RecyclerView.Adapter {
    private List<UserInfo> users;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_info_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserViewHolder contactHolder = (UserViewHolder)holder;
        UserInfo user = users.get(position);
        contactHolder.setName(user.name);
        contactHolder.setAvatar(BuildConfig.HOST +"users/avatar/"+user.uuid+"?width=80&height=80");
        contactHolder.setAddress(user.address);
    }

    @Override
    public int getItemCount() {
        if (users == null)
            return 0;
        return users.size();
    }


    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}
