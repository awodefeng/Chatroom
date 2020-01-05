package com.xxun.watch.xunchatroom.util;

import android.util.Log;

import com.xiaoxun.sdk.XiaoXunNetworkManager;

public class ChatThreadTimer extends Thread {
    public boolean stop;
    String LOG_TAG="chat thread timer";
    int delayTime =0;
    TimerInterface mInterface;

    public interface TimerInterface{
           public void doTimerOut();
    }

    public ChatThreadTimer(int timePiroid,TimerInterface chatInterface){
        this.mInterface=chatInterface;
        this.delayTime=timePiroid;
    }


    public void run() {
        while (!stop) {
            // 处理功能
            Log.i(LOG_TAG,"ChatThreadTimer,run");
            
            // 通过睡眠线程来设置定时时间
            try {
                Thread.sleep(this.delayTime);
		this.mInterface.doTimerOut();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void stopThreadTimer(){
        Log.i(LOG_TAG,"stopThreadTimer");
        this.stop=true;
	//this.stop();
    }

}
