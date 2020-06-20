package com.kma.securechatapp.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.MessageCommand;
import com.kma.securechatapp.core.SocketLoginCommand;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.SocketLoginDTO;
import com.kma.securechatapp.core.api.model.SocketLoginTranferData;
import com.kma.securechatapp.core.api.model.SocketMessageCommand;
import com.kma.securechatapp.core.api.model.UserKey;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.event.EventQrCodeMessage;
import com.kma.securechatapp.core.security.AES;
import com.kma.securechatapp.core.security.RSAUtil;
import com.kma.securechatapp.core.service.DataService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.helper.CommonHelper;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginQrScane extends AppCompatActivity {

    @BindView(R.id.camera_view)
    SurfaceView cameraView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    CameraSource cameraSource;
    ApiInterface api = ApiUtil.getChatApi();
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_qr_scane);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        buildCamera();
        buildCameraView();
    }
    void gotPrivateKey(String json) throws IOException {
        CommonHelper.showLoading(LoginQrScane.this);
        Gson gson = new Gson();
        SocketLoginTranferData message = gson.fromJson(json, SocketLoginTranferData.class);
        UserKey userKey = new UserKey(null,null);

        AppData.getInstance().setToken(message.getToken());
        AppData.getInstance().setRefreshToken(message.getRefresh());
        try {
            AppData.getInstance().currentUser = api.getCurrenUserInfo().execute().body().data;
        } catch (Exception e) {
            AppData.getInstance().currentUser = null;
            CommonHelper.hideLoading();
            return;
        }

        userKey.publicKey = api.getPublicKey(AppData.getInstance().currentUser.uuid).execute().body().data;
        userKey.privateKey = message.getKey() ;
        AppData.getInstance().userKey = userKey;

        DataService.getInstance(this).storeToken(message.getToken(),message.getRefresh());
        DataService.getInstance(this).storeUserUuid(AppData.getInstance().currentUser.uuid);
        DataService.getInstance(this).storePrivateKey(AppData.getInstance().currentUser.uuid,  userKey.privateKey  ,AppData.getInstance().password);
        DataService.getInstance(this).save();

        CommonHelper.hideLoading();

        RealtimeServiceConnection.getInstance().onlyDisconnectSocket();

        EventBus.getInstance().pushOnLogin(AppData.getInstance().currentUser);

        LoginQrScane.this.finishActivity(10);
        LoginQrScane.this.finish();
    }
    void  doBarcodeDetected(String barcode) throws IOException {

        cameraSource.stop();

        String[] code = barcode.split("\\|");
        if (code.length < 4){
            cameraSource.start();
            return;
        }
        try {
            cameraSource.release();
        }catch (Exception e){

        }
        CommonHelper.showLoading(LoginQrScane.this);
        try {
            String userUuid = code[2];
            String randomCode = code[1];
            String topicHash = RSAUtil.bytesToHex(RSAUtil.getSHA(randomCode + userUuid));
            String privateKeyCode = code[3];
            RealtimeServiceConnection.getInstance().connectBeforLogin(topicHash,randomCode, new EventQrCodeMessage() {
                @Override
                public SocketLoginDTO onMessage(SocketLoginDTO data) {
                    if (data.getCommand() == SocketLoginCommand.TRANSFER){
                        CommonHelper.hideLoading();
                        String privateKey = data.getData();
                        try {
                            gotPrivateKey(AES.decrypt(RSAUtil.base64Decode(privateKey),privateKeyCode));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                    return null;
                }
            });

        }catch (Exception e){

        }

    }
    void buildCamera(){
      BarcodeDetector  barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

      cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                   String barcode =  barcodes.valueAt(0).displayValue;

                   if (barcode.startsWith("secure-hash|"));
                   LoginQrScane.this.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               doBarcodeDetected(barcode);
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }

                   });
                   return;
                }
            }
        });
    }
    void startCameraView(){
        try {
            cameraSource.start(cameraView.getHolder());
        } catch (IOException ie) {
            Log.e("CAMERA SOURCE", ie.getMessage());
        }
    }

    void buildCameraView(){
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startCameraView();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }
}
