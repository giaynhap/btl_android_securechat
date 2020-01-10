package com.kma.securechatapp.utils.misc;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.kma.securechatapp.R;
import com.kma.securechatapp.utils.common.EncryptFileLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioUi extends FrameLayout implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {
    private MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds;
    String url;
    boolean set=false;
    private final Handler handler = new Handler();
    @BindView(R.id.progress_bar)
    ProgressBar bar;
    @BindView(R.id.button)
    ImageView button;
    Map<String,String> headers ;

    File file = null;

    public AudioUi(Context context) {
        super(context);
        initView();
    }

    public AudioUi(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AudioUi(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AudioUi(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void setUrl(String url){
        this.url = url;
    }
    public void addHeader(String key,String value){
        if (headers == null)
        headers = new HashMap<String,String>();
        headers.put(key,value);
    }

    public void setPath(File file){
        this.file = file;
    }
    private void initView() {
        View view = inflate(getContext(), R.layout.item_audio_layout, null);
        addView(view);
        ButterKnife.bind(this,view);


        bar.setProgress(0);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (file == null){
                        return;
                    }
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setOnBufferingUpdateListener(AudioUi.this);
                        mediaPlayer.setOnCompletionListener(AudioUi.this);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(MediaPlayer mp) {
                                mediaFileLengthInMilliseconds = mp.getDuration();

                            }
                        });

                        /*if (headers != null) {
                            Method method = mediaPlayer.getClass().getMethod("setDataSource", new Class[]{String.class, Map.class});
                            method.invoke(mediaPlayer, new Object[]{url, headers});
                        } else {
                            mediaPlayer.setDataSource(url);
                        }*/

                        if (file != null){
                            mediaPlayer.setDataSource(file.getAbsolutePath());
                        }
                        mediaPlayer.prepare();
                    }

                    } catch(Exception e){
                        e.printStackTrace();
                    }




                if(!mediaPlayer.isPlaying()){

                    mediaPlayer.start();
                    button.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                }else {
                    mediaPlayer.pause();
                    button.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                }

                primarySeekBarProgressUpdater();
            }
        });


    }
    private void primarySeekBarProgressUpdater() {
        if (mediaPlayer == null){
            return;
        }

        bar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaFileLengthInMilliseconds)*100)); // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification,10);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        button.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        if (mp != null) {
            mp.reset();
            mp.release();
            mediaPlayer = null;
            bar.setProgress(100);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
       bar.setSecondaryProgress(percent);
    }
}
