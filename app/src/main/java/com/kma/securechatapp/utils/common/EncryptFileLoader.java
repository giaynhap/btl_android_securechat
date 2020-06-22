package com.kma.securechatapp.utils.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.kma.securechatapp.core.api.UnsafeOkHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class EncryptFileLoader {
    private static EncryptFileLoader instance = null;
    public static EncryptFileLoader getInstance(){
        if (instance == null){
            instance = new EncryptFileLoader();
        }
        return instance;
    }
    FileCache fileCache;
    ExecutorService executorService;
    private EncryptFileLoader(){
        executorService = Executors.newFixedThreadPool(5);
    }

    public File downloadEncryptFile(String url,byte[]password) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        OkHttpClient client =  UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();
        File f = fileCache.getFile(url);
        if (f.exists()){
            Log.v("EncrFile","File cache Exist");
            return f;
        }
        InputStream is = client.newCall(request).execute().body().byteStream();
        OutputStream os = new FileOutputStream(f);
        SecretKeySpec sks = new SecretKeySpec(password,"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(is, cipher);

        byte[] d = new byte[1024 * 1024];
        int b;
        try {
            while ((b = cis.read(d)) != -1) {
                os.write(d, 0, b);
            }
        }catch ( Exception e){
            e.printStackTrace();
            throw  e;
        }
        os.flush();
        os.close();
        is.close();
        return f;
    }
    public File encryptFile(File file,byte[]key) throws Exception {
        File outFile = new File(FileCache.cacheDir,file.getName().concat(".crypt"));
        FileInputStream fis = new FileInputStream(file);

        FileOutputStream fos = new FileOutputStream(outFile);
        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        int b;
        byte[] d = new byte[1024 * 1024];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        cos.flush();
        cos.close();
        fis.close();
        return outFile;
    }
    public void loadEncryptFile(String url,byte[] password, EventLoadedEncryptFile e){
        executorService.submit( new FileLoader(url,password,e));
    }
    public void loadEncryptImage(String url,byte[] password, ImageView view, Runnable e){
      Runnable loadAble=  new FileLoader(url,password, new EventLoadedEncryptFile() {
            @Override
            public void onLoaded(int error, File file) {
                if (error == 1){
                    return;
                }
                try {
                    Bitmap bitmap = decodeFile(file);
                    ImageDisplayer bd = new ImageDisplayer(bitmap, view);
                    Activity a = (Activity) view.getContext();
                    a.runOnUiThread(bd);
                    if (e != null){
                        e.run();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

     executorService.submit(loadAble);
    }

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

    public void bind(Context context){
        fileCache=new FileCache(context);
    }

    public interface EventLoadedEncryptFile{
        void onLoaded(int error, File file);
    }
    class FileLoader implements Runnable {
        EventLoadedEncryptFile event;
        String url;
        byte[] password;
        FileLoader(String url,byte[]password, EventLoadedEncryptFile e){
            event = e;
            System.out.println("Load file "+url);
            this.url = url;
            this.password = password;
        }

        @Override
        public void run() {
            try {
                File f = downloadEncryptFile(url,password);
                event.onLoaded(0,f);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                }
            event.onLoaded(1,null);
        }
    }
    class ImageDisplayer implements Runnable
    {
        Bitmap bitmap;
         ImageView view;
        public ImageDisplayer(Bitmap b, ImageView view){bitmap=b;this.view=view;}
        public void run()
        {
            try {
                if (bitmap != null) {
                    view.setImageBitmap(bitmap);
                }
            }catch (Exception e){

            }

        }
    }
}
