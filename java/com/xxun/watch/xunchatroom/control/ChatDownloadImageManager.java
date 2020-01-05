

package com.xxun.watch.xunchatroom.control;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.info.ChatNoticeInfo;
import com.xxun.watch.xunchatroom.info.ChatNotifListItem;
import com.xxun.watch.xunchatroom.util.ChatUtil;
import com.xxun.watch.xunchatroom.util.WatchSystemInfo;
import android.content.Intent;
import java.util.ArrayList;
import com.xxun.watch.xunchatroom.info.ChatRecieverImage;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;

public class ChatDownloadImageManager
{
  static String TAG="ChatDownloadImageManager";
  static ArrayList<ChatRecieverImage> downloadList=new ArrayList<ChatRecieverImage>();
  static int downloadStatus=0;//0:idle  1;work 2


  static ChatThreadTimer downloadThread=new ChatThreadTimer(1000, new ChatThreadTimer.TimerInterface() {
                @Override
                public void doTimerOut() {
                    Log.i(TAG, "doTimerOut");
                 
                }
            });

  static void addDownloadImage(ChatRecieverImage downloadimage){
	Log.i(TAG,"ChatDownloadImageManager,downloadStatus="+downloadStatus);
	downloadList.add(downloadimage);
	
	if(downloadStatus==0){
	    downloadStatus=1;
	    downloadThread.start();
	}
}

public static int getChatDownloadImageManagerStatus(){
	Log.i(TAG,"getChatDownloadImageManagerStatus,downloadStatus="+downloadStatus);
	return downloadStatus;

}


  static ChatRecieverImage getDownloadImageBySN(int sn){
	Log.i(TAG,"getDownloadImageBySN,sn="+sn);
        for(ChatRecieverImage image:downloadList){
		if(image.getRecieverImageSN()==sn){
			Log.i(TAG,"getDownloadImageBySN,find it");
			return image;
		}
	}
	return null;
}

  static void deleteDownloadImageBySN(int sn){
	Log.i(TAG,"deleteDownloadImageBySN,sn="+sn);
	int findIndex=0;
        for(ChatRecieverImage image:downloadList){
		if(image.getRecieverImageSN()==sn){
			Log.i(TAG,"getDownloadImageBySN,find it");
			//return image;
			downloadList.remove(findIndex);
			break;
		}
	 findIndex++;
	}
}

  static ChatRecieverImage getDownloadImageByID(long id){
	Log.i(TAG,"getDownloadImageByID,id="+id);
        for(ChatRecieverImage image:downloadList){
		if(image.getRecieverImageID()==id){
			Log.i(TAG,"getDownloadImageByID,find it");
			return image;
		}
	}
	return null;
}

  static void deleteDownloadImageByID(long id){
	Log.i(TAG,"deleteDownloadImageByID,id="+id);
	int findIndex=0;
        for(ChatRecieverImage image:downloadList){
		if(image.getRecieverImageID()==id){
			Log.i(TAG,"deleteDownloadImageByID,find it");
			downloadList.remove(findIndex);
			break;
			//return image;
		}
		findIndex++;
	}
}

  static ChatRecieverImage getDownloadImageByIndex(int index){
	Log.i(TAG,"getDownloadImageByIndex,index="+index);
	int findIndex=0;
        for(ChatRecieverImage image:downloadList){
		if(findIndex==index){
			Log.i(TAG,"getDownloadImageByIndex,find it");
			return image;
		}
	  findIndex++;
	}
	return null;
}

  static void deleteDownloadImageByIndex(int index){
	Log.i(TAG,"getDownloadImageByIndex,index="+index);
	int findIndex=0;
        for(ChatRecieverImage image:downloadList){
		if(findIndex==index){
			Log.i(TAG,"getDownloadImageByIndex,find it");
			downloadList.remove(findIndex);
			break;
			//return image;
		}
	  findIndex++;
	}
}
}

