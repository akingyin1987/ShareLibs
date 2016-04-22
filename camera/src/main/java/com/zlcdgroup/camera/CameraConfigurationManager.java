/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zlcdgroup.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;

import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;

import android.view.WindowManager;


/**
 * A class which deals with reading, parsing, and setting the camera parameters which are used to
 * configure the camera hardware.
 * 摄像头参数的设置类
 */

final class CameraConfigurationManager {

  private static final String TAG = "CameraConfiguration";

  private final Context context;
  private Point screenResolution;
  private Point cameraResolution;

  @SuppressLint("NewApi")
CameraConfigurationManager(Context context) {
    this.context = context;
    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = manager.getDefaultDisplay();

    Point theScreenResolution = new Point();
    if(Build.VERSION.SDK_INT <17){
      display.getSize(theScreenResolution);
    }else{
      display.getRealSize(theScreenResolution);
    }


    screenResolution = theScreenResolution;
  }


  /**
   * Reads, one time, values from the camera that are needed by the app.
   */
  void initFromCameraParameters(Camera camera) {
    Camera.Parameters parameters = camera.getParameters();
  
    Log.i(TAG, "Screen resolution: " + screenResolution);
    cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolution);
    parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
    
  }
  
  private   Point   defaultpoint;
  
  

  public Point getDefaultpoint() {
	return defaultpoint;
}

//获取相机最适合的previewSize
  public   void   initCameraPreviewSizeValue(Camera.Parameters   parameters){
	  if(null != parameters.getPictureSize()){
		  defaultpoint = new  Point(parameters.getPreviewSize().width, parameters.getPreviewSize().height);
	  }
	  System.out.println(screenResolution.x+":"+screenResolution.y);
	  cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolution);

	 // cameraResolution = CameraConfigurationUtils.getCameraResolution(parameters, screenResolution);
	 
  }
  void setDesiredCameraParameters(Camera camera, boolean safeMode) {
    Camera.Parameters parameters = camera.getParameters();

    if (parameters == null) {
      Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
      return;
    }

    Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

    if (safeMode) {
      Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
    }

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    // 初始化闪光灯
    initializeTorch(parameters, prefs, safeMode);

    //设置对焦模式
    CameraConfigurationUtils.setFocus(
        parameters,
        prefs.getBoolean(CameraPreferences.KEY_AUTO_FOCUS, true),
        prefs.getBoolean(CameraPreferences.KEY_DISABLE_CONTINUOUS_FOCUS, true),
        safeMode);
       
    if (!safeMode) {
      if (prefs.getBoolean(CameraPreferences.KEY_INVERT_SCAN, false)) {
        CameraConfigurationUtils.setInvertColor(parameters);
      }

      if (!prefs.getBoolean(CameraPreferences.KEY_DISABLE_BARCODE_SCENE_MODE, true)) {
        CameraConfigurationUtils.setBarcodeSceneMode(parameters);
      }

      if (!prefs.getBoolean(CameraPreferences.KEY_DISABLE_METERING, true)) {
        CameraConfigurationUtils.setVideoStabilization(parameters);
        CameraConfigurationUtils.setFocusArea(parameters);
        CameraConfigurationUtils.setMetering(parameters);
      }

    }

    parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
  
    camera.setParameters(parameters);

    Camera.Parameters afterParameters = camera.getParameters();
    Camera.Size afterSize = afterParameters.getPreviewSize();
    if (afterSize!= null && (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
      Log.w(TAG, "Camera said it supported preview size " + cameraResolution.x + 'x' + cameraResolution.y +
                 ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
      cameraResolution.x = afterSize.width;
      cameraResolution.y = afterSize.height;
    }
  }

  //设备闪光灯
  public     void    setFocusModel(Camera camera, boolean safeMode){
	  Camera.Parameters parameters = camera.getParameters();
	  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	  //设置对焦模式
	    CameraConfigurationUtils.setFocus(
	        parameters,
	        prefs.getBoolean(CameraPreferences.KEY_AUTO_FOCUS, true),
	        prefs.getBoolean(CameraPreferences.KEY_DISABLE_CONTINUOUS_FOCUS, true),
	        safeMode);
	       
	    if (!safeMode) {
	      if (prefs.getBoolean(CameraPreferences.KEY_INVERT_SCAN, false)) {
	        CameraConfigurationUtils.setInvertColor(parameters);
	      }

	      if (!prefs.getBoolean(CameraPreferences.KEY_DISABLE_BARCODE_SCENE_MODE, true)) {
	        CameraConfigurationUtils.setBarcodeSceneMode(parameters);
	      }

	      if (!prefs.getBoolean(CameraPreferences.KEY_DISABLE_METERING, true)) {
	        CameraConfigurationUtils.setVideoStabilization(parameters);
	        CameraConfigurationUtils.setFocusArea(parameters);
	        CameraConfigurationUtils.setMetering(parameters);
	      }

	    }

  }
  
  
  Point getCameraResolution() {
	
    return cameraResolution;
  }

  Point getScreenResolution() {
    return screenResolution;
  }

  //判断闪光灯是否打开
  boolean getTorchState(Camera camera) {
    if (camera != null) {
      Camera.Parameters parameters = camera.getParameters();
      if (parameters != null) {
        String flashMode = camera.getParameters().getFlashMode();
        return flashMode != null &&
            (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) ||
             Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode));
      }
    }
    return false;
  }

  //设置闪光灯（标准模式）
  void setTorch(Camera camera, boolean newSetting) {
    Camera.Parameters parameters = camera.getParameters();
    doSetTorch(parameters, newSetting, false);
    camera.setParameters(parameters);
  }

  //根据配置来设置闪光灯
  void initializeTorch(Camera.Parameters parameters, SharedPreferences prefs, boolean safeMode) {
      
    boolean currentSetting = FrontLightMode.readPref(prefs) == FrontLightMode.ON;
    System.out.println("currentSetting="+currentSetting);
    doSetTorch(parameters, currentSetting, safeMode);
  }

  private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
    CameraConfigurationUtils.setTorch(parameters, newSetting);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    if (!safeMode && !prefs.getBoolean(CameraPreferences.KEY_DISABLE_EXPOSURE, true)) {
      CameraConfigurationUtils.setBestExposure(parameters, newSetting);
    }
  }

}
