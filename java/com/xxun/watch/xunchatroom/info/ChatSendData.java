package com.xxun.watch.xunchatroom.info;

import android.util.Log;

import java.io.Serializable;

public class ChatSendData{

    private int mSN;
    private String mDataPath;
    private int mDataType;
    private int mStatus;
    private String mGID;
    public static int send_data_type_null=0;
    public static int send_data_type_image=1;
    public static int send_data_type_video=2;
    String LOG_TAG="ChatSendData";

public ChatSendData(int sn,String path,int type,String gid ){
	this.mSN=sn;
	this.mDataPath=path;
	this.mDataType=type;
	this.mGID=gid;
	this.mStatus=0;
 }

public void setSendDataType(int type){
	this.mDataType=type;
}

public int getSendDataType(){
	return this.mDataType;
}

public void setSendDataSN(int sn){
	this.mSN=sn;
}

public int getSendDataSN(){
	return this.mSN;
}

public void setSendDataPath(String path){
	this.mDataPath=path;
}

public String getSendDataPath(){
	return this.mDataPath;
}

public void setSendDataStatus(int status){
	this.mStatus=status;
}

public int getSendDataStatus(){
	return this.mStatus;
}

public void setSendDataGID(String gid){
	this.mGID=gid;
}

public String getSendDataGID(){
	return this.mGID;
}

}
