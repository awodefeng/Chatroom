package com.xxun.watch.xunchatroom.info;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatListItemInfo {
    private String mFamilyID;
    private String mGID;
    private String mEID;
    private int mDuration;
    private String mFilePath;
    private int mIs_local;
    private int mItem_cur_state;
    private String mDate;
    private int mIsPlayed;
    private int mContentType;
    private int mSN;
    private long startMills;
    private String mNickName;
  //state start
   public final static int list_tiem_state_null=0;
    public final static int list_tiem_state_recording=1;
    public  final static int list_tiem_state_recording_fail=2;
    public final static int list_tiem_state_recording_finished=3;
    public final static int list_tiem_state_sending=4;
    public final static int list_tiem_state_sent_succ=5;
    public final static int list_tiem_state_sent_fail=6;
    public final static int list_tiem_state_playing=7;
    public  final static int list_tiem_state_playing_succ=8;
    public final static int list_tiem_state_playing_fail=9;
    public final static int list_tiem_state_recieving=10;
    public final static int list_tiem_state_recieving_succ=11;
    public final static int list_tiem_state_recieving_fail=12;
    // state end
    //type statt
    public  final static int content_type_audio=0;
    public  final static int content_type_text=1;
    public final static int content_type_image=2;
    public final static int content_type_sms=3;
    public final static int content_type_photo=4;
    public final static int content_type_video=5;
    //type end

    private  int list_item_photo_id=0;
    private int listSendState=1;
    private String avatar;
    String LOG_TAG="chat";
    public ChatListItemInfo(String EID,String GID,int Duration,String FilePath,int is_local,int state,String date,int isPlayed)
    {
        this.mEID=EID;
        this.mGID=GID;
        this.mFilePath=FilePath;
        this.mDuration=Duration;
        this.mIs_local=is_local;
        this.mItem_cur_state=state;
        this.mDate=date;
        this.mIsPlayed=isPlayed;
	this.listSendState=1;
	this.startMills=0;
    }

    public ChatListItemInfo(String EID,String GID,int Duration,String FilePath,int is_local,int state,String date)
    {
        this.mEID=EID;
        this.mGID=GID;
        this.mFilePath=FilePath;
        this.mDuration=Duration;
        this.mIs_local=is_local;
        this.mItem_cur_state=state;
        this.mDate=date;
        this.mIsPlayed=0;
        this.mContentType=0;
        this.mSN=0;
	this.list_item_photo_id=0;
	this.listSendState=1;
	this.startMills=0;
    }

    public ChatListItemInfo(String EID,String GID,int Duration,String FilePath,int is_local,int state,String date,String nickName)
    {
        this.mEID=EID;
        this.mGID=GID;
        this.mFilePath=FilePath;
        this.mDuration=Duration;
        this.mIs_local=is_local;
        this.mItem_cur_state=state;
        this.mDate=date;
        this.mIsPlayed=0;
        this.mContentType=0;
        this.mSN=0;
	this.list_item_photo_id=0;
	this.listSendState=1;
	this.startMills=0;
	this.mNickName=nickName;
    }

    public ChatListItemInfo()
    {

    }

    public void setListItemStartMills(long mills){
	this.startMills=mills;
}

    public long getListItemStartMills(){
	return this.startMills;
}

    public  String getListItemEID(){
       return this.mEID;
   }

    public  void setListItemEID(String EID){
        this.mEID=EID;
    }
    public   String getListItemGID(){
        return this.mGID;
    }

    public   void setListItemGID(String GID){
        this.mGID=GID;
    }

    public   String getListItemTime(){
        Integer i=new Integer(mDuration);
        return i.toString()+"'";
    }

    public void setDuration(int duration){
        this.mDuration=duration;
    }

    public int getDuration(){
        return this.mDuration;
    }

    public int getListItemType(){
        return mIs_local;
    }

    public void setListItemType(int isLcoal)
    {
        mIs_local=isLcoal;
    }

    public void setListItemState(int state)
    {
        this.mItem_cur_state=state;
    }

    public  int getListItemState()
    {
        return mItem_cur_state;
    }

    public String getmDate()
    {
        return this.mDate;
    }
    public void setmDate(String date)
    {
        this.mDate=date;
    }

    public  String getFilePath()
    {
        Log.i(LOG_TAG,"getFilePath="+ this.mFilePath);
        return this.mFilePath;
    }

    public void setFilePath(String filePath)
     {
         this.mFilePath=filePath;
         Log.i(LOG_TAG,"setFilePath="+ this.mFilePath);
     }

    public int getIsPlayed(){
        return this.mIsPlayed;
    }

    public  void setIsPlayed(int isPlayed)
    {
        this.mIsPlayed=isPlayed;
    }

    public int getContentType(){
        return this.mContentType;
    }

    public void setContentType(int type)
    {
        this.mContentType=type;
    }

    public int getSN(){
        return this.mSN;
    }

    public void setSN(int sn)
    {
        this.mSN=sn;
    }

    public void setPhotoID(int id){
	this.list_item_photo_id=id;
    }

    public int getPhotoID(){
	return this.list_item_photo_id;
    }

    public void setSendState(int state){
	this.listSendState=state;
    }

    public int getSendState(){
	return this.listSendState;
    }

    public  void setContractItemAvatar(String avatar){
       this.avatar= avatar;
    }

    public  String getContractItemAvatar(){
        return this.avatar;
    }

    public void setNickName(String nickName){
	this.mNickName=nickName;
    }

    public String getNickName(){
	return this.mNickName;
    }
}
