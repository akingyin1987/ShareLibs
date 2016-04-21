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
    public static final String RESULT_FILE="result_file";

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
        System.out.println(mSaveDir+":"+mSaveName);
        Intent intent = new Intent(mContext, CaptureActivity.class)
                .putExtra(CameraIntentKey.SAVE_DIR, mSaveDir)
                .putExtra(CameraIntentKey.SAVE_NAME,TextUtils.isEmpty(mSaveName)?UUID.randomUUID().toString().replace("-","")+".jpg":mSaveName);
        return intent;
    }

    public void start(int requestCode) {
        mContext.startActivityForResult(getIntent(), requestCode);
    }
}