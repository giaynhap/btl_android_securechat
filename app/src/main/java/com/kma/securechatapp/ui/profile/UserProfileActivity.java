package com.kma.securechatapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.helper.ImageLoadTask;
import com.kma.securechatapp.ui.contact.ContactListFragment;
import com.kma.securechatapp.ui.conversation.InboxActivity;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.common.Utils;
import com.kma.securechatapp.utils.misc.CircularImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class UserProfileActivity extends AppCompatActivity {

    UserProfileViewModel userProfileViewModel;
    int type;
    String uuid;
    public static final int PICK_IMAGE = 1;

    @BindView(R.id.profil_avatar)
    CircularImageView avartar;

    @BindView(R.id.profile_name)
    TextView profileName;

    @BindView(R.id.profile_address)
    TextView profileAddress;

    @BindView(R.id.profile_phone)
    TextView profilePhone;

    @BindView(R.id.profile_toolbar)
    Toolbar toolbar;

    @BindView(R.id.profile_action)
    LinearLayout layoutActionButton;

    @BindView(R.id.profile_btn_add_del_contact)
    Button addOrDelContact;

    @BindView(R.id.profile_btn_message)
    Button btnMessage;

    @BindView(R.id.btn_edit_name)
    ImageView btnEditName;

    @BindView(R.id.ed_profile_name)
    TextInputEditText edProfileName;

    @BindView(R.id.scan_qrcode)
    ImageView imgBtScanQr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle("");


        Intent intent = this.getIntent();
        type = (intent.getAction().equals("view_user"))?1:0;
        uuid = intent.getStringExtra("uuid");
        if (uuid.equals(AppData.getInstance().currentUser.uuid)){
            type = 0;
        }


        userProfileViewModel =  ViewModelProviders.of(this).get(UserProfileViewModel.class);


        userProfileViewModel.getUsrInfo().observe(this,userInfo -> {
            bindUserInfo(userInfo);
        });

        userProfileViewModel.trigerUserInfo(uuid);
        edProfileName.setVisibility(View.GONE);

        if (type == 0){
            layoutActionButton.setVisibility(View.GONE);
            btnEditName.setVisibility(View.VISIBLE);

        }else
        {

            btnEditName.setVisibility(View.GONE);
            layoutActionButton.setVisibility(View.VISIBLE);
            userProfileViewModel.getHasContact().observe(this,aBoolean -> {
                    if (aBoolean){
                        addOrDelContact.setText("Delete contact");
                        addOrDelContact.setTag(1);
                    }else
                    {
                        addOrDelContact.setText("Add contact");
                        addOrDelContact.setTag(0);
                    }
            });
            userProfileViewModel.trigerCheckHasContact(uuid);
        }

        userProfileViewModel.getConversion().observe(this, conversation -> {
                if (conversation!= null) {
                    Intent intent1 = new Intent(this.getApplication(), InboxActivity.class);
                    intent1.putExtra("uuid", conversation.UUID);
                    startActivity(intent1);
                }
        });

        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edProfileName.getVisibility() == View.GONE) {
                    edProfileName.setVisibility(View.VISIBLE);
                    profileName.setVisibility(View.GONE);
                    edProfileName.setText(profileName.getText());
                }else{

                    edProfileName.setVisibility(View.GONE);
                    profileName.setVisibility(View.VISIBLE);
                    Utils.hideKeyboard(UserProfileActivity.this);
                }
            }
        });

        edProfileName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String name = edProfileName.getText().toString().trim();
                    if (name.length() < 6){
                        return true;
                    }
                    userProfileViewModel.quickChangeName(name).enqueue(new Callback<ApiResponse<UserInfo>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<UserInfo>> call, Response<ApiResponse<UserInfo>> response) {
                            if (response.body()!= null && response.body().error == 0){
                                profileName.setText(name);
                                AppData.getInstance().currentUser = response.body().data;
                                EventBus.getInstance().pushOnChangeProfile();
                            }else{

                            }
                            edProfileName.setVisibility(View.GONE);
                            profileName.setVisibility(View.VISIBLE);
                            Utils.hideKeyboard(UserProfileActivity.this);

                        }

                        @Override
                        public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {
                            edProfileName.setVisibility(View.GONE);
                            profileName.setVisibility(View.VISIBLE);
                            Utils.hideKeyboard(UserProfileActivity.this);
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        imgBtScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, QrCodeView.class);
                startActivity(intent);
            }
        });
    }

    void bindUserInfo(UserInfo info){
        if (info == null){
           onBackPressed();
        }

        ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl(uuid,200,200),avartar);
        profileName.setText(info.name);
        if (info.address == null) {
            profileAddress.setText(" N/a");
        }
        else{
            profileAddress.setText(info.address);
        }
        if (info.phone == null){

            profilePhone.setText(" N/a");
        }else {
            profilePhone.setText(info.phone);
        }

        if (info.getPublicKey() == null){
            btnMessage.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.profile_btn_add_del_contact)
    void onClickAddDelContact(View view){
            if ((int)addOrDelContact.getTag() == 1){
                userProfileViewModel.deleteContact(uuid);
            }else{
                userProfileViewModel.addContact(uuid);
            }
        EventBus.getInstance().pushOnRefreshContact();
    }
    @OnClick(R.id.profile_btn_message)
    void onClickMessage(View view){
            userProfileViewModel.trigerConversation(uuid);
    }
    @OnClick(R.id.profil_avatar)
    void onClickAvatar(View view){
        if (type  != 0 )
            return;
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PICK_IMAGE  && resultCode == RESULT_OK && data != null && data.getData() !=null ) {



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


        userProfileViewModel.uploadAvatar(body, new UserProfileViewModel.UploadAvatarListener() {
            @Override
            public void onSuccess(String url) {

                ImageLoader.getInstance().DisplayImage( ImageLoader.getUserAvatarUrl(uuid,200,200),avartar,true);

            }

            @Override
            public void onFalse(int code, String message) {
                Toast.makeText(UserProfileActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
