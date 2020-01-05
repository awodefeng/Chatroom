package com.xxun.watch.xunchatroom.broadcast;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

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

import com.xxun.watch.xunchatroom.R;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IMessageReceiveListener;
import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xxun.watch.xunchatroom.service.ChatroomService;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;

import android.os.Environment;

import com.xxun.watch.xunchatroom.control.ChatRoomControl;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;

public class BgRecordBroadcastReceiver extends BroadcastReceiver {

    String LOG_TAG = "BgRecord";
    Context mContext = null;
    String bgAudioPath = null;//"chat_audio";
    String chat_audio_folder = "chat_audio";
    private ChatRoomControl bgRecordCtrl;
    private ChatThreadTimer myThread;
    private final int bgTime = 11;//10s

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "BgRecordBroadcastReceiver");
        mContext = context;
        String mAction = intent.getAction();
        Log.i(LOG_TAG, "mAction=" + mAction);
        if (mAction.equals("com.xunchatroom.bgRecord")) {
            // xxun liuluyang 2019/10/25 start
            releaseWakeLock();
            setScreenOn();
            showDialog();
            // xxun liuluyang 2019/10/25 end
            handleBgRecord(context);
        } else if (mAction.equals("com.xunchatroom.stopBgRecord")) {
            if (myThread != null) {
                myThread.stopThreadTimer();
                myThread = null;
                //bgRecordCtrl.StopRecord();
                ConstrctBgMsgSendBuff(mContext);
            }
        }
    }

    public void handleBgRecord(Context context) {
        Log.i(LOG_TAG, "handleBgRecord");
        bgRecordCtrl = ChatRoomControl.getInstance();
        Log.i(LOG_TAG, "ctrl cur state=" + bgRecordCtrl.ChatCurState);
        if (bgRecordCtrl.ChatCurState != ChatRoomControl.ChatStateIdle) {
            Log.i(LOG_TAG, "state is not at idle,return");
            return;
        }

        bgAudioPath = bgRecordCtrl.ChatPrepareRecord();
        Log.i(LOG_TAG, "bgAudioPath=" + bgAudioPath);
        int recordStatus = bgRecordCtrl.StartRecord(bgAudioPath);
        Log.i(LOG_TAG, "recordStatus=" + recordStatus);
        if (recordStatus == 3) {
            Log.i(LOG_TAG, "record error,return");
            return;
        }
        myThread = new ChatThreadTimer(bgTime * 1000, new ChatThreadTimer.TimerInterface() {
            @Override
            public void doTimerOut() {
                Log.i(LOG_TAG, "doTimerOut");
                myThread.stopThreadTimer();
                myThread = null;
                bgRecordCtrl.StopRecord();
                ConstrctBgMsgSendBuff(mContext);
                // xxun liuluyang 2019/10/25 start
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
                // xxun liuluyang 2019/10/25 end
            }
        });

        myThread.start();
    }

    class sendBgMsgCallback extends IResponseDataCallBack.Stub {
        public void onSuccess(ResponseData responseData) {
            Log.i(LOG_TAG, "bg msg send succ");
            Log.i(LOG_TAG, responseData.getResponseCode() + " " + responseData.getResponseData());
            ChatUtil.ChatUtilDeleteFile(bgAudioPath);
        }

        public void onError(int i, String s) {
            Log.i(LOG_TAG, "bg msg send fail");
            Log.i(LOG_TAG, "error i=" + i);
            Log.i(LOG_TAG, "error string=" + s);
            ChatUtil.ChatUtilDeleteFile(bgAudioPath);
        }
    }

    String ConstrctBgMsgSendBuff(Context context) {
        String sendBuff = null;
        String msg_key = null;
        JSONObject msg = new JSONObject();
        String audio_str = null;
        int duration = ChatUtil.getAudioDurationByPath(bgAudioPath);
        XiaoXunNetworkManager networkService = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
        msg.put(ChatKeyString.KEY_CID, CloudBridgeUtil.CID_SEND_BYTE_MESSAGE);
        msg.put(ChatKeyString.KEY_SID, networkService.getSID());
        int sn = ChatUtil.ChatUtilGetServiceSN(context);
        msg.put(ChatKeyString.KEY_SN, sn);
        msg.put(ChatKeyString.KEY_VERSION, CloudBridgeUtil.PROTOCOL_VERSION);
        JSONObject pl = new JSONObject();
        JSONObject value = new JSONObject();
        value.put(ChatKeyString.KEY_EID, networkService.getWatchEid());
        value.put(ChatKeyString.KEY_TYPE, "record");
        value.put(ChatKeyString.KEY_DURATION, duration / 1000);
        byte[] encodedData = ChatUtil.ChatUtilReadDataFromFile(bgAudioPath);
        //byte[] encodedData = audio_str.getBytes();
        String audio_send = Base64.encodeToString(encodedData, Base64.NO_WRAP);
        value.put(ChatKeyString.KEY_CONTENT, audio_send);
        pl.put(ChatKeyString.KEY_VALUE, value);
        pl.put(ChatKeyString.KEY_TGID, WatchSystemInfo.getWatchGID());
        msg_key = "GP/" + WatchSystemInfo.getWatchGID() + "/MSG/#TIME#";
        pl.put(ChatKeyString.KEY_NAME, msg_key);
        msg.put(ChatKeyString.KEY_PL, pl);
        sendBuff = msg.toJSONString();

        Log.i("LOG_TAG", "sendBuff=" + sendBuff);

        networkService.sendJsonMessage(sendBuff, new sendBgMsgCallback());
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
    // xxun liuluyang 2019/10/25 start
    private PowerManager.WakeLock wakeLock;
    private PowerManager powerManager = null;

    private void setScreenOn() {
        if (powerManager == null) {
            powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        }
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "recordBG");
        wakeLock.acquire(10000);
    }

    private void releaseWakeLock() {
        if (wakeLock == null) {
            return;
        }
        try {
            wakeLock.release();
            wakeLock = null;
        } catch (Exception e) {
            wakeLock = null;
        }
    }

    private Dialog mAlertDialog;
    private void showDialog(){
        if(mAlertDialog == null){
            mAlertDialog = new Dialog(mContext, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
            View view = LayoutInflater.from(mContext).inflate(R.layout.bg_record_tips, null);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mAlertDialog.dismiss();
                    mAlertDialog= null;
                    releaseWakeLock();
                    return false;
                }
            });
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mAlertDialog.setContentView(view);
            mAlertDialog.show();
        }else{
            mAlertDialog.dismiss();
            mAlertDialog.show();
        }
    }
    // xxun liuluyang 2019/10/25 end

}
