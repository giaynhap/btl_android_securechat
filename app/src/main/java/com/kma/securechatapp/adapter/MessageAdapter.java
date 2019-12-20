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
import com.kma.securechatapp.utils.common.StringHelper;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends   RecyclerView.Adapter {
    List<MessagePlaneText> messages;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType){

            case 0:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new MessageSenderViewHolder(view);

            case 10:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_image_sent, parent, false);
                return new MessageSenderViewHolder(view);
            case 20:

                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_audio_sent, parent, false);
                return new MessageSenderViewHolder(view);
            case 30:

                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_sticker_sent, parent, false);
                return new MessageSenderViewHolder(view);
            case 11:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_image_received, parent, false);
                return new MessageReceivederViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new MessageReceivederViewHolder(view);
            case 21:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_audio_received, parent, false);
                return new MessageReceivederViewHolder(view);
            case 31:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_sticker_received, parent, false);
                return new MessageReceivederViewHolder(view);

        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        MessagePlaneText message = (MessagePlaneText) messages.get(position);

        if (message.senderUuid.equals(AppData.getInstance().currentUser.uuid)) {
            // If the current user is the sender of the message
            if (message.type==0)
                 return 0 ;
            else if (message.type == 1)
                return 10;
            else if (message.type == 2){
                return 20;
            }
            else if (message.type == 3){
                return 30;
            }
        } else {
            // If some other user sent the message
            if (message.type==0)
                return 1 ;
            else if (message.type == 1)
                return 11;
            else if (message.type == 2){
                return 21;
            }
            else if (message.type == 3 ){
                return 31;
            }
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        boolean hide = false;
        boolean showTime = false;
        if (position < messages.size()-1 ){
            MessagePlaneText msg1 = messages.get(position);
            MessagePlaneText msg2 = messages.get(position+1);
            if(msg1.senderUuid.equals(msg2.senderUuid) &&
                    StringHelper.checkSameDay(msg1.time,msg2.time)
            ){
                hide = true;
            }
            if ( !StringHelper.checkSameDay(msg1.time,msg2.time)){
                showTime = true;
            }
        }else{
            showTime = true;
        }

         if (holder.getItemViewType() == 1|| holder.getItemViewType() == 11|| holder.getItemViewType()==21|| holder.getItemViewType()==31){
             ((MessageReceivederViewHolder)holder).bind(messages.get(position),hide);
             if (showTime)
                 ((MessageReceivederViewHolder)holder).showLongTime();

         }else{
             ((MessageSenderViewHolder)holder).bind(messages.get(position),hide);
             if (showTime)
                 ((MessageSenderViewHolder)holder).showLongTime();
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
