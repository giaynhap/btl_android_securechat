package com.kma.securechatapp.adapter.viewholder;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.helper.ImageLoadTask;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.common.StringHelper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class ConversationViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_message)
    TextView txtMessage;

    @BindView(R.id.text_title)
    TextView txtTitle;


    @BindView(R.id.text_time)
    TextView txtTime;

    @BindView(R.id.img_avatar)
    ImageView imgAvatar;

    @BindView(R.id.conversation_online)
    View online;

    @BindView(R.id.count_un_read)
    TextView txtUnRead;

    public ConversationViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this ,itemView);
    }
    public void setAvatar(String url){

        if (url!=null)
            ImageLoader.getInstance().DisplayImage(url,imgAvatar);
           // new ImageLoadTask(url,imgAvatar).execute();
    }
    public void setTitle(String title){
        this.txtTitle.setText(title);
    }
    public void setMessage(String message){
        if (message==null)
        {
            message ="";
        }
        this.txtMessage.setText(message);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTime(Long time){
        if (time == null)
            return;
        this.txtTime.setText( StringHelper.getLongTextChat(time));


    }

    public void setOnline(boolean isOnline){
        if (isOnline){
            online.setVisibility(View.VISIBLE);
        }else{
            online.setVisibility(GONE);
        }
    }
    public void setNumUnRead(Integer unread){
        if (unread == null ||unread < 1){
            txtUnRead.setVisibility(GONE);
        }else{
            txtUnRead.setVisibility(View.VISIBLE);
            txtUnRead.setText(unread+"");
        }
    }
}
