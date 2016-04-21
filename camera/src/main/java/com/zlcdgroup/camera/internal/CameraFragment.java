package com.zlcdgroup.camera.internal;
import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.zlcdgroup.camera.CameraManager;
import com.zlcdgroup.camera.FrontLightMode;
import com.zlcdgroup.camera.VolumeMode;
import java.io.IOException;

/**
 * camera 5.0以下使用
 */
@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CameraFragment extends BaseCameraFragment implements BaseCaptureInterface,Camera.ErrorCallback {

    public CameraManager mCameraManager;



    public boolean isSend = true;// 是否处于变焦中
    public boolean hasTakePicture = false; // 是否已拍完照片

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }


    Handler   resultHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setmInterface(this);
        mCameraManager = new CameraManager(getActivity(), resultHandler);
        mCameraManager.setErrorCallback(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();
    }



    @Override
    public void openCamera(SurfaceTexture  surfaceTexture) {

        try {
            mCameraManager.openCamera(surfaceTexture);
            mCameraManager.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void closeCamera() {
        try {
             mCameraManager.stopPreview();
             mCameraManager.closeDriver();
        } catch (IllegalStateException e) {
           e.printStackTrace();
        }
    }

    @Override
    public void settingCamera(View view) {

    }

    @Override
    public void onFrontLight(FrontLightMode frontLightMode) {
        mCameraManager.setFrontLightMode(frontLightMode);
    }

    @Override
    public void onVolume(VolumeMode volumeMode) {
       mCameraManager.setVolueMode(volumeMode);
    }

    @Override
    public void onAutoFocus() {
          mCameraManager.startFocus();
    }

    @Override
    public void onCancelFocus() {
          mCameraManager.cancelAutoFocus();
    }

    @Override
    public void onTakePic(String dir, String fileName, boolean sound) {

    }

    @Override
    public void onCameraAngle(int angle) {

    }

    @Override
    public void onError(int error, Camera camera) {
        if(error == Camera.CAMERA_ERROR_UNKNOWN){
            showMessage("拍照错误！");

        }else if(error == Camera.CAMERA_ERROR_SERVER_DIED){
            showMessage("相机服务出错了！");

        }
        getActivity().finish();
    }

    @Override
    public boolean hasTakePicture() {
        return hasTakePicture;
    }
}