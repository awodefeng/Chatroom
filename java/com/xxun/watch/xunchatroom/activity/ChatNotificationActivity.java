package com.xxun.watch.xunchatroom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.control.ChatSoundControl;
import com.xxun.watch.xunchatroom.control.ChatVibratorControl;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.info.ChatNoticeInfo;
import com.xxun.watch.xunchatroom.control.ChatRoomControl;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import com.xxun.watch.xunchatroom.util.ChatUtil;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;
import android.view.WindowManager;

import android.content.BroadcastReceiver;
import android.media.AudioManager;
import android.app.Service;
import com.xxun.watch.xunchatroom.control.ChatContractManager;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.xxun.watch.xunchatroom.util.AsyncImageLoader;
import com.xxun.watch.xunchatroom.util.FileCache;
import com.xxun.watch.xunchatroom.util.ImageUtil;
import com.xxun.watch.xunchatroom.util.MemoryCache;
import java.io.File;
import com.xxun.watch.xunchatroom.activity.CircleDrawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.io.FileInputStream;
import com.xxun.watch.xunchatroom.control.ChatNotificationManager;
import android.util.XiaoXunUtil;

public class ChatNotificationActivity extends Activity {

    String LOG_TAG="chat notification";
    Button enter_btn;
    Button close_btn;
    ImageView photo_img;
    ImageView peer_bg;
    ImageView unread_img;
    ImageView content_img;
    TextView title_tv;
    TextView content_tv;
    ChatGifView notice_gif_view;
    private ChatNoticeInfo notice_info;
    private int max_text_length=9;
    ChatRoomControl mChatContol=null;
    private IntentFilter intentFilter;
    private int audioPlayerState=0;
    private AudioManager mAudioManager=null;
    private int currentVolume=0;
    //定义一个广播监听器；
    private NoticeBroadCastReciever mReceiver;
    private Toast mToast =null;
    Activity mActivity=null;
    private AsyncImageLoader imageLoader;
    private boolean isNeedSearch=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_notification);
	mActivity=this;
	isNeedSearch=true;
        if(mChatContol==null)
        {
            mChatContol=ChatRoomControl.getInstance();
        }
	mAudioManager=(AudioManager)getSystemService(Service.AUDIO_SERVICE);
	currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	Log.i("LOG_TAG","currentVolume="+currentVolume);
        Intent my_intent=getIntent();
        Bundle bundle = my_intent.getExtras();
        notice_info = (ChatNoticeInfo)bundle.getSerializable(ChatKeyString.KEY_NOTICE);

        notice_gif_view=(ChatGifView)findViewById(R.id.gif_play_notice);
        notice_gif_view.setVisibility(View.GONE);

        title_tv=(TextView)findViewById(R.id.notif_title_tv);
        title_tv.setText(notice_info.getNickName());

        content_tv=(TextView)findViewById(R.id.notif_content_tv);
        content_img=(ImageView)findViewById(R.id.notif_content_iv);
        Log.i(LOG_TAG, "notice_info.getContentType="+notice_info.getContentType());
        if(notice_info.getContentType()==1) {
            content_img.setVisibility(View.GONE);
            content_tv.setVisibility(View.VISIBLE);
            byte[] data= ChatUtil.ChatUtilReadDataFromFile(notice_info.getFilePath());
			if(data!=null){
                        String data_text=new String(data);
			String show_text=null;
			if(data_text.length()>max_text_length){
				show_text=data_text.substring(0,max_text_length)+"...";		
			}else{
				show_text=data_text;
			}
			Log.i("chat", "notice text,data_text="+data_text);
			Log.i("chat", "notice text,show_text="+show_text);
                        content_tv.setText(show_text);
			}
        }
        else  if(notice_info.getContentType()==2){
            content_tv.setVisibility(View.GONE);
            content_img.setVisibility(View.VISIBLE);
            content_img.setImageResource(R.mipmap.face_icon_1+Integer.valueOf(notice_info.getFilePath()));
        }
        else  if(notice_info.getContentType()==3){
            content_img.setVisibility(View.GONE);
            content_tv.setVisibility(View.VISIBLE);
            content_tv.setText(getString(R.string.sms_group));
        }
	else  if(notice_info.getContentType()==4&&XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){
            content_img.setVisibility(View.GONE);
            content_tv.setVisibility(View.VISIBLE);
            content_tv.setText(getString(R.string.new_photo));
        }
	else  if(notice_info.getContentType()==5&&XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){
            content_img.setVisibility(View.GONE);
            content_tv.setVisibility(View.VISIBLE);
            content_tv.setText(getString(R.string.new_video));
        }
        else {
            content_img.setVisibility(View.GONE);
            content_tv.setVisibility(View.VISIBLE);
            content_tv.setText(notice_info.getNoticeDuration());
        }

        peer_bg=(ImageView)findViewById(R.id.notif_peer_bg);
        peer_bg.setImageResource(R.mipmap.peer_bg);

        photo_img=(ImageView)findViewById(R.id.notif_photo_img);
	String url = notice_info.getContractItemAvatar();
		Bitmap bitmap = null;
		Log.i(LOG_TAG,"url ="+url);
		if(url != null){
			try {
			String filename = URLEncoder.encode(url.replace("http","https"),"utf-8");
                    	File f = new File("/storage/emulated/0/xiaoxun_cache/photo_img/", filename);
			bitmap=BitmapFactory.decodeStream(new FileInputStream(f), null, null);
			}catch (UnsupportedEncodingException e) {
                    	e.printStackTrace();
                	}catch (Exception e) { }
		       //bitmap = imageLoader.loadBitmap(photo_img, url.replace("http","https"),ChatUtil.ChatUitilGetPhotoByAttr(notice_info.getPhotoID()));
		        if(bitmap !=null){
		                photo_img.setImageDrawable(new CircleDrawable(bitmap));
		        }else{
			photo_img.setImageResource(ChatUtil.ChatUitilGetPhotoByAttr(notice_info.getPhotoID()));
			}
		}else{
		       //viewHolder.img.setImageResource(getPhotoPic(mdata.get(position).attri,false));
		 photo_img.setImageResource(ChatUtil.ChatUitilGetPhotoByAttr(notice_info.getPhotoID()));
		}
        //photo_img.setImageResource(ChatUtil.ChatUitilGetPhotoByAttr(notice_info.getPhotoID()));

        if(notice_info.getIsPlayed()==0) {
            unread_img = (ImageView) findViewById(R.id.notif_unread_img);
            unread_img.setVisibility(View.VISIBLE);
            unread_img.setImageResource(R.mipmap.unread);
        }
        else {
            unread_img = (ImageView) findViewById(R.id.notif_unread_img);
            unread_img.setVisibility(View.GONE);
        }
	audioPlayerState=ChatRoomControl.ChatStateIdle;
        enter_btn=(Button)findViewById(R.id.notif_enter_btn);
        enter_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
		if(checkTemprature()){
			showTip(getString(R.string.temp_high));
			return false;
		}else if(isChargeForbidden()){
            showTip(getString(R.string.charge_tips));
            return false;
        }
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                        //Toast.makeText(ChatNotificationActivity.this, "enter btn down", Toast.LENGTH_SHORT).show();
                        enter_btn.setBackgroundResource(R.drawable.enter_down);
                        break;

                    case MotionEvent.ACTION_UP:
                        //Toast.makeText(ChatNotificationActivity.this, "enter btn up", Toast.LENGTH_SHORT).show();
                        enter_btn.setBackgroundResource(R.drawable.enter_up);
			isNeedSearch=false;
                        finish();
			if(notice_info.getContentType()==3&&notice_info.getNoticeGID().equals("SMS_GROUP")){
			xunEnterSmsMainWindow();
			}else{
			xun_enter_chatroom_main_windows(notice_info.getNoticeGID(),notice_info.getmDate());
			}
                        break;
                }
                return false;
            }
        });

        close_btn=(Button)findViewById(R.id.notif_cancel_btn);
        close_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(isChargeForbidden()){
                    showTip(getString(R.string.charge_tips));
                    return false;
                } 
                
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                        //Toast.makeText(ChatNotificationActivity.this, "close btn down", Toast.LENGTH_SHORT).show();
                        close_btn.setBackgroundResource(R.drawable.close_down);
                        break;

                    case MotionEvent.ACTION_UP:
                        //Toast.makeText(ChatNotificationActivity.this, "close btn up", Toast.LENGTH_SHORT).show();
                        close_btn.setBackgroundResource(R.drawable.close_up);                   
                        finish();
                        break;
                }
                return false;
            }
        });


        peer_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ChatNotificationActivity.this, "tap down", Toast.LENGTH_SHORT).show();
            	 Log.i(LOG_TAG,"tap notice message");
            	if(checkTemprature()){
            		showTip(getString(R.string.temp_high));
            		return ;
            	}else if(isChargeForbidden()){
                    showTip(getString(R.string.charge_tips));
                    return;
                }

                if(notice_info.getContentType()==1||notice_info.getContentType()==3)
                {
			         Log.i(LOG_TAG,"show text");
                    unread_img.setVisibility(View.GONE);
                    ChatListItemInfo notice_chat = ChatListDB.getInstance(getApplicationContext()).readOneChatFromFamily(notice_info.getNoticeGID(), notice_info.getmDate());
                    notice_info.setIsPlayed(1);
                    notice_chat.setIsPlayed(1);
                    ChatListDB.getInstance(getApplicationContext()).updateChatMsg(notice_info.getNoticeGID(), notice_chat, notice_info.getmDate());
                    Intent my_intent=new Intent();
                    my_intent.putExtra(ChatKeyString.KEY_CONTENT,notice_info.getFilePath());
                    my_intent.setClass(ChatNotificationActivity.this,ChatTextContentActivity.class);
                    startActivity(my_intent);
                }else if(notice_info.getContentType()==2){
			 Log.i(LOG_TAG,"tap face");
                    unread_img.setVisibility(View.GONE);
                    ChatListItemInfo notice_chat = ChatListDB.getInstance(getApplicationContext()).readOneChatFromFamily(notice_info.getNoticeGID(), notice_info.getmDate());
                    notice_info.setIsPlayed(1);
                    notice_chat.setIsPlayed(1);
                    ChatListDB.getInstance(getApplicationContext()).updateChatMsg(notice_info.getNoticeGID(), notice_chat, notice_info.getmDate());
		}else if(notice_info.getContentType()==4||notice_info.getContentType()==5){
                    Log.i(LOG_TAG,"tap photo or video");
                    isNeedSearch=false;
                    finish();
                    //if(notice_info.getContentType()==3&&notice_info.getNoticeGID().equals("SMS_GROUP")){
                    //    xunEnterSmsMainWindow();
                   // }else{
                        xun_enter_chatroom_main_windows(notice_info.getNoticeGID(),notice_info.getmDate());
                   // }
                }
                else {
			 Log.i(LOG_TAG,"play voice");
		   if(audioPlayerState==ChatRoomControl.ChatStatePlaying){
			Log.i("LOG_TAG","stop return");
                         notice_gif_view.setVisibility(View.GONE);
			   mChatContol.StopPlaying();
			   audioPlayerState=ChatRoomControl.ChatStateIdle;
			   return;
		   }
		     int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     Log.i("LOG_TAG","maxVolume="+maxVolume);
 		     //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0); 
                    notice_gif_view.setVisibility(View.VISIBLE);
                    unread_img.setVisibility(View.GONE);
                    notice_gif_view.setMovieResource(R.mipmap.play_peer);
                    ChatListItemInfo notice_chat = ChatListDB.getInstance(getApplicationContext()).readOneChatFromFamily(notice_info.getNoticeGID(), notice_info.getmDate());
                    notice_info.setIsPlayed(1);
                    notice_chat.setIsPlayed(1);
                    ChatListDB.getInstance(getApplicationContext()).updateChatMsg(notice_info.getNoticeGID(), notice_chat, notice_info.getmDate());
 			OnCompletionListener complete_listener=new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            Log.i(LOG_TAG,"play complete");
                           notice_gif_view.setVisibility(View.GONE);
			   mChatContol.StopPlaying();
			   audioPlayerState=ChatRoomControl.ChatStateIdle;
			   //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0); 
                        }
                    };

			OnErrorListener error_listener=new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer,int i,int i1) {
                            Log.i(LOG_TAG,"play error");
				mChatContol.StopPlaying();
				notice_gif_view.setVisibility(View.GONE);
				audioPlayerState=ChatRoomControl.ChatStateIdle;
				//mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0); 
                            return false;	
                        }
                    };
                    audioPlayerState=mChatContol.StartPlaying(notice_info.getFilePath(),complete_listener,error_listener );
                }
		ChatContractManager.updateAllMissChatCount(ChatNotificationActivity.this);
            }
        });

			OnCompletionListener complete_listener1=new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                             Log.i(LOG_TAG, "new msg sound play over");
                		ChatSoundControl.StopPlaying();
               			 ChatVibratorControl.StopPlaying();
                        }
                    };

			OnErrorListener error_listener1=new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer,int i,int i1) {
                            Log.i(LOG_TAG,"play error");
                            return false;	
                        }
                    };

        ChatSoundControl.StartPlaying(this, R.raw.new_msg,complete_listener1,error_listener1  );
        ChatVibratorControl.StartPlaying(this);
	setChatNoticeWindowFlag();
	registerBroadcastReciever();
    }

        @Override
        public void onDestroy() {
		Log.i(LOG_TAG, "ChatNotificationActivity,onDestroy");
		super.onDestroy();
               unregisterBroadcastReciever();
		closeTip();
	   	Log.i(LOG_TAG, "ChatNotificationActivity,onDestroy,isNeedSearch="+isNeedSearch);
		if(isNeedSearch){
		sendSearchChatMsgBroacast();
		}   
		handleExitActivity();
           }

    private void handleExitActivity(){
	Log.i(LOG_TAG, "handleExitActivity,audioPlayerState="+audioPlayerState);
	if(audioPlayerState==ChatRoomControl.ChatStatePlaying){
	Log.i("LOG_TAG","stop playing");
        //notice_gif_view.setVisibility(View.GONE);
	mChatContol.StopPlaying();
	audioPlayerState=ChatRoomControl.ChatStateIdle;
	}	
    }    

   private void xunEnterSmsMainWindow(){
        Intent my_intent=new Intent();
        my_intent.setClass(this,ChatSmsActivity.class);
        startActivity(my_intent);
    }

    private void setChatNoticeWindowFlag() {
        // set this flag so this activity will stay in front of the keyguard
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

   
        
                flags |= WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
          
   
        Log.i(LOG_TAG,"setChatNoticeWindowFlag");

        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= flags;
        getWindow().setAttributes(lp);
    }

   private void sendSearchChatMsgBroacast(){
        Intent it = new Intent("com.broadcast.xxun.searchMessage");
        it.setPackage("com.xxun.watch.xunchatroom");
        Log.i(LOG_TAG, "sendSearchChatMsgBroacast");
        sendBroadcast(it);
    }

   private void xun_enter_chatroom_main_windows(String gid,String date){
       Log.i(LOG_TAG,"xun_enter_chatroom_main_windows");
       Log.i(LOG_TAG,"gid="+gid);
       Log.i(LOG_TAG,"date="+date);
        Intent my_intent=new Intent();
        my_intent.putExtra(ChatKeyString.KEY_CUR_GID,gid);
        my_intent.putExtra(ChatKeyString.KEY_AUTO_PLAY,1);
       my_intent.putExtra(ChatKeyString.KEY_DATE,date);
	if(XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){
            my_intent.setClass(this,ChatroomMainNewActivity.class);
	}else{
        my_intent.setClass(this,ChatroomMainActivity.class);
	}
        startActivity(my_intent);
    }

    public void registerBroadcastReciever(){
        Log.i(LOG_TAG, "registerBroadcastReciever");
        //实例化过滤器；
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("com.xxun.watch.checkdelbroadcast");

	intentFilter.addAction("com.broadcast.xxun.watchCall");

	intentFilter.addAction("com.xxun.watch.playringbroadcast");

	intentFilter.addAction("com.xxun.watch.contractrefreshbroadcast");
        //实例化广播监听器；
        mReceiver = new NoticeBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(mReceiver, intentFilter);

    }

    public void unregisterBroadcastReciever(){
        Log.i(LOG_TAG, "unregisterBroadcastReciever");
        unregisterReceiver(mReceiver);
	mReceiver=null;
    }

    class  NoticeBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "NoticeBroadCastReciever,onReceive");
                      //sendSearchChatMsgBroacast();
		String mAction=intent.getAction();
		Log.i(LOG_TAG, "mAction="+mAction);
		if(mAction.equals("com.xxun.watch.checkdelbroadcast")){ 
                      finish();
		}else if(mAction.equals("com.broadcast.xxun.watchCall")){
		      notice_gif_view.setVisibility(View.GONE);
		      mChatContol.StopPlaying();
		      audioPlayerState=ChatRoomControl.ChatStateIdle;
		}else if(mAction.equals("com.xxun.watch.playringbroadcast")){
			OnCompletionListener complete_listener1=new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                             Log.i(LOG_TAG, "new msg sound play over");
                		ChatSoundControl.StopPlaying();
               			 ChatVibratorControl.StopPlaying();
                        }
                    };

			OnErrorListener error_listener1=new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer,int i,int i1) {
                            Log.i(LOG_TAG,"play error");
                            return false;	
                        }
                    };
			setChatNoticeWindowFlag();
		      ChatSoundControl.StartPlaying(mActivity, R.raw.new_msg,complete_listener1,error_listener1  );
		}else if(mAction.equals("com.xxun.watch.contractrefreshbroadcast")){
			if(ChatNotificationManager.compareContractInNotifcation(context)){
				finish();
			}
		}
        }
    }

     private boolean checkTemprature(){
	boolean isHigh=false;
	int currentTmp = Integer.valueOf(SystemProperties.get("persist.sys.xxun.aptemper"));         //ŽËÊôÐÔŽæŽ¢ÁËµ±Ç°ÊÇ·ñžßÎÂ£¬2ÎªÕý³££¬7Îª³¬¹ý43¶È£¬3Îª³¬¹ý46¶È
	if(currentTmp == 7 || currentTmp == 3){       //µ±ÎÂ¶È³¬¹ý43»ò46¶ÈµÄÊ±ºòœøÐÐÏÞÖÆ
	     isHigh=true;
	}
	 //isHigh=true;
	Log.i(LOG_TAG, "checkTemprature,isHigh="+isHigh);
	return isHigh;
   }

     private void showTip(final String str) {  
        //runOnUiThread(new Runnable() {  
           // @Override  
           // public void run() {  
                if (mToast == null) {  
                    mToast = Toast.makeText(getApplicationContext(), "",  
                            Toast.LENGTH_LONG);  
                    LinearLayout layout = (LinearLayout) mToast.getView();  
                    TextView tv = (TextView) layout.getChildAt(0);  
                    tv.setTextSize(40);  
                }  
                //mToast.cancel();  
                mToast.setGravity(Gravity.TOP, 0, 0);  
                mToast.setText(str);  
                mToast.show();  
            //}  
        //});  
    }

    private boolean isChargeForbidden() {
        try{
            if (SystemProperties.get("ro.build.type").equals("user") 
                && !"true".equals(Settings.System.getString(getContentResolver(), "isMidtest")) 
                && Settings.System.getInt(getContentResolver(),"is_localprop_exist") == 0
                &&  "true".equals(SystemProperties.get("persist.sys.isUsbConfigured"))
                ) {
                return true; 
            }
        }catch (SettingNotFoundException e){
            e.printStackTrace();
        }

        return false; 
    }

   private void closeTip(){
	if(mToast!=null){
	mToast.cancel();
	}
   }

  protected void onResume(){
        super.onResume();
	Settings.System.putString(getContentResolver(), "on_xunlauncher_homescreen", "false");
   }

}
