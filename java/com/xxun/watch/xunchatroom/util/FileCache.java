package com.xxun.watch.xunchatroom.util;

import android.content.Context;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jixiang on 2018/8/2.
 */

public class FileCache {
    private File mCacheDir;
    public FileCache(Context context, File cacheDir, String dir){
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            mCacheDir = new File(cacheDir, dir);
        }
        else{
            mCacheDir = context.getCacheDir();
        }
        System.out.println("sylar: mCacheDir is :"+mCacheDir);
        if(!mCacheDir.exists())  mCacheDir.mkdirs();
    }
    public File getFile(String url){
            File f=null;
        try {
            String filename = URLEncoder.encode(url,"utf-8");
            f = new File(mCacheDir, filename);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return f;
    }
    public void clearAll(){
        File[] files = mCacheDir.listFiles();
        for(File f:files)f.delete();
    }
}
