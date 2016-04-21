package com.zlcdgroup.camera.internal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zlcdgroup.camera.CameraPreferences;
import com.zlcdgroup.camera.FrontLightMode;
import com.zlcdgroup.camera.MaterialCamera;
import com.zlcdgroup.camera.R;
import com.zlcdgroup.camera.VolumeMode;
import com.zlcdgroup.camera.util.CameraUtil;
import java.io.File;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 *
 */
abstract class BaseCameraFragment extends Fragment implements CameraUriInterface, View.OnClickListener {

    protected ImageView  flash_model,volume_model;//闪光灯与拍照声音

    protected  ImageView  cancel_camera,tack_camera,config_camera;//拍照及确定取消

    protected  AutoFitTextureView   textureView;//预览界面

    protected RelativeLayout  camera_layout,camera_toploayou;


    protected   View  camera_moveleft,camera_movetop;

    protected TextView mRecordDuration;

    private boolean mIsRecording;
    protected String mOutputUri;
    private BaseCaptureInterface mInterface;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        flash_model = (ImageView)view.findViewById(R.id.flash_model);
        volume_model = (ImageView)view.findViewById(R.id.flash_model);
        cancel_camera = (ImageView)view.findViewById(R.id.cancel_camera);
        tack_camera = (ImageView)view.findViewById(R.id.tack_camera);
        config_camera = (ImageView)view.findViewById(R.id.config_camera);
        textureView = (AutoFitTextureView)view.findViewById(R.id.preview_view);
        camera_layout = (RelativeLayout)view.findViewById(R.id.camera_layout);
        camera_toploayou = (RelativeLayout)view.findViewById(R.id.camera_toploayou);
        camera_moveleft = view.findViewById(R.id.camera_moveleft);
        camera_movetop  = view.findViewById(R.id.camera_movetop);
        flash_model.setOnClickListener(this);
        volume_model.setOnClickListener(this);
        cancel_camera.setOnClickListener(this);
        tack_camera.setOnClickListener(this);
        config_camera.setOnClickListener(this);
        textureView.setOnClickListener(this);
    }

    protected FrontLightMode   frontLightMode;
    protected VolumeMode       volumeMode;
    private  void   initCameraConfig(){
        SharedPreferences     sharedPreferences = getShare();
        int   frontlight = sharedPreferences.getInt(CameraPreferences.KEY_FRONT_LIGHT_MODE, 0);
        initFrontlight(frontlight);
        int  volume = sharedPreferences.getInt(CameraPreferences.KEY_VOLUME,0);
        initVoluem(volume);
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

    public abstract void openCamera();

    public abstract void closeCamera();

    public void cleanup() {
        closeCamera();


    }

    @Override
    public void onPause() {
        super.onPause();
        cleanup();
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
            int   frontlight  = sharedPreferences.getInt(CameraPreferences.KEY_AUTO_FOCUS,0);
            frontlight++;
            if(frontlight==3){
                frontlight=0;
            }
            initFrontlight(frontlight);
            sharedPreferences.edit().putInt(CameraPreferences.KEY_AUTO_FOCUS,frontlight).apply();
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
            Activity act = getActivity();
            if (act != null) {
                act.setResult(RESULT_CANCELED, new Intent().putExtra(MaterialCamera.ERROR_EXTRA, "手动取消"));
                act.finish();
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

            }

        }
    }
}