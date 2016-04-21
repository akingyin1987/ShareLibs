package com.zlcdgroup.camera.internal;

import android.annotation.TargetApi;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.RelativeLayout;
import com.zlcdgroup.camera.FrontLightMode;
import com.zlcdgroup.camera.VolumeMode;


/**
 * camera 5.0以下使用
 */
@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CameraFragment extends BaseCameraFragment implements BaseCaptureInterface {

    CameraPreview mPreviewView;
    RelativeLayout mPreviewFrame;

    private Camera.Size mVideoSize;
    private Camera mCamera;
    private Point mWindowSize;
    private int mDisplayOrientation;
    private boolean mIsAutoFocusing;

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }



    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setmInterface(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPreviewFrame = null;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        if (mCamera != null) mCamera.lock();
        super.onPause();
    }



    @Override
    public void openCamera(SurfaceTexture  surfaceTexture) {

    }




    @Override
    public void closeCamera() {
        try {
            if (mCamera != null) {
                try {
                    mCamera.lock();
                } catch (Throwable ignored) {
                }
                mCamera.release();
                mCamera = null;
            }
        } catch (IllegalStateException e) {
            throwError(new Exception("Illegal state while trying to close camera.", e));
        }
    }

    @Override
    public void settingCamera(View view) {

    }

    @Override
    public void onFrontLight(FrontLightMode frontLightMode) {

    }

    @Override
    public void onVolume(VolumeMode volumeMode) {

    }

    @Override
    public void onAutoFocus() {

    }

    @Override
    public void onCancelFocus() {

    }

    @Override
    public void onTakePic(String dir, String fileName, boolean sound) {

    }
}