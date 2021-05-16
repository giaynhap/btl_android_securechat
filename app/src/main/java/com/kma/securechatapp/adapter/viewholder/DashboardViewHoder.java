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
            ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl("bb55c06f-8c7f-49dd-91be-2492bc0ca84c", 80, 80), avatar);
            name.setText("Trần Dần");
            content.setText("Nội dung bài đăng trên newsfeed");

        } else  if (index < 2) {
            ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl("bb55c06f-8c7f-49dd-91be-2492bc0ca84c", 80, 80), avatar);
            name.setText("Trần Dần");
            content.setText("Demo bài đăng trên new feed");

        }

    }

}
