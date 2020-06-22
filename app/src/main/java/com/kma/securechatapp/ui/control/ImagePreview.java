package com.kma.securechatapp.ui.control;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.utils.common.EncryptFileLoader;
import com.kma.securechatapp.utils.common.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagePreview extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_view)
    ImageView imageView;
    public static Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        ButterKnife.bind(this);
        if (bitmap == null) {
            String url = this.getIntent().getStringExtra("url");
            byte[] key = this.getIntent().getByteArrayExtra("key");
            EncryptFileLoader.getInstance().loadEncryptImage(url, key, imageView, new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        postponeEnterTransition();
                        ImagePreview.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toolbar.setTitle(" ");
                            }
                        });
                    }
                }
            });
        }else{
            imageView.setImageBitmap(bitmap);
        }
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportActionBar().setTitle("");


    }

}
