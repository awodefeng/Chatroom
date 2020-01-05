package com.xxun.watch.xunchatroom.activity;

import android.media.MediaPlayer;
import android.os.Environment;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import com.xxun.watch.xunchatroom.R;
import com.xxun.watch.xunchatroom.util.DownloadManagerUtil;
import android.view.MotionEvent;
import android.view.View;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import com.xxun.watch.xunchatroom.util.ChatKeyString;
import android.content.Intent;
import android.widget.RelativeLayout;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class VideoDisplayActivity extends Activity implements  SurfaceHolder.Callback,MediaPlayer.OnVideoSizeChangedListener{
    String TAG="VideoDisplayActivity";
    private SurfaceView sfv;//能够播放图像的控件
    private SeekBar sb;//进度条
    private String path=null ;//本地文件路径
    private SurfaceHolder holder;
    private MediaPlayer player;//媒体播放器
    private Button Play;//播放按钮
    private Timer timer;//定时器
    private TimerTask task;//定时器任务
    private int position = 0;
    private EditText et;
    long downloadId=0;
    DownloadManagerUtil downloadManagerUtil;
    private int surfaceWidth;
    private int surfaceHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
	Intent intent =getIntent();
	Log.i(TAG,"onCreate");
        path=intent.getStringExtra(ChatKeyString.KEY_PHOTO_PATH);
	Log.i(TAG,"path="+path);
        initView();
	//downloadManagerUtil=new DownloadManagerUtil(this);
	//startDownload();
    }

    public void changeVideoSize() {
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        Log.i(TAG,"changeVideoSize,"+videoWidth+"/"+videoHeight);
        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float max;
        if (getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) surfaceWidth,(float) videoHeight / (float) surfaceHeight);
        } else{
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) videoWidth/(float) surfaceHeight),(float) videoHeight/(float) surfaceWidth);
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil((float) videoWidth / max);
        videoHeight = (int) Math.ceil((float) videoHeight / max);
        Log.i(TAG,"changeVideoSize,"+videoWidth+"/"+videoHeight);


        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        LinearLayout.LayoutParams params  =
                new LinearLayout.LayoutParams(videoWidth, videoHeight);
       // ViewGroup.LayoutParams params=new RelativeLayout.LayoutParams(videoWidth, videoHeight);
        int margin=(220-videoWidth)/2;
        params.setMargins(margin, 0, margin, 0);
        params.gravity=Gravity.CENTER;
        //RelativeLayout.LayoutParams()
       // sfv.setX((240-videoWidth)/2);
       // sfv.setLeft((240-videoWidth)/2);
        //sfv.set
        sfv.setLayoutParams(params);
        sfv.setKeepScreenOn(true);
       // sfv.setX((240-videoWidth)/2);
      //  sfv.setLeft((240-videoWidth)/2);
       // sfv.setGravity(Gravity.CENTER);
    }


    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        changeVideoSize();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeVideoSize();
    }

    void startDownload(){
        Log.i(TAG,"startDownload");
       // path = Environment.getExternalStorageDirectory().getPath()+"/";
       // path = path +"123.mp4";//sdcard的路径加上文件名称是文件全路径
        File file = new File(path);
        if (file.exists()) {//判断需要播放的文件路径是否存在，不存在退出播放流程
            //Toast.makeText(this,"文件路径不存在", Toast.LENGTH_LONG).show();
		Log.i(TAG,"startDownload,file is exsit");
		//play();
            return;
        }
        //if (downloadId != 0) {
        //    downloadManagerUtil.clearCurrentTask(downloadId);
        //}
        //downloadId = downloadManagerUtil.download("https://raw.githubusercontent.com/dongzhong/ImageAndVideoStore/master/Bruno%20Mars%20-%20Treasure.mp4", "123", "456");
       // Log.i(TAG,"startDownload,downloadId="+downloadId);
    }
    //初始化控件，并且为进度条和图像控件添加监听
    private void initView() {
        sfv = (SurfaceView) findViewById(R.id.sfv);
        //sb = (SeekBar) findViewById(R.id.sb);
        //Play = (Button) findViewById(R.id.play);
        //et = (EditText) findViewById(R.id.et);
        //Play.setEnabled(true);

        holder = sfv.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

      /*  sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //当进度条停止拖动的时候，把媒体播放器的进度跳转到进度条对应的进度
                if (player != null) {
                    player.seekTo(seekBar.getProgress());
                }
            }
        });
*/
        holder.addCallback(this);
	//et.setText("123.mp4");
/*
        Play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
      			Log.i(TAG,"play btn down");
                        break;

                    case MotionEvent.ACTION_UP:
    			Log.i(TAG,"play btn up");
			play();
                        break;
                }
                return false;
            }
        });*/
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //为了避免图像控件还没有创建成功，用户就开始播放视频，造成程序异常，所以在创建成功后才使播放按钮可点击
        Log.d(TAG,"surfaceCreated");
        player = new MediaPlayer();
        player.setOnVideoSizeChangedListener(this);
        player.setDisplay(holder);
        //Play.setEnabled(true);
        if(getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            surfaceWidth=sfv.getWidth();
            surfaceHeight=sfv.getHeight();
        }else {
            surfaceWidth=sfv.getHeight();
            surfaceHeight=sfv.getWidth();
        }
        play();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG,"surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //当程序没有退出，但不在前台运行时，因为surfaceview很耗费空间，所以会自动销毁，
        // 这样就会出现当你再次点击进程序的时候点击播放按钮，声音继续播放，却没有图像
        //为了避免这种不友好的问题，简单的解决方式就是只要surfaceview销毁，我就把媒体播放器等
        //都销毁掉，这样每次进来都会重新播放，当然更好的做法是在这里再记录一下当前的播放位置，
        //每次点击进来的时候把位置赋给媒体播放器，很简单加个全局变量就行了。
        Log.d(TAG,"surfaceDestroyed");
        if (player != null) {
            position = player.getCurrentPosition();
            stop();
        }
    }
    private void play() {
	Log.i(TAG,"play start");
        //Play.setEnabled(true);//在播放时不允许再点击播放按钮

        if (isPause) {//如果是暂停状态下播放，直接start
            isPause = false;
            player.start();
            return;
        }

        //path = Environment.getExternalStorageDirectory().getPath()+"/";
        //path = path +"123.mp4";//sdcard的路径加上文件名称是文件全路径
        File file = new File(path);
        if (!file.exists()) {//判断需要播放的文件路径是否存在，不存在退出播放流程
            Toast.makeText(this,"文件路径不存在", Toast.LENGTH_LONG).show();
            return;
        }

        try {

            player.setDataSource(path);
            player.setDisplay(holder);//将影像播放控件与媒体播放控件关联起来

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {//视频播放完成后，释放资源
                    //Play.setEnabled(true);
                    sfv.setKeepScreenOn(false);
                    stop();
                }
            });

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //媒体播放器就绪后，设置进度条总长度，开启计时器不断更新进度条，播放视频
                    Log.d(TAG,"onPrepared");
                    //sb.setMax(player.getDuration());
                    timer = new Timer();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            if (player != null) {
                                //int time = player.getCurrentPosition();
                                //sb.setProgress(time);
                            }
                        }
                    };
                    timer.schedule(task,0,500);
                    //sb.setProgress(position);
                    player.seekTo(position);
                    player.start();
                }
            });

            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(View v) {
        play();
        Log.d(TAG,path);
    }

    private boolean isPause;
    private void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            isPause = true;
            //Play.setEnabled(true);
        }
    }

    public void pause(View v) {
        pause();
    }

    private void replay() {
        isPause = false;
        if (player != null) {
            stop();
            play();
        }
    }

    public void replay(View v) {
        replay();
    }

    private void stop(){
        isPause = false;
        if (player != null) {
           // sb.setProgress(0);
            player.stop();
            player.release();
            player = null;
            if (timer != null) {
                timer.cancel();
            }
            //Play.setEnabled(true);
        }
        sfv.setKeepScreenOn(false);
    }

    public void stop(View v) {
        stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sfv.setKeepScreenOn(false);
        stop();
    }

}
