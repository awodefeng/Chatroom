package com.xxun.watch.xunchatroom.control;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;
import android.provider.Settings;
import java.io.IOException;

public class ChatVibratorControl {

    static Vibrator vibrator = null;
    static String LOG_TAG ="ChatVibratorControl";
    public final static int VIBRATE_RING_MODE=1;
   public  void ChatRoomControl(){

   }

 static  public  void StartPlaying(Context context){
     Log.i(LOG_TAG, "StartPlaying");
	if(isVibrate(context)){
		Log.i(LOG_TAG, "need play vib");
	}else{
		Log.i(LOG_TAG, "forbid play vib,return");
		return;
	}
        StopPlaying();
     vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
     long [] pattern = {100,500,100};   // 停止 开启 停止 开启
      vibrator.vibrate(pattern,-1);

 }

    static  public void StopPlaying(){
        Log.i(LOG_TAG, "StopPlaying");
        if(vibrator!=null) {
            vibrator.cancel();
            vibrator = null;
        }
    }
 
    private static boolean isVibrate(Context context) {
	AudioManager mAudioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int vibrateType = android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.VIBRATE_WHEN_RINGING, 0);
        Log.d(LOG_TAG, "vibrateType: " + vibrateType);
        return vibrateType == VIBRATE_RING_MODE // ÏìÁå+Õñ¶¯Ä£Êœ
                || mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE; //Õñ¶¯Ä£Êœ
    }

}
