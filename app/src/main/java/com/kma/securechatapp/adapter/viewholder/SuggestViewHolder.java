package com.kma.securechatapp.adapter.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.MainActivity;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.ui.control.suggestview.SuggestView;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.common.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuggestViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.suggest_avatar)
    ImageView avatar;
    @BindView(R.id.suggest_name)
    TextView name;

    public SuggestViewHolder(@NonNull View itemView) {

        super(itemView);
        ButterKnife.bind(this,itemView);
    }
    public void bind(UserInfo userInfo){
        ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl(userInfo.uuid,80,80),avatar);
        name.setText(userInfo.name);

    }
}
