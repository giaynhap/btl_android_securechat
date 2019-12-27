package com.kma.securechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.SuggestViewHolder;
import com.kma.securechatapp.adapter.viewholder.UserViewHolder;
import com.kma.securechatapp.core.api.model.UserInfo;

import java.util.List;

public class SuggestViewAdapter extends   RecyclerView.Adapter {
    private List<UserInfo> users;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggest_item, parent, false);
        return new SuggestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SuggestViewHolder)holder).bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        if (users == null){
            return 0;
        }
        return users.size();
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}
