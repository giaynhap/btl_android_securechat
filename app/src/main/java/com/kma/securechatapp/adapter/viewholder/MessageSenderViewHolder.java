package com.kma.securechatapp.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.utils.common.StringHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MessageSenderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_message_body)
    TextView txtBody;
    @BindView(R.id.text_message_time)
    TextView txtTime;

    public MessageSenderViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
    public void bind (MessagePlaneText msg){
        if (txtBody != null ) {
            txtBody.setText(msg.mesage);
            txtTime.setText(StringHelper.getTimeText(msg.time));
        }
    }
}
