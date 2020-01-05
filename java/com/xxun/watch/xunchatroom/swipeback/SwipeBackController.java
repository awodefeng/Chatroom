package com.xxun.watch.xunchatroom.swipeback;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.FrameMetrics.ANIMATION_DURATION;

public class SwipeBackController  {
    private int mScreenWidth;
    private int mTouchSlop;

    private boolean isMoving = false;
    private float mInitX;
    private float mInitY;

    private ViewGroup decorView;//窗口根布局
    private ViewGroup contentView;//content布局
    private ViewGroup userView;//用户添加的布局

    private ArgbEvaluator evaluator;
    private ValueAnimator mAnimator;
    private VelocityTracker mVelTracker;

    String LOG_TAG="swipe back";
    public SwipeBackController(final Activity activity) {
        mScreenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        mTouchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();
        evaluator = new ArgbEvaluator();

        decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ffffff")));
        contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        userView = (ViewGroup) contentView.getChildAt(0);

        mAnimator = new ValueAnimator();
        mAnimator.setDuration(ANIMATION_DURATION);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int x = (Integer) valueAnimator.getAnimatedValue();
                if (x >= mScreenWidth) {
                    activity.finish();
                }

                handleView(x);
                handleBackgroundColor(x);
            }
        });
    }

    public void handleView(int x) {
        userView.setTranslationX(x);
    }
    /**
     * 控制背景颜色和透明度
     * @param x
     */
    private void handleBackgroundColor(float x) {
        int colorValue = (int) evaluator.evaluate(x / mScreenWidth,
                Color.parseColor("#dd000000"), Color.parseColor("#00000000"));
        contentView.setBackgroundColor(colorValue);
        Log.i(LOG_TAG, "x is " + x);
    }

    void getVelocityTracker(MotionEvent event){
        if (null == mVelTracker) {
                    mVelTracker = VelocityTracker.obtain();
                    }
        mVelTracker.addMovement(event);
    }

    void recycleVelocityTracker(){
        if (null != mVelTracker) {
                                mVelTracker.recycle();
        }

    }

    public boolean processEvent(MotionEvent event) {
        getVelocityTracker(event);
        Log.i(LOG_TAG,"processEvent");
        if (mAnimator.isRunning()) {
            return true;
        }

        int pointId = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(LOG_TAG,"tp down");
                mInitX = event.getRawX();
                mInitY = event.getRawY();
                pointId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(LOG_TAG,"tp move="+isMoving);
                if (!isMoving) {
                    float dx = Math.abs(event.getRawX() - mInitX);
                    float dy = Math.abs(event.getRawY() - mInitY);
                    if (dx > mTouchSlop && dx > dy && mInitX < 1000) {
                        isMoving = true;
                    }
                }
                if (isMoving) {
                    handleView((int) event.getRawX());
                    handleBackgroundColor(event.getRawX());
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i(LOG_TAG,"tp up");
                int distance = (int) (event.getRawX() - mInitX);

                mVelTracker.computeCurrentVelocity(1000);
                //获取x方向上的速度
                float velocityX = mVelTracker.getXVelocity(pointId);

                Log.i(LOG_TAG, "mVelocityX is " + velocityX);
                if (isMoving && Math.abs(userView.getTranslationX()) >= 0) {
                    if (distance >= mScreenWidth / 4 || velocityX > 1000f) {
                        mAnimator.setIntValues((int) event.getRawX(), mScreenWidth);
                    } else {
                        mAnimator.setIntValues((int) event.getRawX(), 0);
                    }
                    recycleVelocityTracker();
                    mAnimator.start();
                    isMoving = false;
                }

                mInitX = 0;
                mInitY = 0;

                recycleVelocityTracker();
                break;
        }
        return true;
    }
}
