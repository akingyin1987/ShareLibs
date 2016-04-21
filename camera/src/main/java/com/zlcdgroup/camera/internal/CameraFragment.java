package com.zlcdgroup.camera.internal;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.zlcdgroup.camera.CameraManager;
import com.zlcdgroup.camera.CameraPreferences;
import com.zlcdgroup.camera.FrontLightMode;
import com.zlcdgroup.camera.MathUtils;
import com.zlcdgroup.camera.VolumeMode;

import java.io.File;
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

    @SuppressLint("HandlerLeak")
    Handler   resultHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CameraPreferences.TACKPIC_RESULT_DATA_OK:
                    tackSucShowView();
                    hasTakePicture = true;
                    mCameraManager.stopPreview();
                    viewfinder_view.setVisibility(View.GONE);
                    break;
                case CameraPreferences.TACKPIC_RESULT_DATA_NO:
                    mCameraManager.startCameraPreview();
                    viewfinder_view.setVisibility(View.GONE);
                    showMessage("转换图片失败!");
                    hasTakePicture = false;
                    reStartShowView();

                    break;
                case CameraPreferences.TACKPIC_RESULT_VIEWBASEIMG_OK:
                    viewfinder_view.setVisibility(View.VISIBLE);
                    try {
                        File file = new File(msg.obj.toString());
                        if(file.exists()){
                            viewfinder_view.setImageURI(Uri.parse(msg.obj.toString()));
                        }
                    }catch (Exception e){

                    }

                    break;
                case CameraPreferences.TACKPIC_RESULT_VIEWBASEIMG_ERROR:
                    mCameraManager.startCameraPreview();

                    showMessage("转换图片失败!");
                    hasTakePicture = false;
                    reStartShowView();
                    break;
                case CameraPreferences.TACKPIC_RESULT_VIEWIMG_OK:
                    hasTakePicture = true;
                    tackSucShowView();

                    break;
                case CameraPreferences.TACKPIC_RESULT_VIEWIMG_ERROR:
                    mCameraManager.startCameraPreview();
                    viewfinder_view.setVisibility(View.GONE);
                    hasTakePicture = false;
                    reStartShowView();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setmInterface(this);
        mCameraManager = new CameraManager(getActivity(), resultHandler);
        mCameraManager.setFrontLightMode(frontLightMode);
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

            mCameraManager.setFrontLightMode(frontLightMode,null);
            mCameraManager.startCameraPreview();
            Point screan = mCameraManager.getScreenResolution();

            Point   camera = mCameraManager.getCameraResolution();
            System.out.println("bese=="+camera.x+":"+camera.y);
            Point   best = MathUtils.findBestViewSize(screan, camera);
            if(null != best){
                ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
                ViewGroup.LayoutParams viewfinderViewParams = viewfinder_view.getLayoutParams();
                if(best.x == 0 && layoutParams.height != best.y){
                    layoutParams.height = best.y;
                    textureView.setLayoutParams(layoutParams);
                    viewfinderViewParams.height = layoutParams.height;
                    viewfinder_view.setLayoutParams(viewfinderViewParams);

                }else if(best.y == 0 && layoutParams.width != best.x){
                    layoutParams.width = best.x;
                    textureView.setLayoutParams(layoutParams);
                    viewfinderViewParams.width = layoutParams.width;
                    viewfinder_view.setLayoutParams(viewfinderViewParams);

                }
            }
           mCameraManager.setVolueMode(volumeMode);
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
        mCameraManager.setFrontLightMode(frontLightMode,null);
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
    public void onTakePic(String dir, String fileName, boolean sound,int land) {
         mCameraManager.setLandscape(land);
         mCameraManager.setImageName(fileName);
         mCameraManager.setPath(dir);
        System.out.println(dir+":"+fileName);
         mCameraManager.tackPic(0);
    }

    @Override
    public void onCameraAngle(int angle) {
        mCameraManager.setResult(angle);
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
    public void onZoom(int zoom) {
        mCameraManager.setZoom(zoom);
    }

    @Override
    public boolean hasTakePicture() {
        return hasTakePicture;
    }
}