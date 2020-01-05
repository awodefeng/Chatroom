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

public class ChatBootupBroadcastReceiver extends BroadcastReceiver {

    String LOG_TAG="bootup broadcast";
    Context mContext=null;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG,"ChatBootupBroadcastReceiver");
	mContext=context;
	Intent my_intent=new Intent();
	my_intent.setClass(context,ChatroomService.class);
	context.startService(my_intent);
    }

}
