package com.xxun.watch.xunchatroom.SMS;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IMessageReceiveListener;
import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.control.ChatNotificationManager;
import com.xxun.watch.xunchatroom.control.ChatRecieveMsgManager;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.info.ChatNoticeInfo;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import com.xxun.watch.xunchatroom.util.ChatUtil;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;
import com.xxun.watch.xunchatroom.control.ChatContractManager;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import android.telephony.TelephonyManager;
import com.xxun.watch.xunchatroom.control.ChatRoomControl;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import android.util.Base64;
import java.io.UnsupportedEncodingException;
import com.xxun.watch.xunchatroom.control.ChatVibratorControl;
import  com.xxun.watch.xunchatroom.util.SmsWriteOpUtil;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.XiaoXunUtil;

public class SMSReciever extends BroadcastReceiver {

    String LOG_TAG="sms";
    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder strBody = new StringBuilder();
	String smsBody=null;
	String smsPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ "baseSms.amr";
        // 短信时间
        String strTime = new String();
        // 短信发件人

        Log.i(LOG_TAG,"sms,onReceive");
        StringBuilder number = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] _pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] message = new SmsMessage[_pdus.length];

            for (int i = 0; i < _pdus.length; i++) {

                message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
            }
            for (SmsMessage currentMessage : message) {
		Log.i(LOG_TAG,"sms,get content");
                strBody.append(currentMessage.getDisplayMessageBody());
		if(number.toString().length()<=0){
                number.append(currentMessage.getDisplayOriginatingAddress());
		}
                strTime = formatter.format(new Date(currentMessage.getTimestampMillis()));

            }
            smsBody = strBody.toString();
            String smsNumber = number.toString();
            Log.i(LOG_TAG,"smsNumber="+smsNumber);
            String realNumber= ChatUtil.getRealPhoneNumber(smsNumber);
            Log.i(LOG_TAG,"realNumber="+realNumber);

	 	if(!ChatUtil.checkRomAvailableSize()){
			Log.i(LOG_TAG, "recieve sms,flash size is full,return");
			deleteSMS(context,smsNumber);
            		this.abortBroadcast();
			return;
		}
		OfflineSms offlineSms=OfflineSms.getInstance(context);
		if(offlineSms.JudgementWakeupSms(smsBody)){
			sendOfflineSmsBroadcast(context);
			Log.i(LOG_TAG, "recieve sms,is offline sms,return");
			deleteSMS(context,smsNumber);
            		this.abortBroadcast();
			return;
		}

	    Log.i(LOG_TAG, "smsPath="+smsPath);
	    ChatUtil.ChatUtilDeleteFile(smsPath);
	    ChatUtil.ChatUtilCreateFile(smsPath);
            ChatUtil.ChatUtilSaveDataToFile(smsBody.getBytes(),smsPath);
	    sendSMSToApp(context,smsNumber,smsPath);
	    ChatUtil.ChatUtilDeleteFile(smsPath);
	    if(handleSmsSettings(context,realNumber)){
	    
            //if()
            Log.i(LOG_TAG,"strTime="+strTime);
            Log.i(LOG_TAG,"smsBody="+smsBody);
            //context.getApplicationContext();
            // 取消消息
            ChatListItemInfo new_msg=new ChatListItemInfo();
            String gid=findGidBynumber(context,realNumber,new_msg);
            if(gid==null) {
                gid = WatchSystemInfo.getSmsGID();
            }
            new_msg.setListItemGID(gid);
            new_msg.setIsPlayed(0);
            new_msg.setListItemState(0);
            new_msg.setListItemType(0);
            new_msg.setDuration(0);
            new_msg.setListItemEID(realNumber);
            new_msg.setContentType(3);
            String date=ChatUtil.ChatUtilGetBackDate();
            new_msg.setmDate(date);
            new_msg.setSN(ChatUtil.ChatUtilGetSN());
            String file_path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ date+".amr";
            Log.i(LOG_TAG, "file_path ="+file_path);
            new_msg.setFilePath(file_path);

            ChatUtil.ChatUtilCreateFile(file_path);
            ChatUtil.ChatUtilSaveDataToFile(smsBody.getBytes(),file_path);

            int delCount=ChatRecieveMsgManager.checkRecieveMsgGroup(context.getApplicationContext(),gid);
	    Log.i(LOG_TAG, "delCount ="+delCount);
            ChatListDB.getInstance(context.getApplicationContext()).addChatMsg(gid,new_msg);
	ChatContractManager.updateAllMissChatCount(context);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
	String isIncall = "false";
	isIncall = android.provider.Settings.System.getString(context.getContentResolver(),"isIncall");
	String isInvideo = (android.provider.Settings.System.getString(context.getContentResolver(), "xun_video") == null)?"false":android.provider.Settings.System.getString(context.getContentResolver(), "xun_video");
	ChatRoomControl myChatContol=ChatRoomControl.getInstance(); 
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
		this.abortBroadcast();
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
	boolean isRecording=ChatUtil.isForeground(context,"com.xxun.camera.activity.CameraMainActivity");//;
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
	    ChatNotificationManager.addGroupByGID(context.getApplicationContext(), gid, new_msg);
	    sendCheckRefreshBroacast(context,gid,delCount);
	    ChatVibratorControl.StartPlaying(context);
		}else if(ChatNotificationManager.is_work) {
                ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, new_msg);
		if(delCount<=0||!ChatRecieveMsgManager.isDelNotice){
		ChatVibratorControl.StartPlaying(context);
	        sendPlayRingInNotifBroadcast(context);
		}
		if(!isChargeForbidden(context)){
		//ChatNoticeInfo new_notif= ChatNotificationManager.ConstructNoticeinfo(context,new_msg);
		//sendNewSmsBroacast(context, new_notif);
	     	}
            }else {
                ChatNotificationManager.updateGroupByGID(context.getApplicationContext(), gid, new_msg);
                //perpareNoticeWindow(chat_info);
               ChatNoticeInfo new_notif= ChatNotificationManager.ConstructNoticeinfo(context,new_msg);
                if(new_notif==null){
                    Log.i(LOG_TAG, "new_n1=otif is null");
                }else {
                    sendNewSmsBroacast(context, new_notif);
                }
            }
		
	}	
		deleteSMS(context,smsNumber);
		//deleteRawSms(context);
            //new_msg.s
            this.abortBroadcast();

        }

    }

    private boolean isChargeForbidden(Context context) {
        try{
            if (SystemProperties.get("ro.build.type").equals("user") 
                && !"true".equals(Settings.System.getString(context.getContentResolver(), "isMidtest")) 
                && Settings.System.getInt(context.getContentResolver(),"is_localprop_exist") == 0
                &&  "true".equals(SystemProperties.get("persist.sys.isUsbConfigured"))
                ) {
                return true; 
            }
        }catch (SettingNotFoundException e){
            e.printStackTrace();
        }

        return false; 
    }

  public void sendPlayRingInNotifBroadcast(Context context){
	Log.i(LOG_TAG, "sendPlayRingInNotifBroadcast");

        Intent it = new Intent("com.xxun.watch.playringbroadcast");
it.setPackage("com.xxun.watch.xunchatroom");
        context.sendBroadcast(it);
    }

  public void sendOfflineSmsBroadcast(Context context){
	Log.i(LOG_TAG, "sendOfflineSmsBroadcast");
	Intent exitIntent = new Intent();
	exitIntent.setAction("com.xunlauncher.exitoffline");
	exitIntent.putExtra("exitOfflineType","message");
	exitIntent.setPackage("com.xxun.watch.xunchatroom");
	context.sendBroadcast(exitIntent);
    }

private boolean checkScreenIsOn(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean ifOpen = powerManager.isScreenOn();
	Log.i(LOG_TAG, "checkScreenIsOn,ifOpen="+ifOpen);
	return ifOpen;
}
       
private void sendPlayRingBroadcast(Context context){
	Log.i(LOG_TAG, "sendPlayRingBroadcast");	
       Intent it = new Intent("com.xunlauncher.playring");
        Log.i(LOG_TAG, "sendPlayRingBroadcast");
	it.setPackage("com.xxun.watch.xunchatroom");
        context.sendBroadcast(it);
}

private boolean handleSmsSettings(Context context,String number){
	boolean isSave=false;
	int smsType = android.provider.Settings.System.getInt(context.getApplicationContext().getContentResolver(), "sms_filter",1);
	Log.i(LOG_TAG,"handleSmsSettings,smsType="+smsType);

	switch(smsType){
		case 0:
		isSave=false;
		break;

		case 1:
		isSave=isContractNumber(context,number);
		break;

		case 2:
		isSave=true;
		break;
	}
	Log.i(LOG_TAG,"handleSmsSettings,isSave="+isSave);
	return isSave;
}

private boolean isContractNumber(Context context,String number){
	boolean isFind=false;
	String[] project = new String[]{"mimetype", "data1", "data2", "data3", "data4", "data5", "data6", "data7", "data8", "data9", "data10", "data11", "data12", "data13", "data14", "data15"};
        String name;
        String contract_number;
	String contract_number2;
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        Log.i(LOG_TAG, "isContractNumber");
        String nick_name = null;
        String gid = null;
        int img = R.mipmap.photo_test;

        while (cursor.moveToNext() && !isFind) {

            int contactsId = cursor.getInt(0);
            Log.i(LOG_TAG, "contactsId = " + contactsId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
            Cursor dataCursor = resolver.query(uri, project, null, null, null);
            //SyncArrayBean  arrayBean = new SyncArrayBean();
            while (dataCursor.moveToNext()) {
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(type)) {
                    name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
                    Log.i(LOG_TAG, "name = " + name);
                    nick_name = name;
                } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
                } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                    contract_number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                    Log.i(LOG_TAG, "contract_number = " + contract_number);
                    contract_number2 = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
                    Log.i(LOG_TAG, "contract_number2 = " + contract_number2);
		    if(contract_number!=null){
		            if (contract_number.equals(number)) {
		                isFind = true;
		            }
			}

		    if(contract_number2!=null){
		            if (contract_number2.equals(number)) {
		                isFind = true;
		            }
		 	}
                } else if ("vnd.android.cursor.item/nickname".equals(type)) {

                    int attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));
                    gid = dataCursor.getString(dataCursor.getColumnIndex(project[5]));
                    Log.i(LOG_TAG, "gid = " + gid);
		             img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
			    //chatInfo.setPhotoID(img);
                }
            }

            if(isFind){
                break;
            }
            //if(arrayBean != null) mlist_arraybean.add(arrayBean);
        }
        cursor.close();
        
	Log.i(LOG_TAG,"isContractNumber,isFind="+isFind);
	return isFind;
}

class sendSMSCallback extends IResponseDataCallBack.Stub{
	public void onSuccess(ResponseData responseData){
                Log.i(LOG_TAG,"sms send succ");
                Log.i(LOG_TAG,responseData.getResponseCode() + " " + responseData.getResponseData());
		//ChatUtil.ChatUtilDeleteFile(bgAudioPath);
       }
       
       public void onError(int i, String s){
              Log.i(LOG_TAG,"sms send fail");
	      Log.i(LOG_TAG,"error i="+i);
	      Log.i(LOG_TAG,"error string="+s);
	      ///ChatUtil.ChatUtilDeleteFile(bgAudioPath);
       }
}

private void sendSMSToApp(Context context,String number,String filePath){
        String sendBuff=null;
        String msg_key=null;
        JSONObject msg = new JSONObject();
        String audio_str=null;

	Log.i(LOG_TAG,"sendSMSToApp");

	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)context.getSystemService("xun.network.Service");
        msg.put(ChatKeyString.KEY_CID, CloudBridgeUtil.CID_UPLOAD_NOTICE);
        msg.put(ChatKeyString.KEY_SID, networkService.getSID());
	int sn=ChatUtil.ChatUtilGetServiceSN(context);
        msg.put(ChatKeyString.KEY_SN, sn);
        msg.put(ChatKeyString.KEY_VERSION,CloudBridgeUtil.PROTOCOL_VERSION);
        JSONObject pl = new JSONObject();
        JSONObject value = new JSONObject();
        value.put(ChatKeyString.KEY_EID, networkService.getWatchEid());
        value.put(ChatKeyString.KEY_TYPE, "sms");
        value.put(ChatKeyString.KEY_DURATION, 100);
        byte[] readdData=ChatUtil.ChatUtilReadDataFromFile(filePath);
	String smsText=new String(readdData);
	String smsContent=number+" \n"+smsText;//"高兴，测试我偌莫，高高兴兴";
	Log.i("LOG_TAG","smsContent="+smsContent);
        //String fContent=new String(smsContent.getBytes("UTF-8"));
	//Log.i("LOG_TAG","fContent="+fContent);
	byte[] encodedData=null;
        try {
		//encodedData=fContent.getBytes("UTF-16");
            encodedData=smsContent.getBytes("UTF-16");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] sendData=null;
	Log.i("LOG_TAG","encodedData="+encodedData);
	Log.i("LOG_TAG","length="+encodedData.length);
	int index=0;
	for(index=0;index<encodedData.length;index++){
		Log.i("LOG_TAG","index="+index+",data="+encodedData[index]);
	}
	String smsUnicode=ChatUtil.Byte2UnicodeChange(encodedData,0,encodedData.length);
	Log.i("LOG_TAG","smsUnicode="+smsUnicode);
        try {
            sendData=smsUnicode.getBytes("Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
	/*byte[] sendData=null;
        try {
            sendData=smsContent.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
	index=0;
	for(index=0;index<sendData.length;index++){
		Log.i("LOG_TAG","index="+index+",data1="+sendData[index]);
	}
	byte[] changeData=null;
	changeData=ChatUtil.ByteChange(sendData,0,sendData.length);
	index=0;
	for(index=0;index<changeData.length;index++){
		Log.i("LOG_TAG","index="+index+",data2="+changeData[index]);
	}
        String smsSend = Base64.encodeToString(changeData, Base64.DEFAULT);
	Log.i("LOG_TAG","smsSend="+smsSend);
        byte[] smsRec=Base64.decode(smsSend.getBytes(),Base64.DEFAULT);
	Log.i("LOG_TAG","smsRec="+new String(smsRec));
        value.put(ChatKeyString.KEY_CONTENT, smsSend);
        pl.put(ChatKeyString.KEY_VALUE, value);
        pl.put(ChatKeyString.KEY_TGID,  networkService.getWatchGid());
        msg_key="GP/"+networkService.getWatchGid()+"/MSG/#TIME#";
        pl.put(ChatKeyString.KEY_NAME,  msg_key);
        msg.put(ChatKeyString.KEY_PL, pl);
        sendBuff= msg.toJSONString();

        Log.i("LOG_TAG","sendBuff="+sendBuff);
	
	networkService.sendJsonMessage(sendBuff,new sendSMSCallback());
  }
/*
private void sendSMSToApp(Context context,String number,String filePath){
        String sendBuff=null;
        String msg_key=null;
        JSONObject msg = new JSONObject();
        String audio_str=null;

	Log.i(LOG_TAG,"sendSMSToApp");

	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)context.getSystemService("xun.network.Service");
        msg.put(ChatKeyString.KEY_CID, CloudBridgeUtil.CID_UPLOAD_NOTICE);
        msg.put(ChatKeyString.KEY_SID, networkService.getSID());
	int sn=ChatUtil.ChatUtilGetServiceSN(context);
        msg.put(ChatKeyString.KEY_SN, sn);
        msg.put(ChatKeyString.KEY_VERSION,CloudBridgeUtil.PROTOCOL_VERSION);
        JSONObject pl = new JSONObject();
        JSONObject value = new JSONObject();
        value.put(ChatKeyString.KEY_EID, networkService.getWatchEid());
        value.put(ChatKeyString.KEY_TYPE, "sms");
        value.put(ChatKeyString.KEY_DURATION, 100);
        byte[] readdData=ChatUtil.ChatUtilReadDataFromFile(filePath);
	String smsText=new String(readdData);
	String smsContent=number+" \n"+smsText;
	Log.i("LOG_TAG","smsContent="+smsContent);
	byte[] encodedData=null;
        try {
            encodedData=smsContent.getBytes("utf-16");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] sendData=null;
	String smsUnicode=ChatUtil.Byte2Unicode(encodedData,0,encodedData.length);
	Log.i("LOG_TAG","smsUnicode="+smsUnicode);
        try {
            sendData=smsUnicode.getBytes("Unicode");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String smsSend = Base64.encodeToString(sendData, Base64.DEFAULT);
        value.put(ChatKeyString.KEY_CONTENT, smsSend);
        pl.put(ChatKeyString.KEY_VALUE, value);
        pl.put(ChatKeyString.KEY_TGID,  networkService.getWatchGid());
        msg_key="GP/"+networkService.getWatchGid()+"/MSG/#TIME#";
        pl.put(ChatKeyString.KEY_NAME,  msg_key);
        msg.put(ChatKeyString.KEY_PL, pl);
        sendBuff= msg.toJSONString();

        Log.i("LOG_TAG","sendBuff="+sendBuff);
	
	networkService.sendJsonMessage(sendBuff,new sendSMSCallback());
  }
*/
 public void deleteRawSms( Context context){
	Log.i(LOG_TAG, "deleteRawSms,number");
if (!SmsWriteOpUtil.isWriteEnabled(context.getApplicationContext())) {
            SmsWriteOpUtil.setWriteEnabled(
                    context.getApplicationContext(), true);
}
        try
        {
            // 准备系统短信收信箱的uri地址
            Uri uri = Uri.parse("content://sms/raw");// 收信箱
            // 查询收信箱里所有的短信
            String[] projection = new String[] { "_id"};//"_id", "address", "person",, "date", "type
            String where = " _id >= '0'";
		Log.i(LOG_TAG, "where="+where);
            Cursor curs =    context.getContentResolver().query(uri, projection, where,null, "date desc");
            if(curs.moveToFirst())
            {

                do{
                    // String phone =
                    // isRead.getString(isRead.getColumnIndex("address")).trim();//获取发信人
                    //String body =curs.getString(curs.getColumnIndex("body")).trim();// 获取信息内容
                    //if (body.contains(smscontent))
                    {
                        int id = curs.getInt(curs.getColumnIndex("_id"));
                        int resault=context.getContentResolver().delete(Uri.parse("content://sms/"),"_id=?",new String[]{ String.valueOf(id)});
			Log.i(LOG_TAG, "deleteRawSms,id="+id+",resault="+resault);
                    }
                }while (curs.moveToNext());
            }else{
		Log.i(LOG_TAG, "curs is null");
		}
            curs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
}

 public void deleteSMS( Context context,String number)
  {
	Log.i(LOG_TAG, "deleteSMS,number="+number);
if (!SmsWriteOpUtil.isWriteEnabled(context.getApplicationContext())) {
            SmsWriteOpUtil.setWriteEnabled(
                    context.getApplicationContext(), true);
}
        try
        {
            // 准备系统短信收信箱的uri地址
            Uri uri = Uri.parse("content://sms/inbox");// 收信箱
            // 查询收信箱里所有的短信
            String[] projection = new String[] { "_id"};//"_id", "address", "person",, "date", "type
            String where = " _id >= '0'";
		Log.i(LOG_TAG, "where="+where);
            Cursor curs =    context.getContentResolver().query(uri, projection, where,null, "date desc");
            if(curs.moveToFirst())
            {

                do{
                    // String phone =
                    // isRead.getString(isRead.getColumnIndex("address")).trim();//获取发信人
                    //String body =curs.getString(curs.getColumnIndex("body")).trim();// 获取信息内容
                    //if (body.contains(smscontent))
                    {
                        int id = curs.getInt(curs.getColumnIndex("_id"));
                        int resault=context.getContentResolver().delete(Uri.parse("content://sms/"),"_id=?",new String[]{ String.valueOf(id)});
			Log.i(LOG_TAG, "deleteSMS,id="+id+",resault="+resault);
                    }
                }while (curs.moveToNext());
            }else{
		Log.i(LOG_TAG, "curs is null");
		}
            curs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    String findGidBynumber(Context context,String number,ChatListItemInfo chatInfo) {
        String[] project = new String[]{"mimetype", "data1", "data2", "data3", "data4", "data5", "data6", "data7", "data8", "data9", "data10", "data11", "data12", "data13", "data14", "data15"};
        String name;
        String contract_number;
	String contract_number2;
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        Log.i(LOG_TAG, "findGidBynumber");
        boolean is_find = false;
        String nick_name = null;
        String gid = null;
        int img = R.mipmap.photo_test;
	String avatar=null;

        while (cursor.moveToNext() && !is_find) {

            int contactsId = cursor.getInt(0);
            Log.i(LOG_TAG, "contactsId = " + contactsId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
            Cursor dataCursor = resolver.query(uri, project, null, null, null);
            //SyncArrayBean  arrayBean = new SyncArrayBean();
            while (dataCursor.moveToNext()) {
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(type)) {
                    name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
		    avatar = dataCursor.getString(dataCursor.getColumnIndex(project[3]));
                    Log.i(LOG_TAG, "name = " + name);
		    Log.i(LOG_TAG, "avatar = " + avatar);
                    nick_name = name;
		    chatInfo.setContractItemAvatar(avatar);
                } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
                } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                    contract_number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                    Log.i(LOG_TAG, "contract_number = " + contract_number);
                    contract_number2 = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
                    Log.i(LOG_TAG, "contract_number2 = " + contract_number2);
		    if(contract_number!=null){
			if (contract_number.equals(number)) {
				is_find = true;
			 }	
			}

		    if(contract_number2!=null){
		           if (contract_number2.equals(number)) {
		                is_find = true;
		            }
			}
      
                } else if ("vnd.android.cursor.item/nickname".equals(type)) {

                    int attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));
                    gid = dataCursor.getString(dataCursor.getColumnIndex(project[5]));
                    Log.i(LOG_TAG, "gid = " + gid);
		             //img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
			    chatInfo.setPhotoID(attri);
                }
            }

            if(is_find){
                break;
            }
            //if(arrayBean != null) mlist_arraybean.add(arrayBean);
        }
        cursor.close();
        Log.i(LOG_TAG, "is_find=" + is_find);

        if(is_find) {
        return gid;
        }

        return null;
    }

  private  void sendNewSmsBroacast(Context context,ChatNoticeInfo notif_info){
        Intent it = new Intent("com.broadcast.xxun.newMessage");
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatKeyString.KEY_NOTICE, notif_info);
        //it.setClass(this,ChatNotificationActivity.class);
        it.setPackage("com.xxun.watch.xunchatroom");
        it.putExtras(bundle);
        Log.i(LOG_TAG, "sendNewSmsBroacast");
        context.sendBroadcast(it);

      //add by liaoyi 18/12/13
      Intent intentStory = new Intent("com.broadcast.xxun.newMessage");
      intentStory.setPackage("com.xxun.watch.storydownloadservice");
      context.sendBroadcast(intentStory);
      //end
    }

 private void sendCheckRefreshBroacast(Context context,String gid,int delCount){
	Log.i(LOG_TAG, "sendCheckRefreshBroacast,gid="+gid);
	Log.i(LOG_TAG, "sendCheckRefreshBroacast,delCount="+delCount);
	if(gid==null){
		return;
	}
        Intent it = new Intent("com.xxun.watch.checkrefreshbroadcast");
	it.setPackage("com.xxun.watch.xunchatroom");
	it.putExtra("checkGID",gid);
	it.putExtra("delCount",delCount);
        context.sendBroadcast(it);
    }
}
