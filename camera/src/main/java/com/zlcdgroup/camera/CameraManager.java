/*
 * Copyright (C) 2008 ZXing authors
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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import android.widget.Toast;
import com.zcldgroup.util.BitmapUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding.
 * 
 * @author dswitkin@google.com (Daniel Switkin) 该类封装了相机的所有服务并且是该app中唯一与相机打交道的类
 */

@SuppressWarnings("deprecation")
public final class CameraManager implements AutoFocusListion {

	private static final String TAG = CameraManager.class.getSimpleName();

	private static final int MIN_FRAME_WIDTH = 240;
	private static final int MIN_FRAME_HEIGHT = 240;
	private static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920
	private static final int MAX_FRAME_HEIGHT = 675; // = 5/8 * 1080
	public static final int CHANGE_ZOOM = 3;

	private final Activity context;
	private final CameraConfigurationManager configManager;

	private Camera camera;
	private AutoFocusManager autoFocusManager;
	private Rect framingRect;
	private Rect framingRectInPreview;
	private boolean initialized;
	private boolean previewing;
	private int requestedCameraId = -1;
	private int requestedFramingRectWidth;
	private int requestedFramingRectHeight;
	/**
	 * Preview frames are delivered here, which we pass on to the registered
	 * handler. Make sure to clear the handler so it will only receive one
	 * message.
	 */
	private final PreviewCallback previewCallback;

	private Handler resultHandler;

	// 当前图片的角度
	private int result;

	private String path;

	private String imageName;

	private VolumeMode volueMode = VolumeMode.ON;

	private FrontLightMode frontLightMode;

	private ErrorCallback errorCallback;

	public Camera getCamera() {
		return camera;
	}

	public ErrorCallback getErrorCallback() {
		return errorCallback;
	}

	public void setErrorCallback(ErrorCallback errorCallback) {
		this.errorCallback = errorCallback;
		if (null != camera) {
			camera.setErrorCallback(errorCallback);
		}
	}

	private Point previewsize;

	private Point pictureSize;

	public Point getScreenResolution() {
		return configManager.getScreenResolution();
	}

	public Point getCameraResolution() {
		return pictureSize;
	}

	public Point getPreviewResolution() {
		return previewsize;
	}

	public void setFrontLightMode(FrontLightMode frontLightMode) {
		this.frontLightMode = frontLightMode;
	}

	public FrontLightMode getFrontLightMode() {
		return frontLightMode;
	}

	public VolumeMode getVolueMode() {
		return volueMode;
	}

	public void setVolueMode(VolumeMode volueMode) {
		this.volueMode = volueMode;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	private int landscape;

	public int getLandscape() {
		return landscape;
	}

	public void setLandscape(int landscape) {
		this.landscape = landscape;
	}

	public CameraManager(Activity context, Handler resultHandler) {
		this.context = context;
		this.resultHandler = resultHandler;
		this.configManager = new CameraConfigurationManager(context);
		previewCallback = new PreviewCallback(configManager);
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 * 
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public synchronized void openDriver(SurfaceHolder holder) throws IOException {
		Camera theCamera = camera;
		if (theCamera == null) {
			// 获取手机后置的摄像头
			if (requestedCameraId >= 0) {
				theCamera = OpenCameraInterface.open(requestedCameraId);
			} else {
				theCamera = OpenCameraInterface.open();
			}

			if (theCamera == null) {
				throw new IOException();
			}
			try {
				// 设置摄像头的角度，一般来说90度
				theCamera.setDisplayOrientation(CameraDisplayOrientation(context.getWindowManager().getDefaultDisplay().getRotation()));
			} catch (Exception e) {

				e.printStackTrace();
			}
			camera = theCamera;
			configManager.getCameraResolution();
			if (!initialized) {
				initialized = true;
				configManager.initFromCameraParameters(theCamera);
				if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
					setManualFramingRect(requestedFramingRectWidth, requestedFramingRectHeight);
					requestedFramingRectWidth = 0;
					requestedFramingRectHeight = 0;
				}
			}

			Parameters parameters = theCamera.getParameters();
			String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save
			// these,
			// temporarily
			try {
				// 读取配置并设置相机参数
				configManager.setDesiredCameraParameters(theCamera, false);
			} catch (RuntimeException re) {
				// Driver failed
				Log.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
				Log.i(TAG, "Resetting to saved camera params: " + parametersFlattened);
				// Reset:
				if (parametersFlattened != null) {
					parameters = theCamera.getParameters();
					parameters.unflatten(parametersFlattened);
					try {
						theCamera.setParameters(parameters);
						configManager.setDesiredCameraParameters(theCamera, true);
					} catch (RuntimeException re2) {
						// Well, darn. Give up
						Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
					}
				}
			}
		}
		// 设置摄像头预览view
		theCamera.setPreviewDisplay(holder);

	}

	// 初始花相机及预览
	public synchronized void openCamera(SurfaceTexture holder) throws IOException {
		Camera theCamera = camera;
		if (theCamera == null) {
			// 获取手机后置的摄像头
			if (requestedCameraId >= 0) {
				theCamera = OpenCameraInterface.open(requestedCameraId);
			} else {
				theCamera = OpenCameraInterface.open();
			}

			if (theCamera == null) {
				throw new IOException();
			}
			try {
				// 设置摄像头的角度，一般来说90度
				theCamera.setDisplayOrientation(CameraDisplayOrientation(context.getWindowManager().getDefaultDisplay().getRotation()));
			} catch (Exception e) {

				e.printStackTrace();
			}
			camera = theCamera;

			configManager.setFocusModel(camera, false);

			configManager.initCameraPreviewSizeValue(camera.getParameters());

		}
		// 设置摄像头预览view
		theCamera.setErrorCallback(errorCallback);

		theCamera.setPreviewTexture(holder);
	}

	public synchronized boolean isOpen() {
		return camera != null;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public synchronized void closeDriver() {
		if (camera != null) {
			camera.release();
			camera = null;
			// Make sure to clear these each time we close the camera, so that
			// any scanning rect
			// requested by intent is forgotten.
			framingRect = null;
			framingRectInPreview = null;
		}
	}

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */

	public synchronized void startPreview() {
		Camera theCamera = camera;

		if (theCamera != null && !previewing) {
			// setFrontLightMode(frontLightMode,theCamera.getParameters());
			theCamera.startPreview();

			previewing = true;

			// autoFocusManager = new AutoFocusManager(context, camera);
		}
	}

	public  synchronized void startFocus() {
		try {

			if (previewing) {

				if (autoFocusManager == null) {

					autoFocusManager = new AutoFocusManager(context, camera);
					autoFocusManager.setAutoFocusListion(this);
				}
				autoFocusManager.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized void startCameraPreview() {
		Camera theCamera = camera;

		if (theCamera != null && !previewing) {
			Parameters params = theCamera.getParameters();
			params.setPictureFormat(PixelFormat.JPEG);
			Point cameraResolution = configManager.getCameraResolution();
			params.setPreviewSize(cameraResolution.x, cameraResolution.y);
			params.setPictureSize(cameraResolution.x, cameraResolution.y);
			previewsize = cameraResolution;
			pictureSize = cameraResolution;
			try {

				setFrontLightMode(frontLightMode, params);
				// theCamera.setParameters(params);
				theCamera.startPreview();
			} catch (Exception e) {
				e.printStackTrace();

				// theCamera.startPreview();
			}

			previewing = true;

		}
	}

	public void setDefaultPreviewSize(Parameters parameters, Point defaultpoint) {

		parameters.setPreviewSize(defaultpoint.x, defaultpoint.y);
		if (Math.max(defaultpoint.x, defaultpoint.y) < 960) {
			parameters.setPictureSize(defaultpoint.x * 2, defaultpoint.y * 2);
			this.pictureSize = new Point(defaultpoint.x * 2, defaultpoint.y * 2);
		} else if (Math.max(defaultpoint.x, defaultpoint.y) > 960 * 3) {
			parameters.setPictureSize(defaultpoint.x / 2, defaultpoint.y / 2);
			this.pictureSize = new Point(defaultpoint.x / 2, defaultpoint.y / 2);
		} else {
			this.pictureSize = new Point(defaultpoint.x, defaultpoint.y);
			parameters.setPictureSize(defaultpoint.x, defaultpoint.y);
		}
		this.pictureSize = new Point(defaultpoint.x, defaultpoint.y);

	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public synchronized void stopPreview() {

		if (autoFocusManager != null) {
			autoFocusManager.stop();
			autoFocusManager = null;
		}
		System.out.println("pr=" + previewing);
		if (camera != null && previewing) {
			// Camera.Parameters parameters = camera.getParameters();
			// List<String> supportedFlashModes =
			// parameters.getSupportedFlashModes();
			// String flashMode =
			// CameraConfigurationUtils.findSettableValue("flash mode",
			// supportedFlashModes, Camera.Parameters.FLASH_MODE_OFF);
			// parameters.setFlashMode(flashMode);
			// camera.setParameters(parameters);
			// setFrontLightMode(frontLightMode,parameters);
			camera.stopPreview();
			previewCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	/**
	 * Convenience method for
	 *
	 * 
	 * @param newSetting
	 *            if {@code true}, light should be turned on if currently off.
	 *            And vice versa.
	 */
	public synchronized void setTorch(boolean newSetting) {
		if (newSetting != configManager.getTorchState(camera)) {
			if (camera != null) {
				if (autoFocusManager != null) {
					autoFocusManager.stop();
				}
				configManager.setTorch(camera, newSetting);
				if (autoFocusManager != null) {
					autoFocusManager.start();
				}
			}
		}
	}

	public  synchronized  void  cancelAutoFocus(){
		if(null != autoFocusManager){
			autoFocusManager.stop();
		}
	}

	/**
	 * A single preview frame will be returned to the handler supplied. The data
	 * will arrive as byte[] in the message.obj field, with width and height
	 * encoded as message.arg1 and message.arg2, respectively.
	 * 
	 * @param handler
	 *            The handler to send the message to.
	 * @param message
	 *            The what field of the message to be sent.
	 */
	public synchronized void requestPreviewFrame(Handler handler, int message) {
		Camera theCamera = camera;
		if (theCamera != null && previewing) {
			previewCallback.setHandler(handler, message);
			theCamera.setOneShotPreviewCallback(previewCallback);
		}
	}

	/**
	 * Calculates the framing rect which the UI should draw to show the user
	 * where to place the barcode. This target helps with alignment as well as
	 * forces the user to hold the device far enough away to ensure the image
	 * will be in focus.
	 * 
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public synchronized Rect getFramingRect() {
		if (framingRect == null) {
			if (camera == null) {
				return null;
			}
			Point screenResolution = configManager.getScreenResolution();
			if (screenResolution == null) {
				// Called early, before init even finished
				return null;
			}

			int width = findDesiredDimensionInRange(screenResolution.x, MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
			int height = findDesiredDimensionInRange(screenResolution.y, MIN_FRAME_HEIGHT, MAX_FRAME_HEIGHT);

			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
			Log.d(TAG, "Calculated framing rect: " + framingRect);
		}
		return framingRect;
	}

	private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
		int dim = 5 * resolution / 8; // Target 5/8 of each dimension
		if (dim < hardMin) {
			return hardMin;
		}
		if (dim > hardMax) {
			return hardMax;
		}
		return dim;
	}

	/**
	 * 绘制一个16：9 的区域，以终端的高为基准
	 * 
	 * @return
	 */
	public synchronized Rect getFramingRectInPreview() {
		if (framingRectInPreview == null) {
			Point screenResolution = configManager.getScreenResolution();
			if (null == screenResolution) {
				return null;
			}
			Point cameraResolution = configManager.getCameraResolution();

			if (null == cameraResolution) {
				return null;
			}
			if (cameraResolution.x * 16 == cameraResolution.y * 9 || cameraResolution.y * 16 == cameraResolution.x * 9) {
				return null;
			}
			int width = screenResolution.x;
			int height = screenResolution.y;

			if (width > height) {
				if (width * 9 / 16 > height) {
					width = height * 16 / 9;
				} else {
					height = width * 9 / 16;
				}
			} else {
				if (width * 16 / 9 > height) {
					width = height * 9 / 16;

				} else {
					height = width * 16 / 9;
				}
			}

			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;

			framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);

			framingRectInPreview = framingRect;

		}
		return framingRectInPreview;
	}

	/**
	 * Allows third party apps to specify the camera ID, rather than determine
	 * it automatically based on available cameras and their orientation.
	 * 
	 * @param cameraId
	 *            camera ID of the camera to use. A negative value means
	 *            "no preference".
	 */
	public synchronized void setManualCameraId(int cameraId) {
		if (initialized) {
			throw new IllegalStateException();
		} else {
			requestedCameraId = cameraId;
		}
	}

	/**
	 * Allows third party apps to specify the scanning rectangle dimensions,
	 * rather than determine them automatically based on screen resolution.
	 * 
	 * @param width
	 *            The width in pixels to scan.
	 * @param height
	 *            The height in pixels to scan.
	 */
	public synchronized void setManualFramingRect(int width, int height) {
		if (initialized) {
			Point screenResolution = configManager.getScreenResolution();
			if (width > screenResolution.x) {
				width = screenResolution.x;
			}
			if (height > screenResolution.y) {
				height = screenResolution.y;
			}
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
			Log.d(TAG, "Calculated manual framing rect: " + framingRect);
			framingRectInPreview = null;
		} else {
			requestedFramingRectWidth = width;
			requestedFramingRectHeight = height;
		}
	}

	// 是否支持放大倍数
	public synchronized boolean isSupportZoom() {
		Parameters parameters = camera.getParameters();

		return parameters.isZoomSupported();
	}

	// 获取当前摄像头的放大倍数
	public synchronized int getZoom() {
		int currentZoom;
		Parameters params = camera.getParameters();
		int MAX = params.getMaxZoom();
		currentZoom = params.getZoom() * 100 / MAX;
		return currentZoom;
	}

	// 设置当前摄像头的放大倍数
	public synchronized void setZoom(int value) {
		if (null != camera) {
			Parameters params = camera.getParameters();
			if (params.isZoomSupported()) {
				try {

					int MAX = params.getMaxZoom();
					if (MAX == 0) {
					} else {
						int zoomValue = MAX * value / 100;
						if (zoomValue <= MAX) {
							params.setZoom(zoomValue);
							camera.setParameters(params);
						}
					}
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}

	}

	public int CameraDisplayOrientation(int rotation) throws Exception {
		Camera.CameraInfo info = new Camera.CameraInfo();
		int cameraId = OpenCameraInterface.CameraId();
		if (cameraId == -1) {
			throw new Exception("没有摄像头！");
		}
		Camera.getCameraInfo(cameraId, info);
		int degrees = 0;
		int orientation;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			orientation = (info.orientation + degrees) % 360;
			orientation = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			orientation = (info.orientation - degrees + 360) % 360;
		}
		return orientation;
	}

	private ShutterCallback mShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub

		}
	};

	// 照相
	public synchronized void tackPic(final long time) {

		// 初始化闪光灯
		// configManager.initFromCameraParameters(camera);
		// Parameters parameters = camera.getParameters();
		// parameters.setFlashMode(Parameters.FLASH_MODE_ON);
		// Point camerPoint = configManager.getCameraResolution();
		// parameters.setPreviewSize(camerPoint.x, camerPoint.y);
		// camera.setParameters(parameters);

		if (null != camera) {
			camera.takePicture(volueMode == VolumeMode.ON ? mShutterCallback : null, null, new PictureCallback() {

				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					if (null != data && data.length > 0) {

						resultHandler.sendEmptyMessage(CameraPreferences.TACKPIC_RESULT_DATA_OK);
            System.out.println("path=="+path+":::"+imageName);
						Toast.makeText(context,"获取到数据"+data.length,Toast.LENGTH_SHORT).show();
						Bitmap mBitmap = BitmapUtil.dataToBaseBitmap(data, path, "base_" + imageName, 90);
						Toast.makeText(context,"转换图片="+(null == mBitmap),Toast.LENGTH_SHORT).show();
						if (null != mBitmap) {
							// 旋转
							boolean resultOk = BitmapUtil.zipImageTo960x540(mBitmap, landscape == 1 ? 0 : result, getFramingRect(mBitmap.getWidth(), mBitmap.getHeight()), time, path, imageName);
							Toast.makeText(context,"旋转图片="+resultOk,Toast.LENGTH_SHORT).show();
							try {
								File file = new File(path, imageName);
								if (file.exists()) {
									Message message = resultHandler.obtainMessage(CameraPreferences.TACKPIC_RESULT_VIEWBASEIMG_OK);
									message.obj = file.getPath();
									resultHandler.sendMessage(message);
								} else {
									resultHandler.sendEmptyMessage(CameraPreferences.TACKPIC_RESULT_VIEWBASEIMG_ERROR);
								}

							} catch (Exception e) {
								e.printStackTrace();

								resultHandler.sendEmptyMessage(CameraPreferences.TACKPIC_RESULT_VIEWBASEIMG_ERROR);
							}

							if (resultOk) {
								resultHandler.sendEmptyMessage(CameraPreferences.TACKPIC_RESULT_VIEWIMG_OK);
							} else {
								resultHandler.sendEmptyMessage(CameraPreferences.TACKPIC_RESULT_VIEWIMG_ERROR);
							}

						} else {
							resultHandler.sendEmptyMessage(CameraPreferences.TACKPIC_RESULT_VIEWBASEIMG_ERROR);
						}

						// new Thread(new Runnable() {
						//
						// @Override
						// public void run() {}
						// }).start();

					} else {
						Toast.makeText(context,"未获取到数据",Toast.LENGTH_SHORT).show();
						resultHandler.sendEmptyMessage(CameraPreferences.TACKPIC_RESULT_DATA_NO);
					}

					data = null;
				}
			});
		}
	}

	public static Rect getFramingRect(int imgWidth, int imgHeight) {
		Log.i(TAG, "width = " + imgWidth + "height = " + imgHeight);
		int width;
		int height;
		Rect framingRect;
		if (imgWidth > imgHeight) {
			if (imgWidth * 9 / 16 > imgHeight) {
				width = imgHeight * 16 / 9;
				height = imgHeight;
			} else {
				height = imgWidth * 9 / 16;
				width = imgWidth;
			}
		} else {
			if (imgWidth * 16 / 9 > imgHeight) {
				width = imgHeight * 9 / 16;
				height = imgHeight;

			} else {
				width = imgWidth;
				height = imgWidth * 16 / 9;
			}
		}
		int leftOffset = (imgWidth - width) / 2;
		int topOffset = (imgHeight - height) / 2;
		framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
		Log.d(TAG, "Calculated framing rect: " + framingRect);

		return framingRect;
	}

	// 设置闪光模式
	public void setFrontLightMode(FrontLightMode lightMode, Parameters parameters) {
		try {
			if (null == camera) {
				return;
			}
			if (null == parameters) {
				parameters = camera.getParameters();
			}
			List<String> supportedFlashModes = parameters.getSupportedFlashModes();
			String flashMode = "";
			switch (lightMode) {
			case AUTO:
				flashMode = CameraConfigurationUtils.findSettableValue("flash mode", supportedFlashModes, Parameters.FLASH_MODE_AUTO);
				break;
			case ON:
				flashMode = CameraConfigurationUtils.findSettableValue("flash mode", supportedFlashModes, Parameters.FLASH_MODE_TORCH, Parameters.FLASH_MODE_ON);
				break;
			case OFF:
				flashMode = CameraConfigurationUtils.findSettableValue("flash mode", supportedFlashModes, Parameters.FLASH_MODE_OFF);
				break;
			default:
				break;
			}
			if (!TextUtils.isEmpty(flashMode)) {
				parameters.setFlashMode(flashMode);
				if (null != camera) {
					camera.setParameters(parameters);
				}
				// if(isParament){
				// camera.setParameters(parameters);
				// }
				// camera.setParameters(parameters);
				// camera.startPreview();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (null != configManager.getDefaultpoint()) {
					previewsize = configManager.getDefaultpoint();
					pictureSize = configManager.getDefaultpoint();
					if(null != parameters){
						parameters.setPreviewSize(previewsize.x, previewsize.y);
						parameters.setPictureSize(previewsize.x, previewsize.y);
						camera.setParameters(parameters);
					}
				}
			} catch (Exception e2) {
				e.printStackTrace();
			}

			// TODO: handle exception
		}

	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFaile() {
     System.out.println("");
		//if (null != resultHandler) {
    //
		//	stopPreview();
		//	closeDriver();
		//	resultHandler.sendEmptyMessage(CameraPreferences.RESET_CAMERA);
		//}

	}
}
