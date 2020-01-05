package com.xxun.watch.xunchatroom.control;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.xiaoxun.sdk.XiaoXunNetworkManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatNetService {

    String LOG_TAG="chat net";
    static XiaoXunNetworkManager mChatNetService=null;

    public   static void setmChatNetService(XiaoXunNetworkManager mChatNetService) {
        ChatNetService.mChatNetService = mChatNetService;
    }

    public  static XiaoXunNetworkManager getmChatNetService(){
        return ChatNetService.mChatNetService;
    }
}
