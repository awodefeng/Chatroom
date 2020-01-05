package com.xxun.watch.xunchatroom.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.xxun.watch.xunchatroom.activity.NewChatMsgAlertActivity;
import com.xxun.watch.xunchatroom.activity.ChatNotificationActivity;
import com.xxun.watch.xunchatroom.info.ChatNoticeInfo;
import com.xxun.watch.xunchatroom.util.ChatKeyString;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatNewMsgBroadcastReceiver extends BroadcastReceiver {
    private Context mContext;
    String LOG_TAG="chat msg broadcast rec";
    
    @Override
    public void onReceive(Context context, Intent intent) {
	mContext = context;
        Bundle bundle = intent.getExtras();
        ChatNoticeInfo notice_info = (ChatNoticeInfo)bundle.getSerializable(ChatKeyString.KEY_NOTICE);
        Log.d(LOG_TAG,"ChatNewMsgBroadcastReceiver");

        if(isChargeForbidden()){
            Intent newMsgAlertIntent = new Intent();
            newMsgAlertIntent.setClass(context, NewChatMsgAlertActivity.class);
            context.startActivity(newMsgAlertIntent);
        }else{
	    Log.d(LOG_TAG,"ChatNewMsgBroadcastReceiver,open notice window");
            Intent my_intent=new Intent();
            //Bundle new_bundle = new Bundle();
            bundle.putSerializable(ChatKeyString.KEY_NOTICE, notice_info);
            my_intent.setClass(context,ChatNotificationActivity.class);
            my_intent.putExtras(bundle);
            my_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(my_intent);
        }
        
    }

    /**
     * @author lihaizhou
     * @createtime 2018.06.22
     * @describe 判断当前是否处于充电禁用状态
     */
    private boolean isChargeForbidden() {
        try{
            if (SystemProperties.get("ro.build.type").equals("user") 
                && !"true".equals(Settings.System.getString(mContext.getContentResolver(), "isMidtest")) 
                && Settings.System.getInt(mContext.getContentResolver(),"is_localprop_exist") == 0
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
}
