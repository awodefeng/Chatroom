

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

public class ChatNotificationManager
{
  static ArrayList<ChatNotifListItem> notif_list=new ArrayList<ChatNotifListItem>();
  static ArrayList<ChatNoticeInfo> cur_notif_group=new ArrayList<ChatNoticeInfo>();
  public static ChatNoticeInfo curNoticeInfo=new ChatNoticeInfo();
  static String cur_notif_gid=null;
    public static boolean is_work=false;
  static String LOG_TAG="chat notif mgr";
    public  ChatNotificationManager(Context context){

    }

    public static boolean isGroupExsit(String gid){
        boolean is_exsit=false;

        for(ChatNotifListItem notif_item:notif_list){
            if(gid.equals(notif_item.getGID()) ){
                is_exsit=true;
                break;
            }
        }

        return is_exsit;
    }

   public static boolean compareContractInNotifcation(Context context){
	Log.i(LOG_TAG, "compareContractInNotifcation");
	ArrayList<String> gidList=new ArrayList<String>();
	ChatContractManager.getData(context,gidList);
	boolean isRefresh=true;
	for(int index=0;index<notif_list.size();index++){
		ChatNotifListItem listItem=notif_list.get(index);
		boolean isExist=false;
		for(String contractGid:gidList){

			if(contractGid.equals(cur_notif_gid)){
				isRefresh=false;
			}

			if(contractGid.equals(listItem.getGID())){
			isExist=true;
			break;
			}

			
			
		}
		Log.i(LOG_TAG, "isExist="+isExist);
		if(!isExist){
			notif_list.remove(index);
			index--;
		}
	}

	if(isRefresh){
		cur_notif_group.clear();
	}
	Log.i(LOG_TAG, "isRefresh="+isRefresh);
	return isRefresh;
  }

   public static void addGroupByGID(Context context,String gid,ChatListItemInfo chat_info){
	int attri=0;
	String avatar=null;
        if(gid==null){
            return;
        }

        if(!isGroupExsit(gid)){
            ChatNotifListItem notif_item=new ChatNotifListItem(gid,chat_info.getmDate());
            notif_list.add(notif_item);
        }
        is_work=true;
        if(cur_notif_gid==null){
            cur_notif_gid=notif_list.get(0).getGID();
            cur_notif_group.clear();
            //ChatListDB.getInstance(context).readUnreadChatFromFamily(cur_notif_gid,cur_notif_group,chat_info.getmDate());
	/*    
            curNoticeInfo.setmDate(chat_info.getmDate());
            curNoticeInfo.setDuration(chat_info.getDuration());
            curNoticeInfo.setFilePath(chat_info.getFilePath());
            curNoticeInfo.setIsPlayed(chat_info.getIsPlayed());
            curNoticeInfo.setContentType(chat_info.getContentType());
            curNoticeInfo.setNoticeGID(chat_info.getListItemGID());
            curNoticeInfo.setNoticeEID(chat_info.getListItemEID());
            curNoticeInfo.setNoticeState(chat_info.getListItemState());
            curNoticeInfo.setSN(chat_info.getSN());
            curNoticeInfo.setNickName(nick_name);
            curNoticeInfo.setPhotoID(img);*/
        }
	    if(gid.equals(cur_notif_gid)){
            String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
            String name;
            String number;
            Uri uri = Uri.parse ("content://com.android.contacts/contacts");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
            Log.i(LOG_TAG,"perpareNoticeWindow");
            boolean is_find=false;
            String nick_name=null;
            String find_gid=null;
	    String eid=null;
	   // String avater=null;
            int img=R.mipmap.photo_test;
            Log.i(LOG_TAG, "perpareNoticeWindow");
		if(WatchSystemInfo.getSmsGID().equals(chat_info.getListItemGID())){
		Log.i(LOG_TAG, "is sms");
		nick_name=chat_info.getListItemEID();
		img=R.mipmap.sms_unread;
		is_find=true;
		
		}else{
		Log.i(LOG_TAG, "is chat");
            while(cursor.moveToNext()&&!is_find){

                int contactsId = cursor.getInt(0);
                Log.i(LOG_TAG,"contactsId = " +contactsId);
                uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
                Cursor dataCursor = resolver.query(uri, project, null, null, null);
                //SyncArrayBean  arrayBean = new SyncArrayBean();
                while(dataCursor.moveToNext()) {
                    String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                    if ("vnd.android.cursor.item/name".equals(type)) {
                        name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
			avatar = dataCursor.getString(dataCursor.getColumnIndex(project[3]));
                        Log.i(LOG_TAG,"name = " +name);
                        nick_name=name;
			chat_info.setContractItemAvatar(avatar);
                    } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
                    } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                        number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                        Log.i(LOG_TAG,"number = " +number);
                    }else if("vnd.android.cursor.item/nickname".equals(type)){

             	    attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));
                    find_gid=dataCursor.getString(dataCursor.getColumnIndex(project[5]));
		    eid=dataCursor.getString(dataCursor.getColumnIndex(project[4]));
                    Log.i(LOG_TAG, "find_gid = " + find_gid);
		    Log.i(LOG_TAG, "eid = " + eid);
                    if(find_gid!=null) {
                        if (chat_info.getListItemGID().equals(WatchSystemInfo.getWatchGID())) {
			//
				if(chat_info.getListItemEID().equals(eid)){
				img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
				is_find = true;
				}
			
                        } else if (find_gid.equals(chat_info.getListItemGID())) {
                            img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
                            Log.i(LOG_TAG, "attri = " + attri);
                            is_find = true;
                        }
                    }
                    }
                }



                //if(arrayBean != null) mlist_arraybean.add(arrayBean);
            }
            cursor.close();
	   }
            Log.i(LOG_TAG, "is_find="+is_find);

            ChatNoticeInfo nitice_info=new ChatNoticeInfo();
            nitice_info.setmDate(chat_info.getmDate());
            nitice_info.setDuration(chat_info.getDuration());
            nitice_info.setFilePath(chat_info.getFilePath());
            nitice_info.setIsPlayed(chat_info.getIsPlayed());
            nitice_info.setContentType(chat_info.getContentType());
            nitice_info.setNoticeGID(chat_info.getListItemGID());
            nitice_info.setNoticeEID(chat_info.getListItemEID());
            nitice_info.setNoticeState(chat_info.getListItemState());
            nitice_info.setSN(chat_info.getSN());
            nitice_info.setNickName(nick_name);
            nitice_info.setPhotoID(attri);
	    nitice_info.setContractItemAvatar(avatar);

            cur_notif_group.add(nitice_info);
        }
    }

   public static void updateGroupByGID(Context context,String gid,ChatListItemInfo chat_info){
	int attri=0;
	String avatar=null;
	 Log.i(LOG_TAG, "updateGroupByGID,gid="+gid);
        if(gid==null){
            return;
        }

        if(!isGroupExsit(gid)){
            ChatNotifListItem notif_item=new ChatNotifListItem(gid,chat_info.getmDate());
            notif_list.add(notif_item);
        }
        is_work=true;
        if(cur_notif_gid==null){
            cur_notif_gid=notif_list.get(0).getGID();
            cur_notif_group.clear();
            ChatListDB.getInstance(context).readUnreadChatFromFamily(cur_notif_gid,cur_notif_group,chat_info.getmDate());
	    
            curNoticeInfo.setmDate(chat_info.getmDate());
            curNoticeInfo.setDuration(chat_info.getDuration());
            curNoticeInfo.setFilePath(chat_info.getFilePath());
            curNoticeInfo.setIsPlayed(chat_info.getIsPlayed());
            curNoticeInfo.setContentType(chat_info.getContentType());
            curNoticeInfo.setNoticeGID(chat_info.getListItemGID());
            curNoticeInfo.setNoticeEID(chat_info.getListItemEID());
            curNoticeInfo.setNoticeState(chat_info.getListItemState());
            curNoticeInfo.setSN(chat_info.getSN());
	    curNoticeInfo.setContractItemAvatar(chat_info.getContractItemAvatar());
            //curNoticeInfo.setNickName(nick_name);
            //curNoticeInfo.setPhotoID(img);
	    cur_notif_group.remove(0);
        }else if(gid.equals(cur_notif_gid)){
            String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
            String name;
            String number;
            Uri uri = Uri.parse ("content://com.android.contacts/contacts");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
            Log.i(LOG_TAG,"perpareNoticeWindow");
            boolean is_find=false;
            String nick_name=null;
            String find_gid=null;
	    String eid=null;
            int img=R.mipmap.photo_test;
           
	if(WatchSystemInfo.getSmsGID().equals(chat_info.getListItemGID())){
		Log.i(LOG_TAG, "is sms");
		nick_name=chat_info.getListItemEID();
		img=R.mipmap.sms_unread;
		is_find=true;
		
	}else{
	Log.i(LOG_TAG, "is chat");
            while(cursor.moveToNext()&&!is_find){

                int contactsId = cursor.getInt(0);
                Log.i(LOG_TAG,"contactsId = " +contactsId);
                uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
                Cursor dataCursor = resolver.query(uri, project, null, null, null);
                //SyncArrayBean  arrayBean = new SyncArrayBean();
                while(dataCursor.moveToNext()) {
                    String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                    if ("vnd.android.cursor.item/name".equals(type)) {
                        name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
			avatar = dataCursor.getString(dataCursor.getColumnIndex(project[3]));
			Log.i(LOG_TAG,"avatar = " +avatar);
                        Log.i(LOG_TAG,"name = " +name);
                        nick_name=name;
			chat_info.setContractItemAvatar(avatar);
                    } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
                    } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                        number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                        Log.i(LOG_TAG,"number = " +number);
                    }else if("vnd.android.cursor.item/nickname".equals(type)){

             	    attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));
                    find_gid=dataCursor.getString(dataCursor.getColumnIndex(project[5]));
		    eid=dataCursor.getString(dataCursor.getColumnIndex(project[4]));
                    Log.i(LOG_TAG, "find_gid = " + find_gid);
		    Log.i(LOG_TAG, "eid = " + eid);
                    if(find_gid!=null) {
                        if (chat_info.getListItemGID().equals(WatchSystemInfo.getWatchGID())) {
			//
				if(chat_info.getListItemEID().equals(eid)){
				img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
				is_find = true;
				}
			
                        } else if (find_gid.equals(chat_info.getListItemGID())) {
                            img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
                            Log.i(LOG_TAG, "attri = " + attri);
                            is_find = true;
                        }
                    }
                    }
                }



                //if(arrayBean != null) mlist_arraybean.add(arrayBean);
            }
            cursor.close();
	 }
            Log.i(LOG_TAG, "is_find="+is_find);

            ChatNoticeInfo nitice_info=new ChatNoticeInfo();
            nitice_info.setmDate(chat_info.getmDate());
            nitice_info.setDuration(chat_info.getDuration());
            nitice_info.setFilePath(chat_info.getFilePath());
            nitice_info.setIsPlayed(chat_info.getIsPlayed());
            nitice_info.setContentType(chat_info.getContentType());
            nitice_info.setNoticeGID(chat_info.getListItemGID());
            nitice_info.setNoticeEID(chat_info.getListItemEID());
            nitice_info.setNoticeState(chat_info.getListItemState());
            nitice_info.setSN(chat_info.getSN());
            nitice_info.setNickName(nick_name);
            nitice_info.setPhotoID(attri);
	    nitice_info.setContractItemAvatar(avatar);

            cur_notif_group.add(nitice_info);
        }
    }

    public  static ChatNoticeInfo getNextNotifInfo(Context context){
	int attri=0;
	String avatar=null;
        ChatNoticeInfo next_info=new ChatNoticeInfo();
	Log.i(LOG_TAG, "getNextNotifInfo,group size="+cur_notif_group.size());
	Log.i(LOG_TAG, "curNoticeInfo,date="+curNoticeInfo.getmDate());
        if(cur_notif_group.size()>0){
            ChatNoticeInfo search_info=cur_notif_group.get(0);
            curNoticeInfo.setmDate(search_info.getmDate());
            curNoticeInfo.setDuration(search_info.getDuration());
            curNoticeInfo.setFilePath(search_info.getFilePath());
            curNoticeInfo.setIsPlayed(search_info.getIsPlayed());
            curNoticeInfo.setContentType(search_info.getContentType());
            curNoticeInfo.setNoticeGID(search_info.getNoticeGID());
            curNoticeInfo.setNoticeEID(search_info.getNoticeEID());
            curNoticeInfo.setNoticeState(search_info.getNoticeState());
            curNoticeInfo.setSN(search_info.getSN());
            curNoticeInfo.setNickName(search_info.getNickName());
            curNoticeInfo.setPhotoID(search_info.getPhotoID());
	    curNoticeInfo.setContractItemAvatar(search_info.getContractItemAvatar());
            cur_notif_group.remove(0);
        }
        else {
	if(notif_list.size()>0){
            notif_list.remove(0);
	}

            if(notif_list.size()>0){
                cur_notif_gid=notif_list.get(0).getGID();
                cur_notif_group.clear();
                ChatListDB.getInstance(context).readUnreadChatFromFamily(cur_notif_gid,cur_notif_group,notif_list.get(0).getDate());
		Log.i(LOG_TAG,"cur notif group size="+cur_notif_group.size());
		if(cur_notif_group.size()<=0){
			Log.i(LOG_TAG,"start resarch now");
			boolean isStop=false;
			while(!isStop){
				notif_list.remove(0);
				if(notif_list.size()<=0){
					isStop=true;
					cur_notif_gid=null;
					notif_list.clear();
					cur_notif_group.clear();
					is_work=false;
					//sendResumeStoryBroacast(context);
					ChatSoundControl.releaseAudioPlayerFocus();
					break;
				}else{
					cur_notif_gid=notif_list.get(0).getGID();
					cur_notif_group.clear();
					ChatListDB.getInstance(context).readUnreadChatFromFamily(cur_notif_gid,cur_notif_group,notif_list.get(0).getDate());
					if(cur_notif_group.size()>0){
						isStop=true;
						break;
					}else{
						isStop=false;
					}
				}
			}
		}
//////////////////////////////////////////////////////////////////////////
		int initIndex=0;
		for(initIndex=0;initIndex<cur_notif_group.size();initIndex++)
		{
		   String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
			    String name;
			    String number;
			    Uri uri = Uri.parse ("content://com.android.contacts/contacts");
			    ContentResolver resolver = context.getContentResolver();
			    Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
			    Log.i(LOG_TAG,"getNextNotifInfo");
			    boolean is_find=false;
			    String nick_name=null;
			    String find_gid=null;
			    String eid=null;
			    int img=R.mipmap.photo_test;
			    Log.i(LOG_TAG, "perpareNoticeWindow");
			if(WatchSystemInfo.getSmsGID().equals(cur_notif_group.get(initIndex).getNoticeGID())){
				Log.i(LOG_TAG, "is sms");
				nick_name=cur_notif_group.get(initIndex).getNoticeEID();
				img=R.mipmap.sms_unread;
				is_find=true;
		
			}else{
			Log.i(LOG_TAG, "is chat");
			    while(cursor.moveToNext()&&!is_find){

				int contactsId = cursor.getInt(0);
				Log.i(LOG_TAG,"contactsId = " +contactsId);
				uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
				Cursor dataCursor = resolver.query(uri, project, null, null, null);
				//SyncArrayBean  arrayBean = new SyncArrayBean();
				while(dataCursor.moveToNext()) {
				    String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
				    if ("vnd.android.cursor.item/name".equals(type)) {
				        name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
					avatar = dataCursor.getString(dataCursor.getColumnIndex(project[3]));
					Log.i(LOG_TAG,"avatar = " +avatar);
				        Log.i(LOG_TAG,"name = " +name);
				        nick_name=name;
					//chat_info.setContractItemAvatar(avatar);
				    } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
				    } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
				        number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
				        Log.i(LOG_TAG,"number = " +number);
				    }else if("vnd.android.cursor.item/nickname".equals(type)){

			     	    attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));
				    find_gid=dataCursor.getString(dataCursor.getColumnIndex(project[5]));
				    eid=dataCursor.getString(dataCursor.getColumnIndex(project[4]));
				    Log.i(LOG_TAG, "find_gid = " + find_gid);
				    Log.i(LOG_TAG, "eid = " + eid);
				    if(find_gid!=null) {
				        if (cur_notif_group.get(initIndex).getNoticeGID().equals(WatchSystemInfo.getWatchGID())) {
					//
						if(cur_notif_group.get(initIndex).getNoticeEID().equals(eid)){
						img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
						is_find = true;
						}
			
				        } else if (find_gid.equals(cur_notif_group.get(initIndex).getNoticeGID())) {
				            img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
				            Log.i(LOG_TAG, "attri = " + attri);
				            is_find = true;
				        }
				    }
				    }
				}



				//if(arrayBean != null) mlist_arraybean.add(arrayBean);
			    }
			    cursor.close();
			 }

			if(is_find){
				cur_notif_group.get(initIndex).setNickName(nick_name);
				cur_notif_group.get(initIndex).setPhotoID(attri);
			}
		}
////////////////////////////////////////////////////////////////////////
                ChatNoticeInfo search_info=cur_notif_group.get(0);
                curNoticeInfo.setmDate(search_info.getmDate());
                curNoticeInfo.setDuration(search_info.getDuration());
                curNoticeInfo.setFilePath(search_info.getFilePath());
                curNoticeInfo.setIsPlayed(search_info.getIsPlayed());
                curNoticeInfo.setContentType(search_info.getContentType());
                curNoticeInfo.setNoticeGID(search_info.getNoticeGID());
                curNoticeInfo.setNoticeEID(search_info.getNoticeEID());
                curNoticeInfo.setNoticeState(search_info.getNoticeState());
                curNoticeInfo.setSN(search_info.getSN());
                curNoticeInfo.setNickName(search_info.getNickName());
                curNoticeInfo.setPhotoID(search_info.getPhotoID());
		curNoticeInfo.setContractItemAvatar(avatar);
                cur_notif_group.remove(0);
            }
            else{
                cur_notif_gid=null;
                notif_list.clear();
                cur_notif_group.clear();
                is_work=false;
		//sendResumeStoryBroacast(context);
		ChatSoundControl.releaseAudioPlayerFocus();
                return null;
            }
        }

        return curNoticeInfo;
    }

   public static void delChatNoticeGroupByGID(Context context,String gid){
	Log.i(LOG_TAG, "delChatNoticeGroupByGID,gid="+gid);
	int index=0;
	boolean isDelete=false;
        for(ChatNotifListItem noticeInfo:notif_list){
		if(noticeInfo.getGID().equals(gid)){
			notif_list.remove(index);
			isDelete=true;		
			break;
		}
		index++;
	}
	Log.i(LOG_TAG, "delChatNoticeGroupByGID,isDelete="+isDelete);
	if(isDelete){
		if(notif_list.size()>0){
		cur_notif_gid=notif_list.get(0).getGID();
                cur_notif_group.clear();
                ChatListDB.getInstance(context).readUnreadChatFromFamily(cur_notif_gid,cur_notif_group,notif_list.get(0).getDate());
		Log.i(LOG_TAG, "delChatNoticeGroupByGID,get another one");
		}else{
		cur_notif_gid=null;
                notif_list.clear();
                cur_notif_group.clear();
                is_work=false;
		Log.i(LOG_TAG, "delChatNoticeGroupByGID,remove all");
		}
	}
	Log.i(LOG_TAG, "delChatNoticeGroupByGID,is_work="+is_work);
}

    public static ChatNoticeInfo ConstructNoticeinfo(Context context,ChatListItemInfo chat_info){
        String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
        String name;
        String number=null;
        Uri uri = Uri.parse ("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        Log.i(LOG_TAG,"ConstructNoticeinfo");
        boolean is_find=false;
        String nick_name=null;
        String gid=null;
        int img= R.mipmap.photo_test;
        Log.i(LOG_TAG, "ConstructNoticeinfo");
        ChatNoticeInfo nitice_info=null;
        int attri=0;
	String avatar=null;
	if(WatchSystemInfo.getSmsGID().equals(chat_info.getListItemGID())){
		Log.i(LOG_TAG, "is sms");
		nick_name=chat_info.getListItemEID();
		img=R.mipmap.sms_unread;
		is_find=true;
		
	}else{
	Log.i(LOG_TAG, "is chat");
        while(cursor.moveToNext()&&!is_find){    
            int contactsId = cursor.getInt(0);
            Log.i(LOG_TAG,"contactsId = " +contactsId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
            Cursor dataCursor = resolver.query(uri, project, null, null, null);
            //SyncArrayBean  arrayBean = new SyncArrayBean();
            while(dataCursor.moveToNext()) {
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(type)) {
                    name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
		    avatar = dataCursor.getString(dataCursor.getColumnIndex(project[3]));
                    Log.i(LOG_TAG,"name = " +name);
		    Log.i(LOG_TAG,"avatar = " +avatar);
                    nick_name=name;
		    chat_info.setContractItemAvatar(avatar);
                } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
                } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {

                        number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                        Log.i(LOG_TAG,"number = " +number);


                }else if("vnd.android.cursor.item/nickname".equals(type)){

                    attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));
                    gid=dataCursor.getString(dataCursor.getColumnIndex(project[5]));
                    Log.i(LOG_TAG, "gid = " + gid);

                    //arrayBean.contactWeight =dataCursor.getInt(dataCursor.getColumnIndex(project[3]));
                    //arrayBean.optype = dataCursor.getInt(dataCursor.getColumnIndex(project[1]));
                }
            }

             if(gid!=null) {
                if (gid.equals(WatchSystemInfo.getWatchGID())) {

                }else if(chat_info.getListItemEID().equals(number)){
                         img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
                         Log.i(LOG_TAG, "attri = " + attri);
                         is_find = true;


                 }else if (gid.equals(chat_info.getListItemGID())) {
                    img = ChatUtil.ChatUitilGetPhotoByAttr(attri);
                    Log.i(LOG_TAG, "attri = " + attri);
                    is_find = true;
                }
            }

            //if(arrayBean != null) mlist_arraybean.add(arrayBean);
        }
		cursor.close();
	}
        
        Log.i(LOG_TAG, "is_find="+is_find);

        if(is_find){
            nitice_info=new ChatNoticeInfo();
            nitice_info.setmDate(chat_info.getmDate());
            nitice_info.setDuration(chat_info.getDuration());
            nitice_info.setFilePath(chat_info.getFilePath());
            nitice_info.setIsPlayed(chat_info.getIsPlayed());
            Log.i(LOG_TAG, "getContentType="+chat_info.getContentType());
            nitice_info.setContentType(chat_info.getContentType());
            nitice_info.setNoticeGID(chat_info.getListItemGID());
            nitice_info.setNoticeEID(chat_info.getListItemEID());
            nitice_info.setNoticeState(chat_info.getListItemState());
            nitice_info.setSN(chat_info.getSN());
            nitice_info.setNickName(nick_name);
            nitice_info.setPhotoID(attri);
	    nitice_info.setContractItemAvatar(avatar);
            //xun_enter_chatroom_notification_windows(nitice_info);
            //sendNewChatMsgBroacast(nitice_info);
        }
        else{
            nitice_info=null;
        }
        return nitice_info;
    }

public static boolean delChatCheckNotice(String gid,ArrayList<String> delList){
	boolean isRefresh=false;
	Log.i(LOG_TAG, "delChatCheckNotice");
	Log.i(LOG_TAG, "gid="+gid);
	Log.i(LOG_TAG, "delList.size()="+delList.size());
	Log.i(LOG_TAG, "notif_list.size()="+notif_list.size());
	if(gid==null||delList==null||delList.size()==0||notif_list.size()==0){
	Log.i(LOG_TAG, "no need check,return");
	return isRefresh;
	}

	if(!notif_list.get(0).getGID().equals(gid)){
	Log.i(LOG_TAG, "is not cur gid,return");
	return isRefresh;
	}
	
        if(curNoticeInfo==null){
	Log.i(LOG_TAG, "curNoticeInfo is null,return");
	return isRefresh;
	}

        if(curNoticeInfo.getmDate()==null){
	Log.i(LOG_TAG, "curNoticeInfo date is null,return");
	return isRefresh;
	}
	
	for(String delDate:delList){//modify this
		//String delDate=delList.get(i);
		Log.i(LOG_TAG, "delDate="+delDate);
		int maxCount=cur_notif_group.size();
		Log.i(LOG_TAG, "maxCount="+maxCount);
                if(curNoticeInfo.getmDate().equals(delDate))
		{
		    isRefresh=true;
		}
		if(!isRefresh){
			for(int index=0;index<maxCount;index++){
				ChatNoticeInfo checkData=null;
				checkData=cur_notif_group.get(index);
				Log.i(LOG_TAG, "checkData="+checkData.getmDate());
				if(checkData.getmDate().equals(delDate)){
					cur_notif_group.remove(index);		
					if(index==0){
					isRefresh=true;
					}
					break;
				}
			}
		}
		
	}
	Log.i(LOG_TAG, "delChatCheckNotice,isRefresh="+isRefresh);
	return isRefresh;
}

 public static void sendResumeStoryBroacast(Context context){
        Intent it = new Intent("com.xiaoxun.xxun.story.resume.play");
        Log.i(LOG_TAG, "sendResumeStoryBroacast");
	it.setPackage("com.xxun.watch.storydownloadservice");
        context.sendBroadcast(it);
    }

}

