package com.zlcdgroup.camera.internal;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.zlcdgroup.camera.CameraManager;
import com.zlcdgroup.camera.CameraPreferences;
import com.zlcdgroup.camera.FrontLightMode;
import com.zlcdgroup.camera.MathUtils;
import com.zlcdgroup.camera.VolumeMode;

import com.zlcdgroup.camera.util.CameraDialogUtil;
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
                        e.printStackTrace();
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
                case 0:
                    ZuoBiao zuoBiao = (ZuoBiao) msg.obj;
                    String text = "角度："+zuoBiao.degree+"图片大小： " + zuoBiao.getWidth() + "*" + zuoBiao.getHeight() + "\n坐标(" + zuoBiao.getLeft() + " , " + zuoBiao.getTop() + ") ; 矩形宽=" + zuoBiao.getxDes() + "; 矩形高" + zuoBiao.getyDes();
                    tag_info.setText(text);
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
        DisplayMetrics  displayMetrics =  getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        mCameraManager = new CameraManager(getActivity(), resultHandler);
        mCameraManager.setFrontLightMode(frontLightMode);
        mCameraManager.setErrorCallback(this);
        left = (float) (0.3 * screenWidth / 2);
        top = (float) (screenHeight / 2 - 50 - 0.29 * screenWidth);
        right = (float) (screenWidth - left);

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
            SharedPreferences sharedPreferences = getShare();
            int  guide = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE,2);
            int  top = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_TOP,0);
            int  left = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_LEFT,0);
            initGuide(guide,top,left);
            setReference();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public   void   setReference(){
        int width = 0, height = 0,mwidth=mCameraManager.getCameraResolution().x,mheight=mCameraManager.getCameraResolution().y;
        left = (float) (0.3 * screenWidth / 2);
        top = (float) (screenHeight / 2 - 50 - 0.29 * screenWidth);
        right = (float) (screenWidth - left);
        float top1, left1, xDes, right1, bottom1, yDes;
        float filew = 540,fileh = 960;
        if(Math.abs(mwidth*1.0/mheight-9.0/16) <=0.01){
            filew = 540;
            fileh = 960;
        }else if(Math.abs(mheight*1.0/mwidth-9.0/16) <=0.01){
            filew = 540;
            fileh = 960;
        }
        if (mwidth < mheight) {
            width = mwidth;
            height = mheight;
        } else {
            width = mheight;
            height = mwidth;
        }
        float scaleHeight = ((float) height) / fileh;
        // Log.i("LHT", "scaleHeight " + scaleHeight);
        float nWidth = width / scaleHeight;
        // Log.i("LHT", "nWidth " + nWidth);
        // Log.i("LHT", "screenWidth " + screenWidth);
        float xRadio = nWidth / screenWidth;
        // Log.i("LHT", "xRadio " + xRadio);
        float yRadio = fileh / screenHeight;
        top1 = top * yRadio;
        left1 = left * xRadio;
        right1 = right * xRadio;
        xDes = right1 - left1;
        yDes = (float) (xDes / 4.8);
        bottom1 = (top1 + yDes) / yRadio;
        //Log.i("LHT", "top " + top1 + "left " + left1 + " bottom1 " + bottom1 + " xDestance " + xDes + "  yDestance " + yDes);
        referenceline.setRect(top, left, right, bottom1);
        ZuoBiao zuoBiao = new ZuoBiao(nWidth, fileh, Math.floor(top1),Math.floor(left1),  Math.ceil(xDes), Math.ceil(yDes));
        handler.obtainMessage(0, zuoBiao).sendToTarget();
    }

    public  void  setReference(int  degree){
        if(null == mCameraManager || null  == mCameraManager.getCameraResolution()){
            return;
        }
        int width = 0, height = 0,mwidth=mCameraManager.getCameraResolution().x,mheight=mCameraManager.getCameraResolution().y;

        float top1=0, left1=0, xDes=0, right1=0, bottom1=0, yDes=0;
        float filew = 540,fileh = 960;
        if(Math.abs(mwidth*1.0/mheight-9.0/16) <=0.01){
            filew = 540;
            fileh = 960;
        }else if(Math.abs(mheight*1.0/mwidth-9.0/16) <=0.01){
            filew = 540;
            fileh = 960;
        }
        if (mwidth < mheight) {
            width = mwidth;
            height = mheight;
        } else {
            width = mheight;
            height = mwidth;
        }
        float scaleHeight = ((float) height) / fileh;
        // Log.i("LHT", "scaleHeight " + scaleHeight);
        float nWidth = width / scaleHeight;
        // Log.i("LHT", "nWidth " + nWidth);
        // Log.i("LHT", "screenWidth " + screenWidth);
        float xRadio = nWidth / screenWidth;
        // Log.i("LHT", "xRadio " + xRadio);
        float yRadio = fileh / screenHeight;
        if(degree == 90){
            left = (float) (0.3 * screenWidth / 2);
            top = (float) (screenHeight / 2 - 50 - 0.29 * screenWidth);
            right = (float) (screenWidth - left);
            top1 = top * yRadio;
            left1 = left * xRadio;
            right1 = right * xRadio;
            xDes = right1 - left1;
            yDes = (float) (xDes / 4.8);
            bottom1 = (top1 + yDes) / yRadio;
            System.out.println("bottom1="+bottom1);
        }else if(degree == 0){
            left = (float) (0.3 * screenWidth / 2);
            right = (float) (screenWidth - left);
            left1 = left * xRadio;
            right1 = right * xRadio;
            xDes = Math.abs(right1 - left1);
            System.out.println("xdes1="+xDes/4.8);
            right = (float) ( screenWidth*2.0 / 3.0);
            top = (float) (screenHeight / 2.0 - xDes );
            left = (float) (right-xDes/4.8);

            top1 = top * yRadio;
            left1 = left * xRadio;
            right1 = right * xRadio;
            xDes = Math.abs(right1 - left1);
            System.out.println("xdes2="+xDes);
            yDes = (float) (xDes * 4.8);
            bottom1 = height*yRadio - top1-yDes;
            System.out.println("bottom1="+bottom1);
        }else if(degree == 180){
            left = (float) (0.3 * screenWidth / 2);
            right = (float) (screenWidth - left);
            left1 = left * xRadio;
            right1 = right * xRadio;
            xDes = right1 - left1;


            left = (float) ( screenWidth / 2.0);
            top = (float) (screenHeight / 2.0 - xDes );
            right = (float) (screenWidth - left-xDes/4.8);
            top1 = top * yRadio;
            left1 = left * xRadio;
            right1 = right * xRadio;
            xDes =Math.abs(right1 - left1);
            yDes = (float) (xDes * 4.8);
            bottom1 = (top1 + yDes) / yRadio;
        }else if(degree == 270){
            left = (float) (0.3 * screenWidth / 2);
            top = (float) (screenHeight / 3.0*2.0);
            right = (float) (screenWidth - left);
            top1 = top * yRadio;
            left1 = left * xRadio;
            right1 = right * xRadio;
            xDes = right1 - left1;
            yDes = (float) (xDes / 4.8);
            bottom1 = (top1 + yDes) / yRadio;
        }


        //Log.i("LHT", "top " + top1 + "left " + left1 + " bottom1 " + bottom1 + " xDestance " + xDes + "  yDestance " + yDes);
        referenceline.setRect(top, left, right, bottom1);
        ZuoBiao zuoBiao = new ZuoBiao(nWidth, fileh, Math.floor(top1),Math.floor(left1),  Math.ceil(xDes), Math.ceil(yDes));
        zuoBiao.setDegree(degree);
        resultHandler.obtainMessage(0, zuoBiao).sendToTarget();
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
        CameraDialogUtil.showCameraSetting(getShare(), getActivity(), new CameraApiCallback<String>() {
            @Override public void call(String s) {
                initCameraConfig();
            }
        });
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

         mCameraManager.tackPic(0);
    }

    @Override
    public void onCameraAngle(int angle) {
       if(angle != mCameraManager.getResult()){

           setReference(angle);
       }
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

    @Override
    public void startCamera() {
        hasTakePicture = false;
        reStartShowView();
        mCameraManager.startPreview();
    }


    class ZuoBiao {
        private float width, height;
        private double top, left, xDes, yDes;
        private  int  degree;

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree;
        }

        public ZuoBiao(float width, float height, double top, double left, double xDes, double yDes) {
            this.top = top;
            this.left = left;
            this.xDes = xDes;
            this.yDes = yDes;
            this.width = width;
            this.height = height;
        }

        public double getLeft() {
            return left;
        }

        public double getTop() {
            return top;
        }

        public double getxDes() {
            return xDes;
        }

        public double getyDes() {
            return yDes;
        }

        public float getHeight() {
            return height;
        }

        public float getWidth() {
            return width;
        }
    }
}