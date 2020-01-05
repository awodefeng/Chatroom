package com.xxun.watch.xunchatroom.info;

import android.util.Log;

import java.io.Serializable;

public class ChatRecieverImage{

    private int mSN;
    private String mDataPath;
    private int mDataType;
    private int mStatus;
    private long dlID;
    public static int reciever_data_type_null=0;
    public static int reciever_data_type_image=1;
    public static int reciever_data_type_video=2;
    String LOG_TAG="ChatRecieverImage";

public ChatRecieverImage(int sn,String path,int type){
	this.mSN=sn;
	this.mDataPath=path;
	this.mDataType=type;
	this.mStatus=0;
	this.dlID=-1;
 }

public void setRecieverImageType(int type){
	this.mDataType=type;
}

public int getRecieverImageType(){
	return this.mDataType;
}

public void setRecieverImageSN(int sn){
	this.mSN=sn;
}

public int getRecieverImageSN(){
	return this.mSN;
}

public void setRecieverImagePath(String path){
	this.mDataPath=path;
}

public String getRecieverImagePath(){
	return this.mDataPath;
}

public void setRecieverImageStatus(int status){
	this.mStatus=status;
}

public int getRecieverImageStatus(){
	return this.mStatus;
}

public void setRecieverImageID(long dlID){
	this.dlID=dlID;
}

public long getRecieverImageID(){
	return this.dlID;
}
}
