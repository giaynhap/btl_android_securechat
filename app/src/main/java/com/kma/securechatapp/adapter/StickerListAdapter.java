package com.kma.securechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.StikerListViewHolder;
import com.kma.securechatapp.core.api.model.Sticker;

import java.util.List;

public class StickerListAdapter extends RecyclerView.Adapter {
    List<Sticker> stikers;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticker_list_view, parent, false);
        return new StikerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ( (StikerListViewHolder)holder).bind(stikers.get(position));
    }

    @Override
    public int getItemCount() {
        if (stikers == null){
            return 0;
        }
        return stikers.size();
    }

    public List<Sticker> getStikers() {
        return stikers;
    }

    public void setStikers(List<Sticker> stikers) {
        this.stikers = stikers;
    }
}
