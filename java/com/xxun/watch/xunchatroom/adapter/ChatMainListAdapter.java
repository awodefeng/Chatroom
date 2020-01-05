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
import com.xxun.watch.xunchatroom.util.AsyncPhotoLoader;
import com.xxun.watch.xunchatroom.util.FileCache;
import com.xxun.watch.xunchatroom.util.ImageUtil;
import com.xxun.watch.xunchatroom.util.MemoryCache;
import com.xxun.watch.xunchatroom.util.OnRefreshListener;
import java.io.File;
import com.xxun.watch.xunchatroom.activity.CircleDrawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.graphics.drawable.AnimationDrawable;

public class ChatMainListAdapter extends BaseAdapter  {
    private Activity mContext;
    private ArrayList<ChatListItemInfo> mListDte = null;
    private LayoutInflater mInflater = null;
    private int max_display_length=2;
    private static final int max_text_length=6;
    private AsyncPhotoLoader photoLoader;
    private String TAG="ChatMainListAdapter";
    public ChatMainListAdapter(Activity mContext, ArrayList<ChatListItemInfo>listDte)
    {
        this.mContext=mContext;
        this.mListDte=listDte;
        this.mInflater = LayoutInflater.from(mContext);
	MemoryCache mcache=new MemoryCache();//?š²¡ä??o¡ä?
	photoLoader = new AsyncPhotoLoader(mContext,mcache);
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
   	Log.i("chat","draw list item"+i);
	if(mListDte.get(i).getListItemType()==1) {
		Log.i("chat","draw local item");
		if(mListDte.get(i).getContentType()==ChatListItemInfo.content_type_image){
			Log.i("chat","item is image "+mListDte.get(i).getContentType());
		        view = mInflater.inflate(R.layout.main_list_item_local_image, null);

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
			
			delegateArea.top -= 10;
			delegateArea.left -= 70;
			delegateArea.bottom += 120;
			delegateArea.right += 40;
			
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


		}else if(mListDte.get(i).getContentType() == 1||mListDte.get(i).getContentType() == 3||mListDte.get(i).getContentType() == 0){
			Log.i("chat","item is common "+mListDte.get(i).getContentType());
		        view = mInflater.inflate(R.layout.main_list_item_local_common, null);
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
	
			delegateArea.top -= 10;
			delegateArea.left -= 70;
			delegateArea.bottom += 120;
			delegateArea.right += 40;
			
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


		}else if(mListDte.get(i).getContentType() == 4){
			Log.i("chat","item is photo "+mListDte.get(i).getContentType());
			view = mInflater.inflate(R.layout.main_list_item_local_photo, null);
			ImageView face_img = (ImageView) view.findViewById(R.id.local_face_img_id);
			ChatGifView loadView=(ChatGifView) view.findViewById(R.id.local_load_img_id);
			String imagePath=mListDte.get(i).getFilePath();
			//String imagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ mListDte.get(i)+".jpg";
			Log.i("chat","photo imagePath="+imagePath);
			/*
			 Bitmap ib= BitmapFactory.decodeFile(imagePath);
			Bitmap nb=getBitmap(ib,60,60);
			if(nb!=null)
			face_img.setImageBitmap(nb); */
			Bitmap nb=photoLoader.loadBitmap(face_img,loadView,imagePath,R.mipmap.load_local,new OnRefreshListener(){
				public void onRefresh(){
					Log.i(TAG,"onRefreshlistener 2");
					if(mOnItemRefreshListener!=null){
						Log.i(TAG,"onRefreshlistener 3");
						mOnItemRefreshListener.onRefresh(i);
					}
				}
			});
			if(nb!=null){
				loadView.setVisibility(View.GONE);
				face_img.setVisibility(View.VISIBLE);
			face_img.setImageBitmap(nb);
			}else{
			 //face_img.setImageResource(R.drawable.face_up);
				face_img.setVisibility(View.GONE);
				loadView.setVisibility(View.VISIBLE);
				loadView.setMovieResource(R.mipmap.load_local);
			}
			face_img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnItemPhotoListener.onPhotoClick(i);
				}
			});
		}else if(mListDte.get(i).getContentType() == 5){
			Log.i("chat","item is video "+mListDte.get(i).getContentType());
			view = mInflater.inflate(R.layout.main_list_item_local_video, null);
			ImageView face_img = (ImageView) view.findViewById(R.id.local_face_img_id);
			ChatGifView loadView=(ChatGifView) view.findViewById(R.id.local_load_img_id);
/*
			String path  = Environment.getExternalStorageDirectory().getPath();
			MediaMetadataRetriever media = new MediaMetadataRetriever();
			media.setDataSource(mListDte.get(i).getFilePath()); 
			Bitmap bitmap = media.getFrameAtTime(); 
			//image = (ImageView)this.findViewById(R.id.imageView1);
			Bitmap nb=getBitmap(bitmap,60,60);
			if(nb!=null)
			face_img.setImageBitmap(nb); 
*/
			String imagePath=mListDte.get(i).getFilePath();//Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ mListDte.get(i)+".jpg";
			Bitmap nb=photoLoader.loadBitmap(face_img,loadView,imagePath,R.mipmap.load_local,new OnRefreshListener(){
				public void onRefresh(){
					Log.i(TAG,"onRefreshlistener 2");
					if(mOnItemRefreshListener!=null){
						Log.i(TAG,"onRefreshlistener 3");
						mOnItemRefreshListener.onRefresh(i);
					}
				}
			});
			if(nb!=null){
				loadView.setVisibility(View.GONE);
				face_img.setVisibility(View.VISIBLE);
			face_img.setImageBitmap(nb);
			ImageView play_img = (ImageView) view.findViewById(R.id.local_play_img_id);
			if(play_img!=null){
			 play_img.setImageResource(R.mipmap.ic_play);
			}
			}else{
			 //face_img.setImageResource(R.drawable.face_up);
				face_img.setVisibility(View.GONE);
				loadView.setVisibility(View.VISIBLE);
				loadView.setMovieResource(R.mipmap.load_local);
			}
			face_img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnItemPhotoListener.onPhotoClick(i);
				}
			});
		}

	}else{
		Log.i("chat","draw peer item");
		 if(mListDte.get(i).getContentType()==ChatListItemInfo.content_type_image){
			Log.i("chat","item is image "+mListDte.get(i).getContentType());
			view = mInflater.inflate(R.layout.main_list_item_peer_image, null);

			ImageView bg_img = (ImageView) view.findViewById(R.id.peer_img_bg_id);
		        if (bg_img != null)
		            bg_img.setImageResource(R.mipmap.peer_bg);
			 

                ImageView face_img = (ImageView) view.findViewById(R.id.peer_face_img_id);
                int img_id=R.mipmap.face_icon_1+Integer.parseInt(mListDte.get(i).getFilePath());
                Log.i("chat", "face image index="+Integer.parseInt(mListDte.get(i).getFilePath()));
                if (face_img != null) {
                    face_img.setImageResource(img_id);
                }else{
                    Log.i("chat", "face_img is null");
                }


		TextView nameTv=(TextView)view.findViewById(R.id.peer_name_tv_id);
		Log.i("chat", "nickname="+mListDte.get(i).getNickName());
		if(nameTv!=null)
		nameTv.setText(mListDte.get(i).getNickName());

		}else if(mListDte.get(i).getContentType() == 1||mListDte.get(i).getContentType() == 3||mListDte.get(i).getContentType() == 0){
			view = mInflater.inflate(R.layout.main_list_item_peer_common, null);
		Log.i("chat","item is common "+mListDte.get(i).getContentType());
		TextView nameTv=(TextView)view.findViewById(R.id.peer_name_tv_id);
		if(nameTv!=null)
		nameTv.setText(mListDte.get(i).getNickName());
		Log.i("chat", "nickname="+mListDte.get(i).getNickName());
            ImageView bg_img = (ImageView) view.findViewById(R.id.peer_img_bg_id);
                if (bg_img != null)
                    bg_img.setImageResource(R.mipmap.peer_bg);
                ImageView img = (ImageView) view.findViewById(R.id.peer_img_view_id);
                TextView title = (TextView) view.findViewById(R.id.peer_time_tv_id);
                // ChatGifView gv = (ChatGifView)view.findViewById(R.id.gif1);


                //if (img != null)
                //    img.setImageResource(photoID);
                if (title != null) {
                    if (mListDte.get(i).getContentType() == 1||mListDte.get(i).getContentType() == 3) {
                        String text_file=mListDte.get(i).getFilePath();
                        byte[] data= ChatUtil.ChatUtilReadDataFromFile(text_file);
			if(data!=null){
                        String data_text=new String(data);
			String show_text=null;

				max_display_length=max_text_length;
			
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

		}else if(mListDte.get(i).getContentType() == 4){
			Log.i("chat","item is photo "+mListDte.get(i).getContentType());
			view = mInflater.inflate(R.layout.main_list_item_peer_photo, null);
			ImageView face_img = (ImageView) view.findViewById(R.id.peer_face_img_id);
			 ChatGifView loadView=(ChatGifView) view.findViewById(R.id.peer_load_img_id);
			TextView nameTv=(TextView)view.findViewById(R.id.peer_name_tv_id);
			if(nameTv!=null)
			nameTv.setText(mListDte.get(i).getNickName());
			Log.i("chat", "nickname="+mListDte.get(i).getNickName());
			String imagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ mListDte.get(i).getmDate()+".jpg";
			Log.i("chat","photo imagePath="+imagePath);
/*
			File imageFile=new File(imagePath);
			if(imageFile.exists()){
			Log.i("chat","imageFile is ok");
			 Bitmap ib= BitmapFactory.decodeFile(imagePath);
			if(ib==null){
			  Log.i("chat","bitmap is null");
			}else{
			  Log.i("chat","bitmap is ok");
			Bitmap nb=getBitmap(ib,60,60);
			if(nb!=null)
			face_img.setImageBitmap(nb); 
			}
			}else{
			Log.i("chat","imageFile is missing");
			}
*/
			//String imagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ mListDte.get(i)+".jpg";
			Bitmap nb=photoLoader.loadBitmap(face_img,loadView,imagePath,R.mipmap.load_peer,new OnRefreshListener(){
				public void onRefresh(){
					Log.i(TAG,"onRefreshlistener 2");
					if(mOnItemRefreshListener!=null){
						Log.i(TAG,"onRefreshlistener 3");
						mOnItemRefreshListener.onRefresh(i);
					}
				}
			});
			if(nb!=null){
				//face_img.setImageResource(R.drawable.loading);
				//AnimationDrawable animationDrawable = (AnimationDrawable) face_img.getDrawable();
				//animationDrawable.stop();
				loadView.setVisibility(View.GONE);
				face_img.setVisibility(View.VISIBLE);
			face_img.setImageBitmap(nb);
			}else{
			 //face_img.setImageResource(R.drawable.face_up);
				//face_img.setImageResource(R.drawable.loading);
				//AnimationDrawable animationDrawable = (AnimationDrawable) face_img.getDrawable();
				//animationDrawable.start();
				face_img.setVisibility(View.GONE);
				loadView.setVisibility(View.VISIBLE);
				loadView.setMovieResource(R.mipmap.load_peer);
			}
			 face_img.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 mOnItemPhotoListener.onPhotoClick(i);
				 }
			 });
			 loadView.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 mOnItemPhotoListener.onPhotoClick(i);
				 }
			 });
		}else if(mListDte.get(i).getContentType() == 5){
			Log.i("chat","item is video "+mListDte.get(i).getContentType());
			view = mInflater.inflate(R.layout.main_list_item_peer_video, null);
			TextView nameTv=(TextView)view.findViewById(R.id.peer_name_tv_id);
			 ChatGifView loadView=(ChatGifView) view.findViewById(R.id.peer_load_img_id);
			if(nameTv!=null)
			nameTv.setText(mListDte.get(i).getNickName());
			Log.i("chat", "nickname="+mListDte.get(i).getNickName());

			ImageView face_img = (ImageView) view.findViewById(R.id.peer_face_img_id);
/*
			String path  = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ mListDte.get(i).getmDate()+".mp4";//Environment.getExternalStorageDirectory().getPath();		
			Log.i("chat", "path="+path);
			MediaMetadataRetriever media = new MediaMetadataRetriever();
			File imageFile=new File(path);
			if(imageFile.exists()){
			media.setDataSource(path); 
			Bitmap bitmap = media.getFrameAtTime(); 
			Log.i("chat","imageFile is ok");
			//image = (ImageView)this.findViewById(R.id.imageView1);
				if(bitmap!=null){
				Log.i("chat","bitmap is ok");
				Bitmap nb=getBitmap(bitmap,60,60);
				if(nb!=null)
				face_img.setImageBitmap(nb);
				}else{
				Log.i("chat","bitmap is null");
				}
			}else{
			Log.i("chat","imageFile is missing");
			}
*/
			String imagePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"chat_audio"+"/"+ mListDte.get(i).getmDate()+".jpg";
			Bitmap nb=photoLoader.loadBitmap(face_img,loadView,imagePath,R.mipmap.load_peer,new OnRefreshListener(){
				public void onRefresh(){
					Log.i(TAG,"onRefreshlistener 2");
					if(mOnItemRefreshListener!=null){
						Log.i(TAG,"onRefreshlistener 3");
						mOnItemRefreshListener.onRefresh(i);
					}
				}
			});
			if(nb!=null){
				loadView.setVisibility(View.GONE);
				face_img.setVisibility(View.VISIBLE);
			face_img.setImageBitmap(nb);
			ImageView play_img = (ImageView) view.findViewById(R.id.peer_play_img_id);
			if(play_img!=null){
			 play_img.setImageResource(R.mipmap.ic_play);
			}
			}else{
			 //face_img.setImageResource(R.drawable.face_up);
				face_img.setVisibility(View.GONE);
				loadView.setVisibility(View.VISIBLE);
				loadView.setMovieResource(R.mipmap.load_peer);
			}
			 face_img.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 mOnItemPhotoListener.onPhotoClick(i);
				 }
			 });
			 loadView.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 mOnItemPhotoListener.onPhotoClick(i);
				 }
			 });
		}

	}
;
        return view;
    }

 public  Bitmap getBitmap(Bitmap bitmap, int screenWidth,  int screenHight){  
   if(bitmap==null){
	 Log.i("chat","getBitmap,null");
		return null;
	}
  int w = bitmap.getWidth();  
  int h = bitmap.getHeight();  
   Log.i("chat","getBitmap,"+w+"/"+h);
  Matrix matrix = new Matrix();  
  float scale = (float) screenWidth / w;  
  float scale2 = (float) screenHight / h;  
  // scale = scale < scale2 ? scale : scale2;  
  matrix.postScale(scale, scale);  
  Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);  
  if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled())  
  {  
   bitmap.recycle();  
   bitmap = null;  
  }  
  return bmp;// Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);  
 } 

public interface onItemResentListener {
        void onResentClick(int i);
    }

    private onItemResentListener mOnItemResentListener;

    public void setOnItemResentClickListener(onItemResentListener mOnItemResentListener) {
        this.mOnItemResentListener = mOnItemResentListener;
    }

public interface onItemRefreshListener {
        void onRefresh(int i);
    }

    private onItemRefreshListener mOnItemRefreshListener;

    public void setOnItemRefreshListener(onItemRefreshListener mOnItemRefreshListener) {
        this.mOnItemRefreshListener = mOnItemRefreshListener;
    }

	public interface onItemPhotoListener {
		void onPhotoClick(int i);
	}

	private onItemPhotoListener mOnItemPhotoListener;

	public void setOnItemPhotoClickListener(onItemPhotoListener mOnItemPhotoListener) {
		this.mOnItemPhotoListener = mOnItemPhotoListener;
	}
}
