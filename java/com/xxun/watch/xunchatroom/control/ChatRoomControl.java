package com.xxun.watch.xunchatroom.control;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.media.AudioManager;
import com.xxun.watch.xunchatroom.util.ChatUtil;

import net.minidev.json.JSONObject;

import java.io.IOException;
import java.lang.IllegalStateException;

public class ChatRoomControl{

public static final int ChatStateIdle=0;
public static final int ChatStateRecording=1;
public static final int ChatStatePlaying=2;
public static final int ChatStateError=3;

    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private static ChatRoomControl chatroomControl=null;
    public static int ChatCurState=0;
    String chat_audio_folder="chat_audio";
 private boolean debugMode=false;//true;;
   public  void ChatRoomControl(){
       //ChatCurState=ChatStateIdle;
   }

    public synchronized static ChatRoomControl getInstance() {
        if (chatroomControl == null)
            chatroomControl = new ChatRoomControl();
        return chatroomControl;
    }

   public String ChatPrepareRecord(){
        String FileName=null;
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileName +="/"+chat_audio_folder+"/"+ ChatUtil.ChatUtilGetDate()+".amr";
       return FileName;
    }

    public int StartRecord(String FileName){
       mRecorder = new MediaRecorder();
	if(mRecorder==null){
	Log.i("chat","mRecorder is null,return");
	return ChatStateError;
	}
	if(debugMode){
		ChatCurState=ChatStateError;
	}else{
       mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
       mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
       mRecorder.setAudioEncodingBitRate(5150);//5150
       mRecorder.setOutputFile(FileName);
	mRecorder.setMaxDuration(15900);
      // mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
      // mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
       mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
	    ChatCurState=ChatStateRecording;
           } catch (IOException e) {
            //Log.e(LOG_TAG, "prepare() failed");
	     ChatCurState=ChatStateError;
            }catch (IllegalStateException e){
	     Log.i("chat","start record,error");
	     ChatCurState=ChatStateError;
	    }
	}
       
       Log.i("chat","start record,ChatCurState="+ChatCurState);
       return ChatCurState;
   }

    public void StopRecord(){
	if(mRecorder!=null){
	if(ChatCurState==ChatStateRecording){	
        mRecorder.stop();
	}
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
	}
        Log.i("chat","stop record");
        ChatCurState=ChatStateIdle;
    }

    public int StartPlaying(String FileName, MediaPlayer.OnCompletionListener compleListener,MediaPlayer.OnErrorListener errorlistener){
	StopPlaying();
        mPlayer = new MediaPlayer();
	mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	mPlayer.setOnCompletionListener(compleListener);
	mPlayer.setOnErrorListener(errorlistener);
        Log.i("chat","FileName="+FileName);
         try{
            mPlayer.setDataSource(FileName);
           //  mPlayer.setVolume(100,100);
	    float BEEP_VOLUME = 1.0f;
             mPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mPlayer.prepare();
             mPlayer.start();
	     ChatCurState=ChatStatePlaying;
             Log.i("chat","start playing");
            }catch(IOException e){
	     Log.i("chat","catch playing error1");
	     ChatCurState=ChatStateError;
            //
           }catch(Exception e){
	   Log.i("chat","catch playing error2");
	   ChatCurState=ChatStateError;
	   }
        
        ChatCurState=ChatStatePlaying;
        return ChatCurState;
    }

    public void StopPlaying(){
	if(mPlayer!=null){
        mPlayer.release();
         mPlayer = null;
	}
        ChatCurState=ChatStateIdle;
    }


}
