package com.kma.securechatapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.service.DataService;
import com.kma.securechatapp.core.service.RealtimeService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.ui.about.AboutActivity;
import com.kma.securechatapp.ui.authentication.KeyPasswordActivity;
import com.kma.securechatapp.ui.authentication.LoginActivity;
import com.kma.securechatapp.ui.contact.ContactAddActivity;
import com.kma.securechatapp.ui.profile.SettingsActivity;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.common.EncryptFileLoader;
import com.kma.securechatapp.utils.common.ImageLoader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public  static MainActivity instance;
    ApiInterface api = ApiUtil.getChatApi();
    @BindView(R.id.left_nav)
    NavigationView navLeft;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    NavController navController;
    EventBus.EvenBusAction evenBus;

    class NavigateHeaderBind{
        @BindView(R.id.h_user_name)
        TextView leftUserName;
        @BindView(R.id.h_user_status)
        TextView leftUserStatus;
        @BindView(R.id.h_avatar)
        ImageView leftUserAvatr;
        @OnClick(R.id.h_avatar)
        void onClickProfile(View view)
        {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);

            intent.putExtra("uuid",AppData.getInstance().currentUser.uuid);
            intent.setAction("view_profile");
            startActivity(intent);
        }
    }
    NavigateHeaderBind navHeaderBind = new NavigateHeaderBind();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ButterKnife.bind(navHeaderBind, navLeft.getHeaderView(0));
        instance = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ImageLoader.getInstance().bind(this);
        EncryptFileLoader.getInstance().bind(this);

        Intent intent = new Intent(MainActivity.this, RealtimeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // startForegroundService(intent);
            startService(intent);
        } else {
            startService(intent);
        }

        register();





        AppData.getInstance().deviceId = Settings.Secure.getString(this.getApplication().getContentResolver(),
                Settings.Secure.ANDROID_ID);
      /*  if (BuildConfig.DEBUG){
            AppData.getInstance().setToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMDAtMDAwLTAwMDAiLCJleHAiOjE1OTM2MzIwNTksImlhdCI6MTU3NTYzMjA1OX0.hdJd6_z9Bw37RXEj8EF_rQAi6OJQeYxl1ewm7iTTEwDi8GBcGjOD5UecuFRY--Xt_EAwglJBFRG3FDbcq56_aA");
            AppData.getInstance().currentUser = new UserInfo("000-000-0000","GN","",null);
        }*/
       // else


        if (DataService.getInstance(this).getToken() != null) {

            AppData.getInstance().setToken(DataService.getInstance(this).getToken());

            try {
                AppData.getInstance().currentUser = api.getCurrenUserInfo().execute().body().data;
            } catch (Exception e) {
                AppData.getInstance().currentUser = null;
            }
            if (AppData.getInstance().currentUser == null){
                AppData.getInstance().setToken(null);
                DataService.getInstance(this).storeToken(null,null);
            }else{
                AppData.getInstance().setRefreshToken(DataService.getInstance(this).getRefreshtoken());
                DataService.getInstance(this).storeUserUuid(AppData.getInstance().currentUser.uuid);

            }
        }

        RealtimeServiceConnection.getInstance().bindService(this);
        if (AppData.getInstance().getToken() == null) {
            Intent intent2 = new Intent(this, LoginActivity.class);
            startActivity(intent2);

        }else{
            DataService.getInstance(null).save();
            EventBus.getInstance().pushOnLogin(AppData.getInstance().currentUser);
        }
        DataService.getInstance(null).save();

        getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard,R.id.navigation_conversation, R.id.navigation_notifications)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        bindNavLeft();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            navController.navigate(R.id.navigation_dashboard);
        }

    }
    void register(){

        evenBus = new EventBus.EvenBusAction(){
            @Override
            public void onNetworkStateChange(int state){

            }
            @Override
            public  void onChangeProfile(){
                bindLeftHeader();
            }
            @Override
            public  void onLogin(UserInfo u){
                try {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            RealtimeServiceConnection.getInstance().restart();
                        }
                    });
                    LoginActivity.showInputPass(MainActivity.this,api);
                    bindLeftHeader();
                    EventBus.getInstance().pushOnRefreshConversation();
                    EventBus.getInstance().pushOnRefreshContact();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLogout(UserInfo u){
                AppData.getInstance().clean();
                DataService.getInstance(MainActivity.this).storeToken(null,null);
                DataService.getInstance(MainActivity.this).storeUserUuid(null);
                DataService.getInstance(MainActivity.this).save();
                RealtimeServiceConnection.getInstance().onlyDisconnectSocket();
                Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent2);

            }

        };
        EventBus.getInstance().addEvent(evenBus);

    }
    void bindLeftHeader(){
        navHeaderBind.leftUserName.setText(AppData.getInstance().currentUser.name);
        navHeaderBind.leftUserStatus.setText("Online");
        ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl(AppData.getInstance().currentUser.uuid,200,200),navHeaderBind.leftUserAvatr);
    }
    void bindNavLeft(){
        navLeft.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent;
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId()){

                            case R.id.menu_item_logout:
                               logout();
                                break;
                            case R.id.menu_item_about:
                                  intent = new Intent(MainActivity.this, AboutActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.menu_item_setting:
                                  intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;
                        }

                        return true;
                    }
                });
    }

    void logout(){
        EventBus.getInstance().pushOnLogout(AppData.getInstance().currentUser);
    }


}
