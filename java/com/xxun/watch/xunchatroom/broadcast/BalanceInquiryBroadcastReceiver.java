package com.xxun.watch.xunchatroom.broadcast;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.IntentFilter;
import android.net.Uri;
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
import com.xxun.watch.xunchatroom.control.ChatRoomControl;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import android.telephony.TelephonyManager;
import android.telephony.SmsManager;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.net.Uri;

public class BalanceInquiryBroadcastReceiver extends BroadcastReceiver {

    String LOG_TAG="BalanceInquiry";
    Context mContext=null;
    TelephonyManager telMgr=null;
    String mPlmn=null;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG,"BalanceInquiryBroadcastReceiver");
	mContext=context;
	/*if(intent.getAction().equals("SENT_SMS_ACTION")){
	Log.i(LOG_TAG,"send sms succ");
	}else*/{
	String recMsg=(String)intent.getStringExtra("content");
	 Log.i(LOG_TAG,"recMsg="+recMsg);
       telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);    
   	mPlmn=telMgr.getNetworkOperator();
	Log.i(LOG_TAG,"mPlmn="+mPlmn);
	JSONObject joMsgRec = (JSONObject) JSONValue.parse(recMsg);
	Object snObject=joMsgRec.get("SN");
	int sn=0;
	if(snObject!=null){
	 sn = (Integer) snObject;
	}
	int rc=handleBalanceInquiry(recMsg);
	ConstrctBalanceInquiryRspBuff(context,rc,sn);
	}
    }

public int handleBalanceInquiry(String content){
	int rc=0;
	Log.i(LOG_TAG,"handleBalanceInquiry,content="+content);
        if (telMgr.getSimState() != telMgr.SIM_STATE_READY) {    
            Log.i(LOG_TAG, "sim card not ready,return");  
	    return -1;  
        } 
	JSONObject joContent = (JSONObject) JSONValue.parse(content);
	JSONObject pl = (JSONObject) joContent.get("PL");
	JSONArray simArray=(JSONArray)pl.get("simarray");
	int arraySize=0;
	arraySize=simArray.size();
	Log.i(LOG_TAG, "arraySize="+arraySize); 
	int arrayIndex=0;
	for(arrayIndex=0;arrayIndex<arraySize;arrayIndex++){
		Log.i(LOG_TAG, "arrayIndex="+arrayIndex); 
		JSONObject simInfo=(JSONObject)simArray.get(arrayIndex);
		Log.i(LOG_TAG, "simInfo="+simInfo.toString());

		String plmnList=(String)simInfo.get("MCCMNC");
		String number=(String)simInfo.get("NO");
		String sms=(String)simInfo.get("SMS");
		Log.i(LOG_TAG, "number="+number); 
		Log.i(LOG_TAG, "plmnList.length()="+plmnList.length()); 
		Log.i(LOG_TAG, "sms="+sms); 
		Log.i(LOG_TAG, "sms="+sms); 
                int startIndex=0;
		int endIndex=0;
		int plmnIndex=0;
   	        for(plmnIndex=0;plmnIndex<plmnList.length();plmnIndex++){
			Log.i(LOG_TAG, "plmnIndex="+plmnIndex);
			char curStr=plmnList.charAt(plmnIndex);
			if(curStr==','){
			String curPlmn=plmnList.substring(startIndex,endIndex);
			Log.i(LOG_TAG, "curPlmn="+curPlmn);
			startIndex=plmnIndex+1;
			if(curPlmn.equals(mPlmn)){
				sendBalanceInquirySMS(number,sms);
				return 1;
			}
			}else if(plmnIndex==(plmnList.length()-1)){
			String curPlmn=plmnList.substring(startIndex,plmnIndex);
			Log.i(LOG_TAG, "curPlmn="+curPlmn);
			startIndex=plmnIndex+1;
			if(curPlmn.equals(mPlmn)){
				sendBalanceInquirySMS(number,sms);
				return 1;
			}
			}
			endIndex++;
		}
	}
	
	return -2;
}
 
public void sendBalanceInquirySMS(String number,String sms){
	Log.i(LOG_TAG,"sendBalanceInquirySMS,number="+number);
	Log.i(LOG_TAG,"sendBalanceInquirySMS,sms="+sms);
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();  
//////////////////////////////////////////////////////////////////////
String SENT_SMS_ACTION = "SENT_SMS_ACTION";
Intent sentIntent = new Intent(SENT_SMS_ACTION);
PendingIntent sendIntent= PendingIntent.getBroadcast(mContext, 0, sentIntent,
        0);
// register the Broadcast Receivers
/*mContext.registerReceiver(new BroadcastReceiver() {
    @Override
    public void onReceive(Context _context, Intent _intent) {
        switch (getResultCode()) {
        case Activity.RESULT_OK:
        Log.i(LOG_TAG,"send sms succ");

        break;
        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
        break;
        case SmsManager.RESULT_ERROR_RADIO_OFF:
        break;
        case SmsManager.RESULT_ERROR_NULL_PDU:
        break;
        }
    }
}, new IntentFilter(SENT_SMS_ACTION));*/
//mContext.registerReceiver(this, new IntentFilter(SENT_SMS_ACTION));
///////////////////////////////////////////////////////////////
       /* List<String> divideContents = smsManager.divideMessage(sms);   
        for (String text : divideContents) {    
            smsManager.sendTextMessage(number, null, text, null, null);    
        }  */
	//SmsManager smsManager=SmsManager.getDefault();
	if(smsManager!=null){
	List<String> divideContents = smsManager.divideMessage(sms);   
        for (String text : divideContents) {    
            smsManager.sendTextMessage(number, null, text, sendIntent, null);    
	    //smsManager.sendTextMessage("13918345010", null, text, null, null);   
        } 
	//smsManager.sendTextMessage(number, null, sms, null, null);  
	//smsManager.sendTextMessage("1", null, sms, null, null);  
	}else{
	Log.i(LOG_TAG,"sendBalanceInquirySMS,smsManager is null");
	}
         //Intent intent = new Intent(Intent.ACTION_SENDTO);
        
        //Uri data = Uri.parse("smsto:"+number);
       // intent.setData(data);
        
        //intent.putExtra("sms_body", sms);
        //mContext.startActivity(intent);
	//deleteSentSMS(mContext,number);
}

 public void deleteSentSMS( Context context,String number)
  {
	Log.i(LOG_TAG, "deleteSentSMS,number="+number);
        try
        {
            // 准备系统短信收信箱的uri地址
            Uri uri = Uri.parse("content://sms/sent");// 收信箱
            // 查询收信箱里所有的短信
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" ,"thread_id"};//"_id", "address", "person",, "date", "type
            String where = " address = '"+number+"'";
		Log.i(LOG_TAG, "where="+where);
            Cursor curs =    context.getContentResolver().query(uri, projection, where,null, "date desc");
            if(curs.moveToFirst())
            {

                do{
                    // String phone =
                    // isRead.getString(isRead.getColumnIndex("address")).trim();//获取发信人
                    String body =curs.getString(curs.getColumnIndex("body")).trim();// 获取信息内容
                    //if (body.contains(smscontent))
                    {
                        int id = curs.getInt(curs.getColumnIndex("_id"));
                        int resault=context.getContentResolver().delete(Uri.parse("content://sms/"),"_id=?",new String[]{ String.valueOf(id)});
			Log.i(LOG_TAG, "deleteSentSMS,id="+id+",resault="+resault);
                    }
                }while (curs.moveToNext());
            }
            curs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

class sendBalanceRspCallback extends IResponseDataCallBack.Stub{
	public void onSuccess(ResponseData responseData){
                Log.i(LOG_TAG,"balance inquiry msg send succ");
                Log.i(LOG_TAG,responseData.getResponseCode() + " " + responseData.getResponseData());
		
       }
       
       public void onError(int i, String s){
              Log.i(LOG_TAG,"bg msg send fail");
	      Log.i(LOG_TAG,"error i="+i);
	      Log.i(LOG_TAG,"error string="+s);
	      
       }
}

String ConstrctBalanceInquiryRspBuff(Context context,int rc,int sn){
        String sendBuff=null;
        String msg_key=null;
        JSONObject msg = new JSONObject();
        
	Log.i(LOG_TAG,"ConstrctBalanceInquiryRspBuff");
	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)context.getSystemService("xun.network.Service");
        msg.put(ChatKeyString.KEY_CID, CloudBridgeUtil.CID_E2E_UP);
        msg.put(ChatKeyString.KEY_SID, networkService.getSID());
	//int sn=ChatUtil.ChatUtilGetServiceSN(context);
        msg.put(ChatKeyString.KEY_SN, sn);
        msg.put(ChatKeyString.KEY_VERSION,CloudBridgeUtil.PROTOCOL_VERSION);
	msg.put(ChatKeyString.KEY_EID, networkService.getWatchEid());
        ArrayList teidArray=new ArrayList();
	teidArray.add(networkService.getWatchEid());
	msg.put("TEID", teidArray);
        JSONObject pl = new JSONObject();
        
        
        pl.put("RC", rc);
        pl.put("sub_action", 601);
	if(mPlmn!=null){
	pl.put("plmn", mPlmn);
	}else{
	pl.put("plmn", "");
	}
        msg.put(ChatKeyString.KEY_PL, pl);
        sendBuff= msg.toJSONString();

        Log.i(LOG_TAG,"sendBuff="+sendBuff);
	
	networkService.sendJsonMessage(sendBuff,new sendBalanceRspCallback());
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


}
