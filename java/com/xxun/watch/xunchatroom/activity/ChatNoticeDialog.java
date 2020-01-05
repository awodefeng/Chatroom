package com.xxun.watch.xunchatroom.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.control.ChatRoomControl;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.info.ChatNoticeInfo;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import com.xxun.watch.xunchatroom.util.ChatUtil;


public class ChatNoticeDialog extends Dialog {
    Button enter_btn;
    Button close_btn;
    ImageView photo_img;
    ImageView peer_bg;
    ImageView unread_img;
    TextView title_tv;
    TextView content_tv;
    ChatGifView notice_gif_view;
    private ChatNoticeInfo notice_info;
    ChatRoomControl mChatContol=null;
    private Context context;
    private Context app_context;
    private TouchListenerInterface touchListenerInterface;
    private ClickListenerInterface clickListenerInterface;
    String LOG_TAG="notic dialog";
         public interface TouchListenerInterface {

                       public void doConfirm();

                        public void doCancel();
         }

    public interface ClickListenerInterface {

        public void doPlay();
    }

    public ChatNoticeDialog(Context context) {
        super(context);
    }

    public ChatNoticeDialog(Context context, Context app_context,ChatNoticeInfo notice_info) {
        super(context,R.style.Dialog);
        this.notice_info=notice_info;
        this.app_context=app_context;
        this.context=context;
        Log.i("LOG_TAG","ChatNoticeDialog");
    }

    @Override
         protected void onCreate(Bundle savedInstanceState) {
                // TODO Auto-generated method stub
                super.onCreate(savedInstanceState);
                Log.i("LOG_TAG","ChatNoticeDialog,onCreate");
                 LayoutInflater inflater = LayoutInflater.from(context);
               View view = inflater.inflate(R.layout.chat_notification_dialog, null);
               setContentView(view);
                initViews();
            }

    void initViews(){
        Log.i("LOG_TAG","ChatNoticeDialog,initViews");
        if(mChatContol==null)
        {
            mChatContol=new ChatRoomControl();
        }

        notice_gif_view=(ChatGifView)findViewById(R.id.gif_play_notice);
        notice_gif_view.setVisibility(View.GONE);

        title_tv=(TextView)findViewById(R.id.notif_title_tv);
        title_tv.setText(notice_info.getNickName());

        content_tv=(TextView)findViewById(R.id.notif_content_tv);

        Log.i(LOG_TAG, "notice_info.getContentType="+notice_info.getContentType());
        if(notice_info.getContentType()==1) {
            byte[] data= ChatUtil.ChatUtilReadDataFromFile(notice_info.getFilePath());
            String show_text=new String(data);
            content_tv.setText(show_text);
        }
        else {
            content_tv.setText(Integer.toString(notice_info.getDuration()));
        }

        peer_bg=(ImageView)findViewById(R.id.notif_peer_bg);
        peer_bg.setImageResource(R.mipmap.peer_bg);

        photo_img=(ImageView)findViewById(R.id.notif_photo_img);
        photo_img.setImageResource(notice_info.getPhotoID());

        if(notice_info.getIsPlayed()==0) {
            unread_img = (ImageView) findViewById(R.id.notif_unread_img);
            unread_img.setVisibility(View.VISIBLE);
            unread_img.setImageResource(R.mipmap.unread);
        }
        else {
            unread_img = (ImageView) findViewById(R.id.notif_unread_img);
            unread_img.setVisibility(View.GONE);
        }

        enter_btn=(Button)findViewById(R.id.notif_enter_btn);
        enter_btn.setOnTouchListener(new touchListener());
        close_btn=(Button)findViewById(R.id.notif_cancel_btn);
        close_btn.setOnTouchListener(new touchListener());
        peer_bg.setOnClickListener(new clickListener());


        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
         DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
         lp.width = (int) (d.widthPixels ); // 高度设置为屏幕的0.6
        lp.height = (int) (d.heightPixels ); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);



    }


    public void setTouchlistener(TouchListenerInterface touchListenerInterface) {
                 this.touchListenerInterface = touchListenerInterface;
            }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    void enter_text_content_window(){
        Intent my_intent=new Intent();
        my_intent.putExtra(ChatKeyString.KEY_CONTENT,notice_info.getFilePath());
        my_intent.setClass(app_context,ChatTextContentActivity.class);
        app_context.startActivity(my_intent);
    }
    private class clickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch(id) {
                case R.id.notif_peer_bg:
                    if(notice_info.getContentType()==1){
                        unread_img.setVisibility(View.GONE);
                        ChatListItemInfo notice_chat = ChatListDB.getInstance(app_context).readOneChatFromFamily(notice_info.getNoticeGID(), notice_info.getmDate());
                        notice_info.setIsPlayed(1);
                        notice_chat.setIsPlayed(1);
                        ChatListDB.getInstance(app_context).updateChatMsg(notice_info.getNoticeGID(), notice_chat, notice_info.getmDate());
                        //enter_text_content_window();
                    }else {
                        notice_gif_view.setVisibility(View.VISIBLE);
                        unread_img.setVisibility(View.GONE);
                        notice_gif_view.setMovieResource(R.mipmap.play_peer);
                        ChatListItemInfo notice_chat = ChatListDB.getInstance(app_context).readOneChatFromFamily(notice_info.getNoticeGID(), notice_info.getmDate());
                        notice_info.setIsPlayed(1);
                        notice_chat.setIsPlayed(1);
                        ChatListDB.getInstance(app_context).updateChatMsg(notice_info.getNoticeGID(), notice_chat, notice_info.getmDate());
			OnCompletionListener complete_listener=new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                notice_gif_view.setVisibility(View.GONE);
                            }
                        };

			OnErrorListener error_listener=new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer,int i,int i1) {
                            Log.i(LOG_TAG,"play error");
                            return false;	
                        }
                    };
                        mChatContol.StartPlaying(notice_info.getFilePath(), complete_listener,error_listener);
                        clickListenerInterface.doPlay();
                    }
                    break;
            }
        }
    }

    private class touchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int id = view.getId();

            switch(id){

                case R.id.notif_enter_btn:
                    switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
                    {
                        case MotionEvent.ACTION_DOWN:
                            enter_btn.setBackgroundResource(R.mipmap.enter_down);
                            break;

                        case MotionEvent.ACTION_UP:
                            enter_btn.setBackgroundResource(R.mipmap.enter_up);
                            touchListenerInterface.doConfirm();
                            break;
                    }
                    break;

                case R.id.notif_cancel_btn:
                    switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
                    {
                        case MotionEvent.ACTION_DOWN:
                            close_btn.setBackgroundResource(R.mipmap.close_down);
                            break;

                        case MotionEvent.ACTION_UP:
                            close_btn.setBackgroundResource(R.mipmap.close_up);
                            touchListenerInterface.doCancel();
                            break;
                    }
                    break;
            }
            return false;
        }
    }
}

