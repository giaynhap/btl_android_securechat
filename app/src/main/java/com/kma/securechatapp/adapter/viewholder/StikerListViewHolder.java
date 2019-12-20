package com.kma.securechatapp.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.core.api.model.Sticker;
import com.kma.securechatapp.utils.common.ImageLoader;

public class StikerListViewHolder extends RecyclerView.ViewHolder {
    public StikerListViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void bind(Sticker sticker){
        ImageLoader.getInstance().DisplayImage(ImageLoader.getStickerUrl(sticker.model,0),( (ImageView)this.itemView));
    }
}
