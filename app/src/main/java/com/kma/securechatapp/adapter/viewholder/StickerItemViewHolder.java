package com.kma.securechatapp.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.utils.common.ImageLoader;

public class StickerItemViewHolder extends RecyclerView.ViewHolder{
    public StickerItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void bind(String model,int index){
        ImageLoader.getInstance().DisplayImage(ImageLoader.getStickerUrl(model,index),( (ImageView)this.itemView));
    }
}
