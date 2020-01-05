package com.xxun.watch.xunchatroom.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IMessageReceiveListener;
import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xxun.watch.xunchatroom.control.ChatContractManager;
import com.xxun.watch.xunchatroom.info.ContractListItemInfo;
import com.xxun.watch.xunchatroom.util.ChatUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import com.xxun.watch.xunchatroom.control.ChatNotificationManager;
import com.xxun.watch.xunchatroom.control.ChatOfflineMsgManager;
import com.xxun.watch.xunchatroom.control.ChatRecieveMsgManager;
import com.xxun.watch.xunchatroom.control.ChatRoomControl;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import java.util.ArrayList;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import android.app.Notification;
import com.xxun.watch.xunchatroom.R;
import android.telephony.SmsManager;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;
import  com.xxun.watch.xunchatroom.util.SmsWriteOpUtil;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;
import com.xxun.watch.xunchatroom.control.ChatNotificationManager;
import com.xiaoxun.sdk.utils.Constant;
public class ChatroomService extends Service {
    String LOG_TAG="ChatroomService";
    String TAG="ChatroomService";
    private OfflineBroadCastReciever mReceiver=null;
     private IntentFilter intentFilter;
     private VedioRecordBroadCastReciever vReceiver=null;
     private IntentFilter vIntentFilter;
     private ContractBroadCastReciever cReceiver=null;
     private IntentFilter cIntentFilter;
     private SmsSentBroadCastReciever sReceiver=null;
     private IntentFilter sIntentFilter;
     private ResetNewMsgFlagBroadCastReciever rReceiver=null;
     private IntentFilter rIntentFilter;
    private LTEBroadCastReciever lReceiver;
    private IntentFilter lIntentFilter;
     Context mContext=null;
     private static ChatThreadTimer myThread=null;
     private static final String db_version="3";
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "ChatroomService,onBind");
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
	//RegisterObserve();
	//registerMessageListener();
	//registerOfflineBroadcastReciever();
	//registerContractBroadcastReciever();
	//registerSmsSentBroadcastReciever();
	//registerStopVideoRecordBroadcastReciever();
        Log.i(LOG_TAG, "ChatroomService,onCreate");
	//if(ChatUtil.ChatUtilCheckDBVersion(this,db_version)){
	//	deleteDBByVersionChange();
	//}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "ChatroomService,onStartCommand");
        //startForeground();
	//RegisterObserve();
	registerMessageListener();
	registerOfflineBroadcastReciever();
	registerContractBroadcastReciever();
	registerSmsSentBroadcastReciever();
	registerResetNewMsgFlagBroadcastReciever();
	registerStopVideoRecordBroadcastReciever();
        registerLTEBroadcastReciever();
          flags = START_STICKY;  

          if(Build.VERSION.SDK_INT>=26) {
              String channelID ="1";
              String channelName = "语音";
              NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
              NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
              manager.createNotificationChannel(channel);

              Notification.Builder notification = new Notification.Builder(this);
              notification.setSmallIcon(R.mipmap.man_small_04);
              notification.setTicker(channelID);
              notification.setChannelId(channelID);
              manager.notify(3,notification.build());
          }else {
              //ÆôÓÃÇ°Ìš·þÎñ£¬Ö÷ÒªÊÇstartForeground()
              Notification notification = new Notification(R.mipmap.man_small_04, "1"
                      , System.currentTimeMillis());
              //notification.setLatestEventInfo(this, "1", null);
              //ÉèÖÃÍšÖªÄ¬ÈÏÐ§¹û
              notification.flags = Notification.FLAG_SHOW_LIGHTS;
              startForeground(1, notification);
          }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
	//stopForeground();
	unregisterOfflineBroadcastReciever();
	unregisterContractBroadcastReciever();
	unregisterSmsSentBroadcastReciever();
	unregisterResetNewMsgFlagBroadcastReciever();
	unregisterStopVedioRecordBroadcastReciever();
        unregisterLTEBroadcastReciever();
        super.onDestroy();
        Log.i(LOG_TAG, "ChatroomService,onDestroy");
    }

    private void RegisterObserve(){
        this.getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("dialer_subaction"), true,mHeadsUpObserver);
    }
    
        
    final private ContentObserver mHeadsUpObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            String value = android.provider.Settings.System.getString(getContentResolver(),"dialer_subaction");
            Log.i(LOG_TAG, "contract,onchange");
	    if(value.equals("success")){
            checkContractData();	
	    initContractData();
	    }
        }
    };

    public void registerContractBroadcastReciever(){
        Log.i(LOG_TAG, "registerContractBroadcastReciever");
        //实例化过滤器；
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("com.xungroup.new");

        //实例化广播监听器；
        cReceiver = new ContractBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(cReceiver, intentFilter);

    }

    public void unregisterContractBroadcastReciever(){
        Log.i(LOG_TAG, "unregisterContractBroadcastReciever");
        unregisterReceiver(cReceiver);
	cReceiver=null;
    }

    class  ContractBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "ContractBroadCastReciever,onReceive");
		mContext=context;
		checkContractData();	
	    	initContractData();
		sendContractChangeBroacast();
		ChatContractManager.updateAllMissChatCount(context);
        }
    }

    void checkContractData(){
        ArrayList<String> my_list=new ArrayList<String>();
        ChatContractManager.getData(getApplicationContext(),my_list);
        boolean is_find=false;
	Log.i(LOG_TAG, "checkContractData");

	if(my_list.size()<=0){
	  Log.i(LOG_TAG, "size="+my_list.size());
	  return;
	}

        for(String gid:my_list){
            String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
            String name;
            String number;
            Uri uri = Uri.parse ("content://com.android.contacts/contacts");
            ContentResolver resolver = getApplicationContext().getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
            Log.i(LOG_TAG,"initContractData");
            is_find=false;
	    Log.i(LOG_TAG, "contract gid="+gid);

            while(cursor.moveToNext()&&!is_find){
                boolean is_continue=false;
                ContractListItemInfo add_contract1 =new ContractListItemInfo();
                int contactsId = cursor.getInt(0);
                int contract_type=-1;
                //Log.i(LOG_TAG,"contactsId = " +contactsId);
                uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
                Cursor dataCursor = resolver.query(uri, project, null, null, null);
                //SyncArrayBean  arrayBean = new SyncArrayBean();
                while(dataCursor.moveToNext()&&!is_find) {
                    String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                    if("vnd.android.cursor.item/nickname".equals(type)){
                        String user_gid=dataCursor.getString(dataCursor.getColumnIndex(project[5]));
                        if(gid.equals(user_gid)){
                            is_find=true;
                            break;
                        }
                    }
                }

                if(is_find==true){
                    break;
                }
            }
            cursor.close();
	    Log.i(LOG_TAG, "is_find"+is_find);
            if(!is_find){
                ChatListDB.getInstance(getApplicationContext()).delAllMsg(gid);
            }
        }

    }

 void initContractData(){
        String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
        String name;
        String number;
        Uri uri = Uri.parse ("content://com.android.contacts/contacts");
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        Log.i(LOG_TAG,"initContractData");
	String avatar;


        while(cursor.moveToNext()){
            boolean is_continue=false;
            ContractListItemInfo add_contract1 =new ContractListItemInfo();
            int contactsId = cursor.getInt(0);
            int contract_type=-1;
            //Log.i(LOG_TAG,"contactsId = " +contactsId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
            Cursor dataCursor = resolver.query(uri, project, null, null, null);
            //SyncArrayBean  arrayBean = new SyncArrayBean();
            while(dataCursor.moveToNext()) {
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if("vnd.android.cursor.item/nickname".equals(type)){
                    contract_type=dataCursor.getInt(dataCursor.getColumnIndex(project[1]));
                    if(contract_type==0||contract_type==2){

                    }
                    String user_gid=dataCursor.getString(dataCursor.getColumnIndex(project[5]));
                    // Log.i(LOG_TAG,"user_gid = " +user_gid);
                    add_contract1.setContractItemMissCount(ChatUtil.getGroupMissCount(getApplicationContext(),user_gid));
                    add_contract1.setContractItemGID(user_gid);
                    int attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));
                    add_contract1.setContractItemAttr(attri);
                    // Log.i(LOG_TAG,"attri = " +attri);

                    //arrayBean.contactWeight =dataCursor.getInt(dataCursor.getColumnIndex(project[3]));
                    //arrayBean.optype = dataCursor.getInt(dataCursor.getColumnIndex(project[1]));
                }else  if ("vnd.android.cursor.item/name".equals(type)) {
                    name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
		    avatar = dataCursor.getString(dataCursor.getColumnIndex(project[3]));
                    // Log.i(LOG_TAG,"name = " +name);
                    add_contract1.setListItemName(name);
		    add_contract1.setContractItemAvatar(avatar);
			Log.i(LOG_TAG,"avatar = " +avatar);
                } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
                } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                    number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                    //Log.i(LOG_TAG,"number = " +number);
                }
            }
            if((contract_type==0||contract_type==2)&&add_contract1.getContractItemGID()!=null) {
                // Log.i(LOG_TAG, "add one contracts item");
                ChatContractManager.addOneGID(add_contract1.getContractItemGID());
                //ContractListDte.add(add_contract1);
            }
            //if(arrayBean != null) mlist_arraybean.add(arrayBean);
        }
        cursor.close();
        ChatContractManager.commitData(getApplicationContext());
    }


     class ChatroomIMessageReceiveListener extends IMessageReceiveListener.Stub{
	@Override
        public void onReceive(ResponseData responseData) {
            //Log.e(TAG, "onReceive: " + responseData.getResponseCode() + " " + responseData.getResponseData().toJSONString());
            Log.i(LOG_TAG, "jack on recieve");
	    JSONObject jo = (JSONObject)JSONValue.parse(responseData.getResponseData());
		if(jo==null){
			Log.i(LOG_TAG,"offline msg jo is null");
			return;
		}
	    if(ChatOfflineMsgManager.getOfflineMsgFlag())
            {
	/*	int cid=(int)jo.get(ChatKeyString.KEY_CID);
		Log.i(LOG_TAG,"cid="+cid);
		if(cid!=40192){
			Log.i(LOG_TAG,"offline msg cid is error");
			return;
		}

		JSONObject pl = (JSONObject) jo.get(ChatKeyString.KEY_PL);
		if(pl==null){
			Log.i(LOG_TAG,"offline msg pl is null");
		return;
		}
*/
                Log.i(LOG_TAG,"recieve offline msg body");
                ChatRecieveMsgManager.ParseOfflineList(getApplicationContext(),jo);
            }


            //int msg_action=(int)responseData.getResponseData().get("sub_action");
              // Log.i(LOG_TAG, "msg_action="+msg_action);


        }
}
    static XiaoXunNetworkManager mNetService;
    private ChatroomIMessageReceiveListener mMessageReceiveListener = null;

    void registerMessageListener(){
	Log.i(LOG_TAG, "registerMessageListener");
        mNetService = (XiaoXunNetworkManager)getSystemService("xun.network.Service");
	mMessageReceiveListener=new ChatroomIMessageReceiveListener();
	Log.i(LOG_TAG, "mMessageReceiveListener="+mMessageReceiveListener);
        mNetService.setMessageReceiveListener(mMessageReceiveListener);
       
    }

    public void registerOfflineBroadcastReciever(){
        Log.i(LOG_TAG, "registerOfflineBroadcastReciever");
        //实例化过滤器；
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("com.xxun.watch.sendofflinereqbroadcast");

        //实例化广播监听器；
        mReceiver = new OfflineBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(mReceiver, intentFilter);

    }

    public void unregisterOfflineBroadcastReciever(){
        Log.i(LOG_TAG, "unregisterOfflineBroadcastReciever");
        unregisterReceiver(mReceiver);
	mReceiver=null;
    }

    class  OfflineBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "OfflineBroadCastReciever,onReceive");
		mContext=context;
		ConstrctChatOfflineMsgSendBuff(context);
        }
    }

class sendOfflineRequstCallback extends IResponseDataCallBack.Stub{
	public void onSuccess(ResponseData responseData){
                Log.i(LOG_TAG,"offline msg msg send succ");
                Log.i(LOG_TAG,responseData.getResponseCode() + " " + responseData.getResponseData());
		JSONObject jo = (JSONObject)JSONValue.parse(responseData.getResponseData());

            	int rc=(int) jo.get(ChatKeyString.KEY_RC);
                Log.i(LOG_TAG,"rc="+rc);
                if(rc<0){
			ChatOfflineMsgManager.setOfflineMsgFlag(false);
			ChatOfflineMsgManager.initOfflineManager();
                    return;
                }
                int sn = (int) jo.get(ChatKeyString.KEY_SN);
                JSONObject pl = (JSONObject) jo.get(ChatKeyString.KEY_PL);
                Log.i(LOG_TAG,"sn_str="+sn);

                JSONArray key_list=(JSONArray)pl.get(ChatKeyString.KEY_KEY_LIST);

                if(key_list!=null) {
                    int count = 0;
                    int count_max = key_list.size();
                    Log.i(LOG_TAG, "count_max=" + count_max);
                    if (count_max > 0) {
                        for (count = 0; count < count_max; count++) {
                            String key_str = (String) key_list.get(count);
                            Log.i(LOG_TAG, "key_str=" + key_str);
                            ChatOfflineMsgManager.addKeyToList(key_str);
                        }
                    } else {
                        Log.i(LOG_TAG, "key_list is empty");
                        String mark_key = (String)pl.get(ChatKeyString.KEY_MARK_KEY);

                        if (mark_key != null) {
                            //String mark_str = mark_key.toString();
                            Log.i(LOG_TAG, "mark_key=" + mark_key);
                            ChatUtil.ChatUtilWriteEndkey(mContext.getApplicationContext(), mark_key);
			    ChatOfflineMsgManager.setOfflineMsgFlag(false);
			    ChatOfflineMsgManager.initOfflineManager();
                        }
                    }
                }     

       }
       
       public void onError(int i, String s){
              //;//
       }
}


 public String ConstrctChatOfflineMsgSendBuff(Context context){
        String sendBuff=null;
        String msg_key=null;
        JSONObject msg = new JSONObject();
    	String endKey=ChatUtil.ChatUtilReadEndkey(context.getApplicationContext());
        Log.i(LOG_TAG,"get read,endKey="+endKey);
	if(endKey==null||endKey.length()<10){
		Log.i(LOG_TAG, "endkey missing,return");
		return null;
	}
	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)context.getSystemService("xun.network.Service");
        String audio_str=null;
        Log.i(LOG_TAG, "ConstrctChatOfflineMsgSendBuff");
        msg.put(ChatKeyString.KEY_CID, CloudBridgeUtil.CID_GET_OFFLINE_MESSAGE);
        msg.put(ChatKeyString.KEY_SID, networkService.getSID());

        msg.put(ChatKeyString.KEY_SN, ChatUtil.ChatUtilGetServiceSN(context));
        msg.put(ChatKeyString.KEY_VERSION,CloudBridgeUtil.PROTOCOL_VERSION);
        JSONObject pl = new JSONObject();


        //String new_key=endKey.substring(0,2)+endKey.substring(3,36)+endKey.substring(37,41)+endKey.substring(42,endKey.length());
        //Log.i(LOG_TAG,"new_key,new_key="+new_key);
        pl.put(ChatKeyString.KEY_END_KEY,  endKey);
        pl.put(ChatKeyString.KEY_SIZE,  10);
        pl.put(ChatKeyString.KEY_EID,  networkService.getWatchEid());
        msg.put(ChatKeyString.KEY_PL, pl);
        sendBuff= msg.toJSONString();
        ChatOfflineMsgManager.initOfflineManager();
        ChatOfflineMsgManager.setOfflineMsgFlag(true);
        Log.i("LOG_TAG","sendBuff="+sendBuff);

	
	networkService.sendJsonMessage(sendBuff,new sendOfflineRequstCallback());
/*
        ChatNetService.getmChatNetService().sendJsonMessage(sendBuff, new IResponseDataCallBack<ResponseData>() {
            @Override
            public void onSuccess(ResponseData responseData) {
                Log.i(LOG_TAG,"offline msg send succ");
                Log.i(LOG_TAG,responseData.getResponseCode() + " " + responseData.getResponseData().toJSONString());
                int rc=(int) responseData.getResponseData().get(ChatKeyString.KEY_RC);
                Log.i(LOG_TAG,"rc="+rc);
                if(rc<0){
                    return;
                }
                int sn = (int) responseData.getResponseData().get(ChatKeyString.KEY_SN);
                JSONObject pl = (JSONObject) responseData.getResponseData().get(ChatKeyString.KEY_PL);
                Log.i(LOG_TAG,"sn_str="+sn);

                JSONArray key_list=(JSONArray)pl.get(ChatKeyString.KEY_KEY_LIST);

                if(key_list!=null) {
                    int count = 0;
                    int count_max = key_list.size();
                    Log.i(LOG_TAG, "count_max=" + count_max);
                    if (count_max > 0) {
                        for (count = 0; count < count_max; count++) {
                            String key_str = (String) key_list.get(count);
                            Log.i(LOG_TAG, "key_str=" + key_str);
                            ChatOfflineMsgManager.addKeyToList(key_str);
                        }
                    } else {
                        Log.i(LOG_TAG, "key_list is empty");
                        String mark_key = (String)pl.get(ChatKeyString.KEY_MARK_KEY);
                        if (mark_key != null) {
                            //String mark_str = mark_key.toString();
                            Log.i(LOG_TAG, "mark_key=" + mark_key);
                            ChatUtil.ChatUtilWriteEndkey(getApplicationContext(), mark_key);
                        }
                    }
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
*/
        return sendBuff;
    }

   private void sendContractChangeBroacast(){
        Intent it = new Intent("com.xxun.watch.contractrefreshbroadcast");
        Log.i(LOG_TAG, "sendContractChangeBroacast");
		it.setPackage("com.xxun.watch.xunchatroom");
        sendBroadcast(it);
    }

    private void registerStopVideoRecordBroadcastReciever(){
        Log.i(LOG_TAG, "registerStopVideoRecordBroadcastReciever");
        //实例化过滤器；
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("com.xxun.xuncamera.quitrecord");

        //实例化广播监听器；
        vReceiver = new VedioRecordBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(vReceiver, intentFilter);

    }

    private void unregisterStopVedioRecordBroadcastReciever(){
        Log.i(LOG_TAG, "unregisterStopVedioRecordBroadcastReciever");
        unregisterReceiver(vReceiver);
	vReceiver=null;
    }

    class  VedioRecordBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "VedioRecordBroadCastReciever,onReceive");
		mContext=context;
		sendSearchChatMsgBroacast(context);
		
        }
    }

   private void sendSearchChatMsgBroacast(Context context){
        Intent it = new Intent("com.broadcast.xxun.searchMessage");
        Log.i(LOG_TAG, "sendSearchChatMsgBroacast");
	it.setPackage("com.xxun.watch.xunchatroom");
        context.sendBroadcast(it);
    }
//////////////////////////////////////////////////////////////////////////
    private void registerSmsSentBroadcastReciever(){
        Log.i(LOG_TAG, "registerSmsSentBroadcastReciever");
        //实例化过滤器；
        sIntentFilter = new IntentFilter();
        //添加过滤的Action值；
        sIntentFilter.addAction("SENT_SMS_ACTION");

        //实例化广播监听器；
        sReceiver = new SmsSentBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(sReceiver, sIntentFilter);

    }

    private void unregisterSmsSentBroadcastReciever(){
        Log.i(LOG_TAG, "unregisterSmsSentBroadcastReciever");
        unregisterReceiver(sReceiver);
	sReceiver=null;
    }

    class  SmsSentBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "SmsSentBroadCastReciever,onReceive");
		mContext=context;
		//sendSearchChatMsgBroacast(context);
		switch (getResultCode()) {
		case Activity.RESULT_OK:
		Log.i(LOG_TAG,"send sms succ");

		break;
		case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
		Log.i(LOG_TAG,"send sms fail");
		break;
		case SmsManager.RESULT_ERROR_RADIO_OFF:
		Log.i(LOG_TAG,"send sms radio off");
		break;
		case SmsManager.RESULT_ERROR_NULL_PDU:
		Log.i(LOG_TAG,"send sms pdu null");
		break;
		}
		//deleteAllSentSMS(context);
		if(myThread!=null){
			myThread.stopThreadTimer();
			myThread=null;
		}
		//startDelayToDelete();
		deleteAllSentSMS(context);
        }
    }

 public void startDelayToDelete(){
		myThread=new ChatThreadTimer(5000, new ChatThreadTimer.TimerInterface() {
                @Override
                public void doTimerOut() {
                    Log.i(LOG_TAG, "doTimerOut");
                	myThread.stopThreadTimer();
			deleteAllSentSMS(mContext);
                }
            });

           myThread.start();
}

 public void deleteAllSentSMS( Context context)
  {
	Log.i(LOG_TAG, "deleteAllSentSMS");
if (!SmsWriteOpUtil.isWriteEnabled(context.getApplicationContext())) {
            SmsWriteOpUtil.setWriteEnabled(
                    context.getApplicationContext(), true);
}
        try
        {
            // 准备系统短信收信箱的uri地址
            Uri uri = Uri.parse("content://sms/sent");// 收信箱
            // 查询收信箱里所有的短信
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" ,"thread_id"};//"_id", "address", "person",, "date", "type
            String where = " _id >= '0'";
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
			Log.i(LOG_TAG, "deleteAllSentSMS,id="+id+",resault="+resault);
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
/*
        try {  
            ContentResolver CR = context.getContentResolver(); 

            // Query SMS  
            Uri uriSms = Uri.parse("content://sms/sent");  
            Cursor c = CR.query(uriSms,  
                    new String[] { "_id", "thread_id" }, null, null, null);  
            if (null != c && c.moveToFirst()) {  
                do {  
                    // Delete SMS  
                    long threadId = c.getLong(1);  
                    int resault=CR.delete(Uri.parse("content://sms/conversations/" + threadId),  
                            null, null);  
		    Log.d("deleteAllSentSMS", "threadId:: "+threadId);
                    Log.d("deleteAllSentSMS", "resault:: "+resault);  
                } while (c.moveToNext());  
            }  
        } catch (Exception e) {  
            // TODO: handle exception  
            Log.d("deleteSMS", "Exception:: " + e);  
        }  */
    }

    void deleteDBByVersionChange(){
        ArrayList<String> my_list=new ArrayList<String>();
        ChatContractManager.getData(getApplicationContext(),my_list);
        boolean is_find=false;
	Log.i(LOG_TAG, "deleteDBByVersionChange");
	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)getSystemService("xun.network.Service");
	String watchGID=networkService.getWatchGid();
	Log.i(LOG_TAG, "deleteDBByVersionChange,watchGID="+watchGID);
	if(watchGID!=null){
	ChatListDB.getInstance(getApplicationContext()).delAllMsg(watchGID);
	}

	String smsGID=WatchSystemInfo.getSmsGID();
	Log.i(LOG_TAG, "deleteDBByVersionChange,smsGID="+smsGID);
	if(smsGID!=null){
	ChatListDB.getInstance(getApplicationContext()).delAllMsg(smsGID);
	}

	Log.i(LOG_TAG, "size="+my_list.size());
	if(my_list.size()<=0){
	  ChatContractManager.updateAllMissChatCount(this);
	  return;
	}

        for(String gid:my_list){
      		Log.i(LOG_TAG, "deleteDBByVersionChange,gid="+gid);
                ChatListDB.getInstance(getApplicationContext()).delAllMsg(gid);
            
        }
	ChatContractManager.updateAllMissChatCount(this);
    }
/////////////////////////////////////////////////////////////////////////////////////
    private void registerResetNewMsgFlagBroadcastReciever(){
        Log.i(LOG_TAG, "registerResetNewMsgFlagBroadcastReciever");
        //实例化过滤器；
        rIntentFilter = new IntentFilter();
        //添加过滤的Action值；
        rIntentFilter.addAction("com.xxun.watch.resetNewMsgFlag");

        //实例化广播监听器；
        rReceiver = new ResetNewMsgFlagBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(rReceiver, rIntentFilter);

    }

    private void unregisterResetNewMsgFlagBroadcastReciever(){
        Log.i(LOG_TAG, "unregisterResetNewMsgFlagBroadcastReciever");
        unregisterReceiver(rReceiver);
	rReceiver=null;
    }

    class  ResetNewMsgFlagBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "ResetNewMsgFlagBroadCastReciever,onReceive");
		//mContext=context;
		ChatNotificationManager.is_work=false;

        }
    }


    public void registerLTEBroadcastReciever(){
        Log.i(TAG, "registerLTEBroadcastReciever");
        //实例化过滤器；
        //lIntentFilter = new IntentFilter();
        //添加过滤的Action值；
        IntentFilter lIntentFilter = new IntentFilter(Constant.ACTION_NET_SWITCH_SUCC);
        //实例化广播监听器；
        lReceiver = new LTEBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(lReceiver, lIntentFilter);

    }

    public void unregisterLTEBroadcastReciever(){
        Log.i(TAG, "unregisterLTEBroadcastReciever");
        unregisterReceiver(lReceiver);
        lReceiver=null;
    }

    class  LTEBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "LTEBroadCastReciever,onReceive");
            String mAction=intent.getAction();
            Log.i(TAG, "mAction="+mAction);

            ChatRecieveMsgManager.isSendWork=true;
        }
    }
}
