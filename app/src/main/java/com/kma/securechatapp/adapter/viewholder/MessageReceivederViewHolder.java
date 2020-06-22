package com.kma.securechatapp.adapter.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.MainActivity;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.helper.ImageLoadTask;
import com.kma.securechatapp.ui.control.ImagePreview;
import com.kma.securechatapp.ui.conversation.InboxActivity;
import com.kma.securechatapp.utils.common.EncryptFileLoader;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.common.StringHelper;
import com.kma.securechatapp.utils.misc.AudioUi;

import java.io.File;

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
    @BindView(R.id.msg_time_status)
    TextView txtLongTime;

    public MessageReceivederViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void bind (MessagePlaneText msg, boolean hideIcon){
        if (msg.type == 1) {
           // ImageLoader.getInstance().DisplayImage(BuildConfig.HOST +msg.mesage+"?type=small",(ImageView)txtBody);
            EncryptFileLoader.getInstance().loadEncryptImage(BuildConfig.HOST +msg.mesage,msg.password,(ImageView)txtBody,null);

            txtBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   MainActivity.instance.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.HOST +msg.mesage)));
                    Context context = MessageReceivederViewHolder.this.itemView.getContext();
                    Intent intent = new Intent( context, ImagePreview.class);
                    ImagePreview.bitmap  = ((BitmapDrawable)((ImageView)txtBody).getDrawable()).getBitmap();
                    intent.putExtra("url",BuildConfig.HOST +msg.mesage);
                    intent.putExtra("key",msg.password);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity)context, (View)txtBody, "imageView");

                    context.startActivity(intent,options.toBundle());

                }
            });
        }
        else if (msg.type == 0){
            ((TextView)txtBody).setText(msg.mesage);
        }
        else if (msg.type == 2)
        {
            EncryptFileLoader.getInstance()
            .loadEncryptFile(BuildConfig.HOST + msg.mesage, msg.password, new EncryptFileLoader.EventLoadedEncryptFile() {
                @Override
                public void onLoaded(int error, File file) {
                    if ( error == 1 ){
                        return;
                    }
                    ((AudioUi)txtBody).setPath(file);
                }
            });
            /*
            ((AudioUi)txtBody).setUrl(BuildConfig.HOST +msg.mesage);
            ((AudioUi)txtBody).addHeader("Authorization","Bearer "+ AppData.getInstance().getToken());
            */
        }
        else if (msg.type == 3)
        {
            String[] split = msg.mesage.split("::");
            if (split.length < 2)
                return;
            int index = Integer.decode(split[1]);
            ImageLoader.getInstance().DisplayImage(ImageLoader.getStickerUrl(split[0],index),(ImageView)txtBody);
        }
        if (!hideIcon) {
            txtName.setText(msg.sender.name);
            txtTime.setText(StringHelper.getTimeText(msg.time));
            ImageLoader.getInstance().DisplayImage(BuildConfig.HOST + "users/avatar/" + msg.sender.uuid + "?width=32&height=32", imgAvatar);
        }else{
            txtName.setVisibility(View.GONE);
            txtTime.setVisibility(View.GONE);
            imgAvatar.setVisibility(View.INVISIBLE);
        }
        txtTime.setVisibility(View.GONE);
        if (msg.type != 1 && msg.type != 2){
            this.txtBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txtTime.getVisibility() == View.GONE)
                        txtTime.setVisibility(View.VISIBLE);
                    else
                        txtTime.setVisibility(View.GONE);
                }
            });
        }
        txtLongTime.setText(StringHelper.getLongTextChat(msg.time));
        txtLongTime.setVisibility(View.GONE);
      //  new ImageLoadTask(,imgAvatar).execute();
    }
    public void showLongTime(){
        txtLongTime.setVisibility(View.VISIBLE);
    }
}
