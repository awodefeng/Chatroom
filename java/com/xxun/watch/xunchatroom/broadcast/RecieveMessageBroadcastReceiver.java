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
import com.xxun.watch.xunchatroom.service.ChatroomService;

public class RecieveMessageBroadcastReceiver extends BroadcastReceiver {

    String LOG_TAG="msg broadcast search";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG,"RecieveMessageBroadcastReceiver");

	String mData=(String)intent.getStringExtra("data");

	JSONObject pl = (JSONObject)JSONValue.parse(mData);
	if(ChatOfflineMsgManager.getOfflineMsgFlag()){
		//ChatRecieveMsgManager.ParseOfflineList(context,pl);
		Log.i(LOG_TAG,"it is in pull offline msg duration");
	}
	else{
		ChatRecieveMsgManager.RecieveMessage(context,pl);
	}

    }


}
