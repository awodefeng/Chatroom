package com.xxun.watch.xunchatroom.info;

import android.util.Log;

import java.io.Serializable;

public class ChatNoticeInfo implements Serializable {
    private String mFamilyID;
    private String mGID;
    private String mEID;
    private int mDuration;
    private String mFilePath;
    private int mItem_cur_state;
    private String mDate;
    private int mIsPlayed;
    private int mContentType;
    private int mSN;
    private String mNickName;
    private int mPhotoID;
    private String avatar;
  //state start
    final static int notice_state_null=0;
    final static int notice_state_recording=1;
    final static int notice_state_recording_fail=2;
    final static int notice_state_recording_finished=3;
    final static int notice_state_sending=4;
    final static int notice_state_sent_succ=5;
    final static int notice_state_sent_fail=6;
    final static int notice_state_playing=7;
    final static int notice_state_playing_succ=8;
    final static int notice_state_playing_fail=9;
    final static int notice_state_recieving=10;
    final static int notice_state_recieving_succ=11;
    final static int notice_state_recieving_fail=12;
    // state end
    //type statt
    final static int content_type_audio=0;
    final static int content_type_text=1;
    final static int content_type_image=2;
    final static int content_type_sms=3;
    //type end

    String LOG_TAG="chat";
    public ChatNoticeInfo(String EID, String GID, int Duration, String FilePath, int state, String date, int isPlayed)
    {
        this.mEID=EID;
        this.mGID=GID;
        this.mFilePath=FilePath;
        this.mDuration=Duration;
        this.mItem_cur_state=state;
        this.mDate=date;
        this.mIsPlayed=isPlayed;
    }

    public ChatNoticeInfo(String EID, String GID, int Duration, String FilePath, int state, String date)
    {
        this.mEID=EID;
        this.mGID=GID;
        this.mFilePath=FilePath;
        this.mDuration=Duration;
        this.mItem_cur_state=state;
        this.mDate=date;
        this.mIsPlayed=0;
        this.mContentType=0;
        this.mSN=0;
    }

    public ChatNoticeInfo()
    {

    }
    public String getNoticeEID(){
       return this.mEID;
   }

    public void setNoticeEID(String EID){
        this.mEID=EID;
    }
    public String getNoticeGID(){
        return this.mGID;
    }

    public void setNoticeGID(String GID){
        this.mGID=GID;
    }

    public String getNoticeDuration(){
        Integer i=new Integer(mDuration);
        return i.toString()+"'";
    }

    public  void setDuration(int duration){
        this.mDuration=duration;
    }

    public int getDuration(){
        return this.mDuration;
    }


    public void setNoticeState(int state)
    {
        this.mItem_cur_state=state;
    }

    public int getNoticeState()
    {
        return mItem_cur_state;
    }

    public String getmDate()
    {
        return this.mDate;
    }
    public  void setmDate(String date)
    {
        this.mDate=date;
    }

    public  String getFilePath()
    {
        Log.i(LOG_TAG,"getFilePath="+ this.mFilePath);
        return this.mFilePath;
    }

    public  void setFilePath(String filePath)
     {
         this.mFilePath=filePath;
         Log.i(LOG_TAG,"setFilePath="+ this.mFilePath);
     }

    public int getIsPlayed(){
        return this.mIsPlayed;
    }

    public void setIsPlayed(int isPlayed)
    {
        this.mIsPlayed=isPlayed;
    }

    public int getContentType(){
        return this.mContentType;
    }

    public  void setContentType(int type)
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

    public  void setNickName(String name){
        this.mNickName=name;
    }

    public String getNickName(){
        return this.mNickName;
    }

    public void setPhotoID(int img_id){
        this.mPhotoID=img_id;
    }

    public int getPhotoID(){
        return this.mPhotoID;
    }

    public  void setContractItemAvatar(String avatar){
       this.avatar= avatar;
    }

    public  String getContractItemAvatar(){
        return this.avatar;
    }

}
