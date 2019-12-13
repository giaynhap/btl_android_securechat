package com.kma.securechatapp.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.UserKey;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.security.AES;
import com.kma.securechatapp.core.security.RSAUtil;
import com.kma.securechatapp.core.service.DataService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KeyPasswordActivity extends AppCompatActivity {
    @BindView(R.id.input_password)
    TextInputEditText inputPassword;
    @BindView(R.id.btn_save)
    Button btnSave;
    UserKey userKey ;
    int type;
    ApiInterface api = ApiUtil.getChatApi();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_password);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("data",1);
        try {
            if (type == 1){

                    String[] key = RSAUtil.generateKey();
                    userKey = new UserKey(key[0],key[1]);

            }else{
                userKey = new UserKey(null,null);
                userKey.publicKey = api.getPublicKey(AppData.getInstance().currentUser.uuid).execute().body().data;
                userKey.privateKey = api.getPrivateKey().execute().body().data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_save)
    void onSave(View view){
        String pass = inputPassword.getText().toString().trim();
        if (pass.isEmpty()) {
            return;
        }
        if (type == 1) {

            String privateKey = userKey.privateKey; ;
            userKey.privateKey = RSAUtil.base64Encode(AES.encrypt( RSAUtil.base64Decode(userKey.privateKey) , pass));
            DataService.getInstance(this).storePrivateKey(AppData.getInstance().currentUser.uuid, privateKey,AppData.getInstance().password);
            DataService.getInstance(this).save();

            try {
                api.updateKey(userKey).execute();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,"Something error!!",Toast.LENGTH_SHORT).show();
            }
            userKey.privateKey = privateKey;
            AppData.getInstance().userKey = userKey;
            finishActivity(0);
            finish();

        }else{

            try {
                String priateKey = RSAUtil.base64Encode(AES.decryptBinary(RSAUtil.base64Decode(userKey.privateKey),pass));

                byte[] data = RSAUtil.RSAEncrypt("Helloworld",RSAUtil.stringToPublicKey(userKey.publicKey));

                String data2 = RSAUtil.RSADecrypt(data,RSAUtil.stringToPrivateKey(priateKey));
                if (data2.endsWith("Helloworld"))
                {
                    userKey.privateKey = priateKey;
                    AppData.getInstance().userKey = userKey;
                    DataService.getInstance(this).storePrivateKey(AppData.getInstance().currentUser.uuid, userKey.privateKey,AppData.getInstance().password);
                    finishActivity(0);
                    finish();
                }
                else{
                    Toast.makeText(this,"Private key password not match",Toast.LENGTH_SHORT).show();
                }
            }  catch (Exception e)
            {
                Toast.makeText(this,"Something error!!",Toast.LENGTH_SHORT).show();
            }


        }
        DataService.getInstance(this).save();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            EventBus.getInstance().pushOnLogout(AppData.getInstance().currentUser);
        }
        return super.onKeyDown(keyCode, event);
    }
}
