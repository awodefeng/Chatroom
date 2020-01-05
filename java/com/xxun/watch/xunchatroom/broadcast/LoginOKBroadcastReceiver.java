package com.xxun.watch.xunchatroom.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

public class LoginOKBroadcastReceiver extends BroadcastReceiver {

    String LOG_TAG="msg broadcast search";
    Context mContext=null;
    String chat_audio_path=null;//"chat_audio";
    String chat_audio_folder="chat_audio";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG,"LoginOKBroadcastReceiver");
	mContext=context;
	initWatchSysInfo(context);
	createAudioFileFolder();
	sendOfflineReqBroacast(context);
    }

public void createAudioFileFolder(){
	chat_audio_path=Environment.getExternalStorageDirectory().getAbsolutePath();
        chat_audio_path+="/"+chat_audio_folder;
        Log.i(LOG_TAG,"chat_audio_path = " + chat_audio_path);
        ChatUtil.ChatUtilCreateFolder(chat_audio_path);
}

public void initWatchSysInfo(Context context){
	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)context.getSystemService("xun.network.Service");
	WatchSystemInfo.setWatchGID(networkService.getWatchGid());
	WatchSystemInfo.setWatchEID(networkService.getWatchEid());
}

   private void sendOfflineReqBroacast(Context context){
        Intent it = new Intent("com.xxun.watch.sendofflinereqbroadcast");
        Log.i(LOG_TAG, "sendOfflineReqBroacast");
	it.setPackage("com.xxun.watch.xunchatroom");
        context.sendBroadcast(it);
    }



}
