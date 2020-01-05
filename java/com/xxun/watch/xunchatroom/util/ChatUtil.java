package com.xxun.watch.xunchatroom.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.text.format.Time;

import com.xxun.watch.xunchatroom.R;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
//import com.xiaoxun.sdk.interfaces.IMessageReceiveListener;
import com.xxun.watch.xunchatroom.database.ChatListDB;

import net.minidev.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import com.xxun.watch.xunchatroom.util.SilenceDisturb;
import java.util.List;
import java.util.Calendar;
import android.os.Environment;
import android.os.StatFs;
import java.io.File;
import com.xiaoxun.statistics.XiaoXunStatisticsManager;
import android.database.Cursor;
import android.net.Uri;
import android.content.ContentResolver;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import java.io.InputStream;
public class ChatUtil{

    static String LOG_TAG="chat Util";
    static String CHAT_END_KEY="ChatEndKey";
    static String CHAT_DB_VERSION="ChatDBVersion";
    static String KEY= "key";
    static String setting_folder_name="chat_settings";
    static String chat_audio_folder="chat_audio";
    public static final int STATS_VOICE_RECEIVED = 1; 
    public static final int STATS_VOICE_RECEIVE_ROLLBACK = 2;
    public static final int STATS_VOICE_RECEIVE_ROLLBACK_SUCCESS = 3;
    public static final int STATS_VOICE_SEND = 4;
    public static final int STATS_VOICE_SEND_SUCCESS = 5;
    public static final int STATS_VOICE_SEND_TIMEOUT = 6;
    public static final int STATS_VOICE_SEND_RETRY = 7;
    public static final int STATS_VOICE_SEND_LESSTHEN3 = 9;
    public static final int STATS_VOICE_SEND_LESSTHEN7 = 10;
    public static final int STATS_VOICE_SEND_LESSTHEN15 = 11;
    public static final int STATS_VOICE_SEND_LESSTHEN31 = 12;
    public static final int STATS_VOICE_SEND_LESSTHEN63 = 13;
    public static final int STATS_VOICE_SEND_GREATERTHEN63 = 14;
    private static final long END_TIME = System.currentTimeMillis();
    private static final long TIME_INTERVAL = 7 * 24 * 60 * 60 * 1000L;
    private static final long START_TIME = END_TIME - TIME_INTERVAL;

    public static int ChatUitilGetPhotoByAttr(int attr){
    int photo_id =0;
    switch(attr)
    {
          case 0:
                photo_id = R.mipmap.man_small_04;//baba
                break;
                
            case 1:
                photo_id = R.mipmap.woman_small_13;//mama
                break;
                
            case 2:
                photo_id = R.mipmap.man_small_13;//yeye
                break;
                
            case 3:
                photo_id = R.mipmap.woman_small_03;//nainai
                break;
            case 4:
                photo_id = R.mipmap.man_small_13;//waigong
                break;
            case 5:
                photo_id = R.mipmap.woman_small_10;//waipo
                break;
            case 6:
                photo_id = R.mipmap.man_small_13;//laoye
                break;
            case 7:
                photo_id = R.mipmap.woman_small_10;//laolao
                break;
            case 8:
                photo_id = R.mipmap.man_small_03;//bofu
                break;
            case 9:
                photo_id = R.mipmap.woman_small_07;//bomu
                break;
            case 10:
                photo_id = R.mipmap.man_small_03;//shushu
                break;
            case 11:
                photo_id = R.mipmap.woman_small_07;//shenshen
                break;
            case 12:
                photo_id = R.mipmap.man_small_03;//gufu
                break;            
            case 13:
                photo_id = R.mipmap.woman_small_07;//guma
                break;            
            case 14:
                photo_id = R.mipmap.man_small_03;//yifu
                break;            
            case 15:
                photo_id = R.mipmap.woman_small_07;//yima
                break;            
            case 16:
                photo_id = R.mipmap.man_small_03;//jiujiu
                break;            
            case 17:
                photo_id = R.mipmap.woman_small_07;//jiuma
                break;            
            case 18:
                photo_id = R.mipmap.man_small_03;//ganba
                break;            
            case 19:
                photo_id = R.mipmap.woman_small_07;//ganma
                break;            
            case 20:
                photo_id = R.mipmap.man_small_05;//gege
                break;            
            case 21:
                photo_id = R.mipmap.woman_small_09;//jiejie
                break;  
            case 22:
                photo_id = R.mipmap.man_small_05;//didi
                break; 
            case 23:
                photo_id = R.mipmap.woman_small_09;//meimei
                break; 
            case 24:
                photo_id = R.mipmap.man_small_09;//friend
                break; 
            case 25:
                photo_id = R.mipmap.man_small_09;//friend
                break; 
            case 26:
                photo_id = R.mipmap.man_small_09;//friend
                break;
            case 27:
                photo_id = R.mipmap.man_small_11;//friend
                break;   
            case 101:
                photo_id = R.mipmap.family;//friend
                break;
            case 102:
                photo_id = R.mipmap.sms_unread;//friend
                break;
            default:
                photo_id = R.mipmap.man_small_09;//default
                break;
    }
    return photo_id;
}

    public static String ChatUtilGetDate(){
        SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyyMMddHHmmss");
        Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);
        Log.i("LOG_TAG","ChatUtilGetDate="+str);
        return str;
    }

    public static String ChatUtilGetBackDate(){
        SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyyMMddHHmmss");
        Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);
        Log.i("LOG_TAG","ChatUtilGetBackDate,str="+str);
        long back_value=99999999999999L-Long.valueOf(str);
        String back_str=Long.toString(back_value);
        Log.i("LOG_TAG","ChatUtilGetBackDate,back_str="+back_str);

        return back_str;
    }

    public static int ChatUtilGetSN(){
         SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyyMMddHHmmss");
         Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
         String    str    =    formatter.format(curDate);
         long back_value=99999999999999L-Long.valueOf(str);

         int sn=(int)back_value;//Long.valueOf(str).intValue();
         Log.i("LOG_TAG","ChatUtilGetSN="+sn);
         return sn;
     }

    public static int ChatUtilGetServiceSN(Context context){
	 XiaoXunNetworkManager networkService=(XiaoXunNetworkManager)context.getSystemService("xun.network.Service");
	 int sn=networkService.getMsgSN();
         Log.i("LOG_TAG","ChatUtilGetServiceSN="+sn);
         return sn;
     }

    public static String ChatUtilGetTimeSteamp(){
	Time t =new Time("GMT+8:00");
	t.setToNow();
	int year=9999-t.year;
	int month=99-t.month;
	int day=99-t.monthDay;
	int hour=99-t.hour;
	int minute=99-t.minute;
	int second=99-t.second;
	int sss=999-0;

        String steamp=Integer.toString(year)+Integer.toString(month)+Integer.toString(day)+Integer.toString(hour)+Integer.toString(minute)+Integer.toString(second)+Integer.toString(sss);
	Log.i(LOG_TAG,"ChatUtilGetTimeSteamp="+steamp);
	return steamp;
    }

    public static Time getNowTime(){
	Calendar c = Calendar.getInstance(); 
	int year = c.get(Calendar.YEAR); 
	int month = c.get(Calendar.MONTH); 
	int day = c.get(Calendar.DAY_OF_MONTH); 
	int hour = c.get(Calendar.HOUR_OF_DAY); 
	int minute = c.get(Calendar.MINUTE); 
	int second = c.get(Calendar.SECOND); 
	//int week_month = c.get(Calendar.WEEK_OF_MONTH); 
	//int week_year = c.get(Calendar.WEEK_OF_YEAR); 
	//int week_day = c.get(Calendar.DAY_OF_WEEK); 
	//int week_day_month = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);

	Time t =new Time("GMT+8:00");
	t.year=year;
	t.month=month;
	t.monthDay=day;
	t.hour=hour;
	t.minute=minute;
	t.second=second;
	return t;
    }

    public static void ChatUtilWriteEndkey(Context context,String engkey)
    {
        SharedPreferences.Editor editor=context.getSharedPreferences(CHAT_END_KEY,
                                Activity.MODE_PRIVATE).edit();
                        editor.putString("key", engkey);
        Log.i("chat Util","ChatUtilWriteEndkey="+engkey);
                        editor.commit();

    }

    public static String ChatUtilReadEndkey(Context context)
    {
        String str;
        SharedPreferences mSp=context.getSharedPreferences(CHAT_END_KEY,
                Activity.MODE_PRIVATE);
        str=mSp.getString(KEY," ");
        Log.i("chat Util","ChatUtilReadEndkey="+str);
        return str;
    }

    public static void ChatUtilWriteDBVersion(Context context,String version)
    {
        SharedPreferences.Editor editor=context.getSharedPreferences(CHAT_DB_VERSION,
                                Activity.MODE_PRIVATE).edit();
                        editor.putString("version", version);
        Log.i("chat Util","ChatUtilWriteDBVersion="+version);
                        editor.commit();

    }

    public static String ChatUtilReadDBVersion(Context context)
    {
        String str;
        SharedPreferences mSp=context.getSharedPreferences(CHAT_DB_VERSION,
                Activity.MODE_PRIVATE);
        str=mSp.getString("version"," ");
        Log.i("chat Util","ChatUtilReadDBVersion="+str);
        return str;
    }

    public static boolean ChatUtilCheckDBVersion(Context context,String curVersion){
	Log.i("chat Util","ChatUtilCheckDBVersion,curVersion="+curVersion);
	boolean isUpgrade=false;
	String dbVersion=ChatUtilReadDBVersion(context);
	Log.i("chat Util","ChatUtilCheckDBVersion,dbVersion="+dbVersion);
	if(!curVersion.equals(dbVersion)){
		isUpgrade=true;
		ChatUtilWriteDBVersion(context,curVersion);
	}
	Log.i("chat Util","ChatUtilCheckDBVersion,isUpgrade="+isUpgrade);
	return isUpgrade;
    }

    public static void PrintLog(String buff)
    {
        Log.i("chat Util",buff);
    }

    public static void ChatUtilCreateFolder(String folder_name){
        Log.i("LOG_TAG","ChatUtilCreateFolder="+folder_name);
        File dirFirstFolder = new File(folder_name);
        if(!dirFirstFolder.exists()) { //如果该文件夹不存在，则进行创建
            dirFirstFolder.mkdirs();//创建文件夹
        }

    }

    public static void ChatUtilCreateFile(String file_name){
        File file = new File(file_name) ;

                if(!file.exists()){
                        try {
                                file.createNewFile() ;
                               //file is create
                           } catch (IOException e) {
                                // TODO Auto-generated catch block
                                //e.printStackTrace();
                            }
                    }

    }

    public static void ChatUtilFileRename(String old_name,String new_name) {
         File file = new File(old_name) ;
         file.renameTo(new File(new_name));
     }
    public static void ChatUtilDeleteFile(String path){
        File file = new File(path);
	if(file.exists()){
        file.delete();
	}
    }

    /**
     * 将数据存到文件中
     *
     *
     * @param data 需要保存的数据
     * @param fileName 文件名
     */
    public static void ChatUtilSaveDataToFile( byte[] data, String fileName)
    {
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        File file = new File(fileName);

        try {
            fileOutputStream=new FileOutputStream(file);
            try {
                fileOutputStream.write(data);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }

    /**
     * 从文件中读取数据
     *
     * @param fileName 文件名
     * @return 从文件中读取的数据
     */
    public static byte[] ChatUtilReadDataFromFile( String fileName)
    {
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer=null;
            /**
             * 注意这里的fileName不要用绝对路径，只需要文件名就可以了，系统会自动到data目录下去加载这个文件
             */
            File file = new File(fileName);
        try {
            fileInputStream = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            try {
                fileInputStream.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //fileInputStream = context.openFileInput(fileName);

        return buffer;
    }

    public static String getChatSettingPath(String fileName){
        String FileName=null;
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileName +="/"+setting_folder_name+"/"+ fileName;
        return FileName;	
    }

    public static String getChatSettingFolder(){
        String FileName=null;
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileName +="/"+setting_folder_name;
        return FileName;
    }

    public static int getAudioDurationByPath(String path){
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);  //recordingFilePath（）为音频文件的路径
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        double duration= player.getDuration();//获取音频的时间
        Log.i(LOG_TAG, "### duration: " + duration);
        player.release();//记得释放资源
        return (int)(duration);
    }

    public static int getGroupMissCount(Context context,String gid){
        int miss_count=0;
        miss_count= ChatListDB.getInstance(context).readMissCountFromFamily(gid);
       // Log.i(LOG_TAG, "getGroupMissCount,miss_count=" + miss_count);
        return miss_count;
    }

    public static String getRealPhoneNumber(String old_bumber){
        String real_number=null;
        Log.i(LOG_TAG, "getRealPhoneNumber:old_bumber=" + old_bumber);

        String temp=old_bumber.substring(0,3);
        Log.i(LOG_TAG, "getRealPhoneNumber:temp=" + temp);

        if(temp.equals("+86")){
            real_number=old_bumber.substring(3,old_bumber.length());
        }else{
            real_number=old_bumber;
        }
        Log.i(LOG_TAG, "getRealPhoneNumber:real_number=" + real_number);
        return real_number;
    }

    public static  boolean isDisturb(Context context){
   /*     String result = android.provider.Settings.System.getString(context.getApplicationContext().getContentResolver(), "SilenceList_result");
        boolean SilenceList_result = (result == null?false:Boolean.parseBoolean(result));
        return  SilenceList_result;*/
	String value = android.provider.Settings.System.getString(context.getContentResolver(), "SilenceList");
	       String result = android.provider.Settings.System.getString(context.getContentResolver(), "SilenceList_result");
	       boolean SilenceList_result = (result == null?false:Boolean.parseBoolean(result));
	       boolean initValue = false;
	       if (value != null) {
		    List<SilenceDisturb> mlist = SilenceDisturb.arraySilenceDisturbFromData(value);
		    if(mlist != null){
		        for(SilenceDisturb msilence:mlist){
		               if(isCurrentInTimeScope(Integer.valueOf(msilence.starthour)
		                   ,Integer.valueOf(msilence.startmin),Integer.valueOf(msilence.endhour),Integer.valueOf(msilence.endmin),msilence.days)){
		                       initValue = true;
		               }
		        }
		    }
	       }
	       if(initValue != SilenceList_result){
		   android.provider.Settings.System.putString(context.getContentResolver(), "SilenceList_result",String.valueOf(initValue));

	       }

	return   initValue;
    }
    
    
     private static boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour, int endMin,String days) {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                i = 6;
                break;
            case 2:
                i = 0;
                break;
            case 3:
                i = 1;
                break;
            case 4:
                i = 2;
                break;
            case 5:
                i = 3;
                break;
            case 6:
                i = 4;
                break;
            case 7:
                i = 5;
                break;
            default:
                break;
        }
        if(Integer.valueOf(String.valueOf(days.toCharArray()[i])) == 0){
            return false;
         }
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();
        java.sql.Time now = new java.sql.Time(currentTimeMillis);
        java.sql.Time startTime = new java.sql.Time(currentTimeMillis);
        startTime.setHours(beginHour);
        startTime.setMinutes(beginMin);
        java.sql.Time endTime = new java.sql.Time(currentTimeMillis);
        endTime.setHours(endHour);
        endTime.setMinutes(endMin);
        if (endTime.before(startTime)) {
            startTime.setTime(startTime.getTime() - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime);
            java.sql.Time startTimeInThisDay = new java.sql.Time(startTime.getTime() + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
            result = !now.before(startTime) && !now.after(endTime);
        }
        return result;
    }

    public static boolean  isForeground(Context context, String className) {
	Log.i(LOG_TAG, "isForeground,className="+className);
        if (context == null) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
	Log.i(LOG_TAG, "list.size()="+list.size());
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
		Log.i(LOG_TAG, "cpn.getClassName()="+cpn.getClassName());
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;

    }

public static String string2Unicode(String string) {
  
     StringBuffer unicode = new StringBuffer();
  
     for (int i = 0; i < string.length(); i++) {
  
         // È¡³öÃ¿Ò»žö×Ö·û
        char c = string.charAt(i);
  
         // ×ª»»Îªunicode
         unicode.append("\\u" + Integer.toHexString(c));
     }
  
     return unicode.toString();
 }

public static String unicode2String(String unicode) {
  
     StringBuffer string = new StringBuffer();
  
     String[] hex = unicode.split("\\\\u");
  
     for (int i = 1; i < hex.length; i++) {
  
         // ×ª»»³öÃ¿Ò»žöŽúÂëµã
        int data = Integer.parseInt(hex[i], 16);
  
         // ×·ŒÓ³Éstring
         string.append((char) data);
     }
  
     return string.toString();
 }

public static String Byte2UnicodeChange(byte abyte[], int st, int bEnd) {
    StringBuffer sb = new StringBuffer("");
    for (int j = st; j < bEnd; ) {
        int hi = abyte[j++];
        if (hi < 0) hi += 256;
        int lw = abyte[j++];
        if (lw < 0) lw += 256;
        char c = (char) (lw + (hi << 8));
        sb.append(c);
    }
    return sb.toString();
}

public static byte[] ByteChange(byte abyte[], int st, int bEnd) {
    byte[] sb = new byte[bEnd];
	Log.i(LOG_TAG, "ByteChange,s="+st+",e="+bEnd);
	int index=st;
    for (int j = st; j < bEnd; ) {
        byte hi = abyte[j++];
	//sb[index]=
        //if (hi < 0) hi += 256;
        byte lw = abyte[j++];
        //if (lw < 0) lw += 256;
        sb[index]=lw;
	index++;
        sb[index]=hi;
	index++;
    }
    return sb;
}

public static String Byte2Unicode(byte abyte[], int st, int bEnd) {
    StringBuffer sb = new StringBuffer("");
    for (int j = st; j < bEnd; ) {
        int lw = abyte[j++];
        if (lw < 0) lw += 256;
        int hi = abyte[j++];
        if (hi < 0) hi += 256;
        char c = (char)(lw + (hi << 8));
        sb.append(c);
    }
    return sb.toString();
}

    public static int returnActualLength(byte[] data) { 
        int i = 0; 
        for (; i < data.length; i++) { 
            if (data[i] == '\0') 
                break; 
        } 
        return i; 
    }

   public static String str2HexStr(String str)  
    {    

        char[] chars = "0123456789ABCDEF".toCharArray();    
        StringBuilder sb = new StringBuilder("");  
        byte[] bs = str.getBytes();    
        int bit;    

        for (int i = 0; i < bs.length; i++)  
        {    
            bit = (bs[i] & 0x0f0) >> 4;    
            sb.append(chars[bit]);    
            bit = bs[i] & 0x0f;    
            sb.append(chars[bit]);  
            sb.append(' ');  
        }    
        return sb.toString().trim();    
    } 

   public static boolean checkRomAvailableSize() {  
	Log.i(LOG_TAG, "checkRomAvailableSize");

	boolean isFlashSizeOK=false;
        File path = Environment.getExternalStorageDirectory();  
	Log.i(LOG_TAG, "checkRomAvailableSize,path="+path.getPath());
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long availableBlocks = stat.getAvailableBlocks();  
	
	Log.i(LOG_TAG, "blockSize="+stat.getBlockSize());
	Log.i(LOG_TAG, "availableBlocks="+availableBlocks);
	long watchSize=blockSize*availableBlocks;
	Log.i(LOG_TAG, "watchSize=" + watchSize);
	if(watchSize>(30*1024*1024)){
		isFlashSizeOK=true;
	}
	Log.i(LOG_TAG, "checkRomAvailableSize,isFlashSizeOK="+isFlashSizeOK);
	return isFlashSizeOK;
    } 


 public static long getCurTimeMills(){
 	long currentTimeMillis = System.currentTimeMillis();
	Log.i(LOG_TAG, "getCurTimeMills,currentTimeMillis="+currentTimeMillis);
   	return currentTimeMillis;
}

 public static int getTimeDistance(long startMills,long endMills){
  int timeDistance=0;
  long dis=0;
  Log.i(LOG_TAG, "getTimeDistance,startMills="+startMills);
  Log.i(LOG_TAG, "getTimeDistance,endMills="+endMills);
  if(endMills<=startMills){
	Log.i(LOG_TAG, "timemills error,return timeDistance="+timeDistance);
	return timeDistance;
  }
   dis=endMills-startMills;
  Log.i(LOG_TAG, "getTimeDistance,dis="+dis);
  timeDistance=(int)dis/1000;
    Log.i(LOG_TAG, "getTimeDistance,timeDistance="+timeDistance);
   return timeDistance;
}
 public static void pushStatsOnly(Context context,int type){
	Log.i(LOG_TAG, "pushStatsOnly,type="+type);
	XiaoXunStatisticsManager statisticsManager = (XiaoXunStatisticsManager) context.getSystemService("xun.statistics.service");
	statisticsManager.stats(type);
}

 public static void pushStatsValue(Context context,int type,int value){
	Log.i(LOG_TAG, "pushStatsOnly,type="+type);
	Log.i(LOG_TAG, "pushStatsOnly,value="+value);
	XiaoXunStatisticsManager statisticsManager = (XiaoXunStatisticsManager) context.getSystemService("xun.statistics.service");
	statisticsManager.stats(type,value);
}

public static void pushSendStatusByTime(Context context,int secend){
	//Log.i(LOG_TAG, "pushSendStatusByTime,type="+type);
	int type=0;
	Log.i(LOG_TAG, "pushSendStatusByTime,secend="+secend);
	if(secend>63){
		type=STATS_VOICE_SEND_GREATERTHEN63;
	}else if(secend>31){
		type=STATS_VOICE_SEND_LESSTHEN63;
	}else if(secend>15){
		type=STATS_VOICE_SEND_LESSTHEN31;
	}else if(secend>7){
		type=STATS_VOICE_SEND_LESSTHEN15;
	}else if(secend>3){
		type=STATS_VOICE_SEND_LESSTHEN7;
	}else if(secend<3){
		type=STATS_VOICE_SEND_LESSTHEN3;
	}
	Log.i(LOG_TAG, "pushSendStatusByTime,type="+type);
	pushStatsOnly(context,type);
}

public static int checkPhotoID(Context context,String gid,String eid,int photoID){
        String[] project=new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7","data8","data9","data10","data11","data12","data13","data14","data15"};
        String name;
        String number;
	int realID=0;
	boolean isFind=false;
        Uri uri = Uri.parse ("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        Log.i(LOG_TAG,"checkPhotoID,gid="+gid);
	Log.i(LOG_TAG,"checkPhotoID,eid="+eid);
	Log.i(LOG_TAG,"checkPhotoID,photoID="+photoID);
	if(eid==null||gid==null){
		realID=photoID;
		Log.i(LOG_TAG,"gid or eid error,no need check,return realID="+realID);
		return realID;
	}

	if(photoID<10000){
		realID=photoID;
		Log.i(LOG_TAG,"no need check,return realID="+realID);
		return realID;
	}
	isFind=false;
        while(cursor.moveToNext()&&!isFind){
            boolean is_continue=false;
            int contactsId = cursor.getInt(0);
	    int contract_type=-1;
	    int attri	=0;
	    String user_gid=null;
	    String user_eid=null;
            //Log.i(LOG_TAG,"contactsId = " +contactsId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
            Cursor dataCursor = resolver.query(uri, project, null, null, null);
            //SyncArrayBean  arrayBean = new SyncArrayBean();
            while(dataCursor.moveToNext()) {
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if("vnd.android.cursor.item/nickname".equals(type)){
		    contract_type=dataCursor.getInt(dataCursor.getColumnIndex(project[1]));
                    user_gid=dataCursor.getString(dataCursor.getColumnIndex(project[5]));
                    eid=dataCursor.getString(dataCursor.getColumnIndex(project[4]));
                    attri = dataCursor.getInt(dataCursor.getColumnIndex(project[2]));

                }else  if ("vnd.android.cursor.item/name".equals(type)) {
                   name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
                  
                } else if ("vnd.android.cursor.item/email_v2".equals(type)) {
                } else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                    number = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                    //Log.i(LOG_TAG,"number = " +number);
                }
            }
	    if(gid.equals(WatchSystemInfo.getWatchGID())&&user_eid!=null){
		if(eid.equals(user_eid)){
		realID=attri;
		isFind=true;
		}
	    }else if(user_gid!=null){
		    if((contract_type==0||contract_type==2)&&gid.equals(user_gid)) {
		       // Log.i(LOG_TAG, "add one contracts item");
		        realID=attri;
			isFind=true;
		    }
            }
        }
        cursor.close();
	  Log.i(LOG_TAG,"checkPhotoID,isFind="+isFind);
	if(!isFind){
	realID=photoID;
	}
	Log.i(LOG_TAG,"checkPhotoID,find realID="+realID);
        return realID;
    }

    public static String getVideoThumbnail(int sn,String videoPath){
     Log.i(LOG_TAG,"getVideoThumbnail,sn="+sn);
     Log.i(LOG_TAG,"getVideoThumbnail,videoPath="+videoPath);
     MediaMetadataRetriever media=new MediaMetadataRetriever();
     media.setDataSource(videoPath);
     Bitmap bitmap=null;
     String fileSave=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+chat_audio_folder+"/";
     File fileSAVE=new File(fileSave);
        if(!fileSAVE.exists()){
		fileSAVE.mkdir();
	}

	String picName=sn+".jpg";
      File file=new File(fileSave,picName);
        if(file.exists()){
		return fileSave+picName;
	}

	try{
	bitmap=media.getFrameAtTime();
        FileOutputStream out=new FileOutputStream(file);
	bitmap.compress(Bitmap.CompressFormat.JPEG,20,out);
	out.flush();
 	out.close();
        Log.i(LOG_TAG,"getVideoThumbnail,succ");
	return fileSave+picName;
	}catch(OutOfMemoryError e){
		if(bitmap!=null&&!bitmap.isRecycled()){
		bitmap.recycle();
		bitmap=null;
		}
	}catch(FileNotFoundException e){
		e.printStackTrace();
	}catch(IOException e){
		e.printStackTrace();
	}
	return null;
}

    public static void copyFile(String oldPath, String newPath) {
        Log.i(LOG_TAG,"copyFile,oldPath="+oldPath);
        Log.i(LOG_TAG,"copyFile,newPath="+newPath);
        File newfile = new File(newPath);
        if(!newfile.exists()) {
            ChatUtilCreateFile(newPath);
        }

        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            //System.out.println("复制单个文件操作出错");
            Log.i(LOG_TAG,"copyFile,error");
            e.printStackTrace();

        }

    }

}
