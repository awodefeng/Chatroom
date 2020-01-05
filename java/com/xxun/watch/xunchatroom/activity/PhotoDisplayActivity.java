package com.xxun.watch.xunchatroom.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.swipeback.SwipeBackController;
import com.xxun.watch.xunchatroom.adapter.ChatSmsListAdapter;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;
import com.xxun.watch.xunchatroom.control.ChatContractManager;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import java.util.ArrayList;
import java.util.HashMap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.PointF;
import com.xxun.watch.xunchatroom.activity.ZoomImageView;

import static android.content.ContentValues.TAG;

public class PhotoDisplayActivity extends Activity {

    String LOG_TAG="PhotoDisplayActivity";
    ZoomImageView photo=null;
	//private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	Log.i("PhotoDisplayActivity","onCreate");
        Intent intent =getIntent();
        String imagePath=intent.getStringExtra(ChatKeyString.KEY_PHOTO_PATH);
        setContentView(R.layout.activity_photo);
        photo= (ZoomImageView)findViewById(R.id.photo);
		//ImageView face_img = (ImageView) view.findViewById(R.id.local_face_img_id);
	//String imagePath=mListDte.get(i).getFilePath();
	Log.i("PhotoDisplayActivity","photo imagePath="+imagePath);
	//Bitmap ib= BitmapFactory.decodeFile(imagePath);
	//Bitmap nb=getBitmap(ib,200,200);
	//imageView.setImageBitmap(ib); 


//zoomImageView = (ZoomImageView) findViewById(R.id.zoom_image_view);
	photo.setImagePathBitmap( imagePath, 1.0f);
        photo.setOnMoveFinishListener(new ZoomImageView.onMoveFinishListener() {
            @Override
            public void onFinish() {
                Log.i(TAG,"onFinish");
                finish();
            }
        });
//zoomImageView.setResourceBitmap(MainActivity.this, R.mipmap.ic_launcher);
    }

 public  Bitmap getBitmap(Bitmap bitmap, int screenWidth,  int screenHight){  
   if(bitmap==null){
	Log.i("chat","getBitmap,bitmap is null,return");
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


    protected void onResume(){
        super.onResume();
        Log.i("LOG_TAG","ChatSmsActivity,onResume");

    }




}
