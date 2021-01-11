package com.kma.securechatapp;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.AuthenRequest;
import com.kma.securechatapp.core.api.model.Device;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.api.model.UserKey;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.service.CacheService;
import com.kma.securechatapp.core.service.DataService;
import com.kma.securechatapp.core.service.RealtimeService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.helper.CommonHelper;
import com.kma.securechatapp.ui.about.AboutActivity;
import com.kma.securechatapp.ui.authentication.KeyPasswordActivity;
import com.kma.securechatapp.ui.authentication.LoginActivity;
import com.kma.securechatapp.ui.contact.ContactAddActivity;
import com.kma.securechatapp.ui.profile.SettingsActivity;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.common.EncryptFileLoader;
import com.kma.securechatapp.utils.common.GFingerPrint;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.common.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;

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
    @BindView(R.id.main_status)
    TextView tvMainStatus;

    private androidx.biometric.BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
    GFingerPrint gFingerPrint ;
    private Executor executor;

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

        tvMainStatus.setText("Offline mode");
        tvMainStatus.setVisibility(View.GONE);

        settingFingerPrint();
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
            AppData.getInstance().setRefreshToken(DataService.getInstance(this).getRefreshtoken());
            AppData.getInstance().setUserUUID(DataService.getInstance(this).getUserUuid());
            AppData.getInstance().account = DataService.getInstance(this).getUserAccount();
        }

        RealtimeServiceConnection.getInstance().bindService(this);
      // if (AppData.getInstance().getToken() == null) {
            Intent intent2 = new Intent(this, LoginActivity.class);
            startActivity(intent2);

      /*  }else{
            DataService.getInstance(null).save();
            EventBus.getInstance().pushOnLogin(AppData.getInstance().currentUser);
        }*/
//        Intent intent2 = new Intent(this, LoginActivity.class);
//        startActivity(intent2);

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

    public void settingFingerPrint(){
        gFingerPrint = new GFingerPrint(this);
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this , executor, new BiometricPrompt.AuthenticationCallback(){
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText( MainActivity.this,
                        "Lỗi vân tay: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);


                Cipher cipher =  result.getCryptoObject().getCipher();
                String encryptPassword = gFingerPrint.encryptPassword(cipher,AppData.getInstance().password);

                DataService.getInstance(null).storeFingerPassword(AppData.getInstance().userUUID,encryptPassword);
                DataService.getInstance(null).save();
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sử dụng vân tay để đăng nhập")
                .setNegativeButtonText("Không sử dụng vân tay")
                .build();

    }

    public void checkShowConfirmFigerPrint(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return;
        }
        if (gFingerPrint == null || biometricPrompt== null ){
            return;
        }
        if (!gFingerPrint.initKeyStore()){
            return ;
        }
        if (AppData.getInstance().password != null && DataService.getInstance(MainActivity.this).getFingerSaved(AppData.getInstance().userUUID) == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Bạn có muốn sử dụng đăng nhập nhanh bằng dấu vây tay không").setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Cipher cipher = gFingerPrint.createCipher(Cipher.ENCRYPT_MODE);
                        biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
                    }  catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();

            // show it
            alertDialog.show();
        }
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
            public void onConnectedSocket(){
                MainActivity.this.tvMainStatus.setVisibility(View.GONE);
            }
            @Override
            public void onDisconnectedSocket(){
                MainActivity.this.tvMainStatus.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNetworkStateChange(int state){
                Log.d("GIAYNHAP","NET WORD STATE "+state);
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
                    if (AppData.getInstance().password != null) {
                        CacheService.getInstance().init(MainActivity.this, CacheService.getInstance().accountToDbName(AppData.getInstance().userUUID), AppData.getInstance().password);
                    }
                    if (AppData.getInstance().currentUser != null ) {
                        // online
                        LoginActivity.showInputPass(MainActivity.this,api);
                    } else {
                        tvMainStatus.setVisibility(View.VISIBLE);
                        // offline login
                        AppData.getInstance().currentUser =  CacheService.getInstance().getUser(AppData.getInstance().userUUID);
                        //restore user key

                        String privateKey = DataService.getInstance(MainActivity.this.getApplicationContext()).getPrivateKey(AppData.getInstance().userUUID,AppData.getInstance().password);
                        if (privateKey != null){
                            UserKey userKey  =  new UserKey(null,null);
                            userKey.publicKey =  AppData.getInstance().currentUser.publicKey;
                            userKey.privateKey = privateKey ;
                            AppData.getInstance().userKey = userKey;

                            if (AppData.getInstance().getPrivateKey() == null){
                                EventBus.getInstance().noticShow("Chưa thiết lập private key","Lỗi đăng nhập");
                            }
                        }
                    }
                    CacheService.getInstance().saveUser(AppData.getInstance().currentUser, AppData.getInstance().account);

                    bindLeftHeader();
                    EventBus.getInstance().pushOnRefreshConversation();
                    EventBus.getInstance().pushOnRefreshContact();


                    checkShowConfirmFigerPrint();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLogout(UserInfo u){
                AppData.getInstance().clean();
                DataService.getInstance(MainActivity.this).storeToken(null,null);
                DataService.getInstance(MainActivity.this).storeFingerPassword(AppData.getInstance().userUUID,null);
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
