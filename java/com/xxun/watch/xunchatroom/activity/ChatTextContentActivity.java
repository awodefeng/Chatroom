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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.swipeback.SwipeBackController;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import com.xxun.watch.xunchatroom.util.ChatUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatTextContentActivity extends Activity {

    String LOG_TAG="chat text content";
    TextView content_view;
    SwipeBackController swipeBackController=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_content);

        initContentViews();
        //swipeBackController = new SwipeBackController(this);
    }
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i(LOG_TAG,"ChatSmsActivity,onTouchEvent");
/*
        if (swipeBackController.processEvent(ev)) {
            return true;
        } else {
            return super.onTouchEvent(ev);
        }*/
	return super.onTouchEvent(ev);
    }
    void initContentViews(){
        content_view=(TextView)findViewById(R.id.text_content_tv);
        Intent my_intent=getIntent();
        String file_path=my_intent.getStringExtra(ChatKeyString.KEY_CONTENT);
        byte[] data= ChatUtil.ChatUtilReadDataFromFile(file_path);
	if(data!=null){
        String show_text=new String(data);
        content_view.setText(show_text);
	}
    }
}
