package com.xxun.watch.xunchatroom.control;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import java.io.IOException;
import android.provider.Settings;

public class ChatSoundControl {

    static MediaPlayer mPlayer = null;
    static String LOG_TAG ="ChatSoundControl";
    private static AudioManager mAudioManager=null;
    private static AudioManager.OnAudioFocusChangeListener audioFocusChangeListener=null;
   public  void ChatRoomControl(){

   }

	


 static  public  void StartPlaying(Activity context,int rawId,MediaPlayer.OnCompletionListener compleListener,MediaPlayer.OnErrorListener errorListener){
     Log.i(LOG_TAG, "StartPlaying");
	if(doesRing(context)){
		Log.i(LOG_TAG, "need play ring");
	}else{
		Log.i(LOG_TAG, "forbid play ring,return");
		return;
	}

        StopPlaying();
        mPlayer = new MediaPlayer();
        context.setVolumeControlStream(AudioManager.STREAM_NOTIFICATION);
        mPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        mPlayer.setOnCompletionListener(compleListener);
	mPlayer.setOnErrorListener(errorListener);
////////////////////////////foucus////////////////////////////
	if(mAudioManager==null) {
	mAudioManager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}
	if(mAudioManager!=null){
	if(audioFocusChangeListener==null){
	  audioFocusChangeListener=new AudioManager.OnAudioFocusChangeListener(){
	@Override
	public void onAudioFocusChange(int focusChange) {
		switch(focusChange) {
			case AudioManager.AUDIOFOCUS_LOSS:
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				if (mPlayer != null) {
				//mPlayer.stop();
				}
				break;
				default:
				break;
			}
		}
	};
	}
	mAudioManager.requestAudioFocus(audioFocusChangeListener,AudioManager.STREAM_NOTIFICATION,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
	} 
//////////////////////////////////////////////////////////////

        AssetFileDescriptor file;
        file = context.getResources().openRawResourceFd(rawId);

         try{
             mPlayer.setDataSource(file.getFileDescriptor(),
                     file.getStartOffset(), file.getLength());
             file.close();
             float BEEP_VOLUME = 1.0f;
             mPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
             mPlayer.prepare();
             mPlayer.start();
             Log.i("chat","start playing");
            }catch(IOException e){
	     Log.i("chat","catch playing error");
            //
           }

    }


static public void requestAudioPlayerFocus(Activity context){
	Log.i("chat","requestAudioPlayerFocus");
	if(mAudioManager==null) {
	mAudioManager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}
	if(mAudioManager!=null){
	if(audioFocusChangeListener==null){
	 audioFocusChangeListener=new AudioManager.OnAudioFocusChangeListener(){
	@Override
	public void onAudioFocusChange(int focusChange) {
		switch(focusChange) {
			case AudioManager.AUDIOFOCUS_LOSS:
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				if (mPlayer != null) {
				//mPlayer.stop();
				}
				break;
				default:
				break;
			}
		}
	};
	}
	mAudioManager.requestAudioFocus(audioFocusChangeListener,AudioManager.STREAM_NOTIFICATION,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
	} 
}

static public void releaseAudioPlayerFocus(){
	Log.i("chat","releaseAudioPlayerFocus");
	if(mAudioManager!=null&&audioFocusChangeListener!=null){
		Log.i("chat","releaseAudioPlayerFocus,ok");
		mAudioManager.abandonAudioFocus(audioFocusChangeListener);
	}else{
		Log.i("chat","releaseAudioPlayerFocus,fail");
	}
}

    static  public void StopPlaying(){
        Log.i(LOG_TAG, "StopPlaying");
        if(mPlayer!=null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private  static boolean doesRing(Activity context) {
	AudioManager mAudioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int ringType = mAudioManager.getRingerMode();
        Log.d(LOG_TAG, "vibrateType: " + ringType);
        return ringType == AudioManager.RINGER_MODE_NORMAL;
    } 
}
