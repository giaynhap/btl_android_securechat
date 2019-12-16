package com.kma.securechatapp.adapter.viewholder;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.MainActivity;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.helper.ImageLoadTask;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.common.StringHelper;
import com.kma.securechatapp.utils.misc.AudioUi;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageReceivederViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_message_body)
    View txtBody;
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
        if (msg.type == 1) {
            ImageLoader.getInstance().DisplayImage(BuildConfig.HOST +msg.mesage,(ImageView)txtBody);
            txtBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.instance.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.HOST +msg.mesage)));

                }
            });
        }
        else if (msg.type == 0){
            ((TextView)txtBody).setText(msg.mesage);
        }else if (msg.type == 2)
        {
            ((AudioUi)txtBody).setUrl(BuildConfig.HOST +msg.mesage);
            ((AudioUi)txtBody).addHeader("Authorization","Bearer "+ AppData.getInstance().getToken());

        }
        txtName.setText(msg.sender.name);
        txtTime.setText(StringHelper.getTimeText(msg.time));
        ImageLoader.getInstance().DisplayImage(BuildConfig.HOST +"users/avatar/"+msg.sender.uuid+"?width=32&height=32",imgAvatar);
      //  new ImageLoadTask(,imgAvatar).execute();

    }
}
