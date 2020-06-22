package com.kma.securechatapp.ui.conversation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.MainActivity;
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
import com.kma.securechatapp.ui.conversation.Inbox.ChatFragment;
import com.kma.securechatapp.ui.conversation.Inbox.StickerFragment;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.common.AudioRecorder;
import com.kma.securechatapp.utils.common.EncryptFileLoader;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.common.Utils;
import com.kma.securechatapp.utils.misc.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.View.GONE;
import static butterknife.OnTextChanged.Callback.BEFORE_TEXT_CHANGED;

public class InboxActivity extends AppCompatActivity implements  SocketReceiver.OnSocketMessageListener{

    ApiInterface api = ApiUtil.getChatApi();
    public static final int PICK_IMAGE = 1;
    @BindView(R.id.message_toolbar)
    Toolbar toolbar;

    @BindView(R.id.inbox_status)
    TextView txtStatus;

    @BindView(R.id.process_label)
    TextView processLabel;
    @BindView(R.id.btn_image)
    ImageView btnImage;

    @BindView(R.id.layout_upload)
    LinearLayout uploadLayout;

    @BindView(R.id.edittext_chatbox)
    EditText edit;
    @BindView(R.id.layout_media)
    LinearLayout mediaLayout;
    @BindView(R.id.button_chatbox_send)
    Button btnSend;

    @BindView(R.id.panel_audio)
    LinearLayout panelAudio;

    @BindView(R.id.fragment_bottom)
    View fragmentBottom;
    AudioRecorder recoder  = null;
    FragmentManager fm;

    public ChatFragment.ChatUiEvent uiEvent = null;
    String uuid ;
    private InboxViewModel inboxViewModel ;

    boolean isShowSticker = false;

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


        inboxViewModel.getConversationInfo().observe(this,conversation -> {
            if (conversation == null){
                onBackPressed();
                return;
            }
            toolbar.setTitle(conversation.name);
            inboxViewModel.trigerLoadMessage(0);
            // toolbar.setImageURI()
            ImageLoader.getInstance().loadBitmap(BuildConfig.HOST + "conversation/thumb/" + conversation.UUID + "/" + AppData.getInstance().currentUser.uuid + "?width=80&height=80",bm -> {
                try {
                    toolbar.setLogo(new BitmapDrawable(getResources(), CircularImageView.getRoundBitmap(bm, bm.getWidth())));
                }catch (Exception e){

                }
            },false);

        });
        inboxViewModel.getMessages().observe(this,messages->{
            if (uiEvent!=null){
                uiEvent.loadedMessages(messages);
            }
            processLabel.setVisibility(GONE);

        });

        inboxViewModel.getMessage().observe(this,message->{
            if (uiEvent!=null){
                uiEvent.revcNewMessage(message);
            }
            processLabel.setVisibility(GONE);
        });
        inboxViewModel.setConversationUuid(uuid);

        fm = getSupportFragmentManager();



        txtStatus.setVisibility(GONE);
        btnSend.setVisibility(GONE);
        panelAudio.setVisibility(GONE);
        fragmentBottom.setVisibility(GONE);
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
    @OnClick(R.id.btn_sticker)
    public void onClickSticker(View view){
        isShowSticker = !isShowSticker;
        if (!isShowSticker)
            fragmentBottom.setVisibility(GONE);
        else {
            Utils.hideKeyboard(this);
            fragmentBottom.setVisibility(View.VISIBLE);
        }
    }

    public void sendSticker(String model,int index){
        inboxViewModel.send(3, model+"::"+index,uuid);
    }

    @OnFocusChange(R.id.edittext_chatbox)
    public void onChatChange(View v, boolean hasFocus) {
        if (hasFocus) {
            RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.TYPING,1,uuid);
            RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.READ,1,uuid);
            typing = true;
            mediaLayout.setVisibility(GONE);
            btnSend.setVisibility(View.VISIBLE);
            if (isShowSticker) {
                fragmentBottom.setVisibility(GONE);
                isShowSticker = false;
            }
        }else{
            Utils.hideKeyboard(this,v);
            RealtimeServiceConnection.getInstance().sendStatus(MessageCommand.TYPING,0,uuid);
            typing = false;
            mediaLayout.setVisibility(View.VISIBLE);
            btnSend.setVisibility(GONE);

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

        if (!inboxViewModel.send(0,sendMessage,uuid)){
            Toast.makeText(this,"Something error, can't send message",Toast.LENGTH_SHORT).show();
        }else{
            edit.setText("");
            txtStatus.setVisibility(GONE);
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

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE

                    }, 1);

        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);

    }
    boolean uploadingAudip = false;
    @OnClick(R.id.btn_audio)
    void onClickAudio (View views){
        if (recoder != null){
            stopRecoder (0);
            recoder = null;
            panelAudio.setVisibility(GONE);
            return;
        }
         recoder = new AudioRecorder("rc"+uuid,this.getApplicationContext());
        try {
            panelAudio.setVisibility(View.VISIBLE);
            recoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void stopRecoder(int type){
        Log.d("test","Stop recoder");
        try {
            uploadingAudip = true;
            panelAudio.setVisibility(GONE);
            if (recoder != null) {
                recoder.stop();
            }

            if (type == 1){
                File file = recoder.getFile();
                file = EncryptFileLoader.getInstance().encryptFile(file,inboxViewModel.key);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                api.uploadAudio(body).enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                        uploadingAudip = false;
                        String url = response.body().data;
                        if (! inboxViewModel.send(2,url,uuid) ){
                            Toast.makeText(InboxActivity.this,"Something error, can't send message",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        uploadingAudip = false;

                    }
                });

                recoder = null;
            }else{
                recoder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.panel_audio)
    void audioTouch(View view){
        stopRecoder(1);
    }

    @Override
    public void onNewMessage(MessagePlaneText message) {
        Log.d("Test","THIS "+message.mesage);
        if (!message.threadUuid.equals(this.uuid)){
            return;
        }
        inboxViewModel.trigerNewMessage(message);
        txtStatus.setVisibility(GONE);
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
            txtStatus.setVisibility(GONE);
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
            try {
                file =  EncryptFileLoader.getInstance().encryptFile(file,inboxViewModel.key);
                onChooseImageFile (file,selectedImage);
            } catch (Exception e) {
              Toast.makeText(this,"Encrypt file error ",Toast.LENGTH_SHORT).show();
            }

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
            if ( url== null){
                return;
            }
            if (!inboxViewModel.send(1,url,uuid)){
                Toast.makeText(InboxActivity.this,"Something error, can't send message",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            if (this.isShowSticker){
                isShowSticker = !isShowSticker;
                fragmentBottom.setVisibility(GONE);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
