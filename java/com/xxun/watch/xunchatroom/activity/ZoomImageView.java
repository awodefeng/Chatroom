package com.xxun.watch.xunchatroom.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
//import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.xxun.watch.xunchatroom.util.ImageLoadUtils;
import java.io.File;
import com.xxun.watch.xunchatroom.R;
import android.util.Log;
import com.xxun.watch.xunchatroom.util.ChatThreadTimer;
//import android.support.annotation.Nullable;
/**
 * Created by newcboy on 2018/3/9.
 */

public class ZoomImageView extends View {

    public static final int IMAGE_MAX_SIZE = 500;//����ͼƬ��������size����λkb
    private float minimal = 100.0f;

    private float screenW;//��Ļ���
    private float screenH;//��Ļ�߶�

    //��ָ���µ�����
    private float mFirstX = 0.0f;
    private float mFirstY = 0.0f;

    //��ָ�뿪������
    private float lastMoveX =-1f;
    private float lastMoveY =-1f;

    //��ָ���е�����
    private float centPointX;
    private float centPointY;

    //ͼƬ�Ļ�������
    private float translationX = 0.0f;
    private float translationY = 0.0f;

    //ͼƬ��ԭʼ���
    private float primaryW;
    private float primaryH;

    //ͼƬ��ǰ���
    private float currentW;
    private float currentH;

    private float scale = 1.0f;
    private float maxScale, minScale;
    private Bitmap bitmap;
    private Matrix matrix;

    private int mLocker = 0;
    private float fingerDistance = 0.0f;

    private boolean isLoaded = false;
    private boolean isClickInImage = false;

    private String TAG="ZoomImageView";
    private String LOG_TAG="ZoomImageView";
    private int displayMode=0;
    private ChatThreadTimer myThread=null;
    private int clickStatus=0;
    private int tapCount=0;
    private float scale1 = 1.0f;
    private float scale2 = 1.0f;
    private float downX = 0.0f;
    private float downY = 0.0f;
    //public ZoomImageView(Context context) {
    //    this(context, null);
    //}

   // public ZoomImageView(Context context, AttributeSet attrs) {
       // this(context, attrs,null);
   // }

    public ZoomImageView(Context context,/* @Nullable*/ AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * ����Դ�ļ��ж�ȡͼƬ
     * @param context
     * @param imageId
     */
    public void setResourceBitmap(Context context, int imageId){
        bitmap = BitmapFactory.decodeResource(context.getResources(), imageId);
        isLoaded = true;
        primaryW = bitmap.getWidth();
        primaryH = bitmap.getHeight();
        matrix = new Matrix();
    }

    /**
     * ����·�����ͼƬ
     * @param path
     * @param scale
     */
    public void setImagePathBitmap(String path, float scale){
        this.scale = scale;
        setImageBitmap(path);
    }

 private void initPhotoScale(Bitmap bitmap){
        float xScale, yScale;
	Log.i(TAG,"initPhotoScale");
        //xScale = minimal / primaryW;
        //yScale = minimal / primaryH;
        //minScale = xScale > yScale ? xScale : yScale;
          screenW = 230;//getWidth();
          screenH = 230;//getHeight();
        Log.i(TAG,"initPhotoScale,photo="+primaryW+"/"+primaryH);
	Log.i(TAG,"initPhotoScale,screen="+screenW+"/"+screenH);
        xScale = primaryW / screenW;
        yScale = primaryH / screenH;
	if(xScale>1&&yScale > 1){
	    if (xScale > yScale) {
                this.scale = 1/xScale;
            }else {
                this.scale = 1/yScale;
            }
	}else if(xScale<1&&yScale <1){
	    if(xScale>yScale){
		this.scale = 1/xScale;
	    }else{
		this.scale = 1/yScale;
	    }
	}else if(xScale>1&&yScale<1){
		this.scale = 1/xScale;
	}else if(xScale<1&&yScale>1){
		this.scale = 1/yScale;
	}
	
/*
        if (xScale > 1 || yScale > 1 ) {
            if (xScale > yScale) {
                this.scale = 1/xScale;
            }else {
                this.scale = 1/yScale;
            }
        }else {
            if (xScale > yScale) {
                this.scale = 1/xScale;
            }else {
                this.scale = 1/yScale;
            }
        }
*/
     displayMode=0;
     clickStatus=0;
     tapCount=0;
     this.scale1=this.scale;
     this.scale2=this.scale*2;
	Log.i(TAG,"initPhotoScale,scale="+this.scale);
}

    private void setImageBitmap(String path){
        File file = new File(path);
        if (file.exists()){
            isLoaded = true;
            bitmap = ImageLoadUtils.getImageLoadBitmap(path, IMAGE_MAX_SIZE);
            primaryW = bitmap.getWidth();
            primaryH = bitmap.getHeight();
	    initPhotoScale(bitmap);
            matrix = new Matrix();
        }else {
            isLoaded = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            screenW = getWidth();
            screenH = getHeight();
            translationX = (screenW - bitmap.getWidth() * scale)/  2;
            translationY = (screenH - bitmap.getHeight() * scale) / 2;
            setMaxMinScale();
        }
    }

    /**
     *
     */
    private void setMaxMinScale(){
        float xScale, yScale;

        xScale = minimal / primaryW;
        yScale = minimal / primaryH;
        minScale = xScale > yScale ? xScale : yScale;

        xScale = primaryW / screenW;
        yScale = primaryH / screenH;
        if (xScale > 1 || yScale > 1 ) {
            if (xScale > yScale) {
                maxScale = 1/xScale;
            }else {
                maxScale = 1/yScale;
            }
        }else {
            if (xScale > yScale) {
                maxScale = 1/xScale;
            }else {
                maxScale = 1/yScale;
            }
        }
	minScale=0.5f;
	maxScale=2f;
        if (isScaleError()){
            restoreAction();
        }
    }

    private void startClickTimer(){
        Log.i(TAG,"startClickTimer");
        stopClickTimer();
        myThread=new ChatThreadTimer(500, new ChatThreadTimer.TimerInterface() {
            @Override
            public void doTimerOut() {
                Log.i(LOG_TAG, "doTimerOut");
                stopClickTimer();
                tapCount=0;
            }
        });

        myThread.start();
    }

    private void stopClickTimer(){
        Log.i(TAG,"stopClickTimer");
        if(myThread!=null) {
            myThread.stopThreadTimer();
        }
        myThread=null;
    }

    private void handleTapDown(){
        Log.i(TAG,"handleTapDown,tapCount="+tapCount);
        if(tapCount==0){
            tapCount++;
            startClickTimer();
        }else{
            stopClickTimer();
            tapCount=0;
            changeDisplayMode();
        }


    }

    public interface onMoveFinishListener {
        void onFinish();
    }

    private onMoveFinishListener mOnMoveFinishListener;

    public void setOnMoveFinishListener(onMoveFinishListener mOnMoveFinishListener) {
        this.mOnMoveFinishListener = mOnMoveFinishListener;
    }

    private void changeDisplayMode(){
        Log.i(TAG,"changeDisplayMode,displayMode="+displayMode);
        if(displayMode==0){
            displayMode=1;
            this.scale=this.scale2;
            translationX = (screenW - bitmap.getWidth() *this.scale)/  2;
            translationY = (screenH - bitmap.getHeight() *this.scale) / 2;
        }else{
            displayMode=0;
            this.scale=this.scale1;
            translationX = (screenW - bitmap.getWidth() * this.scale)/  2;
            translationY = (screenH - bitmap.getHeight() * this.scale) / 2;
        }
        invalidate();

    }

    private void handleBigMove(float x,float y){
        Log.i(TAG,"handleBigMove,"+x+"/"+y);
        float distancX=x-downX;
        float distancY=y-downY;

       // translationX = translationX + (distancX/2);
      //  translationY = translationY + (distancY/2);
         translationX = translationX + (distancX/16)*this.scale;
          translationY = translationY + (distancY/16)*this.scale;
        if(translationX>0){
            translationX=0;
        }

        if(translationY>0){
            translationY=0;
        }

        if(translationX<(screenW - bitmap.getWidth() *this.scale)){
            translationX=screenW - bitmap.getWidth() *this.scale;
        }

        if(translationY<(screenH - bitmap.getHeight() * this.scale)){
            translationY=screenH - bitmap.getHeight() * this.scale;
        }

        invalidate();
    }

    private boolean handleSmallMove(float x,float y){
        boolean resault=false;
        float distancX=x-downX;
        float distancY=y-downY;

        if(distancY>=-40&&distancY<40&&distancX>100){
            resault=true;
            if(mOnMoveFinishListener!=null){
                mOnMoveFinishListener.onFinish();
            }
        }
        return resault;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isLoaded){
            return false;
        }
        boolean resault=false;

        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                handleTapDown();
                clickStatus=1;
                downX = event.getX();
                downY = event.getY();
                resault=true;
                break;

            case MotionEvent.ACTION_UP:
                clickStatus=0;
                break;

            case MotionEvent.ACTION_MOVE:
                if(clickStatus==1&&displayMode==1){
                    handleBigMove(event.getX(),event.getY());
                    resault=true;
                }else{
                    resault=handleSmallMove(event.getX(),event.getY());
                }
                break;
        }
        return resault;
	//return false;
        /*
        if (!isLoaded){
            return true;
        }
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mFirstX = event.getX();
                mFirstY = event.getY();
		lastMoveX=mFirstX;
		lastMoveY=mFirstY;
		Log.i(TAG,"tp down,"+mFirstX+"/"+mFirstY);
                isClickInImage();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                fingerDistance = getFingerDistance(event);
		Log.i(TAG,"tp down point,"+fingerDistance);
                isClickInImage(event);
                break;
            case MotionEvent.ACTION_MOVE:
                float fingerNum = event.getPointerCount();
              /*  if (fingerNum == 1 && mLocker == 0 && isClickInImage){
                    movingAction(event);
                }else if (fingerNum == 2 && isClickInImage){
                    zoomAction(event);
                }*//*
		Log.i(TAG,"tp move,fingerNum="+fingerNum);
		if(fingerNum == 1 ){
			movingAction(event);
		}else if(fingerNum == 2){
			zoomAction(event);
		}
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mLocker = 1;
                if (isScaleError()){
                    translationX = (event.getX(1) + event.getX(0)) / 2;
                    translationY = (event.getY(1) + event.getY(0)) / 2;
                }
                break;
            case MotionEvent.ACTION_UP:
                lastMoveX = -1;
                lastMoveY = -1;
                mLocker = 0;
                if (isScaleError()){
                    restoreAction();
                }
                break;
        }
        return true;*/
    }


    /**
     * �ƶ�����
     * @param event
     */
    private void movingAction(MotionEvent event){
        float moveX = event.getX();
        float moveY = event.getY();
        if (lastMoveX == -1 || lastMoveY == -1) {
            //lastMoveX = moveX;
            //lastMoveY = moveY;
        }
        float moveDistanceX = moveX - lastMoveX;
        float moveDistanceY = moveY - lastMoveY;
        translationX = translationX + moveDistanceX;
        translationY = translationY + moveDistanceY;
        lastMoveX = moveX;
        lastMoveY = moveY;
        invalidate();
    }

    /**
     * ���Ų���
     * @param event
     */
    private void zoomAction(MotionEvent event){
        Log.i("TAG","zoomAction");
        midPoint(event);
        float currentDistance = getFingerDistance(event);
	if(Math.abs(currentDistance - fingerDistance) > 1){
		float moveScale = currentDistance / fingerDistance;
		scale=moveScale;
		
		translationX=translationX+(translationX-centPointX)*scale;
		translationY=translationY+(translationY-centPointY)*scale;
		invalidate();
	}
	
      /*  if (Math.abs(currentDistance - fingerDistance) > 1f) {
            float moveScale = currentDistance / fingerDistance;
            scale = scale * moveScale;
            //translationX = translationX * moveScale + centPointX * (1-moveScale);
            //translationY = translationY * moveScale + centPointY * (1-moveScale);
            fingerDistance = currentDistance;
            invalidate();
        }*/
    }

    /**
     * ͼƬ�ָ���ָ����С
     */
    private void restoreAction(){
        if (scale < minScale){
            scale  = minScale;
        }else if (scale > maxScale){
            scale = maxScale;
        }
        translationX = translationX - bitmap.getWidth()*scale / 2;
        translationY = translationY - bitmap.getHeight()*scale / 2;
        invalidate();
    }


    /**
     * �ж���ָ�Ƿ����ͼƬ��(��ָ)
     */
    private void isClickInImage(){
        if (translationX <= mFirstX && mFirstX <= (translationX + currentW)
                && translationY <= mFirstY && mFirstY <= (translationY + currentH)){
            isClickInImage = true;
        }else {
            isClickInImage = false;
        }
    }

    /**
     * �ж���ָ�Ƿ����ͼƬ��(˫ָ)
     * ֻҪ��һֻ��ָ��ͼƬ�ھ�Ϊtrue
     * @param event
     */
    private void isClickInImage(MotionEvent event){
        if (translationX <= event.getX(0) && event.getX(0) <= (translationX + currentW)
                && translationY <= event.getY(0) && event.getY(0) <= (translationY + currentH)){
            isClickInImage = true;
        }else if (translationX <= event.getX(1) && event.getX(1) <= (translationX + currentW)
                && translationY <= event.getY(1) && event.getY(1) <= (translationY + currentH)){
            isClickInImage = true;
        }else {
            isClickInImage = false;
        }
    }


    /**
     * ��ȡ��ָ��ľ���
     * @param event
     * @return
     */
    private float getFingerDistance(MotionEvent event){
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * �ж�ͼƬ��С�Ƿ����Ҫ��
     * @return
     */
    private boolean isScaleError(){
        if (scale > maxScale
                || scale < minScale){
            return true;
        }
        return false;
    }


    /**
     * ��ȡ��ָ����е�����
     * @param event
     */
    private void midPoint(MotionEvent event){
        centPointX = (event.getX(1) + event.getX(0))/2;
        centPointY = (event.getY(1) + event.getY(0))/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isLoaded){
            imageZoomView(canvas);
        }
    }

    private void imageZoomView(Canvas canvas){
	Log.i(TAG,"imageZoomView,scale="+scale);
        currentW = primaryW * scale;
        currentH = primaryH * scale;
        matrix.reset();
        matrix.postScale(scale, scale);//x��y������
       // peripheryJudge();
	Log.i(TAG,"imageZoomView,"+translationX+"/"+translationY);
        matrix.postTranslate(translationX, translationY);//�е������ƶ�
        canvas.drawBitmap(bitmap, matrix, null);
    }

    /**
     * ͼƬ�߽���
     * (ֻ����Ļ��)
     */
    private void peripheryJudge(){
        if (translationX < 0){
            translationX = 0;
        }
        if (translationY < 0){
            translationY = 0;
        }
        if ((translationX + currentW) > screenW){
            translationX = screenW - currentW;
        }
        if ((translationY + currentH) > screenH){
            translationY = screenH - currentH;
        }
    }

}
