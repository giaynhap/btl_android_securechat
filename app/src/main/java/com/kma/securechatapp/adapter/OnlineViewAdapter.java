package com.kma.securechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.ContactOnlineViewHolder;
import com.kma.securechatapp.adapter.viewholder.SuggestViewHolder;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.UserInfo;

import java.util.List;

public class OnlineViewAdapter extends   RecyclerView.Adapter {
    private List<Contact> users;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.online_list_item, parent, false);
        return new ContactOnlineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ContactOnlineViewHolder)holder).bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        if (users == null){
            return 0;
        }
        return users.size();
    }

    public List<Contact> getUsers() {
        return users;
    }

    public void setUsers(List<Contact> users) {
        this.users = users;
    }
}
