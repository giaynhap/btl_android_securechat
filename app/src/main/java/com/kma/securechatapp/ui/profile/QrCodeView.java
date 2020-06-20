package com.kma.securechatapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.SocketLoginCommand;
import com.kma.securechatapp.core.api.model.SocketLoginDTO;
import com.kma.securechatapp.core.api.model.SocketLoginTranferData;
import com.kma.securechatapp.core.event.EventQrCodeMessage;
import com.kma.securechatapp.core.security.AES;
import com.kma.securechatapp.core.security.RSAUtil;
import com.kma.securechatapp.core.service.RealtimeService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.helper.CommonHelper;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class QrCodeView extends AppCompatActivity {
    private static Random rnd = new Random();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.qr_code_view)
    ImageView view;
    Disposable subcribeLogin ;

    public String randomCode(int codeSize){
        StringBuilder sb = new StringBuilder(codeSize);
        for(int i=0; i < codeSize; i++)
            sb.append((char)('A' + rnd.nextInt(24)));
        return sb.toString();
    }
    String genCode(){
            return  randomCode(10);
    }
    String genQrString(){
            String code =  genCode();
            String uuid = AppData.getInstance().currentUser.uuid;
            String hashCode =  code+"|"+uuid;
            String password = genCode();

        try {
            SocketLoginTranferData dataTransfer = new SocketLoginTranferData();
            dataTransfer.setToken(AppData.getInstance().getToken());
            dataTransfer.setRefresh(AppData.getInstance().getRefreshToken());
            dataTransfer.setKey(RSAUtil.base64Encode(AppData.getInstance().getPrivateKey().getEncoded()));

            String stringTransfer = new Gson().toJson(dataTransfer);


            String privateKeyPassword = RSAUtil.bytesToHex( RSAUtil.getSHA(hashCode + password));
            String privateKeyEncrypt = RSAUtil.base64Encode(AES.encrypt( stringTransfer , privateKeyPassword));



            String topicHash = RSAUtil.bytesToHex(RSAUtil.getSHA(code + uuid));

            subcribeLogin = RealtimeServiceConnection.getInstance().subscribeLoginQRTopic(topicHash, new EventQrCodeMessage() {
                @Override
                public SocketLoginDTO onMessage(SocketLoginDTO data) {
                    switch (data.getCommand()){
                        case PREHANDSHAKE:
                            CommonHelper.showLoading(QrCodeView.this );
                             if (data.getData().equals(code)){
                                    SocketLoginDTO message = new SocketLoginDTO();


                                    message.setData(privateKeyEncrypt);
                                    message.setCommand(SocketLoginCommand.TRANSFER);
                                    RealtimeServiceConnection.getInstance().unsubscribeLoginQrTopic(subcribeLogin);
                                     CommonHelper.hideLoading();
                                     QrCodeView.this.runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             QrCodeView.this.finish();
                                         }
                                     });
                                    return message;
                             }
                             break;
                    }
                    return null;
                }

            });

            return hashCode+"|"+privateKeyPassword;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_view);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        QRGEncoder qrgEncoder = new QRGEncoder("secure-hash|"+genQrString(), null, QRGContents.Type.TEXT, 1024);

        try {

            view.setImageBitmap( qrgEncoder.encodeAsBitmap());
        } catch (Exception e) {
            Log.v("GN", e.toString());
        }
    }

}
