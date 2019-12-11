package com.kma.securechatapp.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kma.securechatapp.R;

 

public class CommonHelper {
    private static Dialog loading = null;

    public static void  alert(Context context,String message)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_msg);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView text = (TextView) dialog.findViewById(R.id.textMessage);
        text.setText(message);
        TextView dialogButton = (TextView) dialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showLoading(Context context){
        if (loading==null) {
            loading = new Dialog(context);
            loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loading.setContentView(R.layout.loading_view);
            loading.setCancelable(false);
            loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loading.show();
        }

    }


    public static void hideLoading(){
        if (loading==null) return;

        loading.dismiss();
        loading = null;
    }

}
