package com.xxun.watch.xunchatroom.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.xxun.watch.xunchatroom.activity.CircleDrawable;

/**
 * Created by jixiang on 2018/8/2.
 */

public class AsyncImageLoader{
    private MemoryCache mMemoryCache;
    private FileCache mFileCache;
    private ExecutorService mExecutorService;
    private Map<ImageView, String> mImageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    private List<LoadPhotoTask> mTaskQueue = new ArrayList<LoadPhotoTask>();
    public AsyncImageLoader(Context context, MemoryCache memoryCache, FileCache fileCache) {
        mMemoryCache = memoryCache;
        mFileCache = fileCache;
        mExecutorService = Executors.newFixedThreadPool(5);
    }
    public Bitmap loadBitmap(ImageView imageView, String url,int DefualtPic) {
        mImageViews.put(imageView, url);
        Bitmap bitmap = mMemoryCache.get(url);
        if(bitmap == null) {
            enquequeLoadPhoto(url, imageView,DefualtPic);
        }
        return bitmap;
    }
    private void enquequeLoadPhoto(String url, ImageView imageView,int DefualtPic) {
        if(isTaskExisted(url))
            return;
        LoadPhotoTask task = new LoadPhotoTask(url, imageView,DefualtPic);
        synchronized (mTaskQueue) {
            mTaskQueue.add(task);
        }
        mExecutorService.execute(task);
    }

    private boolean isTaskExisted(String url) {
        if(url == null)
            return false;
        synchronized (mTaskQueue) {
            int size = mTaskQueue.size();
            for(int i=0; i<size; i++) {
                LoadPhotoTask task = mTaskQueue.get(i);
                if(task != null && task.getUrl().equals(url))
                    return true;
            }
        }
        return false;
    }

    public Bitmap getBitmapByUrl(String url) {
        File f = mFileCache.getFile(url);
        Bitmap b = ImageUtil.decodeFile(f);
        if (b != null){
            return b;
        }
        return ImageUtil.loadBitmapFromWeb(url, f);
    }
    
    public Bitmap getLocalBitmap(String url) {
        Bitmap bitmap = null;
        bitmap= mMemoryCache.get(url);
        if(bitmap != null){
            return bitmap;
        }
        File f = mFileCache.getFile(url);
        bitmap = ImageUtil.decodeFile(f);
        return bitmap;
    }

    private boolean imageViewReused(ImageView imageView, String url) {
        String tag = mImageViews.get(imageView);
        if (tag == null || !tag.equals(url))
            return true;
        return false;
    }

    private void removeTask(LoadPhotoTask task) {
        synchronized (mTaskQueue) {
            mTaskQueue.remove(task);
        }
    }

    class LoadPhotoTask implements Runnable {
        private String url;
        private ImageView imageView;
        private int DefaultPic;
        LoadPhotoTask(String url, ImageView imageView,int DefaultPic) {
            this.url = url;
            this.imageView = imageView;
            this.DefaultPic = DefaultPic;
        }

        @Override
        public void run() {
            if (imageViewReused(imageView, url)) {
                removeTask(this);
                return;
            }
            Bitmap bmp = getBitmapByUrl(url);
            if(bmp == null){
                   if (!imageViewReused(imageView, url)) {
                        BitmapDisplayer bd = new BitmapDisplayer(DefaultPic, imageView, url);
                        Activity a = (Activity) imageView.getContext();
                        a.runOnUiThread(bd);
                    }
            }else{
                    mMemoryCache.put(url, bmp);
                    if (!imageViewReused(imageView, url)) {
                        BitmapDisplayer bd = new BitmapDisplayer(bmp, imageView, url);
                        Activity a = (Activity) imageView.getContext();
                        a.runOnUiThread(bd);
                    }
            }
            removeTask(this);
        }
        public String  getUrl() {
            return url;
        }
    }

    class BitmapDisplayer implements Runnable {
        private Bitmap bitmap;
        private ImageView imageView;
        private String url;
        private int DefaultId;
        public BitmapDisplayer(int b, ImageView imageView, String url) {
            DefaultId = b;
            this.imageView = imageView;
            this.url = url;
        }
        
        
        public BitmapDisplayer(Bitmap b, ImageView imageView, String url) {
            bitmap = b;
            this.imageView = imageView;
            this.url = url;
        }
        public void run() {
            if (imageViewReused(imageView, url))
                return;
            if (bitmap != null){
                imageView.setImageDrawable(new CircleDrawable(bitmap));
            }else{
                imageView.setImageResource(DefaultId);
            }
        }
    }

    public void destroy() {
        mMemoryCache.clear();
        mMemoryCache = null;
        mImageViews.clear();
        mImageViews = null;
        mTaskQueue.clear();
        mTaskQueue = null;
        mExecutorService.shutdown();
        mExecutorService = null;
    }
}
