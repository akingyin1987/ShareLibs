package com.zlcdgroup.camera;

import android.app.Fragment;
import android.support.annotation.NonNull;
import com.zlcdgroup.camera.internal.BaseCaptureActivity;
import com.zlcdgroup.camera.internal.CameraFragment;


/**
 *
 */
public class CaptureActivity extends BaseCaptureActivity {

    @Override
    @NonNull
    public Fragment getFragment() {

        return CameraFragment.newInstance();
    }
}