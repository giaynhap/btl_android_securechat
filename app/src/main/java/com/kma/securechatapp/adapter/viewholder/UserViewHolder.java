package com.kma.securechatapp.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.helper.ImageLoadTask;
import com.kma.securechatapp.utils.common.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserViewHolder  extends RecyclerView.ViewHolder {

    @Nullable
    @BindView(R.id.user_item_avatar)
    public ImageView avatar;
    @Nullable
    @BindView(R.id.user_item_name)
    public TextView title;
    @Nullable
    @BindView(R.id.user_item_address)
    public TextView address;


    public UserViewHolder (@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
    public void setAvatar(String url){

        if (url!=null)
            ImageLoader.getInstance().DisplayImage(url,avatar);
    }
    public void setName(String name){

        if (name!= null )
            this.title.setText(name);
    }

    public void setAddress(String txAddress){

        if (txAddress!= null )
            this.address.setText(txAddress);
    }
}
