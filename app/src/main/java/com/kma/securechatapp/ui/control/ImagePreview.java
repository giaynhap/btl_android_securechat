package com.kma.securechatapp.ui.control;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.utils.common.EncryptFileLoader;
import com.kma.securechatapp.utils.common.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagePreview extends AppCompatActivity {

    @BindView(R.id.image_view)
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        ButterKnife.bind(this);
        String url = this.getIntent().getStringExtra("url");
        byte[] key = this.getIntent().getByteArrayExtra("key");
        EncryptFileLoader.getInstance().loadEncryptImage(url,key,imageView);
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
}
