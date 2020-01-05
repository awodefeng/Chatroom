package com.xxun.watch.xunchatroom.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xxun.watch.xunchatroom.R;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
//import com.xiaoxun.sdk.interfaces.IMessageReceiveListener;
//import com.xiaoxun.sdk.interfaces.IResponseDataCallBack;
import com.xxun.watch.xunchatroom.adapter.ContractListAdapter;
import com.xxun.watch.xunchatroom.info.ContractListItemInfo;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;
import com.xxun.watch.xunchatroom.util.ChatUtil;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;
import com.xxun.watch.xunchatroom.control.ChatContractManager;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.Manifest;
import android.content.pm.PackageManager;
import com.xxun.watch.xunchatroom.activity.QrcodeDialogFragment;
import static android.content.ContentValues.TAG;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.os.SystemProperties;
import android.content.Context;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
////////////////////////////////////////////////
import android.util.Base64;
import java.io.UnsupportedEncodingException;
import com.xiaoxun.sdk.utils.CloudBridgeUtil;
import com.xiaoxun.sdk.IResponseDataCallBack;
/////////////////////////////////////////////////////
import android.util.XiaoXunUtil;

public class ContractListActivity extends Activity {
    ArrayList<ContractListItemInfo> ContractListDte=null;
    private ContractListAdapter adapter ;//= new ChatListAdapter();
    ListView contractList;
    String LOG_TAG="chat contract";
    String chat_audio_path=null;//"chat_audio";
    String chat_audio_folder="chat_audio";
    int count=0;
    private ContractBroadCastReciever mReceiver;
    private IntentFilter intentFilter;
    ChatThreadTimer my_thread;
    private Toast mToast =null;
    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.INTERNET,
	    Manifest.permission.RECEIVE_SMS,
	    Manifest.permission.VIBRATE,
	    Manifest.permission.ACCESS_NETWORK_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_list);
        contractList=(ListView) findViewById(R.id.contract_lv);
        chat_audio_path=Environment.getExternalStorageDirectory().getAbsolutePath();
        chat_audio_path+="/"+chat_audio_folder;
        Log.i(LOG_TAG,"chat_audio_path = " + chat_audio_path);
        ChatUtil.ChatUtilCreateFolder(chat_audio_path);
	//if(watch_gid==null){
	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)getSystemService("xun.network.Service");
	String watch_gid=networkService.getWatchGid();
	Log.i(LOG_TAG,"watch_gid = " + watch_gid);
	if(watch_gid!=null){
	WatchSystemInfo.setWatchGID(watch_gid);
	}

	String watch_eid=networkService.getWatchEid();
	Log.i(LOG_TAG,"watch_eid= " + watch_eid);
	if(watch_eid!=null){
	WatchSystemInfo.setWatchEID(watch_eid);
	}
	//}
 	if(checkPermisson()){
	
        initConratcView();
        if(ContractListDte!=null) {
            adapter = new ContractListAdapter(ContractListActivity.this, ContractListDte);
            contractList.setAdapter(adapter);
            contractList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.i(LOG_TAG, "i = " + i);
                    /*if(i==0){
                        Log.i(LOG_TAG, "delete db ");
                        ChatListDB.getInstance(getApplicationContext()).delAllMsg(WatchSystemInfo.getWatchGID());
                    }else*/
		    if(!checkBindStatus()){			
                        ConnectivityManager  mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);                            
                        NetworkInfo networkinfo = mConnectivityManager.getActiveNetworkInfo();
                        Boolean isNetwrokOk = (networkinfo != null && networkinfo.isAvailable());
			//isNetwrokOk=false;
			if (isNetwrokOk) {
                                showQrcodeDialogFragment();
                            } else {
                                //showAlertDialog();
				showTip(getString(R.string.check_net));
                            }
		    }else if(ContractListDte.get(i).getContractItemGID().equals(WatchSystemInfo.getSmsGID())) {
                        xun_enter_chatroom_sms_windows();
                    }
                    else {
                        xun_enter_chatroom_main_windows(ContractListDte.get(i).getContractItemGID());
                    }
                }
            });
        }


	}
	registerBroadcastReciever();
     //int allMissCount=android.provider.Settings.System.getInt(getApplicationContext().getContentResolver(),"ChatMissCount",0);
     //Log.i("LOG_TAG","ContractListActivity,allMissCount="+allMissCount);
     //int smsType = android.provider.Settings.System.getInt(getApplicationContext().getContentResolver(), "sms_filter",0);
     //Log.i("LOG_TAG","ContractListActivity,smsType="+smsType);
	//sendSMSToApp(this,"+8613918345010",null);
    }

////////////////////////////////////////////////////////////////////////
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
        //byte[] readdData=ChatUtil.ChatUtilReadDataFromFile(filePath);
	//String smsText=new String(readdData);
	String smsContent=number+" \n"+"高兴，测试我偌莫，高高兴兴";
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
///////////////////////////////////////////////////////////////////////////
    protected void onDestroy(){
        super.onDestroy();
        Log.i("LOG_TAG","ContractListActivity,onDestroy");
	unregisterBroadcastReciever();
	closeTip();
	}

    public void showQrcodeDialogFragment() {
        QrcodeDialogFragment qrcodefragment = new QrcodeDialogFragment();
        qrcodefragment.show(getFragmentManager(), "qrcode");
    }

    private boolean checkBindStatus(){
	Boolean isBinded = SystemProperties.getBoolean("persist.sys.isbinded", false);
	Log.i("LOG_TAG","checkBindStatus,isBinded="+isBinded);
	//isBinded=false;
	return isBinded;
	}

    protected void onResume(){
        super.onResume();
        Log.i("LOG_TAG","ContractListActivity,onResume");
        if(adapter!=null) {
            if(contractList!=null) {
                Log.i("LOG_TAG","onResume,count="+ContractListDte.size());
                ContractListDte.clear();
                Log.i("LOG_TAG","onResume,count="+ContractListDte.size());
                initConratcView();
                Log.i("LOG_TAG","onResume,count="+ContractListDte.size());
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void refreshContractList(){
	Log.i("LOG_TAG","refreshContractList");
        if(adapter!=null) {
            if(contractList!=null) {
                Log.i("LOG_TAG","refreshContractList,count="+ContractListDte.size());
                ContractListDte.clear();
                Log.i("LOG_TAG","refreshContractList,count="+ContractListDte.size());
                initConratcView();
                Log.i("LOG_TAG","refreshContractList,count="+ContractListDte.size());
            }
            adapter.notifyDataSetChanged();
        }
    }

    private static final int REQUEST_PERMISSION_CHATROOM_CODE = 1;

    private boolean checkPermisson() {
  
	boolean resault=true;
            for(String permisson_str:permissions){
            if (!(checkSelfPermission(permisson_str)
                    == PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(permissions,
                        REQUEST_PERMISSION_CHATROOM_CODE);
                resault= false;
		break;
            } 
	}
    
           return resault;
      
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CHATROOM_CODE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            if (granted) {
              initConratcView();
        if(ContractListDte!=null) {
            adapter = new ContractListAdapter(ContractListActivity.this, ContractListDte);
            contractList.setAdapter(adapter);
            contractList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.i(LOG_TAG, "i = " + i);
                    /*if(i==0){
                        Log.i(LOG_TAG, "delete db ");
                        ChatListDB.getInstance(getApplicationContext()).delAllMsg(WatchSystemInfo.getWatchGID());
                    }else*/
		    if(!checkBindStatus()){			
                        ConnectivityManager  mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);                            
                        NetworkInfo networkinfo = mConnectivityManager.getActiveNetworkInfo();
                        Boolean isNetwrokOk = (networkinfo != null && networkinfo.isAvailable());
			if (isNetwrokOk) {
                                showQrcodeDialogFragment();
                            } else {
                                //showAlertDialog();
				showTip(getString(R.string.check_net));
                            }
		    }else if(ContractListDte.get(i).getContractItemGID().equals(WatchSystemInfo.getSmsGID())) {
                        xun_enter_chatroom_sms_windows();
                    }
                    else {
                        xun_enter_chatroom_main_windows(ContractListDte.get(i).getContractItemGID());
                    }
                }
            });
        }


	adapter.notifyDataSetChanged();
            }
        }
    }
  void intContractData(){
        ArrayList<String> text_list=new ArrayList<String>();
        ChatContractManager.getData(getApplicationContext(),text_list);
        Log.i("LOG_TAG","text_list.size="+text_list.size());
        if(text_list.size()>0){
            for(String gid:text_list){
                ContractListItemInfo item_info=findContractDataByGID(gid);
                if(item_info!=null){
                    ContractListDte.add(item_info);
                }
            }

        }else{
            getContractData();
        }
    }

    ContractListItemInfo findContractDataByGID(String gid){
        String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
        String name;
        String number;
        Uri uri = Uri.parse ("content://com.android.contacts/contacts");
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        Log.i(LOG_TAG,"findContractDataByGID,gid="+gid);
       boolean is_find=false;

        while(cursor.moveToNext()&&!is_find){
            boolean is_continue=false;
            ContractListItemInfo add_contract1 =new ContractListItemInfo();
            int contactsId = cursor.getInt(0);
            //Log.i(LOG_TAG,"contactsId = " +contactsId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
            Cursor dataCursor = resolver.query(uri, project, null, null, null);
            //SyncArrayBean  arrayBean = new SyncArrayBean();
            while(dataCursor.moveToNext()) {
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if("vnd.android.cursor.item/nickname".equals(type)){

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
                    // Log.i(LOG_TAG,"name = " +name);
                    add_contract1.setListItemName(name);
                } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
                } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                    number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                    //Log.i(LOG_TAG,"number = " +number);
                }
            }

            if(add_contract1.getContractItemGID()!=null&&add_contract1.getContractItemGID().equals(gid)) {
                // Log.i(LOG_TAG, "add one contracts item");
                //ContractListDte.add(add_contract1);
                is_find=true;
            }

            if(is_find){
                return add_contract1;
            }
            //if(arrayBean != null) mlist_arraybean.add(arrayBean);
        }
        cursor.close();
        return null;

    }
    void getContractData(){
        String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
        String name;
        String number;
        Uri uri = Uri.parse ("content://com.android.contacts/contacts");
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        Log.i(LOG_TAG,"getContractData");
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
                ContractListDte.add(add_contract1);
            }
            //if(arrayBean != null) mlist_arraybean.add(arrayBean);
        }
        cursor.close();
        ChatContractManager.commitData(getApplicationContext());
    }

    void initFamilyGroup(){
        Log.i(LOG_TAG, "initFamilyGroup,gid="+WatchSystemInfo.getWatchGID());
        if(WatchSystemInfo.getWatchGID()!=null){
            ContractListItemInfo family_info=new ContractListItemInfo();
            family_info.setContractItemGID(WatchSystemInfo.getWatchGID());
            family_info.setListItemName(getString(R.string.family_group));
            family_info.setContractItemAttr(101);
            family_info.setContractItemMissCount(ChatUtil.getGroupMissCount(getApplicationContext(),WatchSystemInfo.getWatchGID()));
            ContractListDte.add(family_info);
        }
    }

    void initSMSGroup(){
       // ChatUtil.ChatUtilGetSN();
        //String smsNumber="+8613918345010";
        //String head=smsNumber.substring(0,2);
        //ChatUtil.getRealPhoneNumber(smsNumber);
        String smsGID=WatchSystemInfo.getSmsGID();
        Log.i(LOG_TAG, "initFamilyGroup,gid="+smsGID);

        ContractListItemInfo family_info=new ContractListItemInfo();
        family_info.setContractItemGID(smsGID);
        //family_info.setListItemName(getString(R.string.sms_group));
	family_info.setListItemName(getString(R.string.sms_group));
        family_info.setContractItemAttr(102);
        family_info.setContractItemMissCount(ChatUtil.getGroupMissCount(getApplicationContext(),smsGID));
        ContractListDte.add(family_info);
/*
        String date=ChatUtil.ChatUtilGetDate();
        String sms_path=chat_audio_path+"/"+date+".amr";
        ChatUtil.ChatUtilCreateFile(sms_path);
        String save_data="肖潇 is handsome";
        ChatUtil.ChatUtilSaveDataToFile(save_data.getBytes(),sms_path);
        ChatListItemInfo new_item=new ChatListItemInfo("13918345010",WatchSystemInfo.getSmsGID(),8,sms_path,0,ChatListItemInfo.list_tiem_state_null,date);
        new_item.setContentType(3);
        new_item.setIsPlayed(0);
        ChatListDB.getInstance(getApplicationContext()).addChatMsg(WatchSystemInfo.getSmsGID(),new_item);*/
    }

    void initConratcView(){
	Log.i(LOG_TAG, "initConratcView");
        if(WatchSystemInfo.getWatchDebugFlag()==1)
        {
            if(ContractListDte==null) {
		Log.i(LOG_TAG, "ContractListDte is null");
                ContractListDte = new ArrayList<ContractListItemInfo>();
            }else{
		Log.i(LOG_TAG, "ContractListDte is ok,clear");
                ContractListDte.clear();
            }
            //ChatRecieveMsgManager.checkRecieveMsgGroup(getApplicationContext(),WatchSystemInfo.getWatchGID());
            initFamilyGroup();
            getContractData();
            initSMSGroup();
           // ChatListItemInfo new_item=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",0,ChatListItemInfo.list_tiem_state_null,ChatUtil.ChatUtilGetDate());
           // new_item.setContentType(1);
           // ChatListDB.getInstance(getApplicationContext()).addChatMsg("1234567890",new_item);

           // ChatListItemInfo new_item2=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",1,ChatListItemInfo.list_tiem_state_null,"2342344");
           // ChatListDB.getInstance(getApplicationContext()).addChatMsg("1234567890",new_item2);
        }
        else
        {

        }
    }

    public void registerBroadcastReciever(){
        Log.i(LOG_TAG, "registerBroadcastReciever");
        //实例化过滤器；
        intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("com.xxun.watch.contractrefreshbroadcast");

        //实例化广播监听器；
        mReceiver = new ContractBroadCastReciever();

        //将广播监听器和过滤器注册在一起；
        registerReceiver(mReceiver, intentFilter);

    }

    public void unregisterBroadcastReciever(){
        Log.i(LOG_TAG, "unregisterBroadcastReciever");
        unregisterReceiver(mReceiver);
	mReceiver=null;
    }

    class  ContractBroadCastReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "ContractBroadCastReciever,onReceive");
		String mAction=intent.getAction();
		Log.i(LOG_TAG, "mAction="+mAction);
		if(mAction.equals("com.xxun.watch.contractrefreshbroadcast")){ 
			refreshContractList();
        }
    }
}

   private void xun_enter_chatroom_main_windows(String gid){
            Intent my_intent=new Intent();
             my_intent.putExtra(ChatKeyString.KEY_CUR_GID,gid);
	if(XiaoXunUtil.XIAOXUN_CONFIG_CHATROOM_PHOTO_VIDEO_SUPPORT){
            my_intent.setClass(this,ChatroomMainNewActivity.class);
	}else{
            my_intent.setClass(this,ChatroomMainActivity.class);
	}
            startActivity(my_intent);
    }

   private void xun_enter_chatroom_sms_windows(){
        Intent my_intent=new Intent();
        my_intent.setClass(this,ChatSmsActivity.class);
        startActivity(my_intent);
    }

     private void showTip(final String str) {  
        //runOnUiThread(new Runnable() {  
           // @Override  
           // public void run() {  
                if (mToast == null) {  
                    mToast = Toast.makeText(getApplicationContext(), "",  
                            Toast.LENGTH_LONG);  
                    LinearLayout layout = (LinearLayout) mToast.getView();  
                    TextView tv = (TextView) layout.getChildAt(0);  
                    tv.setTextSize(40);  
                }  
                //mToast.cancel();  
                mToast.setGravity(Gravity.CENTER, 0, 0);  
                mToast.setText(str);  
                mToast.show();  
            //}  
        //});  
    }

   private void closeTip(){
	if(mToast!=null){
	mToast.cancel();
	}
   }
}
