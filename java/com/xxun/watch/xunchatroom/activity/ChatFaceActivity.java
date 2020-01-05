package com.xxun.watch.xunchatroom.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import com.xxun.watch.xunchatroom.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatFaceActivity extends Activity {

    String LOG_TAG="chat face";
    GridView my_gv;
    Button cancel_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_face);

        my_gv=(GridView)findViewById(R.id.chat_face_grid);
        cancel_button=(Button)findViewById(R.id.chat_face_btn);

        ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();

        for(int i = 1;i < 10;i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.mipmap.face_icon_1+i-1);
            meumList.add(map);
        }

        SimpleAdapter saMenuItem = new SimpleAdapter(this,
                meumList, //数据源
                R.layout.face_item_view, //xml实现
                new String[]{"ItemImage"}, //对应map的Key
                new int[]{R.id.ItemImage});  //对应R的Id

//添加Item到网格中
        my_gv.setAdapter(saMenuItem);
        my_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(LOG_TAG,"GridView click index="+i);
                Intent intent = new Intent();
                intent.putExtra("face_choice", i);
                /*
                 * 调用setResult方法表示我将Intent对象返回给之前的那个Activity，这样就可以在onActivityResult方法中得到Intent对象，
                 */
                setResult(1001, intent);
                finish();
            }
        });

        cancel_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                        //Toast.makeText(ChatFaceActivity.this, "cancel btn down", Toast.LENGTH_SHORT).show();
                        cancel_button.setBackgroundResource(R.mipmap.cancel_down);
                        break;

                    case MotionEvent.ACTION_UP:
                        //Toast.makeText(ChatFaceActivity.this, "cancel btn up", Toast.LENGTH_SHORT).show();
                        cancel_button.setBackgroundResource(R.mipmap.cancel_up);
                        finish();
                        break;
                }
                return false;
            }
        });
    }

}
