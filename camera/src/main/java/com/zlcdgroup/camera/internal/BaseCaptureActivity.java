package com.zlcdgroup.camera.internal;

import android.Manifest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;


import com.zlcdgroup.camera.R;
import com.zlcdgroup.camera.util.CameraUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class BaseCaptureActivity extends Activity {

    private int mCameraPosition = CAMERA_POSITION_UNKNOWN;

    private  String    saveName;

    private  String    saveDir;

    private boolean mRequestingPermission;

    @IntDef({CAMERA_POSITION_UNKNOWN, CAMERA_POSITION_BACK, CAMERA_POSITION_FRONT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CameraPosition {
    }

    public static final int PERMISSION_RC = 69;
    public static final int CAMERA_POSITION_UNKNOWN = 0;
    public static final int CAMERA_POSITION_FRONT = 1;
    public static final int CAMERA_POSITION_BACK = 2;

    @Override
    protected final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CameraIntentKey.SAVE_NAME, saveName);
        outState.putString(CameraIntentKey.SAVE_DIR,saveDir);

    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CameraUtil.hasCamera(this)) {
            new AlertDialog.Builder(this).setTitle("提示").setMessage("访问您相机的权限是必要的，相机现在将关闭。")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            return;
        }
        setContentView(R.layout.camera_activity);
        if(null != getActionBar()){
            getActionBar().hide();
        }


        if (null == savedInstanceState) {
            checkPermissions();
            saveDir = getIntent().getStringExtra(CameraIntentKey.SAVE_DIR);
            saveName = getIntent().getStringExtra(CameraIntentKey.SAVE_NAME);
        } else {
            saveName = savedInstanceState.getString(CameraIntentKey.SAVE_NAME);
            saveDir = savedInstanceState.getString(CameraIntentKey.SAVE_DIR);
        }
        getFragmentManager().beginTransaction()
            .replace(R.id.camera_content, createFragment())
            .commit();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            return;
        }
        final boolean videoGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (videoGranted) {

        } else {
            final boolean audioGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            String[] perms;
            if (audioGranted) perms = new String[]{Manifest.permission.CAMERA};
            else perms = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
            ActivityCompat.requestPermissions(this, perms, PERMISSION_RC);
            mRequestingPermission = true;
        }
    }

    @Override
    protected final void onPause() {
        super.onPause();

    }

    @Override
    public  void onBackPressed() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.camera_content);
        if (frag != null) {
            if (frag instanceof BaseCameraFragment) {
                ((BaseCameraFragment) frag).closeCamera();
            }
        }
        finish();
    }

    @NonNull
    public abstract Fragment getFragment();

    public final Fragment createFragment() {
        Fragment frag = getFragment();
        frag.setArguments(getIntent().getExtras());
        return frag;
    }


    @Override
    protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_RC){

        }

    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestingPermission = false;
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            new AlertDialog.Builder(this).setTitle("提示").setMessage("访问您相机的权限是必要的，相机现在将关闭。")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
        } else {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}