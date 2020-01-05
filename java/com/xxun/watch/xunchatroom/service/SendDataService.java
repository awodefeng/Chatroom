package com.xxun.watch.xunchatroom.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.util.Log;

import com.xxun.watch.xunchatroom.Constants;

import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;

import java.util.ArrayList;
import com.xiaoxun.smart.uploadfile.OnUploadResult;
import com.xiaoxun.smart.uploadfile.ProgressListener;
import com.xiaoxun.smart.uploadfile.UploadFile;
import com.xxun.watch.xunchatroom.info.ChatSendData;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;
import android.content.Intent;  
import android.os.Binder;  
import android.os.IBinder;
import android.os.Environment;
import com.xxun.watch.xunchatroom.util.ChatUtil;
import com.xiaoxun.sdk.utils.Constant;
public class SendDataService extends IntentService implements Constants {

    private static final String name = "359076060015655";  // BtAddr
    private static final String machineSerialNo = "60015655"; //IMEI
    private static final String TAG = "SendDataService";
    private static final String LOG_TAG = "SendDataService";
    String mEID = null;
    String mGID = null;
    String mToken = null;
    String mAES_KEY = null;
    String mImgOriginalPath = "/storage/emulated/0/DCIM/Camera/thumbnails/1504180395200.jpg";
    String mVideoThumbnailPath = "/storage/emulated/0/DCIM/Camera/thumbnails/1504180395200.jpg";
    String mVideoPath = "/storage/emulated/0/DCIM/Camera/IMG_20171107_070248.jpg";

    String VIDEO_TYPE = "video";
    String IMAGE_TYPE = "photo";
    static String chat_audio_folder="chat_audio";
    private Context mContext;
    public static OnUpdateShareView mOnUpdateShareView;
    private ArrayList<ChatSendData> mSendList = null;
    private int inChatroom=0;
    private int curSendSN=0;
    private int sendCount=0;
    private boolean isWaitLTE=false;
    private boolean isSendWork=false;
    private LTEBroadCastReciever lReceiver;
    private IntentFilter lIntentFilter;
    // @step 1

    // @step2

    private static final long MAXFILEZISE = 10 * 1000000;  // 10M

    private int fileType = -1;
    private ChatThreadTimer mThread=null;
    private XiaoXunNetworkManager mXunNetworkManager;
    public SendDataCallback sendDataCallback = null;
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "SendDataService,onBind");
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(LOG_TAG, "SendDataService,onUnbind");
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        public SendDataService getService() {
            return SendDataService.this;
        }


    }


   public void setChatroomWindowStatus(int status){
	Log.i(LOG_TAG, "setChatroomWindowStatus,status="+status);
	inChatroom=status;
}

        public void addSendData(ChatSendData data) {
           // DownloadService.size = size;
		if(mSendList!=null){
		   mSendList.add(data);	
		}
        }

    public SendDataCallback getSendDataCallback() {
        return sendDataCallback;
    }

    public void setSendDataCallback(SendDataCallback Callback) {
        this.sendDataCallback = Callback;
    }


    // Íš¹ý»Øµ÷»úÖÆ£¬œ«ServiceÄÚ²¿µÄ±ä»¯Ž«µÝµœÍâ²¿
    public interface SendDataCallback {
        void sendResault(int sn,int rc);
    }


    @Override
    public void onCreate() {
        super.onCreate();
	Log.i(LOG_TAG, "SendDataService,onCreate");
        mContext = this;
	inChatroom=1;
        isWaitLTE=false;
        isSendWork=false;
        registerLTEBroadcastReciever();
	initSendDataAll();
        mXunNetworkManager = (XiaoXunNetworkManager)getSystemService("xun.network.Service");
        mGID = mXunNetworkManager.getWatchGid();
        mEID = mXunNetworkManager.getWatchEid();
        mToken = mXunNetworkManager.getSID();
        mAES_KEY = mXunNetworkManager.getAESKey();
        Log.d(TAG, "[onCreate] >> isLoginOK " + mXunNetworkManager.isLoginOK());
        Log.d(TAG, "[onCreate] >> mGID " + mGID);
        Log.d(TAG, "[onCreate] >> mEID " + mEID);
        Log.d(TAG, "[onCreate] >> mToken " + mToken);
        Log.d(TAG, "[onCreate] >> mAES_KEY " + mAES_KEY);
    }



    public SendDataService() {
        super(TAG);
    }

   public void initSendDataAll(){
	Log.i(LOG_TAG, "initSendDataAll");
	mSendList=new ArrayList<ChatSendData>();
       if(isWifi(this)){
           //startDownloadService();
           isSendWork=true;
       }else{
           XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)getSystemService("xun.network.Service");
           boolean requireResault=networkService.requireLTEMode("xunchatroom");
           Log.i(TAG, "requireResault="+requireResault);
           if(requireResault){
               isWaitLTE=true;
               isSendWork=false;
           }else{
               //startDownloadService();
               isSendWork=true;
               networkService.releaseLTEMode("xunchatroom");
           }
       }
	initSendThread();
}

   public void initSendThread(){
	Log.i(LOG_TAG, "initSendThread");
            mThread=new ChatThreadTimer(1000, new ChatThreadTimer.TimerInterface() {
                @Override
                public void doTimerOut() {
                    Log.i(LOG_TAG, "doTimerOut,list size="+mSendList.size());
                    if(!isSendWork){
                        Log.i(LOG_TAG, "doTimerOut return,,isSendWork="+isSendWork);
                        return;
                    }
		    if(mSendList.size()>0){
               	   
			    if(mSendList.get(0).getSendDataStatus()==0){
				curSendSN=mSendList.get(0).getSendDataSN();
				sendCount=0;
				mSendList.get(0).setSendDataStatus(1);
				mGID=mSendList.get(0).getSendDataGID();
				Log.i(TAG, "[doTimerOut] >> mGID " + mGID);
				if(mSendList.get(0).getSendDataType()==1){
				setImagePath(mSendList.get(0).getSendDataPath());
				Log.i(LOG_TAG, "upload a new photo,curSendSN="+curSendSN);
                		getAndShareImage();
				}else if(mSendList.get(0).getSendDataType()==2){
				ChatUtil.getVideoThumbnail(curSendSN,mSendList.get(0).getSendDataPath());
				setVideoPath(mSendList.get(0).getSendDataPath(),Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+chat_audio_folder+"/"+curSendSN+".jpg");
				Log.i(LOG_TAG, "upload a new video,curSendSN="+curSendSN);
                		getAndUploadVideo();
				}
			     }
			     if(curSendSN==mSendList.get(0).getSendDataSN()){
				sendCount++;
				Log.i(LOG_TAG, "curSendSN="+curSendSN+" ,sendCount="+sendCount);
				if(sendCount>=60){
				
				}
				}
			   
		    }else{
			Log.i(LOG_TAG, "doTimerOut,list is empty,inChatroom="+inChatroom);
			if(inChatroom==0){
				mThread.stopThreadTimer();
				stopSendDataService();
			}
		    }
                }
            });
	mThread.start();
}


   public void stopSendDataService(){
	Log.i(LOG_TAG, "stopSendDataService");
	stopSelf();
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
        super.onDestroy();
        XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)getSystemService("xun.network.Service");
        networkService.releaseLTEMode("xunchatroom");
        unregisterLTEBroadcastReciever();
	inChatroom=0;
    }

    private void setImagePath(String path) {
        this.mImgOriginalPath = path;
    }

    private void setVideoPath(String videoPath, String thumbnailPath) {
        this.mVideoPath = videoPath;
        this.mVideoThumbnailPath = thumbnailPath;
    }

    /**
     * @step1 视频分享
     * 首先跟服务器握手，获取权限
     * 
     */
    private void getAndUploadVideo() {
        ShakeAndUploadVideoCallback callback = new ShakeAndUploadVideoCallback();
        mXunNetworkManager.setMapMSetValue(mGID, new String[]{"test"}, new String[]{"test"}, callback);
    }

    private void getAndShareImage() {
        ShakeAndUploadImageCallback callback = new ShakeAndUploadImageCallback();
        mXunNetworkManager.setMapMSetValue(mGID, new String[]{"test"}, new String[]{"test"}, callback);
    }




    public interface OnUpdateShareView {
        void onUpdateShareView(Message message);
    }

    public static void setOnUpdateShareView(OnUpdateShareView onUpdateShareView) {
        mOnUpdateShareView = onUpdateShareView;
    }

    private void notifyUISuccess() {
	Log.i(TAG, "notifyUISuccess");
	if(sendDataCallback!=null){
		sendDataCallback.sendResault(mSendList.get(0).getSendDataSN(),1);
        ChatUtil.ChatUtilDeleteFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+chat_audio_folder+"/"+curSendSN+".jpg");
		mSendList.remove(0);
	}
		sendCount=0;
		curSendSN=0;
/*
        if (mOnUpdateShareView != null) {
            Message message = new Message();
            message.what = MSG_SHARE_SUCCESS;
            mOnUpdateShareView.onUpdateShareView(message);
            Log.d(TAG, "[uploadImage] >> send ui handler success.");
        } else {
            Log.d(TAG, "[uploadImage] >> mOnUpdateShareView is null, send ui handler fail.");
        }
*/
    }

    private void notifyUIFail() {
/*
        if (mOnUpdateShareView != null) {
            Message message = new Message();
            message.what = MSG_SHARE_FAIL;
            mOnUpdateShareView.onUpdateShareView(message);
            Log.d(TAG, "mOnUpdateShareView is null, update ui fail.");
        } else {
            Log.d(TAG, "mOnUpdateShareView is null, send ui handler fail.");
        }
*/
	Log.i(TAG, "notifyUIFail");
	if(sendDataCallback!=null){
		sendDataCallback.sendResault(mSendList.get(0).getSendDataSN(),0);
        ChatUtil.ChatUtilDeleteFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+chat_audio_folder+"/"+curSendSN+".jpg");
		mSendList.remove(0);
	}
		sendCount=0;
		curSendSN=0;
    }
	
    /**
     * [uploadFilesLocal 上传文件]
     * @param token           [标志位]
     * @param type            [文件类型]
     * @param eid             [EID]
     * @param gid             [GID]
     * @param filePath        [原文件路径]
     * @param previewFilePath [预览文件路径]
     *
     *  上传结果通过OnUploadResult返回
     *  文件上传成功只是表示服务器收到了文件，最终需要服务器发送给家长APP端才是真正的分享成功
     *  服务器发送文件给家长APP端通过uploadNotice完成，结果在UploadFileCallback回调
     */
	private void uploadFilesLocal(final String token, String type, final String eid, final String gid, final String filePath, final String previewFilePath) {
        UploadFile uploadFile = new UploadFile(mContext, mToken, mAES_KEY);
        Log.d(TAG, "uploadFilesLocal >> filePath " + filePath + " " + previewFilePath);
        uploadFile.uploadFile(token, type, eid, gid, filePath, previewFilePath, 
            new ProgressListener() {
                @Override
                public void transferred(long l) {
                    Log.d(TAG, "[transferred] >> l : " + l);
                }
            }, 
            new OnUploadResult() {
                @Override
                public void onResult(String s) {
                    Log.d(TAG, "[OnUploadResult] >> onResult : " + s);
                    if (s.contains("GP")) {
                        // upload success
                        UploadFileCallback callback = new UploadFileCallback();                        
                        mXunNetworkManager.uploadNotice(eid, gid, type, s, callback);
                    } else {
                        notifyUIFail();
                    }
                }
            });            
        
    }

     private class UploadFileCallback extends IResponseDataCallBack.Stub{
           @Override
           public void onSuccess(ResponseData responseData) {         
               Log.d(TAG,"[UploadFileCallback] onSuccess >> responseData :" + responseData);
               notifyUISuccess();
           }
           @Override
           public void onError(int i, String s) {
               Log.d(TAG,"[UploadFileCallback] onError >> i :" + i + " ; s : " + s);
               notifyUIFail();            
          }    
    }

    private class ShakeAndUploadImageCallback extends IResponseDataCallBack.Stub{
           @Override
           public void onSuccess(ResponseData responseData) {        
               uploadFilesLocal(mToken, IMAGE_TYPE, mEID, mGID, mImgOriginalPath, mImgOriginalPath); 
               Log.d(TAG,"[ShakeAndUploadImageCallback] onSuccess >> responseData :" + responseData);
           }
           @Override
           public void onError(int i, String s) {
               Log.d(TAG,"[ShakeAndUploadImageCallback] onError >> i :" + i + " ; s : " + s);
          }    
    }

    /**
     * setMapValue的回调接口，会将服务器的握手结果返回
    */
    private class ShakeAndUploadVideoCallback extends IResponseDataCallBack.Stub{
           @Override
           public void onSuccess(ResponseData responseData) {
                // 上传文件接口    
                uploadFilesLocal(mToken, VIDEO_TYPE, mEID, mGID, mVideoPath, mVideoThumbnailPath); 
                Log.d(TAG,"[ShakeAndUploadVideoCallback] onSuccess >> responseData :" + responseData);
           }
           @Override
           public void onError(int i, String s) {
               Log.d(TAG,"[ShakeAndUploadVideoCallback] onError >> i :" + i + " ; s : " + s);
          }    
    }

    public void registerLTEBroadcastReciever(){
        Log.i(TAG, "registerLTEBroadcastReciever");
        //实例化过滤器；
        //lIntentFilter = new IntentFilter();
        //添加过滤的Action值；
        IntentFilter lIntentFilter = new IntentFilter(Constant.ACTION_NET_SWITCH_SUCC);
        //实例化广播监听器；
        lReceiver = new LTEBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(lReceiver, lIntentFilter);

    }

    public void unregisterLTEBroadcastReciever(){
        Log.i(TAG, "unregisterLTEBroadcastReciever");
        unregisterReceiver(lReceiver);
        lReceiver=null;
    }

    class  LTEBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "LTEBroadCastReciever,onReceive");
            String mAction=intent.getAction();
            Log.i(TAG, "mAction="+mAction);
            if(isWaitLTE){
                isWaitLTE=false;
                isSendWork=true;
                //startDownloadService();

            }

        }
    }
    private  boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}
