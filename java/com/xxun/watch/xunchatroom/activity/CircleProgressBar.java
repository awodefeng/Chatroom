package com.xxun.watch.xunchatroom.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.xxun.watch.xunchatroom.R;

public class CircleProgressBar extends View {

    // 画圆环的画笔
    private Paint ringPaint;
    // 画字体的画笔
    private Paint textPaint;
    // 圆环颜色
    private int ringColor;
    // 字体颜色
    private int textColor;
    // 半径
    private float radius;
    // 圆环宽度
    private float strokeWidth;
    // 字的长度
    private float txtWidth;
    // 字的高度
    private float txtHeight;
    // 总进度
    private int totalProgress = 159;
    // 当前进度
    private int currentProgress;
    // 透明度
    private int alpha = 25;

    private boolean noPaint=false;
    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("progress","CircleProgressBar");
        initAttrs(context, attrs);
        initVariable();
	//postInvalidate();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        Log.i("progress","initAttrs");
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressbar, 0 , 0);
        radius = typeArray.getDimension(R.styleable.CircleProgressbar_radius, 80);
        strokeWidth = typeArray.getDimension(R.styleable.CircleProgressbar_strokeWidth, 20);
        ringColor = typeArray.getColor(R.styleable.CircleProgressbar_ringColor, 0x00FF00);
        textColor = typeArray.getColor(R.styleable.CircleProgressbar_textColor, 0x00FF00);

    }

    private void initVariable() {
        Log.i("progress","initVariable");
        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setDither(true);
        ringPaint.setColor(ringColor);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setStrokeWidth(strokeWidth);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextSize(radius/2);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        txtHeight = fm.descent + Math.abs(fm.ascent);

    }

    @Override
    protected void onDraw(Canvas canvas) {
	Log.i("progress","getHeight()="+getHeight());	
	Log.i("progress","getWidth()="+getWidth());
	Log.i("progress","noPaint="+noPaint);	
	if(noPaint){
	   Log.i("progress","no need paint,return");	
	   return;
	}
        Paint mPaint=new Paint();
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0,0,getWidth(),getHeight(),mPaint);

        if (currentProgress >= 0&&currentProgress<=totalProgress) {
            //ringPaint.setAlpha((int) (alpha + ((float) currentProgress / totalProgress)*230));
            RectF oval = new RectF(getWidth() / 2 - radius, getHeight() / 2 - radius, getWidth() / 2 + radius, getHeight() / 2 + radius);
            //canvas.drawArc(oval, 0, 0, false, ringPaint);
            canvas.drawArc(oval, -90, ((float) currentProgress / totalProgress) * 360, false, ringPaint);
            String txt = (totalProgress-currentProgress)/10 + "'";
	    Log.i("progress","ondraw currentProgress="+currentProgress);
            Log.i("progress","ondraw txt="+txt);
            txtWidth = textPaint.measureText(txt, 0, txt.length());
            Log.i("progress","txtWidth="+txtWidth);
            Log.i("progress","txtHeight="+txtHeight);
            Log.i("progress","x="+(getWidth() / 2 - txtWidth / 2));
            Log.i("progress","y="+( getHeight() / 2 + txtHeight / 4));
            canvas.drawText(txt, getWidth() / 2 - txtWidth / 2, getHeight() / 2 + txtHeight / 4, textPaint);

        }
    }

    public void setProgress(int progress) {
	noPaint=false;
        currentProgress = progress;
        postInvalidate();
    }


    public void clearProgress(){
        totalProgress = 159;
        // 当前进度
        currentProgress=0;
	noPaint=true;
    }


}
