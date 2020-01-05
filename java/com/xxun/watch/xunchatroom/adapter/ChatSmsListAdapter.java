package com.xxun.watch.xunchatroom.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.activity.FoucusTextView;

import java.util.ArrayList;

public class ChatSmsListAdapter extends BaseAdapter  {
    private Activity mContext;
    private ArrayList<ChatListItemInfo> mListDte = null;
    private LayoutInflater mInflater = null;
    public ChatSmsListAdapter(Activity mContext, ArrayList<ChatListItemInfo>listDte)
    {
        this.mContext=mContext;
        this.mListDte=listDte;
        this.mInflater = LayoutInflater.from(mContext);
    }
            public int getCount() {
                return this.mListDte.size();
            }
            public Object getItem(int pos) {
                return mListDte.get(pos);
            }
            public long getItemId(int pos) { return pos; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        /*
        TextView tview;
        if(view!=null)
        {
            tview=(TextView)view;
        }
         else
        {
            tview=new TextView( this.mContext);
        }

        tview.setText("ListItem " + 1);
        //tview.setTextSize(20f);
        tview.setGravity(Gravity.CENTER);
        tview.setHeight(120);
        return tview;
*/
       // View item;
        Log.e("chat","getView,i=%d");



           ChatListItemInfo show_item=mListDte.get(i);
            //view.setBackgroundResource(R.mipmap.peer_bg);
           // if(view==null) {
                Log.e("chat","contract view is null");
                view = mInflater.inflate(R.layout.sms_list_view, null);
          //  }
            ImageView read_img= (ImageView)view.findViewById(R.id.read_img);
            if(read_img!=null) {
                int img_id=0;
                if(show_item.getIsPlayed()==1){
                    img_id=R.mipmap.sms_read;
                }else{
                    img_id=R.mipmap.sms_unread;
                }
                //img_id=ChatUtil.ChatUitilGetPhotoByAttr(mListDte.get(i).getContractItemAttr());
                read_img.setImageResource(img_id);
            }

            FoucusTextView name = (FoucusTextView)view.findViewById(R.id.text_tv);
            // ChatGifView gv = (ChatGifView)view.findViewById(R.id.gif1);

        Log.i("chat","show_item,name="+show_item.getListItemEID());
        Log.i("chat","show_item,gid="+show_item.getListItemGID());
            if(name!=null) {
                Log.i("chat","show_item,show name="+show_item.getListItemGID());
                name.setText(show_item.getListItemEID());
            }
            //gv.setMovieResource(R.mipmap.waiting);


        return view;
    }





        }
