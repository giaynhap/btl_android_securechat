package com.kma.securechatapp.ui.conversation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.MessageAdapter;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.MessageCommand;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.receivers.SocketReceiver;
import com.kma.securechatapp.core.service.RealtimeService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.core.service.ServiceAction;
import com.kma.securechatapp.ui.contact.ContactAddViewModel;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static butterknife.OnTextChanged.Callback.BEFORE_TEXT_CHANGED;

public class InboxActivity extends AppCompatActivity implements  SocketReceiver.OnSocketMessageListener, SwipeRefreshLayout.OnRefreshListener{

    ApiInterface api = ApiUtil.getChatApi();
    public static final int PICK_IMAGE = 1;
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
    @BindView(R.id.btn_image)
    ImageView btnImage;

    @BindView(R.id.layout_upload)
    LinearLayout uploadLayout;



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

        if (!RealtimeServiceConnection.getInstance().send(0,sendMessage,uuid,inboxViewModel.getConversation().users)){
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

    @OnClick(R.id.btn_image)
    void onUploadImage(View view){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);

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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE  && resultCode == RESULT_OK && data != null && data.getData() !=null ) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    return;
            }

            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath,null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String FilePathStr = c.getString(columnIndex);
            c.close();


            File file = new File(FilePathStr);

            onChooseImageFile (file,selectedImage);

        }
    }

    void onChooseImageFile(File file,Uri uri){

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(160,160);
        imageView.setLayoutParams(params);
        imageView.setImageAlpha(100);
        imageView.setImageURI(uri);
        uploadLayout.addView(imageView);
        new UploadItem(imageView, api.uploadImage(body));
    }
    class UploadItem{
        ImageView imageView;
        public UploadItem(ImageView imageView, Call<ApiResponse<String>> call){
            this.imageView = imageView;
            call.enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.body() == null || response.body().data == null){
                        Toast.makeText(InboxActivity.this,"Upload image error!!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    onComplete(response.body().data);
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Toast.makeText(InboxActivity.this,"Upload image error!!",Toast.LENGTH_SHORT).show();
                    onComplete(null);
                }
            });
        }
        public void onComplete(String url){
            uploadLayout.removeView(imageView);
            if (!RealtimeServiceConnection.getInstance().send(1,url,uuid,inboxViewModel.getConversation().users)){
                Toast.makeText(InboxActivity.this,"Something error, can't send message",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
