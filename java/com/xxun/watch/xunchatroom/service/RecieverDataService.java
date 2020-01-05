package com.xxun.watch.xunchatroom.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import java.io.File;
import com.xxun.watch.xunchatroom.Constants;
import android.app.DownloadManager;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import java.util.ArrayList;
import android.os.Environment;

import com.xxun.watch.xunchatroom.info.ChatRecieverData;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;
import android.content.Intent;  
import android.os.Binder;  
import android.os.IBinder;
import com.xxun.watch.xunchatroom.util.DownloadManagerUtil;

public class RecieverDataService extends IntentService implements Constants {

    private static final String name = "359076060015655";  // BtAddr
    private static final String machineSerialNo = "60015655"; //IMEI
    private static final String TAG = "RecieverDataService";
    private static final String LOG_TAG = "RecieverDataService";
    String mEID = null;
    String mGID = null;
    String mToken = null;
    String mAES_KEY = null;
    String mImgOriginalPath = "/storage/emulated/0/DCIM/Camera/thumbnails/1504180395200.jpg";
    String mVideoThumbnailPath = "/storage/emulated/0/DCIM/Camera/thumbnails/1504180395200.jpg";
    String mVideoPath = "/storage/emulated/0/DCIM/Camera/IMG_20171107_070248.jpg";

    String VIDEO_TYPE = "video";
    String IMAGE_TYPE = "photo";

    private Context mContext;
    private DownloadRecieverBroadCastReciever mReceiver=null;
    private IntentFilter intentFilter;
    private ArrayList<ChatRecieverData> mRecieverList = null;
    // @step 1

    // @step2

    private static final long MAXFILEZISE = 10 * 1000000;  // 10M

    private int fileType = -1;
    private ChatThreadTimer mThread=null;
    private XiaoXunNetworkManager mXunNetworkManager;
    public RecieverDataCallback recieverDataCallback = null;
    DownloadManagerUtil downloadManagerUtil;
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "RecieverDataService,onBind");
        return new RecBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(LOG_TAG, "RecieverDataService,onUnbind");
        return super.onUnbind(intent);
    }

    public class RecBinder extends Binder {
        public RecieverDataService getService() {
            return RecieverDataService.this;
        }
    }

        public void addRecieverData(ChatRecieverData data) {
           // DownloadService.size = size;
		if(mRecieverList!=null){ 
	           long id=startDownload();
		   data.setRecieverDataID(id);
		   mRecieverList.add(data);	
		}
        }

    public RecieverDataCallback getRecieverDataCallback() {
        return recieverDataCallback;
    }

    public void setRecieverDataCallback(RecieverDataCallback Callback) {
        this.recieverDataCallback = Callback;
    }


    // Íš¹ý»Øµ÷»úÖÆ£¬œ«ServiceÄÚ²¿µÄ±ä»¯Ž«µÝµœÍâ²¿
    public interface RecieverDataCallback {
        void RecieverResault(int sn,int rc);
    }


    @Override
    public void onCreate() {
        super.onCreate();
	Log.i(LOG_TAG, "RecieverDataService,onCreate");
        mContext = this;
	initSendDataAll();

    }

   @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "RecieverDataService,onStartCommand");
	registerBroadcastReciever();
        return super.onStartCommand(intent, flags, startId);
    }

    public RecieverDataService() {
        super(TAG);
    }

   public void initSendDataAll(){
	Log.i(LOG_TAG, "initSendDataAll");
	mRecieverList=new ArrayList<ChatRecieverData>();
	downloadManagerUtil=new DownloadManagerUtil(this);
	//initSendThread();
}


    @Override
    protected void onHandleIntent(Intent intent) {
        fileType = intent.getIntExtra(SHARE_TYPE, 0);
        switch (fileType) {
            case SHARE_TYPE_IMAGE:
                //setImagePath(intent.getStringExtra(SHARE_PIC_PATH));
                //getAndShareImage();
                break;

            case SHARE_TYPE_VIDEO:
                //setVideoPath(intent.getStringExtra(SHARE_VIDEO_PATH), intent.getStringExtra(SHARE_VIDEOTHUMB_PATH));
                //getAndUploadVideo();
                break;
        }


    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "[onDestroy] >> perform. ");
	unregisterBroadcastReciever();
        super.onDestroy();
    }

    public void registerBroadcastReciever(){
        Log.i(LOG_TAG, "registerBroadcastReciever");
        //实例化过滤器；
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("android.intent.action.DOWNLOAD_COMPLETE");

        //实例化广播监听器；
        mReceiver = new DownloadRecieverBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(mReceiver, intentFilter);

    }

    public void unregisterBroadcastReciever(){
        Log.i(LOG_TAG, "unregisterBroadcastReciever");
        unregisterReceiver(mReceiver);
	mReceiver=null;
    }

    class  DownloadRecieverBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "DownloadRecieverBroadCastReciever,onReceive");
		String mAction=intent.getAction();
		Log.i(LOG_TAG, "mAction="+mAction);
		if(mAction.equals("android.intent.action.DOWNLOAD_COMPLETE")){ 
		           long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            		   Log.i(TAG,"download complete,id="+id);
				int index=0;
			    for(ChatRecieverData data:mRecieverList){
				if(data.getRecieverDataID()==id){
					if(recieverDataCallback!=null){
					recieverDataCallback.RecieverResault(data.getRecieverDataSN(),1);
					}
					mRecieverList.remove(index);
				   	break;
				}
				index++;
			    }
			    
	      }
        }
    }

    long startDownload(){
        Log.i(TAG,"startDownload");
        String path = Environment.getExternalStorageDirectory().getPath()+"/";
        path = path +"123.mp4";//sdcard的路径加上文件名称是文件全路径
        File file = new File(path);
        if (file.exists()) {//判断需要播放的文件路径是否存在，不存在退出播放流程
            //Toast.makeText(this,"文件路径不存在", Toast.LENGTH_LONG).show();
		Log.i(TAG,"startDownload,file is exsit");
		//play();
            return -1;
        }
       // if (downloadId != 0) {
        //    downloadManagerUtil.clearCurrentTask(downloadId);
        //}
        long downloadId = downloadManagerUtil.download("https://raw.githubusercontent.com/dongzhong/ImageAndVideoStore/master/Bruno%20Mars%20-%20Treasure.mp4", "123", "456");
        Log.i(TAG,"startDownload,downloadId="+downloadId);
	return downloadId;
    }
}
