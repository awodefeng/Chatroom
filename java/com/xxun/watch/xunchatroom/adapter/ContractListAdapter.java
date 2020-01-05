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
import com.xxun.watch.xunchatroom.info.ContractListItemInfo;
import com.xxun.watch.xunchatroom.util.ChatUtil;

import java.util.ArrayList;
import com.xxun.watch.xunchatroom.activity.FoucusTextView;
import com.xxun.watch.xunchatroom.util.AsyncImageLoader;
import com.xxun.watch.xunchatroom.util.FileCache;
import com.xxun.watch.xunchatroom.util.ImageUtil;
import com.xxun.watch.xunchatroom.util.MemoryCache;
import java.io.File;
import com.xxun.watch.xunchatroom.activity.CircleDrawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ContractListAdapter extends BaseAdapter  {
    private Activity mContext;
    private ArrayList<ContractListItemInfo> mListDte = null;
    private LayoutInflater mInflater = null;
    private AsyncImageLoader imageLoader;
    public ContractListAdapter(Activity mContext, ArrayList<ContractListItemInfo>listDte)
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




            //view.setBackgroundResource(R.mipmap.peer_bg);
            if(view==null) {
                Log.e("chat","contract view is null");
                view = mInflater.inflate(R.layout.contract_list_view, null);
            }
            ImageView photo_img= (ImageView)view.findViewById(R.id.photo_img);
            if(photo_img!=null) {
                int img_id=0;
                img_id= ChatUtil.ChatUitilGetPhotoByAttr(mListDte.get(i).getContractItemAttr());
		String url = mListDte.get(i).getContractItemAvatar();
		Bitmap bitmap = null;
		Log.i("chat","url ="+url);
		if(url != null){
		       bitmap = imageLoader.loadBitmap(photo_img, url.replace("http","https"),img_id);
		        if(bitmap !=null){
		                photo_img.setImageDrawable(new CircleDrawable(bitmap));
		        }else{
				photo_img.setImageResource(img_id);
			}
		}else{
		       //viewHolder.img.setImageResource(getPhotoPic(mdata.get(position).attri,false));
		 photo_img.setImageResource(img_id);
		}
               
            }
            ImageView img = (ImageView)view.findViewById(R.id.count_img);
            FoucusTextView name = (FoucusTextView)view.findViewById(R.id.name_tv);
            // ChatGifView gv = (ChatGifView)view.findViewById(R.id.gif1);
            int mCount=mListDte.get(i).getContractItemMissCount();
	    Log.i("chat","mCount ="+mCount);
            if(img!=null&&mCount>0) {
		img.setVisibility(View.VISIBLE);
                if(mCount>9) {
                    img.setImageResource(R.mipmap.small_miss_more);
                }
                else if(mCount>0&&mCount<=9){
                    img.setImageResource(R.mipmap.small_miss_1+mCount-1);
                }

            }else{
		img.setVisibility(View.INVISIBLE);
		}
            if(name!=null)
                name.setText(mListDte.get(i).getContractItemName());
            //gv.setMovieResource(R.mipmap.waiting);


        return view;
    }





        }
