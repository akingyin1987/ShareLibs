package com.zlcdgroup.camera;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.zlcdgroup.camera.internal.BaseCaptureActivity;
import com.zlcdgroup.camera.internal.CameraFragment;


/**
 *
 */
public class CaptureActivity extends BaseCaptureActivity {

    private  CameraFragment   cameraFragment;

    @Override
    @NonNull
    public Fragment getFragment() {

        return cameraFragment=CameraFragment.newInstance();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        cameraFragment.onKeyDown(keyCode,event);
        return super.onKeyDown(keyCode, event);

    }


    @Override
    public void onBackPressed() {
        cameraFragment.onBackPressed();
        super.onBackPressed();

    }

}