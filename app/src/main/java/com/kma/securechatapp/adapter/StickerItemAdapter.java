package com.kma.securechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.StickerItemViewHolder;
import com.kma.securechatapp.adapter.viewholder.StikerListViewHolder;

public class StickerItemAdapter  extends RecyclerView.Adapter {
    public String model;
    public int num = 0;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticker_list_view, parent, false);
        return new StickerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((StickerItemViewHolder)holder).bind(model,position);
    }

    @Override
    public int getItemCount() {
        return num;
    }

    public void setSticker(String model , int num){
        this.model = model;
        this.num = num;
        this.notifyDataSetChanged();
    }
}
