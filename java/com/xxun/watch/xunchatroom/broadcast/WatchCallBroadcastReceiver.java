package com.xxun.watch.xunchatroom.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.app.Service; 
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
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IMessageReceiveListener;
import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xxun.watch.xunchatroom.service.ChatroomService;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;
import android.os.Environment;

import android.telephony.PhoneStateListener; 
import android.telephony.TelephonyManager; 
public class WatchCallBroadcastReceiver extends BroadcastReceiver { 

String LOG_TAG="Call BroadcastReceiver";
Context mContext=null;
@Override 
public void onReceive(Context context, Intent intent) { 
	Log.i(LOG_TAG,"call,on receive");
	mContext=context;
	if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){ 
	String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER); 
	Log.i(LOG_TAG, "call OUT:" + phoneNumber); 
	}else{ 

	TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE); 
	tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE); 

	} 
} 
PhoneStateListener listener=new PhoneStateListener(){ 
@Override 
public void onCallStateChanged(int state, String incomingNumber) { 

super.onCallStateChanged(state, incomingNumber); 
	switch(state){ 
		case TelephonyManager.CALL_STATE_IDLE: 
		Log.i(LOG_TAG,"state:idle");
		break; 
		case TelephonyManager.CALL_STATE_OFFHOOK: 
		Log.i(LOG_TAG,"state:offhook");
		break; 
		case TelephonyManager.CALL_STATE_RINGING: 
		Log.i(LOG_TAG,"state:ring");
		if(mContext!=null){
			Intent it = new Intent("com.broadcast.xxun.watchCall");
			Log.i(LOG_TAG, "send call broadcast to chatroom");
			mContext.sendBroadcast(it);
		}
		break; 
	} 
} 
}; 


} 
