package com.xxun.watch.xunchatroom.activity;

import com.xxun.watch.xunchatroom.R;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.view.View;
import android.media.MediaPlayer;
import android.widget.ImageView;
import android.os.PowerManager;
import android.content.Context;
import android.os.Vibrator;
import android.media.AudioManager;
import android.provider.Settings;
/**
 * @author lihaizhou
 * @time 2018.06.22
 * @class describe 充电禁用状态下，来语音消息弹出的提醒界面
 */

public class NewChatMsgAlertActivity extends Activity {

    private ImageView confirmBtn;
    private MediaPlayer mMediaPlayer;
    private PowerManager.WakeLock wakeLock;
    private PowerManager powerManager = null;
    private Vibrator mVibrator;
    private int MUTE = 0;
    private int VIBRATE = 1;
    private int SOUND = 2;

    private static final String TAG = "NewChatMsgAlertActivity";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_chatmsg);
	confirmBtn = (ImageView) findViewById(R.id.confirm);
	confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
	playAlertSound();
	//lightScreen();
    }  
    
    private void lightScreen(){
        if (powerManager == null) {
            powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        }
	if(!powerManager.isScreenOn()){
	    wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "filtercamera");
            wakeLock.acquire(500);
	}
    }

    /**
     * @author lihaizhou
     * @createtime 2018.06.25
     * @describe play alert sound or Vibrator or null depend on user setting when receive chat msg
     */
    private void playAlertSound(){
	mVibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        final int ringerMode = am.getRingerMode();
	if (ringerMode == MUTE) {
            //do nothing
        }else if (ringerMode == VIBRATE) {
            mVibrator.vibrate(new long[]{100,0,100,500},-1);
        } else if (ringerMode == SOUND) {
	     if(isSetVibrate()){
	        mVibrator.vibrate(new long[]{100,0,100,500},-1);
	     }
             mMediaPlayer = MediaPlayer.create(this, R.raw.new_msg);
	     mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
               @Override
               public void onPrepared(MediaPlayer mp) {
                  mMediaPlayer.start();
               }
             });
        }
     }
     
     /**
     * @author lihaizhou
     * @createtime 2018.06.26
     * @describe judge whether user set Vibrate
     */
     private boolean isSetVibrate() {
	AudioManager mAudioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int vibrateType = Settings.System.getInt(getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING, 0);
        return vibrateType == VIBRATE;
    }
    
      /**
     * @author lihaizhou
     * @createtime 2018.06.28
     * @describe 当前Activity为SingleInstance模式
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        playAlertSound();
    }

    @Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
        super.onWindowFocusChanged(hasFocus);
	if(!hasFocus){
	    Intent intent = new Intent();
            intent.setAction("com.xxun.watch.resetNewMsgFlag");
            sendBroadcast(intent);
	}
    } 
  
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
