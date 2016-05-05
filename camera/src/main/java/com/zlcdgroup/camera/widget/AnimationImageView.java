package com.zlcdgroup.camera.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.zlcdgroup.camera.R;
import com.zlcdgroup.camera.internal.Camera2Fragment;
import com.zlcdgroup.camera.internal.SleepThread;

/**
 *
 */
public class AnimationImageView extends ImageView {
    private Handler mMainHandler;
    private Animation mAnimation;
    private Context mContext;
    /**
     * 防止又换了个text，但是上次哪个还没有消失即将小时就把新的text的给消失了
     */
    public int mTimes = 0;

    public AnimationImageView(Context context) {
        super(context);
        mContext = context;
    }

    public AnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AnimationImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnimationImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public void setmMainHandler(Handler mMainHandler) {
        this.mMainHandler = mMainHandler;
    }

    public void setmAnimation(Animation mAnimation) {
        this.mAnimation = mAnimation;
    }

    public void initFocus() {
        this.setVisibility(VISIBLE);
        new Thread(new SleepThread(mMainHandler, Camera2Fragment.FOCUS_DISAPPEAR, 1000, null)).start();
    }

    public void startFocusing() {
        mTimes++;
        this.setVisibility(View.VISIBLE);
        this.startAnimation(mAnimation);
        this.setBackgroundResource(R.drawable.focus);
        new Thread(new SleepThread(mMainHandler, Camera2Fragment.FOCUS_DISAPPEAR, 1000, Integer.valueOf(mTimes))).start();
    }


    public void focusFailed() {
        mTimes++;

        this.setBackgroundResource(R.drawable.focus_failed);
        new Thread(new SleepThread(mMainHandler, Camera2Fragment.FOCUS_DISAPPEAR, 800, Integer.valueOf(mTimes))).start();
    }

    public void focusSuccess() {
        mTimes++;
        this.setVisibility(View.VISIBLE);
        this.setBackgroundResource(R.drawable.focus_succeed);
        new Thread(new SleepThread(mMainHandler, Camera2Fragment.FOCUS_DISAPPEAR, 800, Integer.valueOf(mTimes))).start();
    }

    public void stopFocus() {
        this.setVisibility(INVISIBLE);
    }
}
