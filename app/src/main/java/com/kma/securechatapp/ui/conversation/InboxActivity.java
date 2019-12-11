package com.kma.securechatapp.ui.conversation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.MessageAdapter;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.MessageCommand;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.receivers.SocketReceiver;
import com.kma.securechatapp.core.service.RealtimeService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.core.service.ServiceAction;
import com.kma.securechatapp.ui.contact.ContactAddViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

import static butterknife.OnTextChanged.Callback.BEFORE_TEXT_CHANGED;

public class InboxActivity extends AppCompatActivity implements  SocketReceiver.OnSocketMessageListener, SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.message_toolbar)
    Toolbar toolbar;
    @BindView(R.id.reyclerview_message_list)
    RecyclerView recyclerView;
    @BindView(R.id.inbox_status)
    TextView txtStatus;
    @BindView(R.id.load_more)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.process_label)
    TextView processLabel;

    MessageAdapter messageAdapter = new MessageAdapter();
    String uuid ;
    private InboxViewModel inboxViewModel ;

    SocketReceiver receiver = new SocketReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        inboxViewModel =  ViewModelProviders.of(this).get(InboxViewModel.class);
        ButterKnife.bind(this);
        Intent actIntent = getIntent();
        receiver.setListener(this);

          uuid = actIntent.getStringExtra("uuid");
        if (uuid == null ){
            onBackPressed();
        }

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setTitle("Inbox");


        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        inboxViewModel.getConversationInfo().observe(this,conversation -> {
            if (conversation == null){
                onBackPressed();
                return;
            }
            toolbar.setTitle(conversation.name);
        });
        inboxViewModel.getMessages().observe(this,messages->{
            refreshLayout.setRefreshing(false);
            messageAdapter.setMessages(messages);
            messageAdapter.notifyDataSetChanged();
            processLabel.setVisibility(View.GONE);

        });

        inboxViewModel.getMessage().observe(this,message->{
            messageAdapter.addNewMessage(message);
            messageAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
            processLabel.setVisibility(View.GONE);
        });
        inboxViewModel.setConversationUuid(uuid);
        inboxViewModel.trigerLoadMessage(0);


        refreshLayout.setOnRefreshListener(this);
        txtStatus.setVisibility(View.GONE);
        register();
    }

    void register(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceAction.REVC_MESSAGE);
        filter.addAction(ServiceAction.REVC_READ);
        filter.addAction(ServiceAction.REVC_TYPING);

        registerReceiver(receiver, filter);
        RealtimeServiceConnection.getInstance().registThreadToSoft(uuid);
        RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.READ,1,uuid);
    }

    @BindView(R.id.edittext_chatbox)
    EditText edit;
    @OnFocusChange(R.id.edittext_chatbox)
    public void onChatChange(View v, boolean hasFocus) {
        if (hasFocus) {
            RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.TYPING,1,uuid);
            RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.READ,1,uuid);
            typing = true;
        }else{
            RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.TYPING,0,uuid);
            typing = false;
        }
    }
    boolean typing = false;
    @OnClick(R.id.button_chatbox_send)
    void onSend(View view){
        String sendMessage = edit.getText().toString().trim();

        if (sendMessage.isEmpty())
        {
            return ;
        }

        if (!RealtimeServiceConnection.getInstance().send(sendMessage,uuid,inboxViewModel.getConversation().users)){
            Toast.makeText(this,"Something error, can't send message",Toast.LENGTH_SHORT).show();
        }else{
            edit.setText("");
            txtStatus.setVisibility(View.GONE);
            typing = false;
        }
    }
    @OnTextChanged(value = R.id.edittext_chatbox, callback = BEFORE_TEXT_CHANGED)
    public void  onChangeText(CharSequence text)
    {
        if ( typing == false)
        {
            typing = true;
            RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.TYPING,1,uuid);
            RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.READ,1,uuid);
        }
    }


    @Override
    public void onNewMessage(MessagePlaneText message) {
        Log.d("Test","THIS "+message.mesage);
        if (!message.threadUuid.equals(this.uuid)){
            return;
        }
        inboxViewModel.trigerNewMessage(message);
        txtStatus.setVisibility(View.GONE);
    }


    @Override
    public void onTyping(String conversationId, String userUuid, boolean typing) {
        if (typing && !userUuid.equals(AppData.getInstance().currentUser.uuid)){
            String user = "";
            for (UserInfo u : inboxViewModel.getConversation().users){
                if (u.uuid.equals(userUuid)){
                    user = u.name;
                }
            }
            txtStatus.setText(user+" typing ....");
            txtStatus.setVisibility(View.VISIBLE);
        }else
        {
            txtStatus.setVisibility(View.GONE);
        }
    }


    @Override
    public void onRead(String conversationId, String username) {

    }

    @Override
    protected void onDestroy() {
        RealtimeServiceConnection.getInstance().unRegistThreadToSoft(uuid);
        unregisterReceiver(receiver);

        if ( typing)
        {
            RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.TYPING,0,uuid);
        }
        super.onDestroy();

    }

    @Override
    public void onRefresh() {
        inboxViewModel.loadMore();
    }
}
