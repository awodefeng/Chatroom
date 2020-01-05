package com.xxun.watch.xunchatroom.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.graphics.Matrix;
import java.io.File;
import java.io.FileInputStream;
import android.util.Log;
/**
 * 图片加载工具类
 *
 * Created by newcboy on 2018/1/25.
 */

public class ImageLoadUtils {
	private static String TAG="ImageLoadUtils";
    /**
     * 原图加载，根据传入的指定图片大小。
     * @param imagePath
     * @param maxSize
     * @return
     */
    public static Bitmap getImageLoadBitmap(String imagePath, int maxSize){
        int fileSize = 1;
        Bitmap bitmap = null;
        int simpleSize = 1;
        File file = new File(imagePath);
        if (file.exists()) {
            Uri imageUri = Uri.parse(imagePath);
            try {
                fileSize = (int) (getFileSize(file) / 1024);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Options options = new Options();
            if (fileSize > maxSize){
                for (simpleSize = 2; fileSize>= maxSize; simpleSize++){
                    fileSize = fileSize / simpleSize;
                }
            }
            options.inSampleSize = simpleSize;
            bitmap = BitmapFactory.decodeFile(imageUri.getPath(), options);
        }
	Bitmap nb=getBitmap(bitmap,200,200);
        return nb;
    }

 public  static Bitmap getBitmap(Bitmap bitmap, int screenWidth,  int screenHight){  
   if(bitmap==null){
  Log.i(TAG,"getBitmap,bitmap is null,return");
   return null;
  }
  int w = bitmap.getWidth();  
  int h = bitmap.getHeight();  
   Log.i("chat","getBitmap,"+w+"/"+h);
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

    /**
     * 获取指定文件的大小
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception{
        if(file == null) {
            return 0;
        }
        long size = 0;
        if(file.exists()) {
            FileInputStream mInputStream = new FileInputStream(file);
            size = mInputStream.available();
        }
        return size;
    }


    /**
     * 获取手机运行内存
     * @param context
     * @return
     */
    public static long getTotalMemorySize(Context context){
        long size = 0;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();//outInfo对象里面包含了内存相关的信息
        activityManager.getMemoryInfo(outInfo);//把内存相关的信息传递到outInfo里面C++思想
        //size = outInfo.totalMem;  //总内存
        size = outInfo.availMem;    //剩余内存
        return (size/1024/1024);
    }

}



