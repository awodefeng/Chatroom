package com.xxun.watch.xunchatroom.activity;

import android.app.Activity;
import android.content.Context;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ComponentName;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.media.AudioManager;
import android.app.Service;
import com.xxun.watch.xunchatroom.R;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IMessageReceiveListener;
import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xxun.watch.xunchatroom.adapter.ChatListAdapter;
import com.xxun.watch.xunchatroom.adapter.ChatMainListAdapter;
import com.xxun.watch.xunchatroom.control.ChatNetService;
import com.xxun.watch.xunchatroom.control.ChatNotificationManager;
import com.xxun.watch.xunchatroom.control.ChatOfflineMsgManager;
import com.xxun.watch.xunchatroom.control.ChatRecieveMsgManager;
import com.xxun.watch.xunchatroom.control.ChatRoomControl;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.info.ChatNoticeInfo;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import com.xxun.watch.xunchatroom.util.ChatUtil;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;
import com.xxun.watch.xunchatroom.control.ChatAudoPlayManager;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Base64;
import android.content.pm.PackageManager;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import static android.content.ContentValues.TAG;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;
import android.view.TouchDelegate;
import android.graphics.Rect;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import java.lang.ref.WeakReference;
import com.xxun.watch.xunchatroom.control.ChatContractManager;
import com.xxun.watch.xunchatroom.control.ChatSoundControl;
import com.xxun.watch.xunchatroom.control.ChatVibratorControl;
import com.xiaoxun.sdk.utils.Constant; 
import android.provider.Settings;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.content.ServiceConnection;
import android.app.Service;  
import android.content.Intent;  
import android.os.Binder;  
import android.os.IBinder;  
import com.xxun.watch.xunchatroom.service.SendDataService;
import com.xxun.watch.xunchatroom.info.ChatSendData;
import com.xxun.watch.xunchatroom.service.RecieverDataService;
import com.xxun.watch.xunchatroom.info.ChatRecieverData;
import java.io.File;
import android.util.XiaoXunUtil;
import java.io.InputStream;
public class ChatroomMainNewActivity extends Activity {
    private ChatMainListAdapter adapter ;//= new ChatListAdapter();
    private Button rec_button;
    private Button face_button;
    private ListView chatList;
    private TextView empty_tv;
    private ChatGifView rec_gif_view=null;
    ArrayList<ChatListItemInfo> ListDte=null;
    private ChatRoomControl mChatContol=null;
    private String path=null;
    private String LOG_TAG="chat";
    private int clickIndex=-1;
    private static String cur_gid=null;
    private static Context myContext=null;
    private boolean is_down=false;
    private CircleProgressBar pb_gif=null;
    final static int xun_chat_record_status_recording=1;
    final static int xun_chat_record_status_record_finish=2;
    final static int xun_chat_record_status_record_cancel=3;
    final static int xun_chat_record_status_record_full=4;
    private ChatThreadTimer my_thread=null;
    private ChatThreadTimer errorThread=null; 
    private String date=null;
    private int autoPlay=0;
    private int audioPlayerState=0;
    private ImageView sendStatusImageView =null;
    int count=0;
    private CheckRefreshBroadCastReciever mReceiver;
    private IntentFilter intentFilter;
    private AudioManager mAudioManager=null;
    private int currentVolume=0;
    private boolean forbidUpdate=false;
    private int faceValue=-1;
    private Toast mToast =null;
    private static boolean isSW710=false;
    private boolean isIncomingCall=false;
    private RelativeLayout myView;
    float mPosX=0;
    float mPosY=0;
    float mCurPosX=0;
    float mCurPosY=0;
    private GridView my_gv;
    private Button gv_cancel_button; 
    private String imagePath=null;
    private ServiceConnection conn;
    private ServiceConnection connRec;
    SendDataService.MyBinder binder = null;
    RecieverDataService.RecBinder recbinder = null;
    private static int IdleDisplayState =0;
    private static int GridDisplayState =1;
    private int curDisplayState =0;
	private ChatThreadTimer mThread=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LOG_TAG","ChatroomMainNewActivity,onCreate");
	Settings.System.putString(getContentResolver(), "on_xunlauncher_homescreen", "false");
        Intent intent =getIntent();
        String gid=intent.getStringExtra(ChatKeyString.KEY_CUR_GID);
        Log.i("LOG_TAG","gid="+gid);
        cur_gid=gid;
	myContext=this;
        autoPlay=intent.getIntExtra(ChatKeyString.KEY_AUTO_PLAY,0);
        Log.i("LOG_TAG","autoPlay="+autoPlay);

       date=intent.getStringExtra(ChatKeyString.KEY_DATE);
        Log.i("LOG_TAG","date="+date);

	isSW710 = "SW710".equals(Constant.PROJECT_NAME);
	Log.i("LOG_TAG","isSW710="+isSW710);
        //ChatListDB.getInstance(getApplicationContext()).delAllMsg(cur_gid);
        ChatRecieveMsgManager.initRecieveMsgGroup(getApplicationContext(),cur_gid);
	ChatNotificationManager.delChatNoticeGroupByGID(this,cur_gid);
        if(mChatContol==null)
        {
            mChatContol=ChatRoomControl.getInstance();
        }
	mAudioManager=(AudioManager)getSystemService(Service.AUDIO_SERVICE);
	currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	//sendFinishStoryBroacast(this);
	Log.i("LOG_TAG","currentVolume="+currentVolume);
        setContentView(R.layout.activity_chatroom_main_new);
	forbidUpdate=false;
        rec_button=(Button) findViewById(R.id.chatroom_rec_btn);
        face_button=(Button) findViewById(R.id.chatroom_face_btn);
	gv_cancel_button=(Button) findViewById(R.id.chatroom_grid_btn);
        chatList=(ListView) findViewById(R.id.chatroom_lv);
	empty_tv=(TextView) findViewById(R.id.empty_chat);
	//sendStatusImageView =(ImageView) findViewById(R.id.local_img_send_id);
	chatList.setEmptyView(empty_tv);
        rec_gif_view=(ChatGifView)findViewById(R.id.gif_recording);
         pb_gif=(CircleProgressBar)findViewById(R.id.pb_gif);
	my_gv=(GridView)findViewById(R.id.chat_func_grid);
        rec_gif_view.setVisibility(View.GONE);
        pb_gif.setVisibility(View.GONE);
	my_gv.setVisibility(View.GONE);
	gv_cancel_button.setVisibility(View.GONE);
        faceValue=-1;
	clickIndex=-1;
	audioPlayerState=ChatRoomControl.ChatStateIdle;
        initViews();
	isIncomingCall=false;
	checkAllItemPhotoID();
        adapter = new ChatMainListAdapter(ChatroomMainNewActivity.this,ListDte);
        chatList.setAdapter(adapter);
        //ViewGroup.LayoutParams params = chatList.getLayoutParams();
       // params.height = 240;
        //chatList.setLayoutParams(params);
        Rect delegateArea = new Rect();
                       Button delegate = rec_button;
                        // Hit rectangle in parent's coordinates
                       delegate.getHitRect(delegateArea);
        delegateArea.top -= 30;
        TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);

                        if(View.class.isInstance(delegate.getParent())){
                               // 设置视图扩大后的触摸区域
                              ((View)delegate.getParent()).setTouchDelegate(expandedArea);
                            }
        rec_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
			closeTip();
			Log.i("LOG_TAG","rec btn down");
                       //Toast.makeText(ChatroomMainNewActivity.this, "rec btn down", Toast.LENGTH_SHORT).show();
			if(!is_down){
			is_down=true;
			//if(isSW710){
			rec_button.setBackgroundResource(R.drawable.rec_btn_down);
			//}else{
                        //rec_button.setBackgroundResource(R.mipmap.rec_btn_down);
			//}
			rec_button.setKeepScreenOn(true);
			if(mChatContol.ChatCurState==mChatContol.ChatStateRecording){
				mChatContol.StopRecord();
				sendStopBgRecordBroacast();
			}else if(mChatContol.ChatCurState==mChatContol.ChatStatePlaying){
				checkAudioState();
				clickIndex=-1;
			}
                        path=mChatContol.ChatPrepareRecord();
                        if(mChatContol.StartRecord(path)!=ChatRoomControl.ChatStateError){
                        handleRecorAction(xun_chat_record_status_recording);
			}else{
			Log.i(LOG_TAG,"recording error");
			}
			}
                        break;

                    case MotionEvent.ACTION_UP:
			Log.i("LOG_TAG","rec btn up");
			//Toast.makeText(ChatroomMainNewActivity.this, "rec btn up", Toast.LENGTH_SHORT).show();
			if(is_down){
			rec_button.setKeepScreenOn(false);
			//if(isSW710){
			rec_button.setBackgroundResource(R.drawable.rec_btn_up);
			//}else{
                        //rec_button.setBackgroundResource(R.mipmap.rec_btn_up);
			//}

			int preStatus=mChatContol.ChatCurState;
                        mChatContol.StopRecord();
                        //mChatContol.StartPlaying(path);
                       // String FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                        //FileName += "/goldfallen.mp3";
                       // mChatContol.StartPlaying(FileName);
			Log.i("LOG_TAG","preStatus="+preStatus);
			if(preStatus==mChatContol.ChatStateRecording){
                        handleRecorAction(xun_chat_record_status_record_finish);
			}
			is_down=false;
			}
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if(is_down) {
                            int x = (int) motionEvent.getX();
                            int y = (int) motionEvent.getY();
                            Log.i(LOG_TAG, "move x=" + x);
                            Log.i(LOG_TAG, "move y=" + y);
                            //int top=rec_button.getTop();
                            // Log.i(LOG_TAG,"button top="+top);
                            if (y <= -30) {
				rec_button.setKeepScreenOn(false);
				//if(isSW710){
				rec_button.setBackgroundResource(R.drawable.rec_btn_up);
				//}else{
                                //rec_button.setBackgroundResource(R.mipmap.rec_btn_up);
				//}
                                mChatContol.StopRecord();
                                handleRecorAction(xun_chat_record_status_record_cancel);
                                is_down=false;
                            }
                        }
                        break;
                }
                return false;
            }
        });

        face_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                        //Toast.makeText(ChatroomMainNewActivity.this, "face btn down", Toast.LENGTH_SHORT).show();
			closeTip();
			//if(isSW710){
			//face_button.setBackgroundResource(R.drawable.face_down);
			//}else{
                        //face_button.setBackgroundResource(R.mipmap.face_down);
			//}
                        break;

                    case MotionEvent.ACTION_UP:
                        //Toast.makeText(ChatroomMainNewActivity.this, "rec btn up", Toast.LENGTH_SHORT).show();
			//if(isSW710){
			//face_button.setBackgroundResource(R.drawable.face_up);
			//}else{
                        //face_button.setBackgroundResource(R.mipmap.face_up);
			//}
			//faceValue=-1;
                        //xun_enter_chatroom_face_windows();
			/*rec_gif_view.setVisibility(View.GONE);
        		pb_gif.setVisibility(View.GONE);
			chatList.setVisibility(View.GONE);
			face_button.setVisibility(View.GONE);
			empty_tv.setVisibility(View.GONE);
			rec_button.setVisibility(View.GONE);
			my_gv.setVisibility(View.VISIBLE);
			gv_cancel_button.setVisibility(View.VISIBLE);*/
			curDisplayState=GridDisplayState;
			showCurMainScreenByState(curDisplayState);
                        break;
                }
                return false;
            }
        });

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("LOG_TAG","chat list item click="+i);
		closeTip();
		checkAudioState();
		if(clickIndex==i){
			Log.i("LOG_TAG","click playing item,stop return");
			clickIndex=-1;
			return;
		}
                clickIndex=i;
                if(ListDte.get(i).getContentType()==1||ListDte.get(i).getContentType()==3){
                    ListDte.get(i).setIsPlayed(1);
                   // ListDte.get(i).setListItemState(ChatListItemInfo.list_tiem_state_playing);
                    //ChatListDB.getInstance(getApplicationContext()).addChatMsg(ListDte.get(i).getListItemGID(),ListDte.get(i));
                    ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(i).getListItemGID(),ListDte.get(i),ListDte.get(i).getmDate());
                    xun_enter_chatroom_text_content_windows(ListDte.get(i).getFilePath());
                }else if(ListDte.get(i).getContentType()==2){
			Log.i("LOG_TAG","click image item, return");
			ChatRecieverData recData=new ChatRecieverData(123,"",1);
			recbinder.getService().addRecieverData(recData);
			return;
		}else if(ListDte.get(i).getContentType()==4){
			Log.i("LOG_TAG","click photo item");
					/*
			String photoPath=null;
			if(ListDte.get(i).getListItemType()==0){
			Log.i("LOG_TAG","peer photo");
			photoPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ ListDte.get(i).getmDate()+".jpg";
			}else{
			Log.i("LOG_TAG","local photo");
			photoPath=ListDte.get(i).getFilePath();//Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ ListDte.get(i).getmDate()+".jpg";
			}
			Log.i("LOG_TAG","photoPath="+photoPath);
			if(photoPath!=null){
			File mFile=new File(photoPath);
			if(mFile.exists()&&!ChatRecieveMsgManager.isInDownloadQue(ListDte.get(i).getmDate())){
			Log.i("LOG_TAG","photoPath file is ok");
			ListDte.get(i).setIsPlayed(1);
			ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(i).getListItemGID(),ListDte.get(i),ListDte.get(i).getmDate());
			xun_enter_photo_display_windows(photoPath);
			}else{
			Log.i("LOG_TAG","photoPath file is missing");
			showTip(getString(R.string.load_data));
			}
			}*/
		}else if(ListDte.get(i).getContentType()==5){
			Log.i("LOG_TAG","click video item");
					/*
			String videoPath=null;
			if(ListDte.get(i).getListItemType()==0){
			Log.i("LOG_TAG","peer video");
			videoPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ ListDte.get(i).getmDate()+".mp4";
			}else{
			Log.i("LOG_TAG","local video");
			videoPath=ListDte.get(i).getFilePath();//Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ ListDte.get(i).getmDate()+".jpg";
			}
			Log.i("LOG_TAG","videoPath="+videoPath);
			if(videoPath!=null){
			File mFile=new File(videoPath);
			if(mFile.exists()&&!ChatRecieveMsgManager.isInDownloadQue(ListDte.get(i).getmDate())){
			Log.i("LOG_TAG","videoPath file is ok");
			ListDte.get(i).setIsPlayed(1);
			ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(i).getListItemGID(),ListDte.get(i),ListDte.get(i).getmDate());
			xun_enter_video_display_windows(videoPath);
			}else{
			Log.i("LOG_TAG","videoPath file is missing");
			showTip(getString(R.string.load_data));
			}
			}*/
		}
		else{
                    //xun_enter_chatroom_notification_windows();
		     int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     Log.i("LOG_TAG","maxVolume="+maxVolume);
 		     //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0); 
                    OnCompletionListener complete_listener=new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            Log.i(LOG_TAG,"play complete");
				mChatContol.StopPlaying();
			    if(clickIndex>=0&&clickIndex<ListDte.size()){
                            ListDte.get(clickIndex).setListItemState(ChatListItemInfo.list_tiem_state_null);
                            //ChatListDB.getInstance(getApplicationContext()).addChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex));
                            ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex),ListDte.get(clickIndex).getmDate());
			    }
			    audioPlayerState=ChatRoomControl.ChatStateIdle;
                            refreshListView(clickIndex);
			    clickIndex=-1;
			   // mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0); 
                        }
                    };

			OnErrorListener error_listener=new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer,int i,int i1) {
                            Log.i(LOG_TAG,"play error");
			     mChatContol.StopPlaying();
			   if(clickIndex>=0&&clickIndex<ListDte.size()){
                            ListDte.get(clickIndex).setListItemState(ChatListItemInfo.list_tiem_state_null);
                            //ChatListDB.getInstance(getApplicationContext()).addChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex));
                            ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex),ListDte.get(clickIndex).getmDate());
			   }
			    audioPlayerState=ChatRoomControl.ChatStateIdle;
                            refreshListView(-1);
			    clickIndex=-1;
			     //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0); 
                            return false;	
                        }
                    };
		Log.i("LOG_TAG","play audio,"+ListDte.get(i).getFilePath());
                    if((audioPlayerState=mChatContol.StartPlaying(ListDte.get(i).getFilePath(), complete_listener ,error_listener))!=ChatRoomControl.ChatStateError){
                    ListDte.get(i).setIsPlayed(1);
                    ListDte.get(i).setListItemState(ChatListItemInfo.list_tiem_state_playing);
                    //ChatListDB.getInstance(getApplicationContext()).addChatMsg(ListDte.get(i).getListItemGID(),ListDte.get(i));
                    ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(i).getListItemGID(),ListDte.get(i),ListDte.get(i).getmDate());
                    refreshListView(clickIndex);
		     }else{
				Log.i("LOG_TAG","play audio error");
				audioPlayerState=ChatRoomControl.ChatStateIdle;
                                clickIndex=-1;
				//mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0); 
			}
                }
		ChatContractManager.updateAllMissChatCount(ChatroomMainNewActivity.this);
            }
        });

       adapter.setOnItemResentClickListener(new ChatMainListAdapter.onItemResentListener() {
            @Override
            public void onResentClick(int i) {
     		Log.i("LOG_TAG","onResentClick,i="+i);
			closeTip();
			ChatListItemInfo updateInfo=ListDte.get(i);//
			if(updateInfo.getSendState()==0){
			Log.i("LOG_TAG","start resent");
			updateInfo.setSendState(1);
           		int sn=ChatUtil.ChatUtilGetServiceSN(getApplicationContext());
            		String date=Integer.toString(sn);
			String oldDate=ListDte.get(i).getmDate();
			updateInfo.setmDate(date);
			updateInfo.setSN(sn);
			updateInfo.setListItemStartMills(ChatUtil.getCurTimeMills());
			ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(i).getListItemGID(),updateInfo,oldDate);
			ListDte.clear();
			ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
			refreshListView(-1);
			ChatUtil.pushStatsOnly(myContext,ChatUtil.STATS_VOICE_SEND_RETRY);
			if(updateInfo.getContentType()==ChatListItemInfo.content_type_image){
				ConstrctChatImageSendBuff(updateInfo);
			}else{
				ConstrctChatSendBuff(updateInfo);
			}

			}else{
			Log.i("LOG_TAG","no need resent");
			}
            }
        });

       adapter.setOnItemRefreshListener(new ChatMainListAdapter.onItemRefreshListener() {
            @Override
            public void onRefresh(int i) {
     		Log.i(TAG,"onRefresh,i="+i);
		Log.i(TAG,"onRefreshlistener 4");
		refreshListView(-1);
            }
        });

		adapter.setOnItemPhotoClickListener(new ChatMainListAdapter.onItemPhotoListener() {
			@Override
			public void onPhotoClick(int i) {
				Log.i(TAG,"onPhotoClick,i="+i);
				if(ListDte.get(i).getContentType()==4){
					Log.i("LOG_TAG","click photo item");
					String photoPath=null;
					if(ListDte.get(i).getListItemType()==0){
						Log.i("LOG_TAG","peer photo");
						photoPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ ListDte.get(i).getmDate()+".jpg";
					}else{
						Log.i("LOG_TAG","local photo");
						photoPath=ListDte.get(i).getFilePath();//Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ ListDte.get(i).getmDate()+".jpg";
					}
					Log.i("LOG_TAG","photoPath="+photoPath);
					if(photoPath!=null){
						File mFile=new File(photoPath);
						if(mFile.exists()&&!ChatRecieveMsgManager.isInDownloadQue(ListDte.get(i).getmDate())){
							Log.i("LOG_TAG","photoPath file is ok");
							ListDte.get(i).setIsPlayed(1);
							ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(i).getListItemGID(),ListDte.get(i),ListDte.get(i).getmDate());
							xun_enter_photo_display_windows(photoPath);
						}else if(!ChatRecieveMsgManager.isInDownloadQue(ListDte.get(i).getmDate())&&!mFile.exists()){
							Log.i("LOG_TAG","photoPath file is reload");
							ChatRecieveMsgManager.startReload(ListDte.get(i),getApplicationContext());
							showTip(getString(R.string.load_data));
						}else{
							Log.i("LOG_TAG","photoPath file is missing");
							showTip(getString(R.string.load_data));
						}
					}
				}else if(ListDte.get(i).getContentType()==5){
					Log.i("LOG_TAG","click video item");
					String videoPath=null;
					if(ListDte.get(i).getListItemType()==0){
						Log.i("LOG_TAG","peer video");
						videoPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ ListDte.get(i).getmDate()+".mp4";
					}else{
						Log.i("LOG_TAG","local video");
						videoPath=ListDte.get(i).getFilePath();//Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ ListDte.get(i).getmDate()+".jpg";
					}
					Log.i("LOG_TAG","videoPath="+videoPath);
					if(videoPath!=null){
						File mFile=new File(videoPath);
						if(mFile.exists()&&!ChatRecieveMsgManager.isInDownloadQue(ListDte.get(i).getmDate())){
							Log.i("LOG_TAG","videoPath file is ok");
							ListDte.get(i).setIsPlayed(1);
							ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(i).getListItemGID(),ListDte.get(i),ListDte.get(i).getmDate());
							xun_enter_video_display_windows(videoPath);
						}else if(!ChatRecieveMsgManager.isInDownloadQue(ListDte.get(i).getmDate())&&!mFile.exists()){
							Log.i("LOG_TAG","videoPath file is reload");
							ChatRecieveMsgManager.startReload(ListDte.get(i),getApplicationContext());
							showTip(getString(R.string.load_data));
						}else{
							Log.i("LOG_TAG","videoPath file is missing");
							showTip(getString(R.string.load_data));
						}
					}
				}
			}
		});
/////////////////////////////////senddataservice start//////////////////////////////////////////
	 conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected");
		}	
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected");
		    binder = (SendDataService.MyBinder) service;
		    binder.getService().setSendDataCallback(new SendDataService.SendDataCallback() {
		        //ÖŽÐÐ»Øµ÷º¯Êý
		        @Override
		        public void sendResault(int sn,int rc) {
		      		Log.i(TAG, "sendResault");
				Log.i(TAG, "sn="+sn);
				Log.i(TAG, "rc="+rc);
		        }
		    });

		}
	};
	startSendDataService();

	 connRec = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected");
		}	
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected");
		    recbinder = (RecieverDataService.RecBinder) service;
		    recbinder.getService().setRecieverDataCallback(new RecieverDataService.RecieverDataCallback() {
		        //ÖŽÐÐ»Øµ÷º¯Êý
		        @Override
		        public void RecieverResault(int sn,int rc) {
		      		Log.i(TAG, "RecieverResault");
				Log.i(TAG, "sn="+sn);
				Log.i(TAG, "rc="+rc);
				//xun_enter_video_display_windows("");
		        }
		    });

		}
	};
	startRecieverDataService();
/////////////////////////////////senddataservice end///////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////
/////////////////////////////func grid start//////////////////////////////
	ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
        
            HashMap<String, Object> map1 = new HashMap<String, Object>();
            map1.put("ItemImage", R.drawable.capture);
            meumList.add(map1);
        
            HashMap<String, Object> map2 = new HashMap<String, Object>();
            map2.put("ItemImage", R.drawable.photo);
            meumList.add(map2);

            HashMap<String, Object> map3 = new HashMap<String, Object>();
            map3.put("ItemImage", R.drawable.face);
            meumList.add(map3);

        SimpleAdapter saMenuItem = new SimpleAdapter(this,
                meumList, //数据源
                R.layout.func_item_view, //xml实现
                new String[]{"ItemImage"}, //对应map的Key
                new int[]{R.id.ItemImage});  //对应R的Id

//添加Item到网格中
        my_gv.setAdapter(saMenuItem);
        my_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(LOG_TAG,"func GridView click index="+i);
		switch(i){
			case 0:
			//xun_enter_video_display();
			curDisplayState=IdleDisplayState;
			showCurMainScreenByState(curDisplayState);
			takePhoto();
			break;

			case 1:
			/*rec_gif_view.setVisibility(View.GONE);
        		pb_gif.setVisibility(View.GONE);
			chatList.setVisibility(View.VISIBLE);
			face_button.setVisibility(View.VISIBLE);
			empty_tv.setVisibility(View.VISIBLE);
			rec_button.setVisibility(View.VISIBLE);
			my_gv.setVisibility(View.GONE);
			gv_cancel_button.setVisibility(View.GONE);*/
			curDisplayState=IdleDisplayState;
			showCurMainScreenByState(curDisplayState);
			selectPhoto();
			break;

			case 2:
			/*rec_gif_view.setVisibility(View.GONE);
        		pb_gif.setVisibility(View.GONE);
			chatList.setVisibility(View.VISIBLE);
			face_button.setVisibility(View.VISIBLE);
			empty_tv.setVisibility(View.VISIBLE);
			rec_button.setVisibility(View.VISIBLE);
			my_gv.setVisibility(View.GONE);
			gv_cancel_button.setVisibility(View.GONE);*/
			curDisplayState=IdleDisplayState;
			showCurMainScreenByState(curDisplayState);
			faceValue=-1;
                        xun_enter_chatroom_face_windows();
			break;

			default:
			break;
		}
      
            }
        });


        gv_cancel_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                        //Toast.makeText(ChatroomMainNewActivity.this, "face btn down", Toast.LENGTH_SHORT).show();
			closeTip();
			//if(isSW710){
			gv_cancel_button.setBackgroundResource(R.mipmap.cancel_down);
			//}else{
                        //face_button.setBackgroundResource(R.mipmap.face_down);
			//}
                        break;

                    case MotionEvent.ACTION_UP:
			Log.i(LOG_TAG,"func GridView cancel click ");
                        //Toast.makeText(ChatroomMainNewActivity.this, "rec btn up", Toast.LENGTH_SHORT).show();
			//if(isSW710){
			gv_cancel_button.setBackgroundResource(R.mipmap.cancel_up);
			//}else{
                        //face_button.setBackgroundResource(R.mipmap.face_up);
			//}
			//faceValue=-1;
                        //xun_enter_chatroom_face_windows();
			/*rec_gif_view.setVisibility(View.GONE);
        		pb_gif.setVisibility(View.GONE);
			chatList.setVisibility(View.VISIBLE);
			face_button.setVisibility(View.VISIBLE);
			empty_tv.setVisibility(View.VISIBLE);
			rec_button.setVisibility(View.VISIBLE);
			my_gv.setVisibility(View.GONE);
			gv_cancel_button.setVisibility(View.GONE);*/
			curDisplayState=IdleDisplayState;
			showCurMainScreenByState(curDisplayState);
                        break;
                }
                return false;
            }
        });

/////////////////////////////func grid end//////////////////////////////
	if(autoPlay==1){
	ChatAudoPlayManager.initAutoPlay(ListDte,date);
	startFindNext();
	}
	registerBroadcastReciever();
	if(ListDte.size()>0){
	refreshListView(ListDte.size()-1);
	}
	setGestureListener();
	ChatSoundControl.requestAudioPlayerFocus(this);
    }


   private void setGestureListener(){
	
	myView=(RelativeLayout)findViewById(R.id.activity_chatroom_main);
        myView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
		 boolean isHandle=true;
                switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mPosX = event.getX();
                    mPosY = event.getY();
		     Log.i("LOG_TAG","tp down,"+mPosX+","+mPosY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurPosX = event.getX();
                    mCurPosY = event.getY();
		    
                    break;
                case MotionEvent.ACTION_UP:
		   
                    mCurPosX = event.getX();
                    mCurPosY = event.getY();
		     Log.i("LOG_TAG","tp up,"+mCurPosX+","+mCurPosY);
                    if (mCurPosX - mPosX > 80){
			//isHandle=true;
			Log.i("LOG_TAG","exit chatoom main screen");
			finish();
                    }
                    
                    break;
                }
                return isHandle;
            }

        });

chatList.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                boolean isHandle=false;
                switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mPosX = event.getX();
                    mPosY = event.getY();
		     Log.i("LOG_TAG","list tp down,"+mPosX+","+mPosY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurPosX = event.getX();
                    mCurPosY = event.getY();
		    
                    break;
                case MotionEvent.ACTION_UP:
		   
                    mCurPosX = event.getX();
                    mCurPosY = event.getY();
		     Log.i("LOG_TAG","list tp up,"+mCurPosX+","+mCurPosY);
                    if (mCurPosX - mPosX > 80){
			Log.i("LOG_TAG","exit chatoom main screen");
			isHandle=true;
			finish();
                    }
                    
                    break;
                }
                return isHandle;
            }

        });

    }
   private void xun_enter_photo_display_windows(String path){
            Intent my_intent=new Intent();
             my_intent.putExtra(ChatKeyString.KEY_PHOTO_PATH,path);
            my_intent.setClass(this,PhotoDisplayActivity.class);
            startActivity(my_intent);
    }

   private void xun_enter_video_display_windows(String path){
	Log.i(TAG, "xun_enter_video_display_windows,path="+path);
            Intent my_intent=new Intent();
             //my_intent.putExtra(ChatKeyString.KEY_PHOTO_PATH,path);
	    my_intent.putExtra(ChatKeyString.KEY_PHOTO_PATH,path);
            my_intent.setClass(this,VideoDisplayActivity.class);
            startActivity(my_intent);
    }

private void showCurMainScreenByState(int state){
	Log.i(TAG, "showCurMainScreenByState,state="+state);
	if(state==IdleDisplayState){
		rec_gif_view.setVisibility(View.GONE);
        	pb_gif.setVisibility(View.GONE);
		chatList.setVisibility(View.VISIBLE);
		face_button.setVisibility(View.VISIBLE);
		if(ListDte.size()>0){
		empty_tv.setVisibility(View.GONE);
		}else{
		empty_tv.setVisibility(View.VISIBLE);
		}
		rec_button.setVisibility(View.VISIBLE);
		my_gv.setVisibility(View.GONE);
		gv_cancel_button.setVisibility(View.GONE);
		updateChatList();
	}else if(state==GridDisplayState){
		rec_gif_view.setVisibility(View.GONE);
        	pb_gif.setVisibility(View.GONE);
		chatList.setVisibility(View.GONE);
		face_button.setVisibility(View.GONE);
		empty_tv.setVisibility(View.GONE);
		rec_button.setVisibility(View.GONE);
		my_gv.setVisibility(View.VISIBLE);
		gv_cancel_button.setVisibility(View.VISIBLE);
	}
}

    private void startSendDataService(){
	Log.i(TAG, "startSendDataService");
        Intent startIntent= new Intent(this,SendDataService.class);
	bindService(startIntent, conn, Context.BIND_AUTO_CREATE);
	startService(startIntent);
   }

    private void startRecieverDataService(){
	Log.i(TAG, "startRecieverDataService");
        Intent startIntent= new Intent(this,RecieverDataService.class);
	bindService(startIntent, connRec, Context.BIND_AUTO_CREATE);
	startService(startIntent);
   }

private void selectPhoto() {
	Log.i(LOG_TAG, "selectPhoto");
    Intent intent = new Intent();
    ComponentName componentName = new ComponentName("com.xxun.xungallery", "com.xxun.xungallery.MainPhotoActivity");
    intent.putExtra("select_photo", 1);
    intent.setComponent(componentName);
    startActivityForResult(intent, 1);
}

private void takePhoto() {
	Log.i(LOG_TAG, "takePhoto");
    Intent intent = new Intent();
    ComponentName componentName = new ComponentName("com.xxun.xuncamera", "com.xxun.xuncamera.CameraMainActivity");
    intent.putExtra("tag_take_photo", 1);
    intent.setComponent(componentName);
    startActivityForResult(intent, 1);
}

public void checkAllItemPhotoID(){
	Log.i(LOG_TAG, "checkAllItemPhotoID");
	int index=0;
	if(ListDte==null&&ListDte.size()==0){
		Log.i(LOG_TAG, "no need check all");
		return;
	}

	for(index=0;index<ListDte.size();index++){
		String itemGid=ListDte.get(index).getListItemGID();
		String itemEid=ListDte.get(index).getListItemEID();
		int itemPhotoID=ListDte.get(index).getPhotoID();
		int attr=0;
		attr=ChatUtil.checkPhotoID(this,itemGid,itemEid,itemPhotoID);
		if(attr!=itemPhotoID){
			ListDte.get(index).setPhotoID(attr);
			ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(index).getListItemGID(),ListDte.get(index),ListDte.get(index).getmDate());
		}
	}
}
 
 public  void sendStopBgRecordBroacast(){
        Intent it = new Intent("com.xunchatroom.stopBgRecord");
        Log.i(LOG_TAG, "sendStopBgRecordBroacast");
        it.setPackage("com.xxun.watch.xunchatroom");
        sendBroadcast(it);
    }

 public  void sendFinishStoryBroacast(Context context){
        Intent it = new Intent("com.xiaoxun.xxun.story.finish");
        it.setPackage("com.xxun.watch.storydownloadservice");
        Log.i(LOG_TAG, "sendFinishStoryBroacast");
        context.sendBroadcast(it);
    }

    private void handleExitActivity(){
	Log.i("LOG_TAG","handleExitActivity,audioPlayerState="+audioPlayerState);
	if(audioPlayerState==ChatRoomControl.ChatStatePlaying){
		Log.i("LOG_TAG","stop first,clickIndex="+clickIndex);
		 mChatContol.StopPlaying();
		if(clickIndex>=0&&clickIndex<ListDte.size()){
                 ListDte.get(clickIndex).setListItemState(ChatListItemInfo.list_tiem_state_null);
                 ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex),ListDte.get(clickIndex).getmDate());
		}
		audioPlayerState=ChatRoomControl.ChatStateIdle;
                //refreshListView(clickIndex);
		
	}
	if(binder!=null){
	binder.getService().setChatroomWindowStatus(0);
	}
	if(conn!=null){
	unbindService(conn);
	conn=null;
	}
	ChatSoundControl.releaseAudioPlayerFocus();
   }

    void checkAudioState(){
	Log.i("LOG_TAG","checkAudioState,audioPlayerState="+audioPlayerState);

	if(audioPlayerState==ChatRoomControl.ChatStatePlaying){
		Log.i("LOG_TAG","stop first,clickIndex="+clickIndex);
		 mChatContol.StopPlaying();
		if(clickIndex>=0&&clickIndex<ListDte.size()){
                 ListDte.get(clickIndex).setListItemState(ChatListItemInfo.list_tiem_state_null);
                 ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex),ListDte.get(clickIndex).getmDate());
		}
		audioPlayerState=ChatRoomControl.ChatStateIdle;
                refreshListView(clickIndex);
		
	}	
    }

    void startAutoPlay(int index){
	checkAudioState();
	clickIndex=index;
	Log.i("LOG_TAG","startAutoPlay,clickIndex="+clickIndex);
	Log.i("LOG_TAG","startAutoPlay,autoPlay="+autoPlay);
	if(autoPlay!=1){
	Log.i("LOG_TAG","startAutoPlay,autoPlay is 0,return");
	return;
	}	
 		OnCompletionListener complete_listener=new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            Log.i(LOG_TAG,"play complete");
				mChatContol.StopPlaying();
			if(clickIndex>=0&&clickIndex<ListDte.size()){
                            ListDte.get(clickIndex).setListItemState(ChatListItemInfo.list_tiem_state_null);
                            //ChatListDB.getInstance(getApplicationContext()).addChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex));
                            ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex),ListDte.get(clickIndex).getmDate());
			}
			    audioPlayerState=ChatRoomControl.ChatStateIdle;
                            refreshListView(clickIndex);
			    clickIndex=-1;
			    startFindNext();
                        }
                    };

			OnErrorListener error_listener=new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer,int i,int i1) {
                            Log.i(LOG_TAG,"play error");
			     mChatContol.StopPlaying();
			if(clickIndex>=0&&clickIndex<ListDte.size()){
                            ListDte.get(clickIndex).setListItemState(ChatListItemInfo.list_tiem_state_null);
                            //ChatListDB.getInstance(getApplicationContext()).addChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex));
                            ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex),ListDte.get(clickIndex).getmDate());
			}
			    audioPlayerState=ChatRoomControl.ChatStateIdle;
                            refreshListView(clickIndex);
			    clickIndex=-1;
			    startFindNext();
                            return false;	
                        }
                    };
		Log.i("LOG_TAG","play audio,"+ListDte.get(clickIndex).getFilePath());
                    if((audioPlayerState=mChatContol.StartPlaying(ListDte.get(clickIndex).getFilePath(), complete_listener ,error_listener))!=ChatRoomControl.ChatStateError){
                    ListDte.get(clickIndex).setIsPlayed(1);
                    ListDte.get(clickIndex).setListItemState(ChatListItemInfo.list_tiem_state_playing);
                    //ChatListDB.getInstance(getApplicationContext()).addChatMsg(ListDte.get(i).getListItemGID(),ListDte.get(i));
                    ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(clickIndex).getListItemGID(),ListDte.get(clickIndex),ListDte.get(clickIndex).getmDate());
                    refreshListView(clickIndex);
		    ChatContractManager.updateAllMissChatCount(ChatroomMainNewActivity.this);
		}else{
				Log.i("LOG_TAG","play audio error");
				audioPlayerState=ChatRoomControl.ChatStateIdle;
				clickIndex=-1;
			}
    }

private static class UIHandler extends Handler {
        private final WeakReference<ChatroomMainNewActivity> mActivity;

        public UIHandler(ChatroomMainNewActivity activity) {
            mActivity = new WeakReference<ChatroomMainNewActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            //System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().todo(msg);
        }
    }

    private UIHandler uiHandler = new UIHandler(this);

     public void todo(Message msg){
       Log.i("LOG_TAG","todo,what="+msg.what);
            switch (msg.what){
                case 1:
		int nowIndex=ChatAudoPlayManager.getNextAutoPlayItem();
		Log.i("LOG_TAG","handleMessage,nowIndex="+nowIndex);
		if(nowIndex!=-1){
			startAutoPlay(nowIndex);
		}
		    break;

		case 2:
 			//if(isSW710){
			rec_button.setBackgroundResource(R.drawable.rec_btn_up);
			//}else{
                        //rec_button.setBackgroundResource(R.mipmap.rec_btn_up);
			//}
                        mChatContol.StopRecord();
                        //mChatContol.StartPlaying(path);
                       // String FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                        //FileName += "/goldfallen.mp3";
                       // mChatContol.StartPlaying(FileName);
                        handleRecorAction(xun_chat_record_status_record_full);
			is_down=false;
                    break;
	
		case 3:
			Log.i(LOG_TAG,"sendImageCallback5,list count="+ListDte.size());
			ListDte.clear();
           		ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
			refreshListView(-1);
			break;

		case 4:
			Log.i(LOG_TAG,"sendImageCallback7,msg.arg1="+msg.arg1);
				forbidUpdate=true;
				int index=msg.arg1;
				ListDte.get(index).setSendState(0);
				String gid=ListDte.get(index).getListItemGID();
				Log.i(LOG_TAG,"find error item,gid="+gid);
				ChatListDB.getInstance(getApplicationContext()).updateChatMsg(gid,ListDte.get(index),ListDte.get(index).getmDate());
				if(ChatUtil.isForeground(getApplicationContext(),"com.xxun.watch.xunchatroom.activity.ChatroomMainNewActivity")&&gid.equals(cur_gid)){
	    				//ListDte.clear();
           				//ChatListDB.getInstance(getApplicationContext()).=;readAllChatFromFamily(cur_gid, ListDte, ListDte);
					Log.i(LOG_TAG,"sendImageCallback2,list count="+ListDte.size());
					updateChatList();
				}
				forbidUpdate=false;
			break;

				case 5:
					Log.i(LOG_TAG,"sendImageCallback7,msg.arg1="+msg.arg1);
					int mindex=msg.arg1;
					ListDte.clear();
					ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
					refreshListView(mindex);
					break;

            }
        
    }

    void updateChatList(){
	Log.i("LOG_TAG","updateChatList");
	Log.i(LOG_TAG,"sendImageCallback4,list count="+ListDte.size());
        Message msg = new Message();
        msg.what = 3;
        uiHandler.sendMessage(msg);
    }

    void startFindNext(){
	Log.i("LOG_TAG","startFindNext");
        Message msg = new Message();
        msg.what = 1;
        uiHandler.sendMessage(msg);
    }

   void updateImageResault(int index){
	Log.i("LOG_TAG","updateImageResault,index="+index);
        Message msg = new Message();
        msg.what = 4;
	msg.arg1=index;
        uiHandler.sendMessage(msg);
   }


	void updateChatListByIndex(int index){
		Log.i("LOG_TAG","updateImageResault,index="+index);
		Message msg = new Message();
		msg.what = 5;
		msg.arg1=index;
		uiHandler.sendMessage(msg);
	}

	protected void onDestroy(){
        super.onDestroy();
        Log.i("LOG_TAG","ChatroomMainNewActivity,onDestroy");
	unregisterBroadcastReciever();
	sendSearchChatMsgBroacast();
	handleExitActivity();
	}

    protected void onResume(){
        super.onResume();
        Log.i("LOG_TAG","ChatroomMainNewActivity,onResume");
        isIncomingCall=false;
        if(adapter!=null) {
            Log.i("LOG_TAG","Cadapter is ok");
            if(ListDte!=null) {
                Log.i("LOG_TAG","onResume,count="+ListDte.size());
                ListDte.clear();
                Log.i("LOG_TAG","onResume,count="+ListDte.size());
                //ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(WatchSystemInfo.getWatchGID(), ListDte, ListDte);
                ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
                Log.i("LOG_TAG","onResume,count="+ListDte.size());
            }
	    checkNickName();
			if(curDisplayState==IdleDisplayState) {
				adapter.notifyDataSetChanged();
			}
        }
		//showCurMainScreenByState(curDisplayState);
	Log.i("LOG_TAG","onResume,faceValue="+faceValue);
       if(faceValue>=0){
	sendChatFaceImage(faceValue);
         faceValue=-1;
	}else if(imagePath!=null){
		   //startCopyDelay();
		   //imagePath=changePath;
		   if(selectMode==1) {
			   sendChatPhotoImage(imagePath,true);
			   imagePath = null;
		   }else{
			   startCopyDelay();
		   }
	}
	ChatSoundControl.requestAudioPlayerFocus(this);
    }

	private void startCopyDelay(){
		mThread=new ChatThreadTimer(100, new ChatThreadTimer.TimerInterface() {
			@Override
			public void doTimerOut() {
				String changePath=changeImagePath(imagePath);
				ChatUtil.copyFile(imagePath,changePath);
				sendChatPhotoImage(changePath,false);
				imagePath=null;
				mThread.stopThreadTimer();
				mThread=null;
			}
		});
		mThread.start();
	}

	private String changeImagePath(String oldPath){
		String newPath=null;
		Log.i("LOG_TAG","changeImagePath,oldPath="+oldPath);
		String fileName=oldPath.substring(oldPath.lastIndexOf("Camera")+7,oldPath.length());
		Log.i("LOG_TAG","changeImagePath,fileName="+fileName);
		newPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+fileName;
		Log.i("LOG_TAG","changeImagePath,newPath="+newPath);
		return newPath;
	}

    public  void onWindowFocusChanged(boolean hasFocus ){
        super.onWindowFocusChanged(hasFocus);
        Log.i("LOG_TAG","ChatroomMainNewActivity,onWindowFocusChanged,hasFocus="+hasFocus);
/*
        if(hasFocus) {
            if (adapter != null) {
                Log.i("LOG_TAG", "Cadapter is ok");
                if (ListDte != null) {
                    Log.i("LOG_TAG", "onWindowFocusChanged,count=" + ListDte.size());
                    ListDte.clear();
                    Log.i("LOG_TAG", "onWindowFocusChanged,count=" + ListDte.size());
                    //ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(WatchSystemInfo.getWatchGID(), ListDte, ListDte);
                    ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
                    Log.i("LOG_TAG", "onWindowFocusChanged,count=" + ListDte.size());
                }
                adapter.notifyDataSetChanged();
            }
        }
*/
    }

    void refreshChatScreen(){
	Log.i("LOG_TAG","refreshChatScreen");
        Message msg = new Message();
        msg.what = 2;
        uiHandler.sendMessage(msg);
    }

    public void handleRecorAction(int record_status)
    {
	Log.i("LOG_TAG","handleRecorAction,record_status="+record_status);
        if(record_status==xun_chat_record_status_recording){
            chatList.setVisibility(View.GONE);
	    empty_tv.setVisibility(View.GONE);
            pb_gif.setVisibility(View.VISIBLE);
           // pb_gif.setMax(15);
            count=0;
            pb_gif.setProgress(0);
	    checkAudioState();
            /*
            rec_gif_view.getBackground().setAlpha(122);
            rec_gif_view.setVisibility(View.VISIBLE);
            rec_gif_view.setMovieResource(R.mipmap.recording);
            rec_gif_view.setMovieTime(15000);
*/
            my_thread=new ChatThreadTimer(100, new ChatThreadTimer.TimerInterface() {
                @Override
                public void doTimerOut() {
                    Log.i(LOG_TAG, "doTimerOut,count="+count);
                    count++;

                    
                    if((count/10)>15){
                        my_thread.stopThreadTimer();
                        pb_gif.clearProgress();
			my_thread=null;
			refreshChatScreen();
                    }
		    else{
			pb_gif.setProgress(count);
			}
                }
            });

            my_thread.start();
            ChatRecieveMsgManager.checkRecieveMsgGroup(getApplicationContext(),cur_gid);
	    ListDte.clear();
           ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
			ChatContractManager.updateAllMissChatCount(ChatroomMainNewActivity.this);
        }
        else  if(record_status==xun_chat_record_status_record_finish){
            //String date=ChatUtil.ChatUtilGetDate();
		Log.i("LOG_TAG","handle record finish");
            int duration= ChatUtil.getAudioDurationByPath(path);
            count=0;
            pb_gif.setProgress(0);
	   if(my_thread!=null){
            my_thread.stopThreadTimer();
	    my_thread=null;
	    }
            if(duration<1000){
                rec_gif_view.setVisibility(View.GONE);
		pb_gif.setVisibility(View.GONE);
                refreshListView(ListDte.size()-1);
		
                //Toast.makeText(ChatroomMainNewActivity.this, getString(R.string.record_time_short), Toast.LENGTH_SHORT).show();
		showTip(getString(R.string.record_time_short));
                return;
            }
            int sn=ChatUtil.ChatUtilGetServiceSN(this);
            String date=Integer.toString(sn);
            Log.i("LOG_TAG","sn="+sn);
            Log.i("LOG_TAG","date="+date);
            //ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(),WatchSystemInfo.getWatchGID(),8,path,1,ChatListItemInfo.list_tiem_state_null,date);
            ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(this),cur_gid,duration/1000,path,1,ChatListItemInfo.list_tiem_state_null,date);
            mItem1.setSN(sn);
	    mItem1.setListItemStartMills(ChatUtil.getCurTimeMills());
           // ChatListItemInfo new_item=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",1,ChatListItemInfo.list_tiem_state_null,"2342344");
           // ChatListDB.getInstance(getApplicationContext()).addChatMsg(WatchSystemInfo.getWatchGID(),mItem1);
            ChatListDB.getInstance(getApplicationContext()).addChatMsg(cur_gid,mItem1);
	    ListDte.clear();
            ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
            //ListDte.add(mItem1);
            rec_gif_view.setVisibility(View.GONE);
	    pb_gif.setVisibility(View.GONE);
            refreshListView(ListDte.size()-1);
            ChatUtil.PrintLog("date is"+ChatUtil.ChatUtilReadEndkey(getApplicationContext()));
            ConstrctChatSendBuff(mItem1);
        }else if(record_status==xun_chat_record_status_record_cancel) {
            count=0;
            pb_gif.setProgress(0);
	   if(my_thread!=null){
            my_thread.stopThreadTimer();
	    my_thread=null;
	    }
            rec_gif_view.setVisibility(View.GONE);
            pb_gif.setVisibility(View.GONE);
	    ListDte.clear();
            ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
            refreshListView(ListDte.size()-1);
            //Toast.makeText(ChatroomMainNewActivity.this, getString(R.string.record_cancel), Toast.LENGTH_SHORT).show();
	    showTip(getString(R.string.record_cancel));
        }
	else if(record_status==xun_chat_record_status_record_full)
	{
            int duration= ChatUtil.getAudioDurationByPath(path);
            count=0;
	   if(my_thread!=null){
            my_thread.stopThreadTimer();
	    my_thread=null;
	    }
            int sn=ChatUtil.ChatUtilGetServiceSN(this);
            String date=Integer.toString(sn);
            Log.i("LOG_TAG","sn="+sn);
            Log.i("LOG_TAG","date="+date);
            //ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(),WatchSystemInfo.getWatchGID(),8,path,1,ChatListItemInfo.list_tiem_state_null,date);
            ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(this),cur_gid,duration/1000,path,1,ChatListItemInfo.list_tiem_state_null,date);
            mItem1.setSN(sn);
	    mItem1.setmDate(date);
	    mItem1.setListItemStartMills(ChatUtil.getCurTimeMills());
           // ChatListItemInfo new_item=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",1,ChatListItemInfo.list_tiem_state_null,"2342344");
           // ChatListDB.getInstance(getApplicationContext()).addChatMsg(WatchSystemInfo.getWatchGID(),mItem1);
            ChatListDB.getInstance(getApplicationContext()).addChatMsg(cur_gid,mItem1);
	    ListDte.clear();
            ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
            //ListDte.add(mItem1);
            rec_gif_view.setVisibility(View.GONE);
	    pb_gif.setVisibility(View.GONE);
            refreshListView(ListDte.size()-1);
            ChatUtil.PrintLog("date is"+ChatUtil.ChatUtilReadEndkey(getApplicationContext()));
            ConstrctChatSendBuff(mItem1);
	}
    }

    public void refreshListView(int position){
	Log.i(LOG_TAG,"refreshListView,position="+position);
	Log.i(LOG_TAG,"refreshListView,isIncomingCall="+isIncomingCall);
        if(isIncomingCall){
	//isIncomingCall=false;
	Log.i(LOG_TAG,"refreshListView,in incoming call,return");
	return;
	}

	if(curDisplayState!=IdleDisplayState){
	Log.i(LOG_TAG,"refreshListView,not in idle state,return");
	return;
	}

	checkNickName();
        chatList.setVisibility(View.VISIBLE);
	Log.i(LOG_TAG,"refreshListView,list count="+ListDte.size());
        adapter.notifyDataSetChanged();
        if(position>=0&&position<ListDte.size()){
            chatList.setSelection(position);
        }
    }


    String findNickNameByGID(String findGid,String findEid){
        String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
        String name=null;
        String number;
        Uri uri = Uri.parse ("content://com.android.contacts/contacts");
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        Log.i(LOG_TAG,"findNickNameByGID");
        boolean is_find=false;
        String nick_name=null;
        String gid=null;
	String eid=null;
        int img=R.mipmap.photo_test;
	int attri =0;
	String avatar=null;
        while(cursor.moveToNext()&&!is_find){

            int contactsId = cursor.getInt(0);
            Log.i(LOG_TAG,"contactsId = " +contactsId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
            Cursor dataCursor = resolver.query(uri, project, null, null, null);
            //SyncArrayBean  arrayBean = new SyncArrayBean();
            while(dataCursor.moveToNext()&&!is_find) {
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(type)) {
                    name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
		    avatar = dataCursor.getString(dataCursor.getColumnIndex(project[3]));
                    Log.i(LOG_TAG,"name = " +name);
                    nick_name=name;
		    //chat_info.setContractItemAvatar(avatar);
                } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
                } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                    number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                    Log.i(LOG_TAG,"number = " +number);
                }else if("vnd.android.cursor.item/nickname".equals(type)){

                    attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));
                    gid=dataCursor.getString(dataCursor.getColumnIndex(project[5]));
		    eid=dataCursor.getString(dataCursor.getColumnIndex(project[4]));
                    Log.i(LOG_TAG, "gid = " + gid);
		    Log.i(LOG_TAG, "eid = " + eid);
                    if(gid!=null&&findGid!=null) {
                        if (findGid.equals(WatchSystemInfo.getWatchGID())) {
			//
				if(findEid!=null) {
					if (findEid.equals(eid)) {
						//img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
						//Log.i("chat","set photoID img="+img);
						//chat_info.setPhotoID(attri);
						is_find = true;
					}
				}
                        } else if (findGid.equals(gid)) {
                            //img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
			    //chat_info.setPhotoID(attri);
			    Log.i("chat","set photoID img="+img);
                            Log.i(LOG_TAG, "attri = " + attri);
                            is_find = true;
                        }
                    }
                    //arrayBean.contactWeight =dataCursor.getInt(dataCursor.getColumnIndex(project[3]));
                    //arrayBean.optype = dataCursor.getInt(dataCursor.getColumnIndex(project[1]));
                }
            }



            //if(arrayBean != null) mlist_arraybean.add(arrayBean);
        }
        cursor.close();
        Log.i(LOG_TAG, "is_find="+is_find);

        if(is_find){
		return name;
        }
	return null;

    }

    public void checkNickName(){
	int index=0;
	Log.i("LOG_TAG","checkNickName");
		if(ListDte.size()<=0){
			return;
		}
	for(index=0;index<ListDte.size();index++){
		String nickName=findNickNameByGID(ListDte.get(index).getListItemGID(),ListDte.get(index).getListItemEID());
		ListDte.get(index).setNickName(nickName);   
	}
   }

    public void initViews(){
        Log.i("LOG_TAG","ChatroomMainNewActivity,initViews");
        if(WatchSystemInfo.getWatchDebugFlag()==1)
        {
            ListDte=new ArrayList<ChatListItemInfo>();
            //ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(WatchSystemInfo.getWatchGID(),ListDte,ListDte);
            ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid,ListDte,ListDte);
	    int mIndex=0;
	    for(mIndex=0;mIndex<ListDte.size();mIndex++){
		if(ListDte.get(mIndex).getContentType()==ChatListItemInfo.content_type_image||ListDte.get(mIndex).getContentType()==4||ListDte.get(mIndex).getContentType()==5){
			ListDte.get(mIndex).setIsPlayed(1);
	    		ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(mIndex).getListItemGID(),ListDte.get(mIndex),ListDte.get(mIndex).getmDate());
		}	
	    }
	   checkNickName();
	   ChatContractManager.updateAllMissChatCount(ChatroomMainNewActivity.this);
            //ChatListItemInfo mItem1=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",1,ChatListItemInfo.list_tiem_state_null,"2342344");
            //ListDte.add(mItem1);

            //ListDte=new ArrayList<ChatListItemInfo>();
            //ChatListItemInfo mItem2=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",0,ChatListItemInfo.list_tiem_state_null,"2342344");
           // ListDte.add(mItem2);
        }
        else {
        //add item here
        }
    }

class sendImageCallback extends IResponseDataCallBack.Stub{
	public void onSuccess(ResponseData responseData){
                Log.i(LOG_TAG,"chat image send succ");
                Log.i(LOG_TAG,responseData.getResponseCode() + " " + responseData.getResponseData());
		JSONObject jo = (JSONObject)JSONValue.parse(responseData.getResponseData());
		int rc=(int) jo.get(ChatKeyString.KEY_RC);
		Log.i(LOG_TAG,"rc="+rc);
                if(rc<0){
		int sn = (int) jo.get(ChatKeyString.KEY_SN);
		Log.i(LOG_TAG,"sn="+sn);
		Log.i(LOG_TAG,"sendImageCallback1,list count="+ListDte.size());
		for(int i=0;i<ListDte.size();i++){
			
			if(ListDte.get(i).getmDate().equals(Integer.toString(sn)))
			{		
				/*ListDte.get(i).setSendState(0);
				String gid=ListDte.get(i).getListItemGID();
				Log.i(LOG_TAG,"find error item,gid="+gid);
				ChatListDB.getInstance(getApplicationContext()).updateChatMsg(gid,ListDte.get(i),ListDte.get(i).getmDate());
				if(ChatUtil.isForeground(getApplicationContext(),"com.xxun.watch.xunchatroom.activity.ChatroomMainNewActivity")&&gid.equals(cur_gid)){
	    				//ListDte.clear();
           				//ChatListDB.getInstance(getApplicationContext()).=;readAllChatFromFamily(cur_gid, ListDte, ListDte);
					Log.i(LOG_TAG,"sendImageCallback2,list count="+ListDte.size());
					updateChatList();
				}*/
				Log.i(LOG_TAG,"sendImageCallback3,list count="+ListDte.size());
				ChatUtil.pushStatsOnly(myContext,ChatUtil.STATS_VOICE_SEND_TIMEOUT);
				updateImageResault(i);
				break;
			}
		}
		}else{
                int sn = (int) jo.get(ChatKeyString.KEY_SN);
		int i=0;
                JSONObject pl = (JSONObject) jo.get(ChatKeyString.KEY_PL);
                Log.i(LOG_TAG,"sn_str="+sn);
		boolean isExsit=false;
		for(i=0;i<ListDte.size();i++){
			
			if(ListDte.get(i).getmDate().equals(Integer.toString(sn)))
			{	
				Log.i(LOG_TAG,"find in listdate,i="+i);
				isExsit=true;
				break;
			}
		}
		Log.i(LOG_TAG,"isExsit="+isExsit);
		if(!isExsit){
			Log.i(LOG_TAG,"it is not in list,return");
			return;
		}
                String key_str=(String)pl.get(ChatKeyString.KEY_NAME);
                Log.i(LOG_TAG,"key_str="+key_str);
                String date_str=key_str.substring(40);
                Log.i(LOG_TAG,"date_str="+date_str);
                int index=0;
                for(ChatListItemInfo info:ListDte) {
                    if(sn==info.getSN()){
                        break;
                    }
                    index++;
                }
                // ChatListItemInfo info=ListDte.get(index);
                String gid=(String)pl.get(ChatKeyString.KEY_TGID);
                String date=null;
                ChatListItemInfo update_info=ChatListDB.getInstance(getApplicationContext()).readOneChatFromFamily(gid,Integer.toString(sn));
                //date=ListDte.get(index).getmDate();
                update_info.setmDate(date_str);
		update_info.setSendState(1);
               // String oldname=update_info.getFilePath();
               // Log.i(LOG_TAG,"oldname="+oldname);
                //Log.i(LOG_TAG,"path_index="+path_index);
               // Log.i(LOG_TAG,"date="+date);
                //String newname=oldname.substring(0,path_index)+"chat_audio/"+date_str+".amr";
               // Log.i(LOG_TAG,"newname="+newname);
               // ChatUtil.ChatUtilFileRename(oldname,newname);
                //update_info.setFilePath(newname);
                //ChatListDB.getInstance(getApplicationContext()).updateChatMsg(WatchSystemInfo.getWatchGID(),ListDte.get(index),date);
                ChatListDB.getInstance(getApplicationContext()).updateChatMsg(cur_gid,update_info,date);
		ListDte.set(i,update_info);
		long endMills=ChatUtil.getCurTimeMills();
		int distanceTime=ChatUtil.getTimeDistance(update_info.getListItemStartMills(),endMills);
		ChatUtil.pushSendStatusByTime(myContext,distanceTime);
                //chatList.setVisibility(View.VISIBLE);
                //adapter.notifyDataSetChanged();
	}
	       
       }
       
       public void onError(int i, String s){
              Log.i(LOG_TAG,"chat image send fail");
	      Log.i(LOG_TAG,"error i="+i);
	      Log.i(LOG_TAG,"error string="+s);
       }
}

    String ConstrctChatImageSendBuff(ChatListItemInfo chat_info){
        String sendBuff=null;
        String msg_key=null;
        JSONObject msg = new JSONObject();
        String audio_str=null;
	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)getSystemService("xun.network.Service");
        msg.put(ChatKeyString.KEY_CID, CloudBridgeUtil.CID_SEND_BYTE_MESSAGE);
        msg.put(ChatKeyString.KEY_SID, networkService.getSID());

        msg.put(ChatKeyString.KEY_SN, chat_info.getSN());
        msg.put(ChatKeyString.KEY_VERSION,CloudBridgeUtil.PROTOCOL_VERSION);
        JSONObject pl = new JSONObject();
        JSONObject value = new JSONObject();
        value.put(ChatKeyString.KEY_EID, WatchSystemInfo.getWatchEID(this));
        value.put(ChatKeyString.KEY_TYPE, "emoji");
        value.put(ChatKeyString.KEY_DURATION, chat_info.getDuration());
        //byte[] encodedData=ChatUtil.ChatUtilReadDataFromFile(chat_info.getFilePath());
        //byte[] encodedData = audio_str.getBytes();
        //String audio_send = Base64.encodeToString(encodedData, Base64.NO_WRAP);
        value.put(ChatKeyString.KEY_CONTENT, getString(R.string.face_key_1+Integer.parseInt(chat_info.getFilePath())));
        pl.put(ChatKeyString.KEY_VALUE, value);
        pl.put(ChatKeyString.KEY_TGID,  chat_info.getListItemGID());
        msg_key="GP/"+chat_info.getListItemGID()+"/MSG/#TIME#";
        pl.put(ChatKeyString.KEY_NAME,  msg_key);
        msg.put(ChatKeyString.KEY_PL, pl);
        sendBuff= msg.toJSONString();
	
        Log.i("LOG_TAG","sendBuff="+sendBuff);
	ChatUtil.pushStatsOnly(myContext,ChatUtil.STATS_VOICE_SEND);
	networkService.sendJsonMessage(sendBuff,new sendImageCallback());
/*
        ChatNetService.getmChatNetService().sendJsonMessage(sendBuff, new IResponseDataCallBack<ResponseData>() {
            @Override
            public void onSuccess(ResponseData responseData) {
                Log.i(LOG_TAG,"image msg send succ");
                Log.i(LOG_TAG,responseData.getResponseCode() + " " + responseData.getResponseData().toJSONString());
                int sn = (int) responseData.getResponseData().get(ChatKeyString.KEY_SN);
                JSONObject pl = (JSONObject) responseData.getResponseData().get(ChatKeyString.KEY_PL);
                Log.i(LOG_TAG,"sn_str="+sn);
                String key_str=(String)pl.get(ChatKeyString.KEY_NAME);
                Log.i(LOG_TAG,"key_str="+key_str);
                String date_str=key_str.substring(40);
                Log.i(LOG_TAG,"date_str="+date_str);
                int index=0;
                for(ChatListItemInfo info:ListDte) {
                    if(sn==info.getSN()){
                        break;
                    }
                    index++;
                }
                // ChatListItemInfo info=ListDte.get(index);
                String gid=(String)pl.get(ChatKeyString.KEY_TGID);
                String date=null;
                ChatListItemInfo update_info=ChatListDB.getInstance(getApplicationContext()).readOneChatFromFamily(gid,Integer.toString(sn));
                //date=ListDte.get(index).getmDate();
                update_info.setmDate(date_str);
               // String oldname=update_info.getFilePath();
               // Log.i(LOG_TAG,"oldname="+oldname);
                //Log.i(LOG_TAG,"path_index="+path_index);
               // Log.i(LOG_TAG,"date="+date);
                //String newname=oldname.substring(0,path_index)+"chat_audio/"+date_str+".amr";
               // Log.i(LOG_TAG,"newname="+newname);
               // ChatUtil.ChatUtilFileRename(oldname,newname);
                //update_info.setFilePath(newname);
                //ChatListDB.getInstance(getApplicationContext()).updateChatMsg(WatchSystemInfo.getWatchGID(),ListDte.get(index),date);
                ChatListDB.getInstance(getApplicationContext()).updateChatMsg(cur_gid,update_info,date);
                //chatList.setVisibility(View.VISIBLE);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
*/
        return sendBuff;
    }
    
   void sendChatFaceImage(int index){
	   Log.i("LOG_TAG", "sendChatFaceImage,index="+index);
           ChatRecieveMsgManager.checkRecieveMsgGroup(getApplicationContext(),cur_gid);
	    ListDte.clear();
           ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
            int duration=1000;//ChatUtil.getAudioDurationByPath(path);

            int sn=ChatUtil.ChatUtilGetServiceSN(getApplicationContext());
            String date=Integer.toString(sn);
            Log.i("LOG_TAG","sn="+sn);
            Log.i("LOG_TAG","date="+date);

            //ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(),WatchSystemInfo.getWatchGID(),8,path,1,ChatListItemInfo.list_tiem_state_null,date);
            ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(this),cur_gid,duration/1000, Integer.toString(index),1,ChatListItemInfo.list_tiem_state_null,date);
            mItem1.setSN(sn);
	    mItem1.setmDate(date);
            mItem1.setContentType(ChatListItemInfo.content_type_image);
	    mItem1.setListItemStartMills(ChatUtil.getCurTimeMills());
            // ChatListItemInfo new_item=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",1,ChatListItemInfo.list_tiem_state_null,"2342344");
            // ChatListDB.getInstance(getApplicationContext()).addChatMsg(WatchSystemInfo.getWatchGID(),mItem1);
            ChatListDB.getInstance(getApplicationContext()).addChatMsg(cur_gid,mItem1);
            ListDte.add(mItem1);
            //rec_gif_view.setVisibility(View.GONE);
            refreshListView(ListDte.size()-1);
            //ChatUtil.PrintLog("date is"+ChatUtil.ChatUtilReadEndkey(getApplicationContext()));
            ConstrctChatImageSendBuff(mItem1);
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

   void sendChatPhotoImage(String imagePath,boolean isRefresh){
	   Log.i("LOG_TAG", "sendChatPhotoImage,imagePath="+imagePath);
           ChatRecieveMsgManager.checkRecieveMsgGroup(getApplicationContext(),cur_gid);
	    ListDte.clear();
           ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
            int duration=1000;//ChatUtil.getAudioDurationByPath(path);

            int sn=ChatUtil.ChatUtilGetServiceSN(getApplicationContext());
            String date=Integer.toString(sn);
            Log.i("LOG_TAG","sn="+sn);
            Log.i("LOG_TAG","date="+date);

            //ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(),WatchSystemInfo.getWatchGID(),8,path,1,ChatListItemInfo.list_tiem_state_null,date);
            ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(this),cur_gid,duration/1000, imagePath,1,ChatListItemInfo.list_tiem_state_null,date);
            mItem1.setSN(sn);
	    mItem1.setmDate(date);
	    if(isVideoFile(imagePath)){
            mItem1.setContentType(ChatListItemInfo.content_type_video);
	    }else{
            mItem1.setContentType(ChatListItemInfo.content_type_photo);
	    }
	    mItem1.setListItemStartMills(ChatUtil.getCurTimeMills());
            // ChatListItemInfo new_item=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",1,ChatListItemInfo.list_tiem_state_null,"2342344");
            // ChatListDB.getInstance(getApplicationContext()).addChatMsg(WatchSystemInfo.getWatchGID(),mItem1);
            ChatListDB.getInstance(getApplicationContext()).addChatMsg(cur_gid,mItem1);
            ListDte.add(mItem1);
            //rec_gif_view.setVisibility(View.GONE);
	   	if(isRefresh) {
			refreshListView(ListDte.size() - 1);
		}else{
			updateChatListByIndex(ListDte.size() - 1);
		}
		ChatSendData sendData=new ChatSendData(sn,imagePath,1,cur_gid);
		if(isVideoFile(imagePath)){
		sendData.setSendDataType(2);
		//ChatUtil.getVideoThumbnail(sn,imagePath);
		}
		binder.getService().addSendData(sendData);
            //ChatUtil.PrintLog("date is"+ChatUtil.ChatUtilReadEndkey(getApplicationContext()));
            //ConstrctChatImageSendBuff(mItem1);
}

int selectMode=0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("LOG_TAG", "ChatroomMainNewActivity,onActivityResult");
        if(data==null){
		Log.i("LOG_TAG", "data is null,return");
		return;
	}

    switch (requestCode) {
        case 1:
		imagePath=null;
		imagePath=data.getStringExtra("select_photo");
			selectMode=0;
		if(imagePath==null){
		imagePath=data.getStringExtra("result_take_photo");
			selectMode=1;
		//imagePath=imagePath.substring(2,imagePath.length());
		}
	     Log.i(LOG_TAG, "old image path=" + imagePath);
            String head=imagePath.substring(0,8);
	     Log.i(LOG_TAG, "head=" + head);
	    if(head.equals("file:///")){
		imagePath=imagePath.substring(7,imagePath.length());
	    }
            Log.i(LOG_TAG, "new image path=" + imagePath);
	
	break;

	case 1000:
	//case 1001:
        if(requestCode == 1000 && resultCode == 1001)
        {
            int face_value = data.getIntExtra("face_choice",0);
            //editText3.setText(result_value);
            Log.i("LOG_TAG", "face_value="+face_value);
	    faceValue=face_value;
            //ChatRecieveMsgManager.checkRecieveMsgGroup(getApplicationContext(),cur_gid);
        /*   ChatRecieveMsgManager.checkRecieveMsgGroup(getApplicationContext(),cur_gid);
	    ListDte.clear();
           ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
            int duration=1000;//ChatUtil.getAudioDurationByPath(path);

            int sn=ChatUtil.ChatUtilGetServiceSN(getApplicationContext());
            String date=Integer.toString(sn);
            Log.i("LOG_TAG","sn="+sn);
            Log.i("LOG_TAG","date="+date);

            //ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(),WatchSystemInfo.getWatchGID(),8,path,1,ChatListItemInfo.list_tiem_state_null,date);
            ChatListItemInfo mItem1=new ChatListItemInfo(WatchSystemInfo.getWatchEID(),cur_gid,duration/1000, Integer.toString(face_value),1,ChatListItemInfo.list_tiem_state_null,date);
            mItem1.setSN(sn);
	    mItem1.setmDate(date);
            mItem1.setContentType(ChatListItemInfo.content_type_image);
            // ChatListItemInfo new_item=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",1,ChatListItemInfo.list_tiem_state_null,"2342344");
            // ChatListDB.getInstance(getApplicationContext()).addChatMsg(WatchSystemInfo.getWatchGID(),mItem1);
            ChatListDB.getInstance(getApplicationContext()).addChatMsg(cur_gid,mItem1);
            ListDte.add(mItem1);
            //rec_gif_view.setVisibility(View.GONE);
            updateChatList();
            //ChatUtil.PrintLog("date is"+ChatUtil.ChatUtilReadEndkey(getApplicationContext()));
            ConstrctChatImageSendBuff(mItem1);*/
        }
	break;
    }

    }

    void xun_enter_chatroom_face_windows(){
        Intent my_intent=new Intent();
        my_intent.setClass(this,ChatFaceActivity.class);
        startActivityForResult(my_intent,1000);
    }

	void xun_enter_video_display(){
		Log.i(LOG_TAG,"xun_enter_video_display");
				Intent mIntent=new Intent();
				mIntent.setClass(ChatroomMainNewActivity.this,VideoDisplayActivity.class);
				startActivity(mIntent);
	}

    void xun_enter_chatroom_text_content_windows(String path){
        Intent my_intent=new Intent();
        my_intent.putExtra(ChatKeyString.KEY_CONTENT,path);
        my_intent.setClass(this,ChatTextContentActivity.class);
        startActivity(my_intent);
    }

    void xun_enter_chatroom_notification_windows(ChatNoticeInfo nitice_info){
        /*
        Intent my_intent=new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("notice", nitice_info);
        my_intent.setClass(this,ChatNotificationActivity.class);
        my_intent.putExtras(bundle);
        startActivity(my_intent);*/
/*
         final ChatNoticeDialog notic_dialog=new ChatNoticeDialog(this,getApplicationContext(),nitice_info);

        notic_dialog.setTouchlistener(new ChatNoticeDialog.TouchListenerInterface() {
            @Override
            public void doConfirm() {
                Log.i("LOG_TAG","doConfirm");
                notic_dialog.dismiss();
            }

            @Override
            public void doCancel() {
                Log.i("LOG_TAG","doCancel");
                notic_dialog.dismiss();
                ChatNoticeInfo next_one=ChatNotificationManager.getNextNotifInfo(getApplicationContext());
                if(next_one!=null){
                    xun_enter_chatroom_notification_windows(next_one);
                }
            }

        });

        notic_dialog.setClicklistener(new ChatNoticeDialog.ClickListenerInterface() {
            @Override
            public void doPlay() {
                Log.i("LOG_TAG","doPlay,cur_gid="+cur_gid);
                if(adapter!=null) {
                    if(ListDte!=null) {
                        Log.i("LOG_TAG","onResume,count="+ListDte.size());
                        ListDte.clear();
                        Log.i("LOG_TAG","onResume,count="+ListDte.size());
                        //ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(WatchSystemInfo.getWatchGID(), ListDte, ListDte);
                        ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
                        Log.i("LOG_TAG","onResume,count="+ListDte.size());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        notic_dialog.create();
        notic_dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//
        notic_dialog.show();
        */
    }

class sendChatCallback extends IResponseDataCallBack.Stub{
	public void onSuccess(ResponseData responseData){
                Log.i(LOG_TAG,"chat msg send succ");
                Log.i(LOG_TAG,responseData.getResponseCode() + " " + responseData.getResponseData());
		JSONObject jo = (JSONObject)JSONValue.parse(responseData.getResponseData());
		int rc=(int) jo.get(ChatKeyString.KEY_RC);
		Log.i(LOG_TAG,"rc="+rc);
                if(rc<0){
		int sn = (int) jo.get(ChatKeyString.KEY_SN);
		Log.i(LOG_TAG,"sn="+sn);
		for(int i=0;i<ListDte.size();i++){
			
			if(ListDte.get(i).getmDate().equals(Integer.toString(sn)))
			{	
				/*ListDte.get(i).setSendState(0);
				String gid=ListDte.get(i).getListItemGID();
				Log.i(LOG_TAG,"find error item,gid="+gid);
				ChatListDB.getInstance(getApplicationContext()).updateChatMsg(gid,ListDte.get(i),ListDte.get(i).getmDate());
				if(ChatUtil.isForeground(getApplicationContext(),"com.xxun.watch.xunchatroom.activity.ChatroomMainNewActivity")&&gid.equals(cur_gid)){
				        //ListDte.clear();
          				//ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
					updateChatList();
				}*/
				ChatUtil.pushStatsOnly(myContext,ChatUtil.STATS_VOICE_SEND_TIMEOUT);
				updateImageResault(i);
				break;
			}
		}
		}
		else{
                int sn = (int) jo.get(ChatKeyString.KEY_SN);
		int i=0;
                JSONObject pl = (JSONObject) jo.get(ChatKeyString.KEY_PL);
                Log.i(LOG_TAG,"sn_str="+sn);
		boolean isExsit=false;
		for(i=0;i<ListDte.size();i++){
			
			if(ListDte.get(i).getmDate().equals(Integer.toString(sn)))
			{	
				Log.i(LOG_TAG,"find in listdate,i="+i);
				isExsit=true;
				break;
			}
		}
		Log.i(LOG_TAG,"isExsit="+isExsit);
		if(!isExsit){
			Log.i(LOG_TAG,"it is not in list,return");
			return;
		}
                String key_str=(String)pl.get(ChatKeyString.KEY_NAME);
                Log.i(LOG_TAG,"key_str="+key_str);
                String date_str=key_str.substring(40);
                Log.i(LOG_TAG,"date_str="+date_str);
                String gid_str=(String)pl.get(ChatKeyString.KEY_TGID);

               // ChatListItemInfo info=ListDte.get(index);
                String date=null;
                ChatListItemInfo update_info=ChatListDB.getInstance(getApplicationContext()).readOneChatFromFamily(gid_str,Integer.toString(sn));
                date=update_info.getmDate();
                update_info.setmDate(date_str);
		update_info.setSendState(1);
                String oldname=update_info.getFilePath();
                int path_index=oldname.lastIndexOf("chat_audio");
                Log.i(LOG_TAG,"oldname="+oldname);
                Log.i(LOG_TAG,"path_index="+path_index);
                Log.i(LOG_TAG,"date="+date);
                String newname=oldname.substring(0,path_index)+"chat_audio/"+date_str+".amr";
                Log.i(LOG_TAG,"newname="+newname);
                ChatUtil.ChatUtilFileRename(oldname,newname);
                update_info.setFilePath(newname);
                //ChatListDB.getInstance(getApplicationContext()).updateChatMsg(WatchSystemInfo.getWatchGID(),ListDte.get(index),date);
                ChatListDB.getInstance(getApplicationContext()).updateChatMsg(cur_gid,update_info,date);
		long endMills=ChatUtil.getCurTimeMills();
		int distanceTime=ChatUtil.getTimeDistance(update_info.getListItemStartMills(),endMills);
		ChatUtil.pushSendStatusByTime(myContext,distanceTime);
		ListDte.set(i,update_info);
		}	
                //chatList.setVisibility(View.VISIBLE);
                //adapter.notifyDataSetChanged();
       }
       
       public void onError(int i, String s){
              Log.i(LOG_TAG,"chat msg send fail");
	      Log.i(LOG_TAG,"error i="+i);
	      Log.i(LOG_TAG,"error string="+s);
       }
}

    String ConstrctChatSendBuff(ChatListItemInfo chat_info){
        String sendBuff=null;
        String msg_key=null;
        JSONObject msg = new JSONObject();
        String audio_str=null;
	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)getSystemService("xun.network.Service");
        msg.put(ChatKeyString.KEY_CID, CloudBridgeUtil.CID_SEND_BYTE_MESSAGE);
        msg.put(ChatKeyString.KEY_SID, networkService.getSID());

        msg.put(ChatKeyString.KEY_SN, chat_info.getSN());
        msg.put(ChatKeyString.KEY_VERSION,CloudBridgeUtil.PROTOCOL_VERSION);
        JSONObject pl = new JSONObject();
        JSONObject value = new JSONObject();
        value.put(ChatKeyString.KEY_EID, networkService.getWatchEid());
        value.put(ChatKeyString.KEY_TYPE, "voice");
        value.put(ChatKeyString.KEY_DURATION, chat_info.getDuration());
        byte[] encodedData=ChatUtil.ChatUtilReadDataFromFile(chat_info.getFilePath());
        //byte[] encodedData = audio_str.getBytes();
        String audio_send = Base64.encodeToString(encodedData, Base64.NO_WRAP);
        value.put(ChatKeyString.KEY_CONTENT, audio_send);
        pl.put(ChatKeyString.KEY_VALUE, value);
        pl.put(ChatKeyString.KEY_TGID,  chat_info.getListItemGID());
        msg_key="GP/"+chat_info.getListItemGID()+"/MSG/#TIME#";
        pl.put(ChatKeyString.KEY_NAME,  msg_key);
        msg.put(ChatKeyString.KEY_PL, pl);
        sendBuff= msg.toJSONString();

        Log.i("LOG_TAG","sendBuff="+sendBuff);
	ChatUtil.pushStatsOnly(myContext,ChatUtil.STATS_VOICE_SEND);
	networkService.sendJsonMessage(sendBuff,new sendChatCallback());
/*
        ChatNetService.getmChatNetService().sendJsonMessage(sendBuff, new IResponseDataCallBack<ResponseData>() {
            @Override
            public void onSuccess(ResponseData responseData) {
                Log.i(LOG_TAG,"msg send succ");
                Log.i(LOG_TAG,responseData.getResponseCode() + " " + responseData.getResponseData().toJSONString());
                int sn = (int) responseData.getResponseData().get(ChatKeyString.KEY_SN);
                JSONObject pl = (JSONObject) responseData.getResponseData().get(ChatKeyString.KEY_PL);
                Log.i(LOG_TAG,"sn_str="+sn);
                String key_str=(String)pl.get(ChatKeyString.KEY_NAME);
                Log.i(LOG_TAG,"key_str="+key_str);
                String date_str=key_str.substring(40);
                Log.i(LOG_TAG,"date_str="+date_str);
                String gid_str=(String)pl.get(ChatKeyString.KEY_TGID);

               // ChatListItemInfo info=ListDte.get(index);
                String date=null;
                ChatListItemInfo update_info=ChatListDB.getInstance(getApplicationContext()).readOneChatFromFamily(gid_str,Integer.toString(sn));
                date=update_info.getmDate();
                update_info.setmDate(date_str);
                String oldname=update_info.getFilePath();
                int path_index=oldname.lastIndexOf("chat_audio");
                Log.i(LOG_TAG,"oldname="+oldname);
                Log.i(LOG_TAG,"path_index="+path_index);
                Log.i(LOG_TAG,"date="+date);
                String newname=oldname.substring(0,path_index)+"chat_audio/"+date_str+".amr";
                Log.i(LOG_TAG,"newname="+newname);
                ChatUtil.ChatUtilFileRename(oldname,newname);
                update_info.setFilePath(newname);
                //ChatListDB.getInstance(getApplicationContext()).updateChatMsg(WatchSystemInfo.getWatchGID(),ListDte.get(index),date);
                ChatListDB.getInstance(getApplicationContext()).updateChatMsg(cur_gid,update_info,date);
                //chatList.setVisibility(View.VISIBLE);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
*/
        return sendBuff;
    }

   private void sendSearchChatMsgBroacast(){
        Intent it = new Intent("com.broadcast.xxun.searchMessage");
        it.setPackage("com.xxun.watch.xunchatroom");
        Log.i(LOG_TAG, "sendSearchChatMsgBroacast");
        sendBroadcast(it);
    }

    public void registerBroadcastReciever(){
        Log.i(LOG_TAG, "registerBroadcastReciever");
        //实例化过滤器；
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("com.xxun.watch.checkrefreshbroadcast");

	intentFilter.addAction("com.broadcast.xxun.watchCall");

	intentFilter.addAction("com.xunlauncher.find");

	intentFilter.addAction("com.xunlauncher.playring");

	intentFilter.addAction("com.xxun.watch.contractrefreshbroadcast");

	intentFilter.addAction("com.xxun.watch.checkdownloadrefresh");
        //实例化广播监听器；
        mReceiver = new CheckRefreshBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(mReceiver, intentFilter);

    }

    public void unregisterBroadcastReciever(){
        Log.i(LOG_TAG, "unregisterBroadcastReciever");
        unregisterReceiver(mReceiver);
	mReceiver=null;
    }

    class  CheckRefreshBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "CheckRefreshBroadCastReciever,onReceive");
		String mAction=intent.getAction();
		Log.i(LOG_TAG, "mAction="+mAction);
		if(mAction.equals("com.xxun.watch.checkrefreshbroadcast")){ 
		String checkGid=intent.getStringExtra("checkGID");
		int delCount=intent.getIntExtra("delCount",0);
		Log.i(LOG_TAG, "checkGid="+checkGid);
		Log.i(LOG_TAG, "cur_gid="+cur_gid);
		Log.i(LOG_TAG, "delCount="+delCount);
		if(checkGid.equals(cur_gid)){
			ChatNotificationManager.delChatNoticeGroupByGID(context,checkGid);
			 if(adapter!=null) {
			    Log.i("LOG_TAG","Cadapter is ok");
			    if(ListDte!=null) {
				Log.i("LOG_TAG","onResume,count="+ListDte.size());
				ListDte.clear();
				Log.i("LOG_TAG","onResume,count="+ListDte.size());
				//ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(WatchSystemInfo.getWatchGID(), ListDte, ListDte);
				ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
				clickIndex=clickIndex-delCount;
				if(clickIndex<0){
				clickIndex=-1;
				}
				Log.i("LOG_TAG","clickIndex="+clickIndex);
				Log.i("LOG_TAG","onResume,count="+ListDte.size());

			    int mIndex=0;
			    for(mIndex=0;mIndex<ListDte.size();mIndex++){
				if(ListDte.get(mIndex).getContentType()==ChatListItemInfo.content_type_image){
					ListDte.get(mIndex).setIsPlayed(1);
			    		ChatListDB.getInstance(getApplicationContext()).updateChatMsg(ListDte.get(mIndex).getListItemGID(),ListDte.get(mIndex),ListDte.get(mIndex).getmDate());
				}	
			    }

			   ChatContractManager.updateAllMissChatCount(ChatroomMainNewActivity.this);


			    }
			    //adapter.notifyDataSetChanged();
			   refreshListView(ListDte.size()-1);
			}
		}
	}else if(mAction.equals("com.broadcast.xxun.watchCall")){
			Log.i("LOG_TAG","incoming call,stop record and sending,is_down="+is_down);
			if(is_down){
 			//if(isSW710){
			rec_button.setBackgroundResource(R.drawable.rec_btn_up);
			//}else{
                        //rec_button.setBackgroundResource(R.mipmap.rec_btn_up);
			//}
                        mChatContol.StopRecord();
                        //mChatContol.StartPlaying(path);
                       // String FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                        //FileName += "/goldfallen.mp3";
                       // mChatContol.StartPlaying(FileName);
			isIncomingCall=true;
                        handleRecorAction(xun_chat_record_status_record_finish);
			is_down=false;
			}
			autoPlay=0;
			checkAudioState();
	}else if(mAction.equals("com.xunlauncher.find")){
			Log.i("LOG_TAG","find watch,stop record and sending,is_down="+is_down);
			if(is_down){
			//if(isSW710){
			rec_button.setBackgroundResource(R.drawable.rec_btn_up);
			//}else{
                        //rec_button.setBackgroundResource(R.mipmap.rec_btn_up);
			//}
                        mChatContol.StopRecord();
                        //mChatContol.StartPlaying(path);
                       // String FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                        //FileName += "/goldfallen.mp3";
                       // mChatContol.StartPlaying(FileName);
                        handleRecorAction(xun_chat_record_status_record_finish);
			is_down=false;
			}
			autoPlay=0;
			checkAudioState();
	}else if(mAction.equals("com.xunlauncher.playring")){
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

        ChatSoundControl.StartPlaying(ChatroomMainNewActivity.this, R.raw.new_msg,complete_listener1,error_listener1  );
	}else if(mAction.equals("com.xxun.watch.contractrefreshbroadcast")){
		ListDte.clear();
            	ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
            	refreshListView(ListDte.size()-1);
	}else if(mAction.equals("com.xxun.watch.checkdownloadrefresh")){
		//ListDte.clear();
            	//ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(cur_gid, ListDte, ListDte);
            	refreshListView(-1);
	}
        }
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

   private void closeTip(){
	if(mToast!=null){
	mToast.cancel();
	mToast=null;
	}
   }
}
