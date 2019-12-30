package com.kma.securechatapp.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.utils.common.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactOnlineViewHolder extends RecyclerView.ViewHolder{
@BindView(R.id.suggest_avatar)
ImageView avatar;


public ContactOnlineViewHolder(@NonNull View itemView){

        super(itemView);
        ButterKnife.bind(this,itemView);
        }
        public void bind(Contact userInfo){
            ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl(userInfo.contactUuid,80,80),avatar);
          //  name.setText(userInfo.contactName);
        }

}