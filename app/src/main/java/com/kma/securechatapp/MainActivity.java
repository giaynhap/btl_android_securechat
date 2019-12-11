package com.kma.securechatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.service.DataService;
import com.kma.securechatapp.core.service.RealtimeService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.ui.authentication.KeyPasswordActivity;
import com.kma.securechatapp.ui.authentication.LoginActivity;
import com.kma.securechatapp.utils.common.ImageLoader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public  static MainActivity instance;
    ApiInterface api = ApiUtil.getChatApi();
    @BindView(R.id.left_nav)
    NavigationView navLeft;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ImageLoader.getInstance().bind(this);


        AppData.getInstance().deviceId = Settings.Secure.getString(this.getApplication().getContentResolver(),
                Settings.Secure.ANDROID_ID);
      /*  if (BuildConfig.DEBUG){
            AppData.getInstance().setToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMDAtMDAwLTAwMDAiLCJleHAiOjE1OTM2MzIwNTksImlhdCI6MTU3NTYzMjA1OX0.hdJd6_z9Bw37RXEj8EF_rQAi6OJQeYxl1ewm7iTTEwDi8GBcGjOD5UecuFRY--Xt_EAwglJBFRG3FDbcq56_aA");
            AppData.getInstance().currentUser = new UserInfo("000-000-0000","GN","",null);
        }*/
       // else
            {

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
        }
        RealtimeServiceConnection.getInstance().bindService(this);
        if (AppData.getInstance().getToken() == null) {
            Intent intent2 = new Intent(this, LoginActivity.class);
            startActivity(intent2);

        }else{
            DataService.getInstance(null).save();
            Intent intent = new Intent(this, RealtimeService.class);
            startService(intent);
            RealtimeServiceConnection.getInstance().startService();

            try {
                LoginActivity.showInputPass(this,api);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DataService.getInstance(null).save();
        instance = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

    void bindNavLeft(){
        navLeft.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId()){

                            case R.id.menu_item_logout:
                               logout();
                                break;
                        }

                        return true;
                    }
                });
    }

    void logout(){
        AppData.getInstance().clean();
        DataService.getInstance(this).storeToken(null,null);
        DataService.getInstance(this).storeUserUuid(null);
        DataService.getInstance(this).save();
        RealtimeServiceConnection.getInstance().onlyDisconnectSocket();
        Intent intent2 = new Intent(this, LoginActivity.class);
        startActivity(intent2);
    }


}


/*

database:
124.158.6.219
3306
root/ht15181012

 */