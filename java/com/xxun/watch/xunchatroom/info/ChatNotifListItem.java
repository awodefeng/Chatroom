

package com.xxun.watch.xunchatroom.info;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class ChatNotifListItem
{

  static String LOG_TAG="chat notif mgr";
    String mGID;
    String mDate;
    private String avatar;

    public ChatNotifListItem(String gid,String date){
        this.mDate=date;
        this.mGID=gid;
    }

    public void setGID(String gid){
        this.mGID=gid;
    }

    public String getGID(){
        return this.mGID;
    }

    public void setDate(String date){
        this.mDate=date;
    }

    public String getDate(){
        return this.mDate;
    }

    public  void setContractItemAvatar(String avatar){
       this.avatar= avatar;
    }

    public  String getContractItemAvatar(){
        return this.avatar;
    }

}

