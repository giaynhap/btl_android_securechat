package com.kma.securechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.adapter.viewholder.MessageReceivederViewHolder;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.MessageSenderViewHolder;
import com.kma.securechatapp.core.api.model.MessagePlaneText;

import java.util.List;

public class MessageAdapter extends   RecyclerView.Adapter {
    List<MessagePlaneText> messages;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new MessageSenderViewHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new MessageReceivederViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        MessagePlaneText message = (MessagePlaneText) messages.get(position);

        if (message.senderUuid.equals(AppData.getInstance().currentUser.uuid)) {
            // If the current user is the sender of the message
            return 0;
        } else {
            // If some other user sent the message
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
         if (holder.getItemViewType() == 1){
             ((MessageReceivederViewHolder)holder).bind(messages.get(position));
         }else{
             ((MessageSenderViewHolder)holder).bind(messages.get(position));
         }
    }

    @Override
    public int getItemCount() {
        if (messages == null){
            return 0;
        }
        return messages.size();
    }

    public List<MessagePlaneText> getMessages() {
        return messages;
    }

    public void setMessages(List<MessagePlaneText> messages) {
        this.messages = messages;
    }
    public void addNewMessage(MessagePlaneText msg){
        this.messages.add(0,msg);
    }
}
