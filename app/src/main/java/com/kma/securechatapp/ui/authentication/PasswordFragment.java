package com.kma.securechatapp.ui.authentication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kma.securechatapp.MainActivity;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.AuthenRequest;
import com.kma.securechatapp.core.api.model.AuthenResponse;
import com.kma.securechatapp.core.api.model.Device;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.service.CacheService;
import com.kma.securechatapp.core.service.DataService;
import com.kma.securechatapp.core.service.RealtimeService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.helper.CommonHelper;
import com.kma.securechatapp.utils.common.GFingerPrint;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.common.Utils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PasswordFragment extends Fragment {

    ApiInterface api = ApiUtil.getChatApi();

    @BindView(R.id.password_input)
    TextInputEditText text_password;
    @BindView(R.id.opt_layout)
    LinearLayout optLayout;
    @BindView(R.id.opt_input)
    TextInputEditText optInput;
    @BindView(R.id.login_avatar)
    ImageView loginAvatar;
    @BindView(R.id.login_name)
    TextView loginName;
    @BindView(R.id.fingerprint_image)
    ImageView fingerprint;
    private Executor executor;
    private androidx.biometric.BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
    NavController navController;
    boolean checkopt = false;
    GFingerPrint gFingerPrint ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login2, container, false);
        ButterKnife.bind(this, root);
        optLayout.setVisibility(View.GONE);
        fingerprint.setVisibility(View.VISIBLE);
        NavController navController = NavHostFragment.findNavController(this);
        if (AppData.getInstance().currentUser != null) {
            CheckOpt();
            ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl(AppData.getInstance().currentUser.uuid,200,200),loginAvatar);
            loginName.setText(AppData.getInstance().currentUser.name);
        } else {
            ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl(AppData.getInstance().userUUID,200,200),loginAvatar);
            loginName.setText(AppData.getInstance().account);

        }

        //finger print
        SettingFingerPrint();
        return root;
    }

    public void SettingFingerPrint(){
        gFingerPrint = new GFingerPrint(this.getContext());
        executor = ContextCompat.getMainExecutor(getContext());
        biometricPrompt = new BiometricPrompt(this , executor, new BiometricPrompt.AuthenticationCallback(){
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getContext(),
                        "Lỗi vân tay: " + errString, Toast.LENGTH_SHORT)
                        .show();

            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String password = null;
                //Toast.makeText(getContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                CommonHelper.showLoading(getContext());
                String encryptPassword = DataService.getInstance(getContext()).getFingerSaved(AppData.getInstance().userUUID);
                Cipher cipher =  result.getCryptoObject().getCipher();
                try {
                    password =   gFingerPrint.decipher(cipher,encryptPassword );
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                AuthenRequest auth = new AuthenRequest(AppData.getInstance().account,password);
                auth.token = optInput.getText().toString();
                auth.device = new Device();
                auth.device.deviceCode = AppData.getInstance().deviceId;
                auth.device.deviceOs = "android";
                AppData.getInstance().password = password;
                if ( Utils.haveNetworkConnection(getContext())  ) {
                    onlineLogin(auth);
                } else {
                    onLoginSuccess();

                }
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


        fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        Cipher cipher = gFingerPrint.createCipher(Cipher.DECRYPT_MODE);
                        biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
                    }  catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (gFingerPrint.initKeyStore()){
                if (DataService.getInstance(this.getContext()).getFingerSaved(AppData.getInstance().userUUID) != null){
                    fingerprint.setVisibility(View.VISIBLE);
                } else {
                    fingerprint.setVisibility(View.GONE);
                }
            } else {
                fingerprint.setVisibility(View.GONE);
            }
        } else {
            fingerprint.setVisibility(View.GONE);
        }
    }

    public void CheckOpt() {
            if (checkopt )
                return;
            checkopt =true;
            CommonHelper.showLoading(this.getContext());
            try {

                Response<ApiResponse<UserInfo>> data =api.preLogin(AppData.getInstance().account,  AppData.getInstance().deviceId).execute();
                if (data.body().error!= 0){
                    showOpt();
                }
                else{
                    optLayout.setVisibility(View.GONE);
                }
            } catch (IOException e) {
                CommonHelper.hideLoading();
                navController.navigate(R.id.navigation_account);
                Toast.makeText(PasswordFragment.this.getContext(),"Lỗi đăng nhập, xin kiểm tra lại mã đã nhập",Toast.LENGTH_SHORT).show();

                return;
            }
            CommonHelper.hideLoading();


    }

    public void showOpt(){
        fingerprint.setVisibility(View.GONE);
        optLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_login2)
    public void buttonLoginClick(View view){
        String password = text_password.getText().toString();
        CommonHelper.showLoading(this.getContext());
        AuthenRequest auth = new AuthenRequest(AppData.getInstance().account,password);
        auth.token = optInput.getText().toString();
        auth.device = new Device();
        auth.device.deviceCode = AppData.getInstance().deviceId;
        auth.device.deviceOs = "android";
        AppData.getInstance().password = password;
        if ( Utils.haveNetworkConnection(this.getContext())  ) {
            onlineLogin(auth);
        } else {
            onLoginSuccess();

        }

    }


    void onlineLogin(AuthenRequest auth){

        api.login(auth).enqueue(new Callback<ApiResponse<AuthenResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthenResponse>> call, Response<ApiResponse<AuthenResponse>> response) {
                CommonHelper.hideLoading();
                if (response.body() == null || response.body().error != 0 ){
                    if (response.body() == null || response.body().error == 1) {
                        Toast.makeText(PasswordFragment.this.getContext(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(PasswordFragment.this.getContext(), "Mã OTP không đúng", Toast.LENGTH_SHORT).show();
                    }

                    return;
                }
                // login success
                AppData.getInstance().setToken(response.body().data.token);
                AppData.getInstance().setRefreshToken(response.body().data.refreshToken);
                try {
                    AppData.getInstance().currentUser = api.getCurrenUserInfo().execute().body().data;
                } catch (IOException e) {
                    return;
                }
                DataService.getInstance(PasswordFragment.this.getContext()).storeToken(response.body().data.token,response.body().data.refreshToken);
                DataService.getInstance(null).storeUserUuid( AppData.getInstance().currentUser.uuid);
                //end screen
                DataService.getInstance(null).save();

                onLoginSuccess();
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthenResponse>> call, Throwable t) {
                CommonHelper.hideLoading();
                Toast.makeText(PasswordFragment.this.getContext(),"Đăng nhập thấy bại",Toast.LENGTH_SHORT).show();
            }
        });

    }

    void onLoginSuccess(){
        EventBus.getInstance().pushOnLogin(AppData.getInstance().currentUser);
        PasswordFragment.this.getActivity().finishActivity(0);
        PasswordFragment.this.getActivity().finish();
    }


}
