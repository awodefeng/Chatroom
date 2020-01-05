package com.xxun.watch.xunchatroom.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.activity.ChatGifView;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.util.ChatUtil;
import android.view.TouchDelegate;
import android.graphics.Rect;
import java.util.ArrayList;
import com.xiaoxun.sdk.utils.Constant; 
import com.xxun.watch.xunchatroom.util.AsyncImageLoader;
import com.xxun.watch.xunchatroom.util.FileCache;
import com.xxun.watch.xunchatroom.util.ImageUtil;
import com.xxun.watch.xunchatroom.util.MemoryCache;
import java.io.File;
import com.xxun.watch.xunchatroom.activity.CircleDrawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ChatListAdapter extends BaseAdapter  {
    private Activity mContext;
    private ArrayList<ChatListItemInfo> mListDte = null;
    private LayoutInflater mInflater = null;
    private int max_display_length=2;
    private static final int max_text_length=6;
    private AsyncImageLoader imageLoader;
    public ChatListAdapter(Activity mContext, ArrayList<ChatListItemInfo>listDte)
    {
        this.mContext=mContext;
        this.mListDte=listDte;
        this.mInflater = LayoutInflater.from(mContext);
      	MemoryCache mcache=new MemoryCache();//内存缓存
        File sdCard = android.os.Environment.getExternalStorageDirectory();//获得SD卡
        File cacheDir = new File(sdCard, "xiaoxun_cache" );//缓存根文件夹
        FileCache fcache=new FileCache(mContext, cacheDir, "photo_img");//文件缓存
        imageLoader = new AsyncImageLoader(mContext, mcache,fcache);
    }
            public int getCount() {
	        Log.i("chat","getCount="+mListDte.size());
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
	boolean isSW730 = "SW730".equals(Constant.PROJECT_NAME);
        Log.e("chat","getView,i="+i);
        Log.e("chat","getView,is_local="+mListDte.get(i).getListItemType());
        if(mListDte.get(i).getListItemType()==1) {
            Log.e("chat","draw local item "+i);
            if(mListDte.get(i).getContentType()==ChatListItemInfo.content_type_image){
                Log.e("chat", "local view is null");
                view = mInflater.inflate(R.layout.main_list_view_local_image, null);

                ImageView bg_img = (ImageView) view.findViewById(R.id.local_img_bg_id);
                if (bg_img != null)
                    bg_img.setImageResource(R.mipmap.local_bg);

                ImageView face_img = (ImageView) view.findViewById(R.id.local_face_img_id);
                int img_id=R.mipmap.face_icon_1+Integer.parseInt(mListDte.get(i).getFilePath());
                Log.i("chat", "face image index="+Integer.parseInt(mListDte.get(i).getFilePath()));
                if (face_img != null) {
                    face_img.setImageResource(img_id);
                }else{
                    Log.i("chat", "face_img is null");
                }

                if (mListDte.get(i).getSendState() == 0) {
                    ImageView send_img = (ImageView) view.findViewById(R.id.local_img_send_id);
                    if (send_img != null){
                        send_img.setImageResource(R.mipmap.send_fail);
       Rect delegateArea = new Rect();
                       ImageView delegate = send_img;
                        // Hit rectangle in parent's coordinates
                       delegate.getHitRect(delegateArea);
                 Log.i("chat", "send_img old rect:"+delegateArea.left+","+delegateArea.top+","+delegateArea.right+","+delegateArea.bottom);
		if(isSW730){
		delegateArea.top -= 20;
		delegateArea.left -= 100;
		delegateArea.bottom += 140;
		delegateArea.right += 60;
		}else{
		delegateArea.top -= 10;
		delegateArea.left -= 70;
		delegateArea.bottom += 120;
		delegateArea.right += 40;
		}
		Log.i("chat", "send_img new rect:"+delegateArea.left+","+delegateArea.top+","+delegateArea.right+","+delegateArea.bottom);
        TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);
                        if(View.class.isInstance(delegate.getParent())){
                               // 设置视图扩大后的触摸区域
                              ((View)delegate.getParent()).setTouchDelegate(expandedArea);
                            }
			send_img.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
				mOnItemResentListener.onResentClick(i);
			    }
			});
		}
                }
            }else {
                //if(view==null) {
                Log.e("chat", "local view is null");
                view = mInflater.inflate(R.layout.main_list_view_local, null);
                // }
                ImageView bg_img = (ImageView) view.findViewById(R.id.local_img_bg_id);
                if (bg_img != null)
                    bg_img.setImageResource(R.mipmap.local_bg);

                TextView title = (TextView) view.findViewById(R.id.local_time_tv_id);
                // ChatGifView gv = (ChatGifView)view.findViewById(R.id.gif1);
                if (title != null)
                    title.setText(mListDte.get(i).getListItemTime());

                if (mListDte.get(i).getListItemState() == ChatListItemInfo.list_tiem_state_playing) {
                    ChatGifView gv = (ChatGifView) view.findViewById(R.id.gif_play_local);
                    gv.setVisibility(View.VISIBLE);
                    gv.setMovieResource(R.mipmap.play_local);
                } else {
                    ChatGifView gv = (ChatGifView) view.findViewById(R.id.gif_play_local);
                    gv.setVisibility(View.INVISIBLE);
                    //gv.setMovieResource(R.mipmap.play_local);
                }

                if (mListDte.get(i).getSendState() == 0) {
                    ImageView send_img = (ImageView) view.findViewById(R.id.local_img_send_id);
                    if (send_img != null){
                        send_img.setImageResource(R.mipmap.send_fail);
        Rect delegateArea = new Rect();
                       ImageView delegate = send_img;
                        // Hit rectangle in parent's coordinates
                       delegate.getHitRect(delegateArea);
		 Log.i("chat", "send_img old rect:"+delegateArea.left+","+delegateArea.top+","+delegateArea.right+","+delegateArea.bottom);
		if(isSW730){
		delegateArea.top -= 20;
		delegateArea.left -= 100;
		delegateArea.bottom += 140;
		delegateArea.right += 60;
		}else{
		delegateArea.top -= 10;
		delegateArea.left -= 70;
		delegateArea.bottom += 120;
		delegateArea.right += 40;
		}
		Log.i("chat", "send_img new rect:"+delegateArea.left+","+delegateArea.top+","+delegateArea.right+","+delegateArea.bottom);
        TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);

                        if(View.class.isInstance(delegate.getParent())){
                               // 设置视图扩大后的触摸区域
                              ((View)delegate.getParent()).setTouchDelegate(expandedArea);
                            }
			send_img.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
				mOnItemResentListener.onResentClick(i);
			    }
			});
		}
                }
            }
        }
        else
        {

            //view.setBackgroundResource(R.mipmap.peer_bg);
	    int photoID=R.mipmap.photo_test;
	    photoID=ChatUtil.ChatUitilGetPhotoByAttr(mListDte.get(i).getPhotoID());
	    if(photoID==0){
		photoID=R.mipmap.photo_test;
	    }
            Log.e("chat","draw peer item "+i);
            if(mListDte.get(i).getContentType()==ChatListItemInfo.content_type_image) {
                Log.e("chat", "peer view is null");
                view = mInflater.inflate(R.layout.main_list_view_peer_image, null);

                ImageView bg_img = (ImageView) view.findViewById(R.id.peer_img_bg_id);
                if (bg_img != null)
                    bg_img.setImageResource(R.mipmap.peer_bg);
		ImageView img = (ImageView) view.findViewById(R.id.peer_img_view_id);
		Log.i("chat","photoID="+photoID);
		if (img != null){
		String url = mListDte.get(i).getContractItemAvatar();
		Bitmap bitmap = null;
		Log.i("chat","url ="+url);
		if(url != null){
		       bitmap = imageLoader.loadBitmap(img, url.replace("http","https"),photoID);
		        if(bitmap !=null){
		                img.setImageDrawable(new CircleDrawable(bitmap));
		        }else{
				img.setImageResource(photoID);
			}
		}else{
		       //viewHolder.img.setImageResource(getPhotoPic(mdata.get(position).attri,false));
		 img.setImageResource(photoID);
		}
		}
		//if (img != null)
                //    img.setImageResource(photoID);
                ImageView face_img = (ImageView) view.findViewById(R.id.peer_face_img_id);
                int img_id=R.mipmap.face_icon_1+Integer.parseInt(mListDte.get(i).getFilePath());
                Log.i("chat", "face image index="+Integer.parseInt(mListDte.get(i).getFilePath()));
                if (face_img != null) {
                    face_img.setImageResource(img_id);
                }else{
                    Log.i("chat", "face_img is null");
                }
            }
            else {

                //if(view==null) {
                Log.e("chat", "peer view is null");
                view = mInflater.inflate(R.layout.main_list_view_peer, null);
                //}
                ImageView bg_img = (ImageView) view.findViewById(R.id.peer_img_bg_id);
                if (bg_img != null)
                    bg_img.setImageResource(R.mipmap.peer_bg);
                ImageView img = (ImageView) view.findViewById(R.id.peer_img_view_id);
                TextView title = (TextView) view.findViewById(R.id.peer_time_tv_id);
                // ChatGifView gv = (ChatGifView)view.findViewById(R.id.gif1);
		Log.i("chat","photoID="+photoID);
		if (img != null){
		String url = mListDte.get(i).getContractItemAvatar();
		Bitmap bitmap = null;
		Log.i("chat","url ="+url);
		if(url != null){
		       bitmap = imageLoader.loadBitmap(img, url.replace("http","https"),photoID);
		        if(bitmap !=null){
		                img.setImageDrawable(new CircleDrawable(bitmap));
		        }else{
				img.setImageResource(photoID);
			}
		}else{
		       //viewHolder.img.setImageResource(getPhotoPic(mdata.get(position).attri,false));
		 img.setImageResource(photoID);
		}
		}
                //if (img != null)
                //    img.setImageResource(photoID);
                if (title != null) {
                    if (mListDte.get(i).getContentType() == 1||mListDte.get(i).getContentType() == 3) {
                        String text_file=mListDte.get(i).getFilePath();
                        byte[] data= ChatUtil.ChatUtilReadDataFromFile(text_file);
			if(data!=null){
                        String data_text=new String(data);
			String show_text=null;
			if(!isSW730){
				max_display_length=max_text_length;
			}
			if(data_text.length()>max_display_length){
				show_text=data_text.substring(0,max_display_length)+"...";		
			}else{
				show_text=data_text;
			}
			Log.i("chat", "peer text,data_text="+data_text);
			Log.i("chat", "peer text,show_text="+show_text);
                        title.setText(show_text);
			}
                    } else {
                        title.setText(mListDte.get(i).getListItemTime());
                    }
                }
                //gv.setMovieResource(R.mipmap.waiting);
                if (mListDte.get(i).getListItemState() == ChatListItemInfo.list_tiem_state_playing) {
                    ChatGifView gv = (ChatGifView) view.findViewById(R.id.gif_play_peer);
                    if (gv != null) {
                        // gv.setVisibility(View.VISIBLE);
                        gv.setMovieResource(R.mipmap.play_peer);
                    }
                } else {
                    ChatGifView gv = (ChatGifView) view.findViewById(R.id.gif_play_peer);
                    // gv.setVisibility(View.INVISIBLE);
                    //gv.setMovieResource(R.mipmap.play_local);
                    gv.setPaused(true);
                }

                if (mListDte.get(i).getIsPlayed() == 0) {
                    ImageView unread_img = (ImageView) view.findViewById(R.id.peer_img_unread_id);
                    if (unread_img != null){
                        unread_img.setImageResource(R.mipmap.unread);

		}
                }
            }
        }

        return view;
    }


public interface onItemResentListener {
        void onResentClick(int i);
    }

    private onItemResentListener mOnItemResentListener;

    public void setOnItemResentClickListener(onItemResentListener mOnItemResentListener) {
        this.mOnItemResentListener = mOnItemResentListener;
    }


        }
