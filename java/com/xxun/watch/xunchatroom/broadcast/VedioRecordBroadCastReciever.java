package com.xxun.watch.xunchatroom.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.xxun.watch.xunchatroom.activity.ChatNotificationActivity;
import com.xxun.watch.xunchatroom.control.ChatContractManager;
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
import java.util.ArrayList;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;

public class VedioRecordBroadCastReciever extends BroadcastReceiver {
    String LOG_TAG="vedio record";
    Context mContext=null;
    String chat_audio_path=null;//"chat_audio";
    String chat_audio_folder="chat_audio";
    ChatThreadTimer my_thread=null;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG,"VedioRecordBroadcastReciever,onReceive");
	mContext=context;
        String value=(String)intent.getStringExtra("value");
	Log.i(LOG_TAG,"VedioRecordBroadcastReciever,value="+value);
	sendSearchChatMsgBroacast(context);
/*
	if(value.equals("normal")){
	sendSearchChatMsgBroacast(context);
	}else if(value.equals("gallery")){
	         my_thread=new ChatThreadTimer(5000, new ChatThreadTimer.TimerInterface() {
                @Override
                public void doTimerOut() {
                    Log.i(LOG_TAG, "doTimerOut");
                        my_thread.stopThreadTimer();
			my_thread=null;
			sendSearchChatMsgBroacast(mContext);
                 
                }
            });

            my_thread.start();
	}
*/
    }

   public void sendSearchChatMsgBroacast(Context context){
        Intent it = new Intent("com.broadcast.xxun.searchMessage");
        it.setPackage("com.xxun.watch.xunchatroom");
        Log.i(LOG_TAG, "sendSearchChatMsgBroacast");
        context.sendBroadcast(it);
    }

}
