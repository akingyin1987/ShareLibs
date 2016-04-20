package com.zlcdgroup.camera;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import com.zlcdgroup.camera.internal.CameraIntentKey;
import com.zlcdgroup.camera.util.CameraUtil;
import java.io.File;
import java.util.UUID;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialCamera {

    public static final String ERROR_EXTRA = "mcam_error";
    public static final String STATUS_EXTRA = "mcam_status";

    public static final int STATUS_RECORDED = 1;
    public static final int STATUS_RETRY = 2;

    private Activity mContext;

    private String mSaveDir;

    private String mSaveName;

    private FrontLightMode    frontLightMode; //闪光灯


    private VolumeMode     volumeMode; //声音

    private  boolean    isGridlines =false;//网格线

    private  boolean    isLocklandScreen = false;//是否锁定横屏


    public MaterialCamera(@NonNull Activity context) {
        mContext = context;

    }


    public  MaterialCamera  saveName(String   mSavename){
        if(TextUtils.isEmpty(mSavename)){
            this.mSaveName = UUID.randomUUID().toString().replace("-","");
        }else{
            this.mSaveName = mSavename;
        }
        return  this;
    }


    public MaterialCamera saveDir(@Nullable File dir) {
        if (dir == null) return saveDir((String) null);
        return saveDir(dir.getAbsolutePath());
    }

    public MaterialCamera saveDir(@Nullable String dir) {
        mSaveDir = dir;
        return this;
    }




    public Intent getIntent() {
        final Class<?> cls = CameraUtil.hasCamera2(mContext) ?
                CaptureActivity2.class : CaptureActivity.class;
        Intent intent = new Intent(mContext, cls)
                .putExtra(CameraIntentKey.LENGTH_LIMIT, mLengthLimit)
                .putExtra(CameraIntentKey.ALLOW_RETRY, mAllowRetry)
                .putExtra(CameraIntentKey.AUTO_SUBMIT, mAutoSubmit)
                .putExtra(CameraIntentKey.SAVE_DIR, mSaveDir)
                .putExtra(CameraIntentKey.PRIMARY_COLOR, mPrimaryColor)
                .putExtra(CameraIntentKey.SHOW_PORTRAIT_WARNING, mShowPortraitWarning)
                .putExtra(CameraIntentKey.DEFAULT_TO_FRONT_FACING, mDefaultToFrontFacing)
                .putExtra(CameraIntentKey.COUNTDOWN_IMMEDIATELY, mCountdownImmediately)
                .putExtra(CameraIntentKey.RETRY_EXITS, mRetryExists)
                .putExtra(CameraIntentKey.RESTART_TIMER_ON_RETRY, mRestartTimerOnRetry)
                .putExtra(CameraIntentKey.CONTINUE_TIMER_IN_PLAYBACK, mContinueTimerInPlayback);


        return intent;
    }

    public void start(int requestCode) {
        mContext.startActivityForResult(getIntent(), requestCode);
    }
}