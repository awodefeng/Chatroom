package com.xxun.watch.xunchatroom.activity;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

public class ContractListActivityTest extends Activity{
    /*
     TextView my_tv;
     Button my_button;

    Button my_add_btn_1;
    Button my_add_btn_2;
    Button my_del_btn;
    Button my_print_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_list);
        my_tv=(TextView)findViewById(R.id.text_view_id);
        my_button=(Button)findViewById(R.id.button_id);
        my_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xun_enter_chatroom_main_windows();
            }
        });

        my_add_btn_1=(Button)findViewById(R.id.add_btn_1);
        my_add_btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatListItemInfo new_item=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",1,ChatListItemInfo.list_tiem_state_null,"2342344");
                ChatListDB.getInstance(getApplicationContext()).addChatMsg("1234567890",new_item);
            }
        });

        my_add_btn_2=(Button)findViewById(R.id.add_btn_2);
        my_add_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        my_del_btn=(Button)findViewById(R.id.del_btn);
        my_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatListItemInfo new_item=new ChatListItemInfo("13918345010","81952901234",8,"d://audio",1,ChatListItemInfo.list_tiem_state_null,"2342344");
                ChatListDB.getInstance(getApplicationContext()).delChatMsg("1234567890",new_item);
            }
        });

        my_print_btn=(Button)findViewById(R.id.print_btn);
        my_print_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChatListItemInfo new_item=ChatListDB.getInstance(getApplicationContext()).getLatestMessage("1234567890");
                String print_buff="";
                if(new_item!=null) {
                    print_buff += "db is" + new_item.getmDate();
                    my_tv.setText(print_buff);
                }
                else
                {
                    Log.e("chat db","item is null");
                }
            }
        });
    }

    void xun_enter_chatroom_main_windows(){
            Intent my_intent=new Intent();
            my_intent.setClass(this,ChatroomMainActivity.class);
            startActivity(my_intent);
    }
*/
}
