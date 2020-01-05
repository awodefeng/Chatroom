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

public class ChatSearchMsgBroadcastReceiver extends BroadcastReceiver {

    String LOG_TAG="msg broadcast search";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG,"ChatSearchMsgBroadcastReceiver");
        ChatNoticeInfo next_one= ChatNotificationManager.getNextNotifInfo(context);
        if(next_one!=null){
            Intent my_intent=new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ChatKeyString.KEY_NOTICE, next_one);
            my_intent.setClass(context,ChatNotificationActivity.class);
            my_intent.putExtras(bundle);
            my_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(my_intent);
        }
    }
}
