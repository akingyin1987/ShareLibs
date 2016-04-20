/*
 * Copyright (C) 2012 ZXing authors
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

import android.hardware.Camera;
import android.util.Log;

/**
 * 该类用于检测手机上摄像头的个数，如果有多个摄像头，则取背面的摄像头
 * @author king
 *
 */

public final class OpenCameraInterface {

  private static final String TAG = OpenCameraInterface.class.getName();

  private OpenCameraInterface() {
  }

  
  /**
   * Opens the requested camera with {@link Camera#open(int)}, if one exists.
   *
   * @param cameraId camera ID of the camera to use. A negative value means "no preference"
   * @return handle to {@link Camera} that was opened
   */
  public static Camera open(int cameraId) {
    
      //手机上camera的数量 =0 则当前手机无摄像头
    int numCameras = Camera.getNumberOfCameras();
    
    if (numCameras == 0) {
      Log.w(TAG, "No cameras!");
      return null;
    }
	  
    boolean explicitRequest = cameraId >= 0;
	  
    if (!explicitRequest) {
      // Select a camera if no explicit camera requested
      int index = 0;
      //遍历当前所有摄像头信息
      while (index < numCameras) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(index, cameraInfo);
        //判断是否是后置摄像头
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
              break;
        }
        index++;
      }
      
      cameraId = index;
    }
	  
    Camera camera;
    if (cameraId < numCameras) {
      Log.i(TAG, "Opening camera #" + cameraId);
      camera = Camera.open(cameraId);
    } else {
      if (explicitRequest) {
        Log.w(TAG, "Requested camera does not exist: " + cameraId);
        camera = null;
      } else {
        Log.i(TAG, "No camera facing back; returning camera #0");
        camera = Camera.open(0);
      }
    }
    
    return camera;
  }
  
  
  /**
   * Opens a rear-facing camera with {@link Camera#open(int)}, if one exists, or opens camera 0.
   *
   * @return handle to {@link Camera} that was opened
   */
  public static Camera open() {
    return open(-1);
  }

  //获取到当前后置摄像头的ID
  public    static   int   CameraId(){
      int numCameras = Camera.getNumberOfCameras();     
      if (numCameras == 0) {
        Log.w(TAG, "No cameras!");
        return -1;
      }    
      int index = 0;    
      while (index < numCameras) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(index, cameraInfo);
       
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
              break;
        }
        index++;
      }
    return index;
  }
}
