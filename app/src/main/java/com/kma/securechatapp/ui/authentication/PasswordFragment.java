package com.kma.securechatapp.ui.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.kma.securechatapp.core.service.DataService;
import com.kma.securechatapp.core.service.RealtimeService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.helper.CommonHelper;
import com.kma.securechatapp.utils.common.ImageLoader;

import java.io.IOException;

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

    NavController navController;
    boolean checkopt = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login2, container, false);
        ButterKnife.bind(this, root);
        optLayout.setVisibility(View.GONE);
        NavController navController = NavHostFragment.findNavController(this);
        CheckOpt();
        ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl(AppData.getInstance().currentUser.uuid,200,200),loginAvatar);
        loginName.setText(AppData.getInstance().currentUser.name);
        return root;
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
                Toast.makeText(PasswordFragment.this.getContext(),"Request error !",Toast.LENGTH_SHORT).show();

                return;
            }
            CommonHelper.hideLoading();


    }

    public void showOpt(){
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

        api.login(auth).enqueue(new Callback<ApiResponse<AuthenResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthenResponse>> call, Response<ApiResponse<AuthenResponse>> response) {
                CommonHelper.hideLoading();
                if (response.body() == null || response.body().error != 0 ){
                    if (response.body() == null || response.body().error == 1) {
                        Toast.makeText(PasswordFragment.this.getContext(), "Password not match!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(PasswordFragment.this.getContext(), "Opt code not match!", Toast.LENGTH_SHORT).show();
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
                DataService.getInstance(null).save();

                onLoginSuccess();

            }

            @Override
            public void onFailure(Call<ApiResponse<AuthenResponse>> call, Throwable t) {
                CommonHelper.hideLoading();
                Toast.makeText(PasswordFragment.this.getContext(),"Request error !",Toast.LENGTH_SHORT).show();
            }
        });


    }
    void onLoginSuccess(){
        EventBus.getInstance().pushOnLogin(AppData.getInstance().currentUser);
        PasswordFragment.this.getActivity().finishActivity(0);
        PasswordFragment.this.getActivity().finish();
    }


}
