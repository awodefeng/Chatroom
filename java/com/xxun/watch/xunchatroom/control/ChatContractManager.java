

package com.xxun.watch.xunchatroom.control;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.info.ChatNoticeInfo;
import com.xxun.watch.xunchatroom.info.ChatNotifListItem;
import com.xxun.watch.xunchatroom.util.ChatUtil;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatContractManager
{

  static String LOG_TAG="chat contract mgr";
  static String CHAT_GID_TAB="GID_TAB";
    static String CHAT_TAB="TAB";
   static ArrayList<String> gid_tab=null;
    public ChatContractManager(Context context){

    }

    public static void addOneGID(String gid){
        if(gid_tab==null){
            gid_tab=new ArrayList<String>();
        }

        gid_tab.add(gid);
    }

    public static void commitData(Context context){
        JSONArray gid_list=new JSONArray();
        if(gid_tab==null){
            return;
        }
        for(String gid:gid_tab) {
            gid_list.put((String)gid);
        }

        JSONObject jData=new JSONObject();
        try {
            jData.put("Tab",gid_list);
            String data=jData.toString();
            Log.i(LOG_TAG,"commitData="+data);
            ChatWriteContractGidList(context,data);
            gid_tab.clear();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getData(Context context,ArrayList<String> myList){
       String data= ChatReadContractGidList(context);
        JSONArray gid_list=null;
        Log.i(LOG_TAG,"getData="+data);

        if(data==null||data.equals("")){
            myList.clear();
            return;
        }

        try {
            JSONObject jData=new JSONObject(data);
            gid_list=jData.getJSONArray("Tab");
            int count=gid_list.length();
            Log.i(LOG_TAG,"getData,count="+count);
            for(int index=0;index<count;index++){
                String gid=gid_list.getString(index);
                Log.i(LOG_TAG,"gid="+gid);
                myList.add(gid);
            }
        } catch (JSONException e) {
            Log.i(LOG_TAG,"getData,error");
            e.printStackTrace();
        }

    }

    public static void ChatWriteContractGidList(Context context,String list)
    {
        SharedPreferences.Editor editor=context.getSharedPreferences(CHAT_GID_TAB,
                Activity.MODE_PRIVATE).edit();
        editor.putString(CHAT_TAB, list);
        Log.i(LOG_TAG,"ChatWriteContractGidList="+list);
        editor.commit();

    }

    public static String ChatReadContractGidList(Context context)
    {
        String str;
        SharedPreferences mSp=context.getSharedPreferences(CHAT_GID_TAB,
                Activity.MODE_PRIVATE);
        str=mSp.getString(CHAT_TAB,"");
        Log.i(LOG_TAG,"ChatReadContractGidList="+str);
        return str;
    }

    public static void destroyData(Context context){
        JSONArray gid_list=new JSONArray();
        if(gid_tab!=null){
            gid_tab.clear();
        }
    	
        JSONObject jData=new JSONObject();
        try {
            jData.put("Tab",gid_list);
            String data=jData.toString();
            Log.i(LOG_TAG,"commitData="+data);
            ChatWriteContractGidList(context,data);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   public static int getAllMissChatCount(Context context){
	Log.i(LOG_TAG,"getAllMissChatCount");
	ArrayList<String> gidList=new ArrayList<String>();
	getData(context,gidList);
	int listSize=0;
	listSize=gidList.size();
        Log.i(LOG_TAG,"gid list size="+listSize);
	int allCount=0;
	if(listSize>0){
	for(String sGid:gidList){
	     //Log.i(LOG_TAG,"sGid="+sGid);	
	     int missCount=ChatUtil.getGroupMissCount(context.getApplicationContext(),sGid);
	     Log.i(LOG_TAG,"sGid="+sGid+",missCount="+missCount);
	     allCount+=missCount;
	}
	}
	int familyCount=ChatUtil.getGroupMissCount(context.getApplicationContext(),WatchSystemInfo.getWatchGID());
	Log.i(LOG_TAG,"familyCount="+familyCount);
	allCount+=familyCount;
	int smsCount=ChatUtil.getGroupMissCount(context.getApplicationContext(),WatchSystemInfo.getSmsGID());
	Log.i(LOG_TAG,"smsCount="+smsCount);
	allCount+=smsCount;
	Log.i(LOG_TAG,"getAllMissChatCount,allCount="+allCount);
	return allCount;
   }


   public static void updateAllMissChatCount(Context context){
	int allMissCount=getAllMissChatCount(context);
	Log.i(LOG_TAG,"updateAllMissChatCount,allMissCount="+allMissCount);
	android.provider.Settings.System.putInt(context.getContentResolver(),"ChatMissCount",allMissCount);
   }
}

