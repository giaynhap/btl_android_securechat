package com.kma.securechatapp.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.helper.CommonHelper;
import com.kma.securechatapp.ui.profile.QrCodeView;
import com.kma.securechatapp.ui.profile.UserProfileActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    @BindView(R.id.account_input)
    TextInputEditText text_account;
    @BindView(R.id.img_scane)
    ImageView imgBtnScane ;



    ApiInterface api = ApiUtil.getChatApi();
    NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, root);
        navController = NavHostFragment.findNavController(this);
        return root;
    }

    @OnClick(R.id.btn_signup)
    public void buttonSignUpClick(View view){

        navController.navigate(R.id.navigation_create_account);

    }
    @OnClick(R.id.img_scane)
    public void imgBtnScaneClick(View view){
        Intent intent = new Intent(AccountFragment.this.getContext(), LoginQrScane.class);
        startActivityForResult(intent,10);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 ){
            if (AppData.getInstance().currentUser != null) {
                this.getActivity().finishActivity(0);
                this.getActivity().finish();
            }
        }

    }
    @OnClick(R.id.btn_login)
    public void continueLogin(View view) {
       // Toast.makeText(this.getContext(),text_account.getText().toString(),Toast.LENGTH_SHORT).show();
        CommonHelper.showLoading(this.getContext());
        AppData.getInstance().account = text_account.getText().toString();


        api.userExist( AppData.getInstance().account).enqueue(new Callback<ApiResponse<UserInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserInfo>> call, Response<ApiResponse<UserInfo>> response) {
                CommonHelper.hideLoading();
                if (response.body() == null){
                    Toast.makeText(AccountFragment.this.getContext(),"Request error !",Toast.LENGTH_SHORT).show();
                    return ;
                }

                if (response.body().error != 0){
                    Toast.makeText(AccountFragment.this.getContext(),"Account not exist",Toast.LENGTH_SHORT).show();
                    return ;
                }

                AppData.getInstance().currentUser = response.body().data;

                navController.navigate(R.id.navigation_password);

            }

            @Override
            public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {
                CommonHelper.hideLoading();
                Toast.makeText(AccountFragment.this.getContext(),"Request error !"+t.toString(),Toast.LENGTH_SHORT).show();
            }
        });


    }
}
