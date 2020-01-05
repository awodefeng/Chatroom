package com.xxun.watch.xunchatroom.SMS;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;

import com.xxun.watch.xunchatroom.R;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import java.math.BigInteger;
import java.security.MessageDigest;
import android.os.SystemProperties;
import android.provider.Settings;
import java.security.NoSuchAlgorithmException;
import android.text.TextUtils;

public class OfflineSms {
    private static String LOG_TAG="OfflineSms";
    private static OfflineSms smsControl=null;
    private static final int single_blank=3;
    private static final int double_blank=2;
    private static String realMd5=null;
    private static String dateTime=null;
    private static Context mContext;
    public synchronized static OfflineSms getInstance(Context context) {
        if (smsControl == null)
            smsControl = new OfflineSms();
	mContext=context;
        return smsControl;
    }

    public boolean JudgementWakeupSms(String smsStr){
        boolean isWakeup=false;
        String localMd5=null;
	Log.i(LOG_TAG,"JudgementWakeupSms");
	int offlineValue = Settings.System.getInt(mContext.getContentResolver(),"offlinevalue",0);
	Log.i(LOG_TAG,"offlineValue="+offlineValue);
	if(offlineValue!=2){
		isWakeup=false;
		Log.i(LOG_TAG,"it is not in offlinemode");
	}else{

	XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)mContext.getSystemService("xun.network.Service");
	String eid=networkService.getWatchEid();
	Log.i(LOG_TAG,"eid="+eid);
        if(isWakeupSms(smsStr)){
           getSmsRealMd5String(smsStr);
            localMd5=CreateLocalMd5(dateTime,eid);
            Log.i(LOG_TAG,"localMd5="+localMd5);
            Log.i(LOG_TAG,"realMd5="+realMd5);
            if(realMd5.equals(localMd5)){
                isWakeup=true;
            }
        }
	}
        Log.i(LOG_TAG,"isWakeup="+isWakeup);
        return isWakeup;
    }

    public String CreateLocalMd5(String date,String eid){
        String localMd5=null;
        String localStr="<"+date+","+eid+","+"E820,wake_up>";
        Log.i(LOG_TAG,"localStr="+localStr);
        localMd5= GetStringMd5(localStr);
        Log.i(LOG_TAG,"localMd5="+localMd5);
        return localMd5;
    }

    public void getSmsRealMd5String(String smsStr){
        realMd5=null;
        String mainStr=null;
        dateTime=null;
        int index=0;
        int cut_len=0;
        int cut_date=0;
        Log.i(LOG_TAG,"smsStr="+smsStr);
        mainStr=smsStr.substring(4,smsStr.length());
        Log.i(LOG_TAG,"mainStr="+mainStr);
        char[] firstChar = new char[2];

        mainStr.getChars(0,1,firstChar,0);
        int firstInt=xun_change_hex_to_dex(firstChar[0]);
        int curBlank=0;
        if(firstInt%2==0){
            curBlank=double_blank;
        }else{
            curBlank=single_blank;
        }
	Log.i(LOG_TAG,"firstInt="+firstInt);
	Log.i(LOG_TAG,"curBlank="+curBlank);
        int pos=0;
        //int first_code=xun_change_hex_to_dex(sms_content[pos]);
         cut_len=firstInt+1;


        while(pos<mainStr.length()||cut_len==0)
        {
            if(index==0)
            {
                cut_len=firstInt+1;
            }
            else if(index==1)
            {
                //pos=pos+cut_len-1;
                cut_len=4;
            }
            else if(index%2==0)
            {
                cut_len=curBlank;
            }
            else if(index%2==1)
            {
                //pos=pos+cut_len-1;
                cut_len=2;
            }

            if(cut_date>=6)
            {
                cut_len=mainStr.length()-pos;
            }
            else if((pos+cut_len)>=mainStr.length())
            {
                cut_len=mainStr.length()-pos;
            }

            if(index%2==0)
            {
                //strncat(real_md5,&sms_content[pos],cut_len);
                if(realMd5==null){
                    realMd5 =  mainStr.substring(pos, pos + cut_len);
                 }else{
                    realMd5 = realMd5 + mainStr.substring(pos, pos + cut_len);
                 }
            }
            else
            {
                //strncat(dateTime,&sms_content[pos],cut_len);
                if(dateTime==null){
                    dateTime=mainStr.substring(pos,pos+cut_len);
                }else {
                    dateTime = dateTime + mainStr.substring(pos, pos + cut_len);
                }
                cut_date++;
            }
            pos=pos+cut_len;
            index++;

        }
        Log.i(LOG_TAG,"dateTime="+dateTime);
        Log.i(LOG_TAG,"realMd5="+realMd5);
    }

    int xun_change_hex_to_dex(char in_hex)
    {
        int out_dex=0;
        Log.i(LOG_TAG,"in_hex="+in_hex);
        switch(in_hex)
        {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                out_dex=in_hex-'0';
                break;

            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                out_dex=10+in_hex-'A';
                break;

            default:
                break;
        }
        Log.i(LOG_TAG,"out_dex="+out_dex);
        return out_dex;

    }

    public boolean isWakeupSms(String smsStr){
        String headString=null;
        boolean isWakeSms=false;
        Log.i(LOG_TAG,"smsStr="+smsStr);
        headString=smsStr.substring(0,4);
        if(headString.equals("WAKE")){
            isWakeSms=true;
        }
        Log.i(LOG_TAG,"isWakeSms="+isWakeSms);
        return isWakeSms;
    }

    public String GetStringMd5(String string){
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
/*{
                MessageDigest md5 = null;
                 try {
                         md5 = MessageDigest.getInstance("MD5");
                     } catch (Exception e) {
                         e.printStackTrace();
                         return "";
                     }

                 char[] charArray = str.toCharArray();
                 byte[] byteArray = new byte[charArray.length];

                 for (int i = 0; i < charArray.length; i++) {
                         byteArray[i] = (byte) charArray[i];
                     }
                 md5.update(byteArray,0,byteArray.length);
                BigInteger bigInteger=new BigInteger(1,md5.digest());
                String md5String=bigInteger.toString(16).toUpperCase();
                Log.i(LOG_TAG,"md5String="+md5String);
                return md5String;
    }
*/
}
