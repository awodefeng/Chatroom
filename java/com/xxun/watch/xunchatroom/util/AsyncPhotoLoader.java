package com.xxun.watch.xunchatroom.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import android.view.View;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.xxun.watch.xunchatroom.activity.CircleDrawable;
import android.util.Log;
import android.graphics.Matrix;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.graphics.drawable.AnimationDrawable;
import com.xxun.watch.xunchatroom.activity.ChatGifView;
/**
 * Created by jack on 2019/4/9.
 */

public class AsyncPhotoLoader{
    private MemoryCache mMemoryCache;
    //private FileCache mFileCache;
    private String TAG="AsyncPhotoLoader";
    private ExecutorService mExecutorService;
    private Map<ImageView, String> mImageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    private List<LoadPhotoTask> mTaskQueue = new ArrayList<LoadPhotoTask>();
    public AsyncPhotoLoader(Context context,MemoryCache memoryCache) {
        mMemoryCache = memoryCache;
        //mFileCache = fileCache;
        mExecutorService = Executors.newFixedThreadPool(5);
    }
    public Bitmap loadBitmap(ImageView imageView,ChatGifView loadView, String url,int DefualtPic,OnRefreshListener refreshListener) {
	Log.i(TAG,"loadBitmap");
        mImageViews.put(imageView, url);
        Bitmap bitmap = mMemoryCache.get(url);
        if(bitmap == null) {
	    Log.i(TAG,"loadBitmap,bitmap is null");
            enquequeLoadPhoto(url, imageView,loadView,DefualtPic,refreshListener);
        }
        return bitmap;
    }
    private void enquequeLoadPhoto(String url, ImageView imageView,ChatGifView loadView,int DefualtPic,OnRefreshListener refreshListener) {
        if(isTaskExisted(url))
            return;
        LoadPhotoTask task = new LoadPhotoTask(url, imageView,loadView,DefualtPic,refreshListener);
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

 public  Bitmap getBitmap(Bitmap bitmap, int screenWidth,  int screenHight){  
   if(bitmap==null){
	 Log.i(TAG,"getBitmap,null");
		return null;
	}
  int w = bitmap.getWidth();  
  int h = bitmap.getHeight();  
   Log.i(TAG,"getBitmap,"+w+"/"+h);
  Matrix matrix = new Matrix();  
  float scale = (float) screenWidth / w;  
  float scale2 = (float) screenHight / h;  
  // scale = scale < scale2 ? scale : scale2;  
  matrix.postScale(scale, scale);  
  Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);  
  if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled())  
  {  
   bitmap.recycle();  
   bitmap = null;  
  }  
  return bmp;// Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);  
 } 
    //public Bitmap getBitmapByUrl(String url) {
        //File f = mFileCache.getFile(url);
        //Bitmap b = ImageUtil.decodeFile(f);
        //if (b != null){
         //   return b;
        //}
        //return ImageUtil.loadBitmapFromWeb(url, f);
    //}
    
    public Bitmap getLocalBitmap(String url) {
        Bitmap bitmap = null;
        //bitmap= mMemoryCache.get(url);
        if(bitmap != null){
            return bitmap;
        }
        //File f = mFileCache.getFile(url);
        //bitmap = ImageUtil.decodeFile(f);
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
private boolean isVideoFile(String sourcePath){
	Log.i("LOG_TAG", "isVideoFile,sourcePath="+sourcePath);
      String type=sourcePath.substring(sourcePath.length()-4,sourcePath.length());
	Log.i("LOG_TAG", "isVideoFile,type="+type);
	if(type.equals(".mp4")||type.equals(".avi")||type.equals(".3gp")){
		return true;
	}
        return false;
}
    class LoadPhotoTask implements Runnable {
        private String url;
        private ImageView imageView;
        private int DefaultPic;
        private ChatGifView loadView;
	private OnRefreshListener refreshListener;
        LoadPhotoTask(String url, ImageView imageView,ChatGifView loadView,int DefaultPic,OnRefreshListener refreshListener) {
            this.url = url;
            this.imageView = imageView;
            this.DefaultPic = DefaultPic;
            this.loadView=loadView;
	    this.refreshListener = refreshListener;
        }

        @Override
        public void run() {
            if (imageViewReused(imageView, url)) {
                removeTask(this);
                return;
            }
            //Bitmap bmp = getBitmapByUrl(url);
			
			Bitmap nb=null;
			Bitmap ib=null;
			if(isVideoFile(url)){
			File imageFile=new File(url);
			if(imageFile.exists()){
			Log.i(TAG,"imageFile is ok");
			  //ib= BitmapFactory.decodeFile(url);
			MediaMetadataRetriever media = new MediaMetadataRetriever();
			media.setDataSource(url); 
			ib = media.getFrameAtTime(); 
			if(ib==null){
			  Log.i(TAG,"bitmap is null");
			}else{
			  Log.i(TAG,"bitmap is ok");
			 nb=getBitmap(ib,80,80);
			//if(nb!=null)
			//face_img.setImageBitmap(nb); 
			}
			}else{
			Log.i(TAG,"imageFile is missing");
			}

			}else{
			File imageFile=new File(url);
			if(imageFile.exists()){
			Log.i(TAG,"imageFile is ok");
			  ib= BitmapFactory.decodeFile(url);
			if(ib==null){
			  Log.i(TAG,"bitmap is null");
			}else{
			  Log.i(TAG,"bitmap is ok");
			 nb=getBitmap(ib,80,80);
			//if(nb!=null)
			//face_img.setImageBitmap(nb); 
			}
			}else{
			Log.i(TAG,"imageFile is missing");
			}
			}
		//nb=null;
            if(nb == null){
		   Log.i(TAG,"nb is null");
                   if (!imageViewReused(imageView, url)) {
			//imageView.setImageResource(DefaultPic);
                        BitmapDisplayer bd = new BitmapDisplayer(DefaultPic, imageView,loadView, url,this.refreshListener);
                        Activity a = (Activity) imageView.getContext();
			Log.i(TAG,"refresh activity");
                        a.runOnUiThread(bd);
                    }
            }else{
			Log.i(TAG,"nb is ok");
                    mMemoryCache.put(url, nb);
                    if (!imageViewReused(imageView, url)) {	
			//imageView.setImageBitmap(nb);
                        BitmapDisplayer bd = new BitmapDisplayer(nb, imageView,loadView, url,this.refreshListener);
                        Activity a = (Activity) imageView.getContext();
			Log.i(TAG,"refresh activity");
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
        private ChatGifView loadView;
	private OnRefreshListener refreshListener;
        public BitmapDisplayer(int b, ImageView imageView,ChatGifView loadView, String url,OnRefreshListener refreshListener) {
            DefaultId = b;
            this.imageView = imageView;
            this.loadView = loadView;
            this.url = url;
	    this.refreshListener=refreshListener;
        }
        
        
        public BitmapDisplayer(Bitmap b, ImageView imageView,ChatGifView loadView, String url,OnRefreshListener refreshListener) {
            bitmap = b;
            this.imageView = imageView;
            this.loadView = loadView;
            this.url = url;
	     this.refreshListener=refreshListener;
        }
        public void run() {
            if (imageViewReused(imageView, url))
                return;
		//Log.i(TAG,"set image data");
            if (bitmap != null){
		Log.i(TAG,"set image data,bitmap is ok");
                loadView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            }else{
		Log.i(TAG,"set image data,bitmap is missing");
                imageView.setVisibility(View.GONE);
                loadView.setVisibility(View.VISIBLE);
                loadView.setMovieResource(DefaultId);
                //imageView.setImageResource(DefaultId);
                //face_img.setImageResource(R.drawable.loading);
                //AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
               // animationDrawable.start();
            }
			if(refreshListener!=null&&bitmap!=null){
				Log.i(TAG,"onRefreshlistener 1");
				refreshListener.onRefresh();
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
