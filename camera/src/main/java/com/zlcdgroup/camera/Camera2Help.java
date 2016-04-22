package com.zlcdgroup.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @ Description:
 * camera2相机帮助类
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/4/22 13:18
 * @ Version V1.0
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Help {

  private static final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen

  private static final double MAX_ASPECT_DISTORTION = 0.15;

  private static final String TAG = "Camera2-Configuration";

  private   int    angle;//旋转角度

  private   Point   screenResolution;

  private   Point   cameraResolution;

  public int getAngle() {
    return angle;
  }

  public void setAngle(int angle) {
    this.angle = angle;
  }

  public Point getScreenResolution() {
    return screenResolution;
  }

  public void setScreenResolution(Point screenResolution) {
    this.screenResolution = screenResolution;
  }

  public Point getCameraResolution() {
    return cameraResolution;
  }

  public void setCameraResolution(Point cameraResolution) {
    this.cameraResolution = cameraResolution;
  }

  public Context  mContext;

  public  Camera2Help(Context  mContext){
    this.mContext = mContext;

    WindowManager manager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
    Display display = manager.getDefaultDisplay();

    Point theScreenResolution = new Point();
    if(Build.VERSION.SDK_INT <17){
      display.getSize(theScreenResolution);
    }else{
      display.getRealSize(theScreenResolution);
    }
    screenResolution = new Point(theScreenResolution.x,theScreenResolution.y);
  }

  /**
   * 找到最合适的PreviewSize，如果有当前技能的Size 进行计算
   * 1.Size >= 480 * 320
   * 2.屏幕的Size 与当前 Size 宽与高比值之差《=0.15
   * @param
   * @param screenResolution 当前屏幕Size
   * @return
   */
  public static Point findBestPreviewSizeValue(List<Size> rawSupportedSizes , Point screenResolution) {

    //找到所有支持的Size
    Log.d(TAG,"screen="+screenResolution.x+":"+screenResolution.y);
    if (rawSupportedSizes == null) {

      return new Point(screenResolution.x, screenResolution.y);
    }
    List<Size>  supporteds = new ArrayList<>(rawSupportedSizes);


    //自定义排序规则
    Collections.sort(supporteds, new Comparator<Size>() {
      @Override
      public int compare(Size a, Size b) {
        int aPixels = a.getHeight() * a.getWidth();
        int bPixels = b.getHeight() * b.getWidth();
        if (bPixels < aPixels) {
          return -1;
        }
        if (bPixels > aPixels) {
          return 1;
        }
        return 0;
      }
    });


    double screenAspectRatio = (double) Math.max(screenResolution.x, screenResolution.y) / (double) Math.min(screenResolution.x, screenResolution.y);
    Log.d(TAG,"screenAspectRatio="+screenAspectRatio);
    // Remove sizes that are unsuitable
    Iterator<Size> it = supporteds.iterator();
    int  maxscreenX = Math.max(screenResolution.x,screenResolution.y);
    int  minscreenY = Math.min(screenResolution.x,screenResolution.y);
    while (it.hasNext()) {
      Size supportedPreviewSize = it.next();
      int realWidth = supportedPreviewSize.getWidth();
      int realHeight = supportedPreviewSize.getHeight();
      if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
        it.remove();
        continue;
      }

      boolean isCandidatePortrait = realWidth < realHeight;
      int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
      int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
      double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
      Log.d(TAG,"aspectRatio="+aspectRatio);
      double distortion = Math.abs(aspectRatio - screenAspectRatio);
      if (distortion > MAX_ASPECT_DISTORTION) {
        it.remove();
        continue;
      }
      Log.d(TAG,"maybeFlippedWidth="+maybeFlippedWidth+":"+maybeFlippedHeight);
      if (maybeFlippedWidth % maxscreenX ==0 && maybeFlippedHeight % minscreenY ==0) {
        Point exactPoint = new Point(realWidth, realHeight);
        Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
        return exactPoint;
      }
    }

    // If no exact match, use largest preview size. This was not a great idea on older devices because
    // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
    // the CPU is much more powerful.
    //排除完上述条件后，获取第一个Size
    Log.d(TAG,"mabesize="+supporteds.size());
    if (!supporteds.isEmpty()) {

      Size largestPreview = supporteds.get(0);
      Point largestSize = new Point(largestPreview.getWidth(), largestPreview.getHeight());
      Log.i(TAG, "Using largest suitable preview size: " + largestSize);
      return largestSize;
    }

    // If there is nothing at all suitable, return current preview size

    Point defaultSize = new Point(screenResolution.x, screenResolution.y);
    Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
    return defaultSize;
  }

}
