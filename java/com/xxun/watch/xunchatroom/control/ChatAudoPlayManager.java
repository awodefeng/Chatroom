

package com.xxun.watch.xunchatroom.control;

import android.content.Context;
import android.util.Log;

import com.xxun.watch.xunchatroom.database.ChatListDB;
import com.xxun.watch.xunchatroom.info.ChatListItemInfo;

import java.util.ArrayList;

public class ChatAudoPlayManager
{
    static String LOG_TAG="auto play";
    static ArrayList<ChatListItemInfo> autoPlayList=null;
    static int curIndex=0;
    public static void initAutoPlay(ArrayList<ChatListItemInfo> playList,String date){
        Log.i(LOG_TAG,"initAutoPlay");
        autoPlayList=playList;
	curIndex=0;
        if(autoPlayList==null||date==null){
            Log.i(LOG_TAG,"initAutoPlay,null return");
            return;
        }
        boolean isPass=false;
        for(ChatListItemInfo info:autoPlayList){
            if(date.equals(info.getmDate())){
                isPass=true;
            }

            if(isPass&&info.getIsPlayed()==0&&info.getContentType()==0){
                break;
            }
            curIndex++;
        }
        Log.i(LOG_TAG,"initAutoPlay,curIndex="+curIndex);
    }

  public static int getNextAutoPlayItem(){
      Log.i(LOG_TAG,"getNextAutoPlayItem,curIndex="+curIndex);
      int playIndex=-1;
      int i=0;
      boolean isFind=false;
      for(i=curIndex;i<autoPlayList.size();i++){
          ChatListItemInfo info=autoPlayList.get(i);
          if(info.getIsPlayed()==0&&info.getContentType()==0){
              isFind=true;
              break;
          }
      }
      if(isFind){
          curIndex=i;
          playIndex=curIndex;
      }
      else {
          curIndex=0;
          playIndex=-1;
      }
      Log.i(LOG_TAG,"getNextAutoPlayItem,playIndex="+playIndex);
      return playIndex;
  }
}

