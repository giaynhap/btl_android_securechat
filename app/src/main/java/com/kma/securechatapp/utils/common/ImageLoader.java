package com.kma.securechatapp.utils.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.UnsafeOkHttpClient;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ImageLoader {

    private static ImageLoader instance;
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    public interface OnLoadedBitmap{
            public void onBitmap(Bitmap bm);
    }

    public static ImageLoader getInstance(){

        if (instance == null){
            instance = new ImageLoader();
        }

        return instance;
    }
    public ImageLoader(){

    }
    public void bind(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }

    final int stub_id= R.mipmap.ic_launcher;
    public void DisplayImage(String url, ImageView imageView)
    {
        try {
            DisplayImage(url, imageView, false);
        }catch (Exception e){

        }
    }

    public void DisplayImage(String url, ImageView imageView,boolean reload)
    {

        imageViews.put(imageView, url);
        Bitmap bitmap= null;

        bitmap = memoryCache.get(url);
        if (reload && bitmap != null) {
            imageView.setImageBitmap(bitmap);
            bitmap = null;
        }

        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
            queuePhoto(url, imageView,reload);
            imageView.setImageResource(stub_id);
        }
    }
    public void loadBitmap(String url,OnLoadedBitmap onload,boolean reload){
        Bitmap bitmap= null;
        if (!reload) {
            bitmap = memoryCache.get(url);
        }
        if(bitmap!=null)
            onload.onBitmap(bitmap);
        else {
            Bitmap bmp=getBitmap(url,reload);
            memoryCache.put(url, bmp);
            onload.onBitmap(bmp);
        }
    }

    private void queuePhoto(String url, ImageView imageView,boolean reload)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView,reload);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url,boolean reload)
    {
        File f = fileCache.getFile(url);

        if (!reload) {
            //from SD cache
            Bitmap b = decodeFile(f);
            if (b != null)
                return b;
        }
        //from web
        try {
            Bitmap bitmap=null;
          
            OkHttpClient client =  UnsafeOkHttpClient.getUnsafeOkHttpClient();
            Request request = new Request.Builder()
                    .url(url).build();

            InputStream is= client.newCall(request).execute().body().byteStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            final int REQUIRED_SIZE=1024;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public boolean reload;
        public PhotoToLoad(String u, ImageView i,boolean reload){
            url=u;
            imageView=i;
            this.reload = reload;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }

        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url,photoToLoad.reload);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad){
        try {
            String tag = imageViews.get(photoToLoad.imageView);
            if (tag == null || !tag.equals(photoToLoad.url))
                return true;
        }catch (Exception e){

        }
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    public static String getUserAvatarUrl(String uuid,int width,int height){
        return BuildConfig.HOST +"users/avatar/"+uuid+"?width="+width+"&height="+height;
    }
    public static String getStickerUrl(String model,int index){
        return BuildConfig.HOST +"sticker/"+model+"/"+index;
    }
}