package com.kma.securechatapp.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.helper.ImageLoadTask;
import com.kma.securechatapp.utils.common.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    @Nullable

    @BindView(R.id.contact_item_avatar)
    public ImageView avatar;
    @Nullable
    @BindView(R.id.contact_item_name)
    public TextView title;
    @Nullable
    @BindView(R.id.contact_item_subname)

    public TextView subname;
    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this ,itemView);
    }

    public void setContactAvatar(String url){
        if (this.avatar == null){
            ButterKnife.bind(this,itemView);
        }
        if (avatar!=null)
            ImageLoader.getInstance().DisplayImage(url,avatar);


       // new ImageLoadTask(url,avatar).execute();
    }
    public void setContactName(String name){
        if (this.title == null){
            ButterKnife.bind(this,itemView);
        }
        if (name!= null && this.title != null)
            this.title.setText(name);

    }

    public void setSubName(String name){
        if (this.subname == null){
            ButterKnife.bind(this,itemView);
        }
        if (name!= null && this.subname != null)
            this.subname.setText(name);

    }
}
