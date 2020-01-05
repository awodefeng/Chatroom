

package com.xxun.watch.xunchatroom.control;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.R;
import java.util.ArrayList;
import com.xxun.watch.xunchatroom.activity.ChatNotificationActivity;
import com.xxun.watch.xunchatroom.control.ChatNotificationManager;
import com.xxun.watch.xunchatroom.info.ChatNoticeInfo;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import com.xxun.watch.xunchatroom.adapter.ChatListAdapter;
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
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import android.util.Base64;
import android.os.Environment;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.xxun.watch.xunchatroom.control.ChatVibratorControl;
import com.xxun.watch.xunchatroom.control.ChatContractManager;
import com.xiaoxun.sdk.utils.Constant;
import android.os.PowerManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import com.xxun.watch.xunchatroom.R;
import android.media.MediaPlayer;
import android.view.WindowManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.xxun.watch.xunchatroom.util.DownloadManagerUtil;
import com.xxun.watch.xunchatroom.util.OnDownloadListener;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;
import android.util.XiaoXunUtil;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

public class ChatRecieveMsgManager
{
    static String LOG_TAG="msg rec";
	static String TAG="msg rec";
    static  int chat_list_max_count=5;
    public static boolean isDelNotice=false;
    static ArrayList<ChatListItemInfo> msgList=new ArrayList<ChatListItemInfo>();
    static boolean isLoading=false;
    static public int loadSN=-1;
    static ChatThreadTimer mLoadThread;
    static Context mContext=null;
    public static boolean isSendWork=false;
    public static int checkRecieveMsgGroup(Context appContext,String gid){
     int max_count=ChatListDB.getInstance(appContext).getMsgCount(gid);
     boolean isSW730 = "SW730".equals(Constant.PROJECT_NAME);
     isDelNotice=false;
     if(isSW730){
	chat_list_max_count=10;
     }else{
	chat_list_max_count=5;
     }
     Log.i(LOG_TAG,"checkRecieveMsgGroup,isSW730="+isSW730);
     Log.i(LOG_TAG,"checkRecieveMsgGroup,chat_list_max_count="+chat_list_max_count);
     int del_count=0;
     ArrayList<String> dateList=new ArrayList<String>();
    Log.i(LOG_TAG,"checkRecieveMsgGroup,max_count="+max_count);
     if(max_count>=chat_list_max_count){
         del_count=max_count-chat_list_max_count+1;
         Log.i(LOG_TAG,"checkRecieveMsgGroup,del_count="+del_count);
         ChatListDB.getInstance(appContext).deleteChatFromFamily(gid,del_count,dateList);
	 if(ChatNotificationManager.delChatCheckNotice(gid,dateList)){
		sendDelChatMsgBroacast(appContext);
		isDelNotice=true;
	 }
     }
     Log.i(LOG_TAG,"checkRecieveMsgGroup,del_count="+del_count);
     return del_count;
     //ChatListDB.getInstance(appContext).readAllChatFromFamily(gid,msgList,msgList);
 }

public static void handleLoadFinish(){
	Log.i(LOG_TAG,"handleLoadFinish,isLoading="+isLoading);

	if(msgList.size()>0&&isLoading){
	msgList.remove(0);
	}
	isLoading=false;
	if(msgList.size()==0&&mLoadThread!=null){
		Log.i(LOG_TAG,"handleLoadFinish,msgList is empty,close thread");
		mLoadThread.stopThreadTimer();
		mLoadThread=null;
		XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)mContext.getSystemService("xun.network.Service");
		networkService.releaseLTEMode("xunchatroom");
	}
	sendDownloadRefreshBroacast();
}


public static boolean isInDownloadQue(String date){
	Log.i(LOG_TAG,"isInDownloadQue,date="+date);
	boolean resault=false;
	if(msgList.size()>0){
	for(ChatListItemInfo data:msgList){
		if(date.equals(data.getmDate())){
			resault=true;
			break;
		}
	}
	}
	Log.i(LOG_TAG,"isInDownloadQue,resault="+resault);
	return resault;
}

public static void startReload(ChatListItemInfo data,Context context){
	mContext=context;
	startDownloadThread(data);
}

	public static  int getDownloadType(Context context,ChatListItemInfo chat_info){
		String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
		String name;
		String number;
		Uri uri = Uri.parse ("content://com.android.contacts/contacts");
		ContentResolver resolver = context.getApplicationContext().getContentResolver();
		Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
		Log.i(LOG_TAG,"getDownloadType");
		boolean is_find=false;
		String nick_name=null;
		String gid=null;
		String eid=null;
		int img=R.mipmap.photo_test;
		int attri =0;
		String avatar=null;
		int contract_type=0;
		while(cursor.moveToNext()&&!is_find){

			int contactsId = cursor.getInt(0);
			Log.i(LOG_TAG,"contactsId = " +contactsId);
			uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
			Cursor dataCursor = resolver.query(uri, project, null, null, null);
			//SyncArrayBean  arrayBean = new SyncArrayBean();
			while(dataCursor.moveToNext()) {
				String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/name".equals(type)) {

					//name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
					//avatar = dataCursor.getString(dataCursor.getColumnIndex(project[3]));
					//Log.i(LOG_TAG,"name = " +name);
					//nick_name=name;
					//chat_info.setContractItemAvatar(avatar);
				} else if ("vnd.android.cursor.item/email_v2".equals(type)) {
				} else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
					//number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
					//Log.i(LOG_TAG,"number = " +number);
				}else if("vnd.android.cursor.item/nickname".equals(type)){
					contract_type=dataCursor.getInt(dataCursor.getColumnIndex(project[1]));
					//attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));
					gid=dataCursor.getString(dataCursor.getColumnIndex(project[5]));
					eid=dataCursor.getString(dataCursor.getColumnIndex(project[4]));
					Log.i(LOG_TAG, "gid = " + gid);
					Log.i(LOG_TAG, "eid = " + eid);
					if(gid!=null) {
						if (chat_info.getListItemGID().equals(WatchSystemInfo.getWatchGID())) {
							//

								contract_type=0;
								is_find = true;


						} else if (gid.equals(chat_info.getListItemGID())) {

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
		Log.i(LOG_TAG, "contract_type="+contract_type);

		return contract_type;
	}

public static void startDownloadThread(ChatListItemInfo data){
	 Log.i(LOG_TAG,"startDownloadThread,isLoading="+isLoading);
	Log.i(LOG_TAG,"startDownloadThread,msgList.size()="+msgList.size());
	boolean isStart=false;
         if(msgList.size()==0){
		isStart=true;
	}
	 msgList.add(data);
	if((isLoading||msgList.size()>0)&&!isStart){
		Log.i(LOG_TAG,"thread is already working,return");
		return;
	}

	if(isWifi(mContext)){
		//startDownloadService();
		isSendWork=true;
	}else{
		XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)mContext.getSystemService("xun.network.Service");
		boolean requireResault=networkService.requireLTEMode("xunchatroom");
		Log.i(TAG, "requireResault="+requireResault);
		if(requireResault){
			//isWaitLTE=true;
			isSendWork=false;
		}else{
			//startDownloadService();
			isSendWork=true;
			networkService.releaseLTEMode("xunchatroom");
		}
	}

           mLoadThread=new ChatThreadTimer(1000, new ChatThreadTimer.TimerInterface() {
                @Override
                public void doTimerOut() {
                    Log.i(LOG_TAG, "doTimerOut,listsize="+msgList.size()+",isLoading="+isLoading);
					if(!isSendWork){
						Log.i(LOG_TAG, "doTimerOut return,,isSendWork="+isSendWork);
						return;
					}

			if(msgList.size()>0&&!isLoading){
		isLoading=true;
		String file_path=null;
		String file_name=null;
		if(msgList.get(0).getContentType()==4){
			//chat_info.setContentType(4);
			file_path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ msgList.get(0).getmDate()+".jpg";
			file_name=msgList.get(0).getmDate()+".jpg";
			Log.i(LOG_TAG, "file_path ="+file_path);
			String dlKeyString=msgList.get(0).getFilePath();
			Log.i(LOG_TAG, "dlKeyString ="+dlKeyString);
			DownloadManagerUtil dmu=DownloadManagerUtil.getInstance(mContext);
			int type=getDownloadType(mContext,msgList.get(0));
			Log.i(LOG_TAG, "type ="+type);
			String eid=null;
			if(type==2){
				eid=msgList.get(0).getListItemEID();
			}else{
				eid=WatchSystemInfo.getWatchEID(mContext);
			}
			dmu.downloadNoticeVideo(mContext,eid, dlKeyString,new OnDownloadListener(){
			@Override
			public void onSuccess(String s){
			Log.i(LOG_TAG, "photo download onSuccess");
			handleLoadFinish();
			}

			@Override
			public void onFail(){
			Log.i(LOG_TAG, "photo download onFail");
			handleLoadFinish();
			}
		 },Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/",file_name);
		}else if(msgList.get(0).getContentType()==5){
			//chat_info.setContentType(5);
			file_path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ msgList.get(0).getmDate()+".jpg";
			file_name=msgList.get(0).getmDate()+".jpg";

			Log.i(LOG_TAG, "file_path ="+file_path);
			String dlKeyString=msgList.get(0).getFilePath();
			Log.i(LOG_TAG, "dlKeyString ="+dlKeyString);
			DownloadManagerUtil dmu=DownloadManagerUtil.getInstance(mContext);
			int type=getDownloadType(mContext,msgList.get(0));
			Log.i(LOG_TAG, "type ="+type);
			String eid=null;
			if(type==2){
				eid=msgList.get(0).getListItemEID();
			}else{
				eid=WatchSystemInfo.getWatchEID(mContext);
			}
			dmu.downloadNoticeVideo(mContext,eid, dlKeyString,new OnDownloadListener(){
			@Override
			public void onSuccess(String s){
			Log.i(LOG_TAG, "video image download onSuccess");
			}

			@Override
			public void onFail(){
			Log.i(LOG_TAG, "video image download onFail");
			}
		 },Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/",file_name);
			//String dlKeyString=msgList.get(0).getFilePath();
			Log.i(LOG_TAG, "dlKeyString ="+dlKeyString);
			String sourceKey=getSourceFileKey(dlKeyString);
			file_name=msgList.get(0).getmDate()+".mp4";
			dmu.downloadNoticeVideo(mContext,eid, sourceKey,new OnDownloadListener(){
			@Override
			public void onSuccess(String s){
			Log.i(LOG_TAG, "video source download onSuccess");
			handleLoadFinish();
			}

			@Override
			public void onFail(){
			Log.i(LOG_TAG, "video source download onFail");
			handleLoadFinish();
			}
		 },Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/",file_name);

		}

			}
                }
            });
	mLoadThread.start();	
}

  static private void sendDownloadRefreshBroacast(){
        Intent it = new Intent("com.xxun.watch.checkdownloadrefresh");
        Log.i(LOG_TAG, "sendDownloadRefreshBroacast");
	it.setPackage("com.xxun.watch.xunchatroom");
        mContext.sendBroadcast(it);
    }

public static int initRecieveMsgGroup(Context appContext,String gid){
     int max_count=ChatListDB.getInstance(appContext).getMsgCount(gid);
     int del_count=0;
     boolean isSW730 = "SW730".equals(Constant.PROJECT_NAME);
     isDelNotice=false;
     if(isSW730){
	chat_list_max_count=10;
     }else{
	chat_list_max_count=5;
     }
     Log.i(LOG_TAG,"initRecieveMsgGroup,isSW730="+isSW730);
     Log.i(LOG_TAG,"initRecieveMsgGroup,chat_list_max_count="+chat_list_max_count);
     ArrayList<String> dateList=new ArrayList<String>();
    Log.i(LOG_TAG,"initRecieveMsgGroup,max_count="+max_count);
     if(max_count>chat_list_max_count){
         del_count=max_count-chat_list_max_count;
         Log.i(LOG_TAG,"initRecieveMsgGroup,del_count="+del_count);
         ChatListDB.getInstance(appContext).deleteChatFromFamily(gid,del_count,dateList);
	 if(ChatNotificationManager.delChatCheckNotice(gid,dateList)){
		sendDelChatMsgBroacast(appContext);
		isDelNotice=true;
	 }
     }
     Log.i(LOG_TAG,"initRecieveMsgGroup,del_count="+del_count);
     return del_count;
     //ChatListDB.getInstance(appContext).readAllChatFromFamily(gid,msgList,msgList);
 }

  static private void sendDelChatMsgBroacast(Context context){
        Intent it = new Intent("com.xxun.watch.checkdelbroadcast");
        Log.i(LOG_TAG, "sendDelChatMsgBroacast");
	it.setPackage("com.xxun.watch.xunchatroom");
        context.sendBroadcast(it);
    }

 public static int getImageIndexByName(Context context,String name){
    int index=0;
    boolean is_find=false;
	Log.i(LOG_TAG, "getImageIndexByName,name="+name);
    for(index=0;index<10;index++){

          if(name.equals(context.getString(R.string.face_key_1+index))){
		is_find=true;
                 break;
	   }
	}
	Log.i(LOG_TAG, "is_find="+is_find);
	Log.i(LOG_TAG, "index="+index);
	if(is_find){
	   return index;
	}

      return -1;
}

private static boolean checkScreenIsOn(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean ifOpen = powerManager.isScreenOn();
	Log.i(LOG_TAG, "checkScreenIsOn,ifOpen="+ifOpen);
	return ifOpen;
}

private static void sendPlayRingBroadcast(Context context){
	Log.i(LOG_TAG, "sendPlayRingBroadcast");
       Intent it = new Intent("com.xunlauncher.playring");
        Log.i(LOG_TAG, "sendPlayRingBroadcast");
	it.setPackage("com.xxun.watch.xunchatroom");
        context.sendBroadcast(it);
}

    private static boolean isChargeForbidden(Context context) {
        try{
            if (SystemProperties.get("ro.build.type").equals("user")
                && !"true".equals(Settings.System.getString(context.getContentResolver(), "isMidtest"))
                && Settings.System.getInt(context.getContentResolver(),"is_localprop_exist") == 0
                &&  "true".equals(SystemProperties.get("persist.sys.isUsbConfigured"))
                ) {
		Log.i(LOG_TAG, "isChargeForbidden,true");
                return true;
            }
        }catch (SettingNotFoundException e){
            e.printStackTrace();
        }
	Log.i(LOG_TAG, "isChargeForbidden,false");
        return false;
    }


public static String getSourceFileKey(String oldKey){
	Log.i(LOG_TAG, "getSourceFileKey,oldKey="+oldKey);

	int cutStartIndex=oldKey.lastIndexOf("PREVIEW");
	int cutEndIndex=oldKey.lastIndexOf("/");

	String newKey=oldKey.substring(0,cutStartIndex)+"SOURCE"+oldKey.substring(cutEndIndex,oldKey.length()-8)+".mp4";
	Log.i(LOG_TAG, "getSourceFileKey,newKey="+newKey);
	return newKey;
}

 public static void RecieveMessage(Context context ,JSONObject pl){
        Log.i(LOG_TAG, "RecieveMessage");
	if(!ChatUtil.checkRomAvailableSize()){
		Log.i(LOG_TAG, "RecieveMessage,flash size is full,return");
		return;
	}
	mContext=context;
        ChatListItemInfo chat_info=new ChatListItemInfo();

	int subAction=(int)pl.get(ChatKeyString.KEY_SUB_ACTION);
	Log.i(LOG_TAG, "subAction ="+subAction);
	if(subAction==102&&XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){
	JSONObject valueJ = (JSONObject) pl.get("Value");
	String content_type=(String)valueJ.get(ChatKeyString.KEY_TYPE);
	Log.i(LOG_TAG, "content_type ="+content_type);
		if(content_type.equals("photo")){
			chat_info.setContentType(4);
		}else if(content_type.equals("video")){
			chat_info.setContentType(5);
		}
		 
		String dlKeyString=(String)valueJ.get("Content");
		Log.i(LOG_TAG, "dlKeyString ="+dlKeyString);

	        String eid=(String)valueJ.get(ChatKeyString.KEY_EID);
		Log.i(LOG_TAG, "eid ="+eid);
		chat_info.setListItemEID(eid);

		String key_str=(String)pl.get(ChatKeyString.KEY_NAME);
		Log.i(LOG_TAG, "key_str ="+key_str);
		ChatUtil.ChatUtilWriteEndkey(context.getApplicationContext(),key_str);
		String date=key_str.substring(40);
		Log.i(LOG_TAG, "date ="+date);
		chat_info.setmDate(date);
	        String gid=key_str.substring(3,35);
		Log.i(LOG_TAG, "gid ="+gid);
		int delCount=ChatRecieveMsgManager.checkRecieveMsgGroup(context.getApplicationContext(),gid);
		Log.i(LOG_TAG, "delCount ="+delCount);
		chat_info.setListItemGID(gid);

		String file_path=null;
		String file_name=null;
		if(content_type.equals("photo")){
			//chat_info.setContentType(4);
			file_path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ date+".jpg";
			file_name=date+".jpg";
			Log.i(LOG_TAG, "file_path ="+file_path);
			chat_info.setFilePath(dlKeyString);
			/*DownloadManagerUtil dmu=DownloadManagerUtil.getInstance(context);
			dmu.downloadNoticeVideo(context,WatchSystemInfo.getWatchEID(context), dlKeyString,new OnDownloadListener(){
			@Override
			public void onSuccess(String s){
			Log.i(LOG_TAG, "photo download onSuccess");
			}

			@Override
			public void onFail(){
			Log.i(LOG_TAG, "photo download onFail");
			}
		 },Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/",file_name);*/
		}else if(content_type.equals("video")){
			//chat_info.setContentType(5);
			file_path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ date+".jpg";
			file_name=date+".jpg";

			Log.i(LOG_TAG, "file_path ="+file_path);
			chat_info.setFilePath(dlKeyString);
/*
			DownloadManagerUtil dmu=DownloadManagerUtil.getInstance(context);
			dmu.downloadNoticeVideo(context,WatchSystemInfo.getWatchEID(context), dlKeyString,new OnDownloadListener(){
			@Override
			public void onSuccess(String s){
			Log.i(LOG_TAG, "video image download onSuccess");
			}

			@Override
			public void onFail(){
			Log.i(LOG_TAG, "video image download onFail");
			}
		 },Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/",file_name);

			String sourceKey=getSourceFileKey(dlKeyString);
			file_name=date+".mp4";
			dmu.downloadNoticeVideo(context,WatchSystemInfo.getWatchEID(context), sourceKey,new OnDownloadListener(){
			@Override
			public void onSuccess(String s){
			Log.i(LOG_TAG, "video source download onSuccess");
			}

			@Override
			public void onFail(){
			Log.i(LOG_TAG, "video source download onFail");
			}
		 },Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/",file_name);
*/
		}


        chat_info.setIsPlayed(0);
        chat_info.setListItemType(0);
	ChatNoticeInfo nitice_info=new ChatNoticeInfo();
	boolean isFind=perpareNoticeWindow(context,chat_info,nitice_info);
	startDownloadThread(chat_info);
        ChatListDB.getInstance(context.getApplicationContext()).addChatMsg(gid,chat_info);
	ChatContractManager.updateAllMissChatCount(context);
       // if(cur_gid.equals(gid)) {
        //    ListDte.add(chat_info);
        //    refreshListView(ListDte.size() - 1);
        //}
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	String isIncall = "false";
	isIncall = android.provider.Settings.System.getString(context.getContentResolver(),"isIncall");
	String isInvideo = (android.provider.Settings.System.getString(context.getContentResolver(), "xun_video") == null)?"false":android.provider.Settings.System.getString(context.getContentResolver(), "xun_video");
	ChatRoomControl myChatContol=ChatRoomControl.getInstance();

        ChatUtil.pushStatsOnly(context,ChatUtil.STATS_VOICE_RECEIVED);

	int isChatroomExit= android.provider.Settings.System.getInt(context.getContentResolver(),"chatroom_exit",1);
	Log.i(LOG_TAG, "isChatroomExit="+isChatroomExit);
        /**
         * ·µ»Øµç»°×ŽÌ¬ 
         *
         * CALL_STATE_IDLE ÎÞÈÎºÎ×ŽÌ¬Ê±  
         * CALL_STATE_OFFHOOK œÓÆðµç»°Ê± 
         * CALL_STATE_RINGING µç»°œøÀŽÊ±  
         */
        if(tm.getCallState()!=TelephonyManager.CALL_STATE_IDLE||"true".equals(isIncall)||"true".equals(isInvideo)){
		Log.i(LOG_TAG, "in call,no need notice,return");
		return;
	}
	else if(ChatUtil.isDisturb(context)){
		Log.i(LOG_TAG, "in silence mode,no need notice,return");
		return;
	}else if(myChatContol.ChatCurState==ChatRoomControl.ChatStateRecording){
		Log.i(LOG_TAG, "chatroom is recording,no need notice,return");
		ChatVibratorControl.StartPlaying(context);
		return;
	}else if(isChatroomExit!=1){
		Log.i(LOG_TAG, "chatroom function is not exit,return");
		return;
	}
	//ChatVibratorControl.StartPlaying(context);
        //return;
	boolean isOpen=false;
	if(XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){
	isOpen=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatroomMainNewActivity");
	}else{
	isOpen=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatroomMainActivity");
	}
       String recordingStr = android.provider.Settings.System.getString(context.getContentResolver(), "camera_isRecording");
	boolean isRecording=ChatUtil.isForeground(context,"com.xxun.xuncamera.CameraMainActivity");//false;
	boolean isScreenOn=checkScreenIsOn(context);
	boolean isInNotif=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatNotificationActivity");//;

	Log.i(LOG_TAG, "isScreenOn ="+ isScreenOn);
	//if("true".equals(recordingStr)) {
	//	isRecording=true;
	//}
	Log.i(LOG_TAG, "isRecording ="+ isRecording);
	Log.i(LOG_TAG, "isOpen ="+ isOpen);
        Log.i(LOG_TAG, "is_work ="+ ChatNotificationManager.is_work);
	Log.i(LOG_TAG, "delCount ="+ delCount);
	Log.i(LOG_TAG, "isInNotif ="+ isInNotif);

	if(!isInNotif&&!isRecording&&!isOpen&&ChatNotificationManager.is_work){
		ChatNotificationManager.is_work=false;
	}

	if((!isScreenOn)&&isOpen){
		sendPlayRingBroadcast(context);
		ChatVibratorControl.StartPlaying(context);
	}else if(isOpen||isRecording){
	    ChatNotificationManager.addGroupByGID(context.getApplicationContext(), gid, chat_info);
	    sendCheckRefreshBroacast(context,gid,delCount);
	    /*if(isRecording)*/{
		ChatVibratorControl.StartPlaying(context);
		}
	}
        else if(ChatNotificationManager.is_work){
            ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, chat_info);
	    if(delCount<=0||!isDelNotice){
	    ChatVibratorControl.StartPlaying(context);
	    sendPlayRingInNotifBroadcast(context);
	    }
	     if(!isChargeForbidden(context)){
		//sendNewChatMsgBroacast(context,nitice_info);
	     }
        }else{
            ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, chat_info);
                       if(isFind){
		sendNewChatMsgBroacast(context,nitice_info);
		}
        }

	}else{// sub_action==105
		int duration=(int)pl.get(ChatKeyString.KEY_DURATION);
		Log.i(LOG_TAG, "duration ="+duration);
		chat_info.setDuration(duration);
		int content_type=(int)pl.get(ChatKeyString.KEY_TYPE);
		Log.i(LOG_TAG, "content_type ="+content_type);
		if(content_type==1) {
		    chat_info.setContentType(0);
		}
		else if(content_type==4){
		    chat_info.setContentType(1);
		}
		else if(content_type==8)
		{
		     chat_info.setContentType(2);
		}

		        String eid=(String)pl.get(ChatKeyString.KEY_EID);
        Log.i(LOG_TAG, "eid ="+eid);
        chat_info.setListItemEID(eid);

        String key_str=(String)pl.get(ChatKeyString.KEY_NAME);
        Log.i(LOG_TAG, "key_str ="+key_str);
        ChatUtil.ChatUtilWriteEndkey(context.getApplicationContext(),key_str);
        String date=key_str.substring(40);
        Log.i(LOG_TAG, "date ="+date);
        chat_info.setmDate(date);

        String gid=key_str.substring(3,35);
        Log.i(LOG_TAG, "gid ="+gid);
        int delCount=ChatRecieveMsgManager.checkRecieveMsgGroup(context.getApplicationContext(),gid);
	Log.i(LOG_TAG, "delCount ="+delCount);
        chat_info.setListItemGID(gid);
        byte[] bytes=null;
        String gb=null;
	String file_path=null;
        if(chat_info.getContentType()==2){
            //String text_content=(String) pl.get(ChatKeyString.KEY_CONTENT);
		bytes = Base64.decode((String) pl.get(ChatKeyString.KEY_CONTENT), Base64.NO_WRAP);
		String text_content = new String(bytes);
            Log.i(LOG_TAG, "text_content ="+text_content);

            /*try {
                 gb = new String(text_content.getBytes("UTF-8"), "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
             bytes=gb.getBytes();*/
		int image_index=getImageIndexByName(context,text_content);
		 file_path=Integer.toString(image_index);
	}else if(chat_info.getContentType()==4||chat_info.getContentType()==5){
		bytes = Base64.decode((String) pl.get(ChatKeyString.KEY_CONTENT), Base64.NO_WRAP);
		String text_content = new String(bytes);
            Log.i(LOG_TAG, "text_content ="+text_content);
	}else {
            bytes = Base64.decode((String) pl.get(ChatKeyString.KEY_CONTENT), Base64.NO_WRAP);
	    file_path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ date+".amr";
        }

        Log.i(LOG_TAG, "file_path ="+file_path);
        chat_info.setFilePath(file_path);
	if(chat_info.getContentType()!=2){
        ChatUtil.ChatUtilCreateFile(file_path);
        ChatUtil.ChatUtilSaveDataToFile(bytes,file_path);
	}
        chat_info.setIsPlayed(0);
        chat_info.setListItemType(0);
	ChatNoticeInfo nitice_info=new ChatNoticeInfo();
	boolean isFind=perpareNoticeWindow(context,chat_info,nitice_info);
        ChatListDB.getInstance(context.getApplicationContext()).addChatMsg(gid,chat_info);
	ChatContractManager.updateAllMissChatCount(context);
       // if(cur_gid.equals(gid)) {
        //    ListDte.add(chat_info);
        //    refreshListView(ListDte.size() - 1);
        //}
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	String isIncall = "false";
	isIncall = android.provider.Settings.System.getString(context.getContentResolver(),"isIncall");
	String isInvideo = (android.provider.Settings.System.getString(context.getContentResolver(), "xun_video") == null)?"false":android.provider.Settings.System.getString(context.getContentResolver(), "xun_video");
	ChatRoomControl myChatContol=ChatRoomControl.getInstance();

        ChatUtil.pushStatsOnly(context,ChatUtil.STATS_VOICE_RECEIVED);

	int isChatroomExit= android.provider.Settings.System.getInt(context.getContentResolver(),"chatroom_exit",1);
	Log.i(LOG_TAG, "isChatroomExit="+isChatroomExit);
        /**
         * ·µ»Øµç»°×ŽÌ¬ 
         *
         * CALL_STATE_IDLE ÎÞÈÎºÎ×ŽÌ¬Ê±  
         * CALL_STATE_OFFHOOK œÓÆðµç»°Ê± 
         * CALL_STATE_RINGING µç»°œøÀŽÊ±  
         */
        if(tm.getCallState()!=TelephonyManager.CALL_STATE_IDLE||"true".equals(isIncall)||"true".equals(isInvideo)){
		Log.i(LOG_TAG, "in call,no need notice,return");
		return;
	}
	else if(ChatUtil.isDisturb(context)){
		Log.i(LOG_TAG, "in silence mode,no need notice,return");
		return;
	}else if(myChatContol.ChatCurState==ChatRoomControl.ChatStateRecording){
		Log.i(LOG_TAG, "chatroom is recording,no need notice,return");
		ChatVibratorControl.StartPlaying(context);
		return;
	}else if(isChatroomExit!=1){
		Log.i(LOG_TAG, "chatroom function is not exit,return");
		return;
	}
	//ChatVibratorControl.StartPlaying(context);
        //return;

	boolean isOpen=false;
	if(XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){
	isOpen=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatroomMainNewActivity");
	}else{
	isOpen=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatroomMainActivity");
	}
       String recordingStr = android.provider.Settings.System.getString(context.getContentResolver(), "camera_isRecording");
	boolean isRecording=ChatUtil.isForeground(context,"com.xxun.xuncamera.CameraMainActivity");//false;
	boolean isScreenOn=checkScreenIsOn(context);
	boolean isInNotif=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatNotificationActivity");//;

	Log.i(LOG_TAG, "isScreenOn ="+ isScreenOn);
	//if("true".equals(recordingStr)) {
	//	isRecording=true;
	//}
	Log.i(LOG_TAG, "isRecording ="+ isRecording);
	Log.i(LOG_TAG, "isOpen ="+ isOpen);
        Log.i(LOG_TAG, "is_work ="+ ChatNotificationManager.is_work);
	Log.i(LOG_TAG, "delCount ="+ delCount);
	Log.i(LOG_TAG, "isInNotif ="+ isInNotif);

	if(!isInNotif&&!isRecording&&!isOpen&&ChatNotificationManager.is_work){
		ChatNotificationManager.is_work=false;
	}

	if((!isScreenOn)&&isOpen){
		sendPlayRingBroadcast(context);
		ChatVibratorControl.StartPlaying(context);
	}else if(isOpen||isRecording){
	    ChatNotificationManager.addGroupByGID(context.getApplicationContext(), gid, chat_info);
	    sendCheckRefreshBroacast(context,gid,delCount);
	    /*if(isRecording)*/{
		ChatVibratorControl.StartPlaying(context);
		}
	}
        else if(ChatNotificationManager.is_work){
            ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, chat_info);
	    if(delCount<=0||!isDelNotice){
	    ChatVibratorControl.StartPlaying(context);
	    sendPlayRingInNotifBroadcast(context);
	    }
	     if(!isChargeForbidden(context)){
		//sendNewChatMsgBroacast(context,nitice_info);
	     }
        }else{
            ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, chat_info);
                       if(isFind){
		sendNewChatMsgBroacast(context,nitice_info);
		}
        }

	}

	
}
  public static void sendPlayRingInNotifBroadcast(Context context){
	Log.i(LOG_TAG, "sendPlayRingInNotifBroadcast");

        Intent it = new Intent("com.xxun.watch.playringbroadcast");
it.setPackage("com.xxun.watch.xunchatroom");
        context.sendBroadcast(it);
    }


  public static void ParseOfflineList(Context context,JSONObject pl){
 	Log.i(LOG_TAG,"ParseOfflineList");

 	if(!ChatUtil.checkRomAvailableSize()){
		Log.i(LOG_TAG, "ParseOfflineList,flash size is full,return");
		ChatOfflineMsgManager.setOfflineMsgFlag(false);
		ChatOfflineMsgManager.initOfflineManager();
		return;
	}
                JSONArray msg_list = (JSONArray)pl.get(ChatKeyString.KEY_LIST);
                if(msg_list!=null) {
                    int count = 0;
                    int count_max=msg_list.size();
                    Log.i(LOG_TAG,"count_max="+count_max);
		    ChatUtil.pushStatsValue(context,ChatUtil.STATS_VOICE_RECEIVE_ROLLBACK,count_max);
                    if(count_max>0) {
                        for (count = 0; count <count_max; count++) {
                            RecieveOfflineMessage(context,(JSONObject) msg_list.get(count));
                        }
                    }else{
			ChatOfflineMsgManager.setOfflineMsgFlag(false);
			ChatOfflineMsgManager.initOfflineManager();
                        Log.i(LOG_TAG,"msg_list is empty");
                    }
		}
                else{
		ChatOfflineMsgManager.setOfflineMsgFlag(false);
		ChatOfflineMsgManager.initOfflineManager();
		Log.i(LOG_TAG,"msg_list is null");
		}
}

  public static  void RecieveOfflineMessage(Context context,JSONObject pl){
        Log.i(LOG_TAG, "RecieveOfflineMessage");
        ChatListItemInfo chat_info=new ChatListItemInfo();
	mContext=context;
	int content_type=0;
        int duration=(int)pl.get(ChatKeyString.KEY_DURATION);
        Log.i(LOG_TAG, "duration ="+duration);
        chat_info.setDuration(duration);

        String content_type_str=(String)pl.get(ChatKeyString.KEY_TYPE);
        Log.i(LOG_TAG, "content_type_str ="+content_type_str);
        if(content_type_str.equals("voice")) {
            chat_info.setContentType(0);
	    content_type=0;
        }
        else if(content_type_str.equals("text")){
            chat_info.setContentType(1);
	    content_type=1;
        }
        else if(content_type_str.equals("emoji")){
            chat_info.setContentType(2);
	    content_type=2;
        }
        else if(content_type_str.equals("photo")){
            chat_info.setContentType(4);
	    content_type=4;
        }
        else if(content_type_str.equals("video")){
            chat_info.setContentType(5);
	    content_type=5;
        }
	else{
		ChatOfflineMsgManager.setOfflineMsgFlag(false);
		ChatOfflineMsgManager.initOfflineManager();
		return;
	}
	Log.i(LOG_TAG, "content_type ="+content_type);

	if(content_type==0||content_type==1||content_type==2){//audio,image,text
        String eid=(String)pl.get(ChatKeyString.KEY_EID);
        Log.i(LOG_TAG, "eid ="+eid);
        chat_info.setListItemEID(eid);

        String key_str=(String)pl.get(ChatKeyString.KEY_NAME);
        Log.i(LOG_TAG, "key_str ="+key_str);
        int resault =ChatOfflineMsgManager.compareList(key_str);
        Log.i(LOG_TAG, "resault ="+resault);
        if(resault==ChatOfflineMsgManager.compare_resault_over){
		ChatOfflineMsgManager.setOfflineMsgFlag(false);
		ChatOfflineMsgManager.initOfflineManager();
        }
	else if(resault==ChatOfflineMsgManager.compare_resault_error){
		ChatOfflineMsgManager.setOfflineMsgFlag(false);
		ChatOfflineMsgManager.initOfflineManager();
	}

        ChatUtil.ChatUtilWriteEndkey(context.getApplicationContext(),key_str);
        String date=key_str.substring(40);
        Log.i(LOG_TAG, "date ="+date);
        chat_info.setmDate(date);

        String gid=key_str.substring(3,35);
        Log.i(LOG_TAG, "gid ="+gid);
        int delCount=ChatRecieveMsgManager.checkRecieveMsgGroup(context.getApplicationContext(),gid);
	Log.i(LOG_TAG, "delCount ="+delCount);
        chat_info.setListItemGID(gid);
        byte[] bytes=null;
	String file_path=null;
        if(content_type==1){
            bytes=((String) pl.get(ChatKeyString.KEY_CONTENT)).getBytes();
        }
	else if(content_type==2){
		//bytes = Base64.decode((String) pl.get(ChatKeyString.KEY_CONTENT), Base64.NO_WRAP);
		String text_content =(String) pl.get(ChatKeyString.KEY_CONTENT);// new String(bytes);
            Log.i(LOG_TAG, "text_content ="+text_content);
		
            /*try {
                 gb = new String(text_content.getBytes("UTF-8"), "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
             bytes=gb.getBytes();*/
		int image_index=getImageIndexByName(context,text_content);
		 file_path=Integer.toString(image_index);
	}
	else {
            bytes = Base64.decode((String) pl.get(ChatKeyString.KEY_CONTENT), Base64.NO_WRAP);

        }
	if(content_type!=2){
        file_path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ date+".amr";
	}
        Log.i(LOG_TAG, "file_path ="+file_path);
        chat_info.setFilePath(file_path);
	if(content_type!=2){
        ChatUtil.ChatUtilCreateFile(file_path);
        ChatUtil.ChatUtilSaveDataToFile(bytes,file_path);
	}
        chat_info.setIsPlayed(0);
        chat_info.setListItemType(0);
	ChatNoticeInfo nitice_info=new ChatNoticeInfo();
	boolean isFind=perpareNoticeWindow(context,chat_info,nitice_info);
        ChatListDB.getInstance(context.getApplicationContext()).addChatMsg(gid,chat_info);
	ChatContractManager.updateAllMissChatCount(context);
        //ListDte.add(chat_info);
        //refreshListView(ListDte.size()-1);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	String isIncall = "false";
	isIncall = android.provider.Settings.System.getString(context.getContentResolver(),"isIncall");
	String isInvideo = (android.provider.Settings.System.getString(context.getContentResolver(), "xun_video") == null)?"false":android.provider.Settings.System.getString(context.getContentResolver(), "xun_video");

	ChatRoomControl myChatContol=ChatRoomControl.getInstance();

	ChatUtil.pushStatsValue(context,ChatUtil.STATS_VOICE_RECEIVE_ROLLBACK_SUCCESS,1);
	int isChatroomExit= android.provider.Settings.System.getInt(context.getContentResolver(),"chatroom_exit",1);
	Log.i(LOG_TAG, "isChatroomExit="+isChatroomExit);
        /**
         * ·µ»Øµç»°×ŽÌ¬ 
         *
         * CALL_STATE_IDLE ÎÞÈÎºÎ×ŽÌ¬Ê±  
         * CALL_STATE_OFFHOOK œÓÆðµç»°Ê± 
         * CALL_STATE_RINGING µç»°œøÀŽÊ±  
         */
        if(tm.getCallState()!=TelephonyManager.CALL_STATE_IDLE||"true".equals(isIncall)|| "true".equals(isInvideo)){
		Log.i(LOG_TAG, "in call,no need notice,return");
		return;
	}
	else if(ChatUtil.isDisturb(context)){
		Log.i(LOG_TAG, "in silence mode,no need notice,return");
		return;
	}else if(myChatContol.ChatCurState!=ChatRoomControl.ChatStateIdle){
		Log.i(LOG_TAG, "chatroom is not in idle,no need notice,return");
		ChatVibratorControl.StartPlaying(context);
		return;
	}else if(isChatroomExit!=1){
		Log.i(LOG_TAG, "chatroom function is not exit,return");
		return;
	}

	boolean isOpen=false;
	if(XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){
	isOpen=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatroomMainNewActivity");
	}else{
	isOpen=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatroomMainActivity");
	}
       String recordingStr = android.provider.Settings.System.getString(context.getContentResolver(), "camera_isRecording");
	boolean isRecording=ChatUtil.isForeground(context,"com.xxun.camera.activity.CameraMainActivity");//false;
	boolean isScreenOn=checkScreenIsOn(context);
	boolean isInNotif=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatNotificationActivity");//;

	Log.i(LOG_TAG, "isScreenOn ="+ isScreenOn);
	//if("true".equals(recordingStr)) {
	//	isRecording=true;
	//}
	Log.i(LOG_TAG, "isRecording ="+ isRecording);
	Log.i(LOG_TAG, "isOpen ="+ isOpen);
        Log.i(LOG_TAG, "is_work ="+ ChatNotificationManager.is_work);
	Log.i(LOG_TAG, "isInNotif ="+ isInNotif);

	if(!isInNotif&&!isRecording&&!isOpen&&ChatNotificationManager.is_work){
		ChatNotificationManager.is_work=false;
	}

	if((!isScreenOn)&&isOpen){
		sendPlayRingBroadcast(context);
		ChatVibratorControl.StartPlaying(context);
	}
	else if(isOpen||isRecording){
	    ChatNotificationManager.addGroupByGID(context.getApplicationContext(), gid, chat_info);
	    sendCheckRefreshBroacast(context,gid,delCount);
	    ChatVibratorControl.StartPlaying(context);
	}
        else if(ChatNotificationManager.is_work) {
            ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, chat_info);
	    if(delCount<=0||!isDelNotice){
	    ChatVibratorControl.StartPlaying(context);
	    sendPlayRingInNotifBroadcast(context);
	    }
	if(!isChargeForbidden(context)){
		//sendNewChatMsgBroacast(context,nitice_info);
	     }
        }else {
            ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, chat_info);
            if(isFind){
		sendNewChatMsgBroacast(context,nitice_info);
		}
        }
	}else if((content_type==4||content_type==5)&&XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){//photo,video
   String eid=(String)pl.get(ChatKeyString.KEY_EID);
        Log.i(LOG_TAG, "eid ="+eid);
        chat_info.setListItemEID(eid);

        String key_str=(String)pl.get(ChatKeyString.KEY_NAME);
        Log.i(LOG_TAG, "key_str ="+key_str);
        int resault =ChatOfflineMsgManager.compareList(key_str);
        Log.i(LOG_TAG, "resault ="+resault);
        if(resault==ChatOfflineMsgManager.compare_resault_over){
		ChatOfflineMsgManager.setOfflineMsgFlag(false);
		ChatOfflineMsgManager.initOfflineManager();
        }
	else if(resault==ChatOfflineMsgManager.compare_resault_error){
		ChatOfflineMsgManager.setOfflineMsgFlag(false);
		ChatOfflineMsgManager.initOfflineManager();
	}

        ChatUtil.ChatUtilWriteEndkey(context.getApplicationContext(),key_str);
        String date=key_str.substring(40);
        Log.i(LOG_TAG, "date ="+date);
        chat_info.setmDate(date);

        String gid=key_str.substring(3,35);
        Log.i(LOG_TAG, "gid ="+gid);
        int delCount=ChatRecieveMsgManager.checkRecieveMsgGroup(context.getApplicationContext(),gid);
	Log.i(LOG_TAG, "delCount ="+delCount);
        chat_info.setListItemGID(gid);
        byte[] bytes=null;
	String file_path=null;
	String file_name=null;
		String dlKeyString =(String) pl.get(ChatKeyString.KEY_CONTENT);// new String(bytes);
           Log.i(LOG_TAG, "dlKeyString ="+dlKeyString);
		

	//if(content_type!=2){
        file_path=dlKeyString;//Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ date+".amr";
	//}
        Log.i(LOG_TAG, "file_path ="+file_path);
        chat_info.setFilePath(file_path);
		if(content_type==4){
			chat_info.setContentType(4);
			file_path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ date+".jpg";
			file_name=date+".jpg";
			Log.i(LOG_TAG, "file_path ="+file_path);
			chat_info.setFilePath(dlKeyString);
			/*DownloadManagerUtil dmu=DownloadManagerUtil.getInstance(context);
			dmu.downloadNoticeVideo(context,WatchSystemInfo.getWatchEID(context), dlKeyString,new OnDownloadListener(){
			@Override
			public void onSuccess(String s){
			Log.i(LOG_TAG, "photo download onSuccess");
			}

			@Override
			public void onFail(){
			Log.i(LOG_TAG, "photo download onFail");
			}
		 },Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/",file_name);*/
		}else if(content_type==5){
			chat_info.setContentType(5);
			file_path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ date+".jpg";
			file_name=date+".jpg";

			Log.i(LOG_TAG, "file_path ="+file_path);
			chat_info.setFilePath(dlKeyString);
/*
			DownloadManagerUtil dmu=DownloadManagerUtil.getInstance(context);
			dmu.downloadNoticeVideo(context,WatchSystemInfo.getWatchEID(context), dlKeyString,new OnDownloadListener(){
			@Override
			public void onSuccess(String s){
			Log.i(LOG_TAG, "video image download onSuccess");
			}

			@Override
			public void onFail(){
			Log.i(LOG_TAG, "video image download onFail");
			}
		 },Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/",file_name);

			String sourceKey=getSourceFileKey(dlKeyString);
			file_name=date+".mp4";
			dmu.downloadNoticeVideo(context,WatchSystemInfo.getWatchEID(context), sourceKey,new OnDownloadListener(){
			@Override
			public void onSuccess(String s){
			Log.i(LOG_TAG, "video source download onSuccess");
			}

			@Override
			public void onFail(){
			Log.i(LOG_TAG, "video source download onFail");
			}
		 },Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/",file_name);
*/
		}
        chat_info.setIsPlayed(0);
        chat_info.setListItemType(0);
	ChatNoticeInfo nitice_info=new ChatNoticeInfo();
	boolean isFind=perpareNoticeWindow(context,chat_info,nitice_info);
	startDownloadThread(chat_info);
        ChatListDB.getInstance(context.getApplicationContext()).addChatMsg(gid,chat_info);
	ChatContractManager.updateAllMissChatCount(context);
        //ListDte.add(chat_info);
        //refreshListView(ListDte.size()-1);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	String isIncall = "false";
	isIncall = android.provider.Settings.System.getString(context.getContentResolver(),"isIncall");
	String isInvideo = (android.provider.Settings.System.getString(context.getContentResolver(), "xun_video") == null)?"false":android.provider.Settings.System.getString(context.getContentResolver(), "xun_video");

	ChatRoomControl myChatContol=ChatRoomControl.getInstance();

	ChatUtil.pushStatsValue(context,ChatUtil.STATS_VOICE_RECEIVE_ROLLBACK_SUCCESS,1);
	int isChatroomExit= android.provider.Settings.System.getInt(context.getContentResolver(),"chatroom_exit",1);
	Log.i(LOG_TAG, "isChatroomExit="+isChatroomExit);
        /**
         * ·µ»Øµç»°×ŽÌ¬ 
         *
         * CALL_STATE_IDLE ÎÞÈÎºÎ×ŽÌ¬Ê±  
         * CALL_STATE_OFFHOOK œÓÆðµç»°Ê± 
         * CALL_STATE_RINGING µç»°œøÀŽÊ±  
         */
        if(tm.getCallState()!=TelephonyManager.CALL_STATE_IDLE||"true".equals(isIncall)|| "true".equals(isInvideo)){
		Log.i(LOG_TAG, "in call,no need notice,return");
		return;
	}
	else if(ChatUtil.isDisturb(context)){
		Log.i(LOG_TAG, "in silence mode,no need notice,return");
		return;
	}else if(myChatContol.ChatCurState!=ChatRoomControl.ChatStateIdle){
		Log.i(LOG_TAG, "chatroom is not in idle,no need notice,return");
		ChatVibratorControl.StartPlaying(context);
		return;
	}else if(isChatroomExit!=1){
		Log.i(LOG_TAG, "chatroom function is not exit,return");
		return;
	}

       	boolean isOpen=false;
	if(XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){
	isOpen=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatroomMainNewActivity");
	}else{
	isOpen=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatroomMainActivity");
	}
       String recordingStr = android.provider.Settings.System.getString(context.getContentResolver(), "camera_isRecording");
	boolean isRecording=ChatUtil.isForeground(context,"com.xxun.camera.activity.CameraMainActivity");//false;
	boolean isScreenOn=checkScreenIsOn(context);
	boolean isInNotif=ChatUtil.isForeground(context,"com.xxun.watch.xunchatroom.activity.ChatNotificationActivity");//;

	Log.i(LOG_TAG, "isScreenOn ="+ isScreenOn);
	//if("true".equals(recordingStr)) {
	//	isRecording=true;
	//}
	Log.i(LOG_TAG, "isRecording ="+ isRecording);
	Log.i(LOG_TAG, "isOpen ="+ isOpen);
        Log.i(LOG_TAG, "is_work ="+ ChatNotificationManager.is_work);
	Log.i(LOG_TAG, "isInNotif ="+ isInNotif);

	if(!isInNotif&&!isRecording&&!isOpen&&ChatNotificationManager.is_work){
		ChatNotificationManager.is_work=false;
	}

	if((!isScreenOn)&&isOpen){
		sendPlayRingBroadcast(context);
		ChatVibratorControl.StartPlaying(context);
	}
	else if(isOpen||isRecording){
	    ChatNotificationManager.addGroupByGID(context.getApplicationContext(), gid, chat_info);
	    sendCheckRefreshBroacast(context,gid,delCount);
	    ChatVibratorControl.StartPlaying(context);
	}
        else if(ChatNotificationManager.is_work) {
            ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, chat_info);
	    if(delCount<=0||!isDelNotice){
	    ChatVibratorControl.StartPlaying(context);
	    sendPlayRingInNotifBroadcast(context);
	    }
	if(!isChargeForbidden(context)){
		//sendNewChatMsgBroacast(context,nitice_info);
	     }
        }else {
            ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, chat_info);
            if(isFind){
		sendNewChatMsgBroacast(context,nitice_info);
		}
        }
	}
    }

 public static void sendCheckRefreshBroacast(Context context,String gid,int delCount){
	Log.i(LOG_TAG, "sendCheckRefreshBroacast,gid="+gid);
	Log.i(LOG_TAG, "sendCheckRefreshBroacast,delCount="+delCount);
	if(gid==null){
		return;
	}
        Intent it = new Intent("com.xxun.watch.checkrefreshbroadcast");
	it.putExtra("checkGID",gid);
	it.putExtra("delCount",delCount);
	it.setPackage("com.xxun.watch.xunchatroom");
        context.sendBroadcast(it);
    }

 public static void sendNewChatMsgBroacast(Context context,ChatNoticeInfo notif_info){
        Intent it = new Intent("com.broadcast.xxun.newMessage");
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatKeyString.KEY_NOTICE, notif_info);
        //it.setClass(this,ChatNotificationActivity.class);
        it.putExtras(bundle);
        it.setPackage("com.xxun.watch.xunchatroom");
        Log.i(LOG_TAG, "sendNewChatMsgBroacast");
        context.sendBroadcast(it);

        //add by liaoyi 18/12/13
        Intent intentStory = new Intent("com.broadcast.xxun.newMessage");
        intentStory.setPackage("com.xxun.watch.storydownloadservice");
        context.sendBroadcast(intentStory);
        //end

    }

 public static  boolean perpareNoticeWindow(Context context,ChatListItemInfo chat_info,ChatNoticeInfo nitice_info){
        String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
        String name;
        String number;
        Uri uri = Uri.parse ("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        Log.i(LOG_TAG,"perpareNoticeWindow");
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
		    chat_info.setContractItemAvatar(avatar);
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
                    if(gid!=null) {
                        if (chat_info.getListItemGID().equals(WatchSystemInfo.getWatchGID())) {
			//
				if(chat_info.getListItemEID().equals(eid)){
				img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
				Log.i("chat","set photoID img="+img);
				chat_info.setPhotoID(attri);
				is_find = true;
				}

                        } else if (gid.equals(chat_info.getListItemGID())) {
                            img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
			    chat_info.setPhotoID(attri);
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

            nitice_info.setmDate(chat_info.getmDate());
            nitice_info.setDuration(chat_info.getDuration());
            nitice_info.setFilePath(chat_info.getFilePath());
            nitice_info.setIsPlayed(chat_info.getIsPlayed());
            Log.i(LOG_TAG, "getContentType="+chat_info.getContentType());
            nitice_info.setContentType(chat_info.getContentType());
            nitice_info.setNoticeGID(chat_info.getListItemGID());
            nitice_info.setNoticeEID(chat_info.getListItemEID());
            nitice_info.setNoticeState(chat_info.getListItemState());
            nitice_info.setSN(chat_info.getSN());
            nitice_info.setNickName(nick_name);
            nitice_info.setPhotoID(attri);
	    nitice_info.setContractItemAvatar(avatar);
            //xun_enter_chatroom_notification_windows(nitice_info);
            //sendNewChatMsgBroacast(context,nitice_info);
        }
	return is_find;
    }


	public static   boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
}

