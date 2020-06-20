package com.kma.securechatapp.ui.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.KeyEvent;

import com.kma.securechatapp.AppPermission;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.UserKey;
import com.kma.securechatapp.core.service.DataService;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_account, R.id.navigation_password)
                .build();
        setSupportActionBar(toolbar);
        navController = Navigation.findNavController(this, R.id.nav_login);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(toolbar, navController);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        AppPermission.requireAll(this);

    }
    @Override
    public void setTitle(CharSequence title){

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            if ( navController.getCurrentDestination().getId() == R.id.navigation_account){
                finishAffinity();
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    public static void  showInputPass(Context context,ApiInterface api) throws IOException {
        boolean show = false;
        Intent intent = new Intent(context,KeyPasswordActivity.class);
        Response<ApiResponse<String>> response =  api.getPublicKey(AppData.getInstance().currentUser.uuid).execute();
        if (response.body() == null || response.body().error == 1){

            intent.putExtra("data",1);
            show = true;
        }
        else {
            String privateKey = DataService.getInstance(context).getPrivateKey(AppData.getInstance().currentUser.uuid,AppData.getInstance().password);
            if ( privateKey == null){
                intent.putExtra("data",2);
                show = true;
            }else{
                UserKey userKey  =  new UserKey(null,null);
                userKey.publicKey = api.getPublicKey(AppData.getInstance().currentUser.uuid).execute().body().data;
                userKey.privateKey = privateKey ;
                AppData.getInstance().userKey = userKey;

                if (AppData.getInstance().getPrivateKey() == null){
                    intent.putExtra("data",2);
                    show = true;
                }

            }
        }
        if (show){
            context.startActivity(intent);
        }
    }


}
