package com.xxun.watch.xunchatroom.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.TextView;
import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.swipeback.SwipeBackController;
import com.xxun.watch.xunchatroom.adapter.ChatSmsListAdapter;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;
import com.xxun.watch.xunchatroom.control.ChatContractManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatSmsActivity extends Activity {

    String LOG_TAG="chat sms";
    ListView sms_list_view;
    TextView empty_tv;
    private ChatSmsListAdapter adapter ;//= new ChatListAdapter();
    ArrayList<ChatListItemInfo> smsListDte=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);
        sms_list_view= (ListView)findViewById(R.id.sms_lv);
	empty_tv= (TextView)findViewById(R.id.empty_tv);
	sms_list_view.setEmptyView(empty_tv);
        initSmsViews();
        adapter = new ChatSmsListAdapter(ChatSmsActivity.this, smsListDte);
        sms_list_view.setAdapter(adapter);
        sms_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(LOG_TAG,"sms tap i="+i);
                ChatListItemInfo tap_item=smsListDte.get(i);
                tap_item.setIsPlayed(1);
                ChatListDB.getInstance(getApplicationContext()).updateChatMsg(WatchSystemInfo.getSmsGID(),tap_item,tap_item.getmDate());
		ChatContractManager.updateAllMissChatCount(ChatSmsActivity.this);
                Intent my_intent =new Intent();
                my_intent.putExtra("Content",tap_item.getFilePath());
                my_intent.setClass(ChatSmsActivity.this,ChatTextContentActivity.class);
                startActivity(my_intent);
            }
        });


    }


    void initSmsViews(){
        if(smsListDte==null) {
            smsListDte = new ArrayList<ChatListItemInfo>();
        }else{
            smsListDte.clear();
        }
        ChatListDB.getInstance(getApplicationContext()).readAllChatFromFamily(WatchSystemInfo.getSmsGID(),smsListDte,smsListDte);
    }

    protected void onResume(){
        super.onResume();
        Log.i("LOG_TAG","ChatSmsActivity,onResume");
        if(adapter!=null) {
            if(smsListDte!=null) {
                Log.i("LOG_TAG","onResume,count="+smsListDte.size());
                smsListDte.clear();
                Log.i("LOG_TAG","onResume,count="+smsListDte.size());
                initSmsViews();
                Log.i("LOG_TAG","onResume,count="+smsListDte.size());
            }
            adapter.notifyDataSetChanged();
        }
    }
}
