package com.kma.securechatapp.ui.authentication.createaccount;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.VerifyRegister;
import com.kma.securechatapp.helper.CommonHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountValidateFragment extends Fragment {


    public CreateAccountValidateFragment() {
        // Required empty public constructor
    }
    ApiInterface api = ApiUtil.getChatApi();
    String uuid;
    String username;
    String password;

    @BindView(R.id.btn_done)
    Button btnDone;
    @BindView(R.id.input_opt)
    TextInputEditText txtOpt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_account_validate, container, false);
        Bundle bundle = this.getArguments();
        uuid = bundle.getString("uuid");
        username = bundle.getString("username");
        password = bundle.getString("password");
        ButterKnife.bind(this,root);
        return root;
    }
    @OnClick(R.id.btn_done)
    void onDone(View view){
        VerifyRegister verify = new VerifyRegister();
        verify.deviceCode = AppData.getInstance().deviceId;
        verify.opt = txtOpt.getText().toString();
        verify.uuid = uuid;
        verify.username = username;
        verify.password = password;
        CommonHelper.showLoading(this.getContext());
        api.verifyRegister(verify).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                CommonHelper.hideLoading();
                if (response.body() == null){
                    Toast.makeText(CreateAccountValidateFragment.this.getContext(),"Something error !!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body().error  != 0 ){
                    Toast.makeText(CreateAccountValidateFragment.this.getContext(),response.body().message,Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CreateAccountValidateFragment.this.getContext(),"Create account "+username+" success",Toast.LENGTH_SHORT).show();

               // CreateAccountValidateFragment.this.getActivity().finishActivity(1);
               // CreateAccountValidateFragment.this.getActivity().finish();
               NavController navController = NavHostFragment.findNavController(CreateAccountValidateFragment.this);
               navController.navigate(R.id.navigation_account);

            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(CreateAccountValidateFragment.this.getContext(),"Something error !!",Toast.LENGTH_SHORT).show();
                CommonHelper.hideLoading();
            }
        });
    }

}
