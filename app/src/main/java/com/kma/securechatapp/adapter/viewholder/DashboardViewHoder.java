package com.kma.securechatapp.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.utils.common.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardViewHoder extends RecyclerView.ViewHolder {
    @BindView(R.id.item_avatar)
    ImageView avatar;
    @BindView(R.id.item_name)
    TextView name;
    @BindView(R.id.item_content)
    TextView content;
    public DashboardViewHoder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
    public void bind(int index){
        if (index < 1) {
            ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl("e758cb50-3d30-498a-a0a9-5bc3a876b8c3", 80, 80), avatar);
        } if (index == 1){
            ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl("e758cb50-3d30-498a-a0a9-5bc3a876b8c8", 80, 80), avatar);
            name.setText("Yasuo");
            content.setText("Hmm!!!  Sr manfai Hết mana không R được. đen VL.\n Ctrl + 6");
        }
        else  if (index == 2 ){
            ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl("d02dd4f6-66ab-4d24-9c26-b298d07fed62", 80, 80), avatar);
            name.setText("KimJongun");
            content.setText("0h45: Bản tin Triều Tiên phóng tên lửa lúc 0h22 là lỗi đánh máy\n-----\nĐmm đăng lung tung nữa là tao phóng thật bây zờ");
        } if (index == 3){
            ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl("e758cb50-3d30-498a-a0a9-5bc3a876b8c8", 80, 80), avatar);
            name.setText("Yasuo");
            content.setText("Color like the wind, always  by my side!\n Một mình t chấp HẾT!!!");
        }

    }

}
