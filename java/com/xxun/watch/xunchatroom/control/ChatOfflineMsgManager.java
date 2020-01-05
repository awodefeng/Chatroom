

package com.xxun.watch.xunchatroom.control;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class ChatOfflineMsgManager
{
  static ArrayList<String> key_list=new ArrayList<String>();
  static boolean is_in_offline_msg=false;
  static  int index=0;
   static int  fail_times=0;
    static int succ_times=0;
  static String LOG_TAG="chat offline msg mgr";

    public  final static int compare_resault_ok=0;
    public  final static int compare_resault_error=1;
    public  final static int compare_resault_over=2;
    public  final static int fail_times_max=3;
    public  final static int succ_times_max=2;
    public  ChatOfflineMsgManager(Context context){

    }

    public  static boolean increaseFailTimes(){
        boolean need_retry=false;
        fail_times++;
        if(fail_times<fail_times_max){
            need_retry=true;
        }
         return need_retry;
    }

    public static boolean increaseSuccTimes(){
        boolean need_retry=false;
        succ_times++;
        if(succ_times<succ_times_max){
            need_retry=true;
        }
        return need_retry;
    }

    public static void setOfflineMsgFlag(boolean flag){
        Log.i(LOG_TAG, "setOfflineMsgFlag,flag="+flag);
        is_in_offline_msg=flag;
    }

    public static boolean getOfflineMsgFlag(){
        Log.i(LOG_TAG, "getOfflineMsgFlag,flag="+is_in_offline_msg);
        return is_in_offline_msg;
    }

    public static void clearKeyList(){
        key_list.clear();
    }

    public static void initOfflineManager(){
        is_in_offline_msg=false;
        index=0;
        clearKeyList();
    }

    public static void addKeyToList(String key){
        key_list.add(key);
    }

    public static int getKeyListCount(){
        return key_list.size();
    }

    public static int compareList(String key){
        int resault=compare_resault_ok;
        Log.i(LOG_TAG, "compareList,key="+key);
        if(!key_list.get(index).equals(key)){
            Log.i(LOG_TAG, "compareList,error="+index);
            return compare_resault_error;
        }
        index++;
        if(index>=key_list.size())
        {
            resault=compare_resault_over;
        }
        Log.i(LOG_TAG, "compareList,resault="+resault);
        return resault;
    }
}

