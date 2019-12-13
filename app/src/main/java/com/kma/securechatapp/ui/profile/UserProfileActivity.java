package com.kma.securechatapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle; 
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.helper.ImageLoadTask;
import com.kma.securechatapp.ui.conversation.InboxActivity;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.misc.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends AppCompatActivity {

    UserProfileViewModel userProfileViewModel;
    int type;
    String uuid;

    @BindView(R.id.profil_avatar)
    CircularImageView avartar;

    @BindView(R.id.profile_name)
    TextView profileName;

    @BindView(R.id.profile_address)
    TextView profileAddress;

    @BindView(R.id.profile_toolbar)
    Toolbar toolbar;

    @BindView(R.id.profile_action)
    LinearLayout layoutActionButton;

    @BindView(R.id.profile_btn_add_del_contact)
    Button addOrDelContact;

    @BindView(R.id.profile_btn_message)
    Button btnMessage;

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

        if (type == 0){
            layoutActionButton.setVisibility(View.INVISIBLE);
        }else
        {
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

    }

    void bindUserInfo(UserInfo info){
        if (info == null){
           onBackPressed();
        }

        ImageLoader.getInstance().DisplayImage(BuildConfig.HOST +"users/avatar/"+uuid+"?width=160&height=160",avartar);
        profileName.setText(info.name);
        profileAddress.setText(info.address);
    }

    @OnClick(R.id.profile_btn_add_del_contact)
    void onClickAddDelContact(View view){
            if ((int)addOrDelContact.getTag() == 1){
                userProfileViewModel.deleteContact(uuid);
            }else{
                userProfileViewModel.addContact(uuid);
            }
    }
    @OnClick(R.id.profile_btn_message)
    void onClickMessage(View view){
            userProfileViewModel.trigerConversation(uuid);
    }
}
