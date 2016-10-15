package com.zlcdgroup.camera.internal;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import android.widget.Toast;
import com.zlcdgroup.camera.CameraManager;
import com.zlcdgroup.camera.CameraPreferences;
import com.zlcdgroup.camera.FrontLightMode;
import com.zlcdgroup.camera.MaterialCamera;
import com.zlcdgroup.camera.R;
import com.zlcdgroup.camera.VolumeMode;
import com.zlcdgroup.camera.util.CameraUtil;
import com.zlcdgroup.camera.widget.FocusView;
import com.zlcdgroup.camera.widget.ReferenceLine;
import com.zlcdgroup.camera.widget.TouchImageView;

import java.io.File;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 *
 */
abstract class BaseCameraFragment extends Fragment implements CameraUriInterface,TextureView.SurfaceTextureListener, View.OnClickListener,SensorEventListener {

    protected ImageView  flash_model,volume_model;//闪光灯与拍照声音

    protected  ImageView  cancel_camera,tack_camera,config_camera;//拍照及确定取消

    protected  AutoFitTextureView   textureView;//预览界面

    protected TouchImageView   viewfinder_view;

    protected SeekBar    zoomBar;

    protected  ImageView  camera_screen,camera_guideline,camera_setting;

    protected RelativeLayout  camera_layout,camera_toploayou;

    protected FocusView    focusView;

    protected   View  camera_moveleft,camera_movetop;

    public ReferenceLine  referenceline;

    protected TextView mRecordDuration;

    private boolean mIsRecording;
    protected String mOutputUri;
    private BaseCaptureInterface mInterface;
    public   TextView  tag_info;

    public float left, right, top;
    public float screenHeight, screenWidth;

    public BaseCaptureInterface getmInterface() {
        return mInterface;
    }

    public void setmInterface(BaseCaptureInterface mInterface) {
        this.mInterface = mInterface;
    }

    protected MediaRecorder mMediaRecorder;

    protected static void LOG(Object context, String message) {
        Log.d(context instanceof Class<?> ? ((Class<?>) context).getSimpleName() :
                context.getClass().getSimpleName(), message);
    }



    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.camera__fragment, container, false);
    }

    boolean  isSend = false;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        focusView = (FocusView)view.findViewById(R.id.fv_view);
        tag_info = (TextView)view.findViewById(R.id.tag_info);
        referenceline = (ReferenceLine)view.findViewById(R.id.referenceline);
        flash_model = (ImageView)view.findViewById(R.id.flash_model);
        volume_model = (ImageView)view.findViewById(R.id.volume_model);
        cancel_camera = (ImageView)view.findViewById(R.id.cancel_camera);
        tack_camera = (ImageView)view.findViewById(R.id.tack_camera);
        config_camera = (ImageView)view.findViewById(R.id.config_camera);
        textureView = (AutoFitTextureView)view.findViewById(R.id.preview_view);
        camera_layout = (RelativeLayout)view.findViewById(R.id.camera_layout);
        camera_toploayou = (RelativeLayout)view.findViewById(R.id.camera_toploayou);
        camera_moveleft = view.findViewById(R.id.camera_moveleft);
        camera_movetop  = view.findViewById(R.id.camera_movetop);
        camera_screen = (ImageView)view.findViewById(R.id.camera_screen);
        camera_setting = (ImageView)view.findViewById(R.id.camera_setting);
        camera_guideline = (ImageView)view.findViewById(R.id.camera_guideline);
        zoomBar = (SeekBar)view.findViewById(R.id.bar);
        viewfinder_view = (TouchImageView)view.findViewById(R.id.viewfinder_view);
        camera_screen.setOnClickListener(this);
        camera_guideline.setOnClickListener(this);
        camera_setting.setOnClickListener(this);
        flash_model.setOnClickListener(this);
        volume_model.setOnClickListener(this);
        cancel_camera.setOnClickListener(this);
        tack_camera.setOnClickListener(this);
        config_camera.setOnClickListener(this);
        textureView.setOnClickListener(this);
        textureView.setSurfaceTextureListener(this);
        zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(hasTakePicture()){
                    return  ;
                }

                if (!isSend) {
                    handler.sendEmptyMessageDelayed(CameraManager.CHANGE_ZOOM, 3000);
                    isSend = true;
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                    mInterface.onZoom(seekBar.getProgress());
            }
        });
        initCameraConfig();
    }

    protected FrontLightMode   frontLightMode;
    protected VolumeMode       volumeMode;
    protected  SensorManager  mManager;
    protected  Sensor   mSensor;
    protected   void   initCameraConfig(){
        mManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SharedPreferences     sharedPreferences = getShare();
        int   frontlight = sharedPreferences.getInt(CameraPreferences.KEY_FRONT_LIGHT_MODE, 0);
        System.out.println("frontlight="+frontlight);
        initFrontlight(frontlight);
        int  volume = sharedPreferences.getInt(CameraPreferences.KEY_VOLUME,0);
        initVoluem(volume);
        screen = sharedPreferences.getInt(CameraPreferences.KEY_SCREEN,0);
        initScreen(screen);
        int  guide,top,left;
        guide = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE,0);
        top = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_TOP,0);
        left = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_LEFT,0);
        initGuide(guide,top,left);
    }
    public   void   initVoluem(int  volume){
        if(volume == 0){
            volumeMode = VolumeMode.ON;
            volume_model.setImageResource(R.drawable.ic_device_access_volume_on);
        }else{
            volumeMode = VolumeMode.OFF;
            volume_model.setImageResource(R.drawable.ic_device_access_volume_muted);
        }
    }
    public   void   initFrontlight(int frontlight){

        if(frontlight == 0){
            frontLightMode = FrontLightMode.AUTO;
            flash_model.setImageResource(R.drawable.ic_device_access_flash_automatic);
        }else if(frontlight == 1){
            flash_model.setImageResource(R.drawable.ic_device_access_flash_on);
            frontLightMode = FrontLightMode.ON;
        }else{
            flash_model.setImageResource(R.drawable.ic_device_access_flash_off);
            frontLightMode = FrontLightMode.OFF;
        }
    }

    private   int    screen;
    public   void   initScreen(int  screen){

        if(screen == 0){
            camera_screen.setVisibility(View.GONE);
        }else if(screen == 1){
            camera_screen.setVisibility(View.VISIBLE);
            camera_screen.setImageResource(R.drawable.screen_rotation);
        }else if(screen == 2){
            camera_screen.setVisibility(View.VISIBLE);
            camera_screen.setImageResource(R.drawable.screen_lock_landscape);
        }
    }

    public   void    initGuide(int  guide,int  top,int  left){
          System.out.println(guide+":"+top+":"+left);
        if(guide == 0){
            camera_guideline.setVisibility(View.GONE);
            camera_layout.setVisibility(View.GONE);
            camera_movetop.setVisibility(View.GONE);

        }else if(guide == 1){
            camera_guideline.setVisibility(View.VISIBLE);
            camera_layout.setVisibility(View.VISIBLE);
            camera_moveleft.setVisibility(View.VISIBLE);
            camera_movetop.setVisibility(View.VISIBLE);
            camera_toploayou.setVisibility(View.VISIBLE);
            camera_guideline.setImageResource(R.drawable.camera_guideline_red);
            try{
                int  cameraheight = Math.max(textureView.getWidth(), textureView.getHeight()) * left/100;
                System.out.println("cameraheight="+cameraheight);
                if(cameraheight>0){

                    RelativeLayout.LayoutParams topLayoutParams = new RelativeLayout.LayoutParams(camera_moveleft.getLayoutParams());
                    topLayoutParams.topMargin = cameraheight;
                    camera_moveleft.setLayoutParams(topLayoutParams);
                }else{
                    camera_moveleft.setVisibility(View.GONE);
                }
                int   camerawidth = Math.min(textureView.getWidth(), textureView.getHeight()) * (100-top)/100;
                System.out.println("camerawidt="+camerawidth);
                if(camerawidth>0){
                    RelativeLayout.LayoutParams topLayoutParams = new RelativeLayout.LayoutParams(camera_movetop.getLayoutParams());
                    topLayoutParams.leftMargin = camerawidth;
                    camera_movetop.setLayoutParams(topLayoutParams);
                }else{
                    camera_movetop.setVisibility(View.GONE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(guide == 2){
            //界面隐长
            camera_guideline.setVisibility(View.VISIBLE);
            camera_guideline.setImageResource(R.drawable.camera_guideline);
            camera_layout.setVisibility(View.GONE);
            camera_movetop.setVisibility(View.GONE);
        }
    }

    public   SharedPreferences   getShare(){
        return  getActivity().getSharedPreferences("camera_config", Activity.MODE_APPEND);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRecordDuration = null;
    }



    @Override
    public void onResume() {
        super.onResume();
        if (null != mManager) {
            mManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_UI);
        }


    }

    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @NonNull
    protected final File getOutputMediaFile() {
        return CameraUtil.makeTempFile(getActivity(), getArguments().getString(CameraIntentKey.SAVE_DIR), getArguentsByKey(CameraIntentKey.SAVE_NAME), ".jpg");
    }

    public   String    getArguentsByKey(String  key){
       return getArguments().getString(key);
    }

    //初始化相机
    public abstract void openCamera(SurfaceTexture surface);

    public abstract void closeCamera();

    public abstract  void  startCamera();

    public abstract void settingCamera(View  view);

    public abstract void  onCameraAngle(int  angle);

    public abstract  boolean  hasTakePicture();//是否在拍照中







    @Override
    public void onPause() {
        super.onPause();
        if (null != mManager) {
            mManager.unregisterListener(this);
        }

    }

    @Override
    public final void onDetach() {
        super.onDetach();
        mInterface = null;
    }


    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("output_uri", mOutputUri);
    }

    @Override
    public final String getOutputUri() {
        return mOutputUri;
    }

    protected final void throwError(Exception e) {
        Activity act = getActivity();
        if (act != null) {
            act.setResult(RESULT_CANCELED, new Intent().putExtra(MaterialCamera.ERROR_EXTRA, e));
            act.finish();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.flash_model) {
            //闪光灯
            SharedPreferences  sharedPreferences = getShare();
            int   frontlight  = sharedPreferences.getInt(CameraPreferences.KEY_FRONT_LIGHT_MODE,0);
            frontlight++;
            if(frontlight==3){
                frontlight=0;
            }
            initFrontlight(frontlight);
            sharedPreferences.edit().putInt(CameraPreferences.KEY_FRONT_LIGHT_MODE,frontlight).apply();
            mInterface.onFrontLight(frontLightMode);
        }else if(view.getId() == R.id.volume_model){
            //拍照声音
            SharedPreferences  sharedPreferences = getShare();
            int   volume  = sharedPreferences.getInt(CameraPreferences.KEY_VOLUME,0);
            volume = volume==0?1:0;
            sharedPreferences.edit().putInt(CameraPreferences.KEY_VOLUME,volume).apply();
            initVoluem(volume);
            mInterface.onVolume(volumeMode);
        }else if(view.getId() == R.id.cancel_camera){
            //取消拍照
            try{
                File  file = getOutputMediaFile();
                if(file.exists())
                file.delete();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(hasTakePicture()){
                viewfinder_view.setImageURI(null);
                startCamera();
            }else{
                Activity act = getActivity();
                if (act != null) {
                    act.setResult(RESULT_CANCELED, new Intent().putExtra(MaterialCamera.ERROR_EXTRA, "手动取消"));
                    act.finish();
                }
            }

        }else if(view.getId() == R.id.config_camera){
            //拍照确认
            try {
                File  file = getOutputMediaFile();

                if(file.exists()){
                    Activity act = getActivity();
                    if (act != null) {
                        act.setResult(RESULT_OK, new Intent().putExtra(MaterialCamera.RESULT_FILE,file.getAbsolutePath()));
                        act.finish();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(view.getId() == R.id.camera_setting){
            settingCamera(view);
        }else if(view.getId() == R.id.camera_guideline){
            SharedPreferences  sharedPreferences = getShare();
            int  guide = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE,2);

            guide = guide==2?1:2;
            int  top = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_TOP,0);
            int  left = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_LEFT,0);

            initGuide(guide,top,left);

            sharedPreferences.edit().putInt(CameraPreferences.KEY_GUIDE,guide).apply();
        }else if(view.getId() == R.id.camera_screen){
            SharedPreferences  sharedPreferences = getShare();
            int  screen  = sharedPreferences.getInt(CameraPreferences.KEY_SCREEN,0);
            screen = screen==0?1:0;
            camera_screen.setImageResource(screen==0?R.drawable.screen_rotation:R.drawable.screen_lock_landscape);
            sharedPreferences.edit().putInt(CameraPreferences.KEY_SCREEN,screen).apply();
        }else if(view.getId() == R.id.preview_view){
            if(null != mInterface){
                mInterface.onAutoFocus();
            }
        }else if(view.getId() == R.id.tack_camera){
            if(null != mInterface){

                mInterface.onTakePic(getArguentsByKey(CameraIntentKey.SAVE_DIR),getArguentsByKey(CameraIntentKey.SAVE_NAME),volumeMode==VolumeMode.ON,screen);
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera(surface);

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        //int  guide,top,left;
        //SharedPreferences  sharedPreferences = getShare();
        //guide = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE,0);
        //top = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_TOP,0);
        //left = sharedPreferences.getInt(CameraPreferences.KEY_GUIDE_LEFT,0);
        //initGuide(guide,top,left);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        closeCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    private float oldX = 0;
    private float oldY = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        float x = event.values[SensorManager.DATA_X];

        float y = event.values[SensorManager.DATA_Y];
        // Log.i(TAG, "onSensorChanged = " + x + "--------" + y);
        if ((Math.abs(y) > Math.abs(x)) && y > 0) {
            if ((Math.abs(oldX) < Math.abs(oldY)) && oldY > 0) {
            } else {
                oldX = x;
                oldY = y;
                onCameraAngle(90);
                tack_camera.setImageResource(R.drawable.btn_style_takepicture_2);

            }

        } else if ((Math.abs(x) - Math.abs(y) > 1) && x > 0) {
            if ((Math.abs(oldX) > Math.abs(oldY)) && oldX > 0) {
            } else {
                oldX = x;
                oldY = y;
                onCameraAngle(0);
                tack_camera.setImageResource(R.drawable.btn_style_takepicture_1);
            }
        } else if ((Math.abs(x) - Math.abs(y) > 1) && x < 0) {
            if ((Math.abs(oldX) > Math.abs(oldY)) && oldX < 0) {
            } else {
                oldX = x;
                oldY = y;
                onCameraAngle(180);
                tack_camera.setImageResource(R.drawable.btn_style_takepicture_3);
            }
        } else if ((Math.abs(x) < Math.abs(y)) && y < 0) {
            if ((Math.abs(oldX) < Math.abs(oldY)) && oldY < 0) {
            } else {
                oldX = x;
                oldY = y;
                onCameraAngle(270);
                tack_camera.setImageResource(R.drawable.btn_style_takepicture_4);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

      @SuppressLint("HandlerLeak")
      Handler   handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 隐藏zoombar
                case CameraManager.CHANGE_ZOOM:
                    zoomBar.setVisibility(View.GONE);
                    isSend = false;
                    break;

                default:
                    break;
            }
        }
    };

    public  boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(hasTakePicture()){
                    return  false;
                }

                zoomBar.setVisibility(View.VISIBLE);
                zoomBar.setProgress(zoomBar.getProgress() - 10);
                handler.sendEmptyMessageDelayed(CameraManager.CHANGE_ZOOM, 3000);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(hasTakePicture()){
                    return  false;
                }

                zoomBar.setVisibility(View.VISIBLE);
                zoomBar.setProgress(zoomBar.getProgress() + 10);
                handler.sendEmptyMessageDelayed(CameraManager.CHANGE_ZOOM, 3000);
                break;
            case KeyEvent.KEYCODE_MENU:
                return false;
            default:
                break;
        }
        return true;
    }


    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    public   void   tackingPicShowView(){
        tack_camera.setVisibility(View.INVISIBLE);
        config_camera.setVisibility(View.INVISIBLE);
        cancel_camera.setVisibility(View.INVISIBLE);
    }

    public   void   tackSucShowView(){
        tack_camera.setVisibility(View.INVISIBLE);
        config_camera.setVisibility(View.VISIBLE);
        cancel_camera.setVisibility(View.VISIBLE);
    }

    public   void   reStartShowView(){
        tack_camera.setVisibility(View.VISIBLE);
        config_camera.setVisibility(View.INVISIBLE);
        cancel_camera.setVisibility(View.INVISIBLE);
        viewfinder_view.setVisibility(View.GONE);
    }


    public void onBackPressed() {
       try{
           File  file = getOutputMediaFile();
           if(file.exists())
           file.delete();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

}