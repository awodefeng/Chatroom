package com.xxun.watch.xunchatroom.util;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

public class WatchSystemInfo {

final static int watch_width=240;
final static int watch_height=240;
final static int is_watch_debug=1;
final static int list_item_height=95;
 static String watch_gid=null;
 static String watch_eid=null;
 static String LOG_TAG="chat sys";
     static String watch_gid_file="watch_gid.txt";
    public  static int getWatchWidth()
    {
        return watch_width;
    }

    public static int getWatchHeight()
    {
        return watch_height;
    }

    public  static int getWatchDebugFlag(){
        return is_watch_debug;
    }

    public static int getWatchListItemHeight(){
        return list_item_height;
    }

    public static void setWatchGID(String gid){
        watch_gid=gid;
        Log.i(LOG_TAG,"setWatchGID="+watch_gid);
        ChatUtil.ChatUtilCreateFolder(ChatUtil.getChatSettingFolder());
        String gid_file_path=ChatUtil.getChatSettingPath(watch_gid_file);
        Log.i(LOG_TAG,"gid_file_path="+gid_file_path);
        ChatUtil.ChatUtilDeleteFile(gid_file_path);
        ChatUtil.ChatUtilCreateFile(gid_file_path);
        ChatUtil.ChatUtilSaveDataToFile(gid.getBytes(),gid_file_path);
    }

   public static String getWatchGID(){

        Log.i(LOG_TAG,"watch_gid="+watch_gid);
        if(watch_gid==null){
            String gid_file_path=ChatUtil.getChatSettingPath(watch_gid_file);
            byte[] Data=ChatUtil.ChatUtilReadDataFromFile(gid_file_path);
            Log.i(LOG_TAG,"gid_file_path="+gid_file_path);
            if(Data!=null) {
                String res = new String(Data);
                watch_gid = res;
            }
        }
        Log.i(LOG_TAG,"getWatchGID="+watch_gid);

        return watch_gid;
    }

    public  static void setWatchEID(String eid){
        watch_eid=eid;
    }

    public  static String getWatchEID(Context context){
	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)context.getSystemService("xun.network.Service");
	watch_eid=networkService.getWatchEid();
	Log.i(LOG_TAG,"getWatchEID="+watch_eid);
        return watch_eid;
    }

    public  static String getSmsGID(){
        Log.i(LOG_TAG,"getSmsGID");

        return "SMS_GROUP";
    }
}
