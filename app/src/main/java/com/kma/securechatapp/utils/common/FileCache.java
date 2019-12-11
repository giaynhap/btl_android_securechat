package com.kma.securechatapp.utils.common;

import android.content.Context;

import java.io.File;

public class FileCache {
    private File cacheDir;

    public FileCache(Context context){
       try {
          /* if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
               cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "TTImages_cache");
           else*/
               cacheDir = context.getCacheDir();
           if (!cacheDir.exists())
               cacheDir.mkdirs();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    public File getFile(String url){

        String filename=String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
}
