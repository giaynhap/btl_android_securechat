package com.kma.securechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.DashboardViewHoder;
import com.kma.securechatapp.adapter.viewholder.SuggestViewHolder;

public class DashboardAdapter extends   RecyclerView.Adapter {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_item, parent, false);
        return new DashboardViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((DashboardViewHoder)holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
