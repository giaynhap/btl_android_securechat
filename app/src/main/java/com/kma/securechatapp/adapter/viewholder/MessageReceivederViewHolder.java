package com.kma.securechatapp.adapter.viewholder;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.helper.ImageLoadTask;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.common.StringHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageReceivederViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_message_body)
    TextView txtBody;
    @BindView(R.id.text_message_time)
    TextView txtTime;
    @BindView(R.id.text_message_name)
    TextView txtName;
    @BindView(R.id.image_message_profile)
    ImageView imgAvatar;

    public MessageReceivederViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void bind (MessagePlaneText msg){
        txtBody.setText(msg.mesage);
        txtName.setText(msg.sender.name);
        txtTime.setText(StringHelper.getTimeText(msg.time));
        ImageLoader.getInstance().DisplayImage(BuildConfig.HOST +"users/avatar/"+msg.sender.uuid+"?width=32&height=32",imgAvatar);
      //  new ImageLoadTask(,imgAvatar).execute();
    }
}
