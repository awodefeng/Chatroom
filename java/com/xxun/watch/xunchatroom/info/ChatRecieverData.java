package com.xxun.watch.xunchatroom.info;

import android.util.Log;

import java.io.Serializable;

public class ChatRecieverData{

    private int mSN;
    private String mDataPath;
    private int mDataType;
    private int mStatus;
    private long dlID;
    public static int reciever_data_type_null=0;
    public static int reciever_data_type_image=0;
    public static int reciever_data_type_video=0;
    String LOG_TAG="ChatRecieverData";

public ChatRecieverData(int sn,String path,int type){
	this.mSN=sn;
	this.mDataPath=path;
	this.mDataType=type;
	this.mStatus=0;
	this.dlID=-1;
 }

public void setRecieverDataType(int type){
	this.mDataType=type;
}

public int getRecieverDataType(){
	return this.mDataType;
}

public void setRecieverDataSN(int sn){
	this.mSN=sn;
}

public int getRecieverDataSN(){
	return this.mSN;
}

public void setRecieverDataPath(String path){
	this.mDataPath=path;
}

public String getRecieverDataPath(){
	return this.mDataPath;
}

public void setRecieverDataStatus(int status){
	this.mStatus=status;
}

public int getRecieverDataStatus(){
	return this.mStatus;
}

public void setRecieverDataID(long dlID){
	this.dlID=dlID;
}

public long getRecieverDataID(){
	return this.dlID;
}
}
