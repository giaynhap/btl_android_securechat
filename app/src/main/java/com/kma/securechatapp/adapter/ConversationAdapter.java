package com.kma.securechatapp.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.ConversationViewHolder;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.model.Conversation;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter {
    List<Conversation> conversationList;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_item, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ConversationViewHolder conversationHolder = (ConversationViewHolder)holder;
        Conversation conversation = this.conversationList.get(position);
        String avatar_id = "";
        if (conversation.users.size()>1){
            avatar_id = conversation.users.get(1).uuid;
        }else{
            avatar_id = conversation.user_uuid;
        }
        conversationHolder.setAvatar(BuildConfig.HOST +"conversation/thumb/"+conversation.UUID+"/"+ AppData.getInstance().currentUser.uuid +"?width=80&height=80");
        conversationHolder.setTitle(conversation.name);
       // conversationHolder.setMessage(conversation.lastMessage);
        conversationHolder.setMessage("⚿ encrypted");
        conversationHolder.setTime(conversation.lastMessageAt);
        conversationHolder.setOnline(conversation.isOnline());
        conversationHolder.setNumUnRead(conversation.unRead);
    }

    @Override
    public int getItemCount() {
        if (conversationList != null){
            return conversationList.size();
        }
        return 0;
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
    }
}
