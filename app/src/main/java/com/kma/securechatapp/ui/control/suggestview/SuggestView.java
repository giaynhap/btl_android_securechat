package com.kma.securechatapp.ui.control.suggestview;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.SuggestViewAdapter;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.ui.conversation.ConversationListFragment;
import com.kma.securechatapp.ui.conversation.InboxActivity;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.misc.RecyclerItemClickListener;

import java.util.List;

public class SuggestView {
    RecyclerView recyclerView;
    SuggestViewAdapter adapter;

    public SuggestView(Activity activity, View layout){

        recyclerView = (RecyclerView)layout.findViewById(R.id.suggest_recycler_view);
        adapter = new SuggestViewAdapter();

        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(activity, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        UserInfo userInfo = adapter.getUsers().get(position);
                        Intent intent1 = new Intent(activity, UserProfileActivity.class);
                        intent1.putExtra("uuid",userInfo.uuid);
                        intent1.setAction("view_user");
                        activity.startActivity(intent1);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

    }

    public void upDateList(List<UserInfo> users){
        adapter.setUsers(users);
        adapter.notifyDataSetChanged();
    }

}
