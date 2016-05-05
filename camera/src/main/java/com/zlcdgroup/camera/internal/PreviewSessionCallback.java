package com.zlcdgroup.camera.internal;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.zlcdgroup.camera.widget.AnimationImageView;

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/5/5 19:06
 * @ Version V1.0
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PreviewSessionCallback  extends  CameraCaptureSession.CaptureCallback implements AutoFitTextureView.FocusPositionTouchEvent{

  private int mAfState = CameraMetadata.CONTROL_AF_STATE_INACTIVE;
  private  CaptureResultCallback  captureResultCallback;

  private   int   mStatus;

  public int getmStatus() {
    return mStatus;
  }

  public void setmStatus(int mStatus) {
    this.mStatus = mStatus;
  }

  public CaptureResultCallback getCaptureResultCallback() {
    return captureResultCallback;
  }

  public void setCaptureResultCallback(CaptureResultCallback captureResultCallback) {
    this.captureResultCallback = captureResultCallback;
  }

  private AnimationImageView mFocusImage;
  private Handler mMainHandler;
  private int mRawX;
  private int mRawY;
  private boolean mFlagShowFocusImage = false;
  public PreviewSessionCallback(AnimationImageView mFocusImage, Handler mMainHandler, AutoFitTextureView mMyTextureView) {
    this.mFocusImage = mFocusImage;
    this.mMainHandler = mMainHandler;
    mMyTextureView.setmFocusPositionTouchEvent(this);
  }

  @Override
  public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp,
      long frameNumber) {
    super.onCaptureStarted(session, request, timestamp, frameNumber);
  }

  @Override
  public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
      CaptureResult partialResult) {
    super.onCaptureProgressed(session, request, partialResult);
    if(null != captureResultCallback){
      captureResultCallback.onCaptureProgressed(session,request,partialResult);
    }
  }

  @Override
  public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
      TotalCaptureResult result) {
    if(null != captureResultCallback){
      captureResultCallback.onCaptureCompleted(session,request,result);
    }
    if(mStatus != Camera2Fragment.STATE_PREVIEW){
       return;
    }
    Log.i("Thread", "onCaptureCompleted---->" + Thread.currentThread().getName());
    Log.i("PreviewSessionCallback", "onCaptureCompleted");
    Integer nowAfState = result.get(CaptureResult.CONTROL_AF_STATE);
    //获取失败
    if (nowAfState == null) {
      return;
    }
    //这次的值与之前的一样，忽略掉
    if (nowAfState.intValue() == mAfState) {
      return;
    }
    mAfState = nowAfState.intValue();
    mMainHandler.post(new Runnable() {
      @Override
      public void run() {
        judgeFocus();
      }
    });
  }

  @Override
  public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request,
      CaptureFailure failure) {
    super.onCaptureFailed(session, request, failure);
  }

  @Override
  public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId,
      long frameNumber) {
    super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);

  }

  private void judgeFocus() {
    switch (mAfState) {
      case CameraMetadata.CONTROL_AF_STATE_ACTIVE_SCAN:
      case CameraMetadata.CONTROL_AF_STATE_PASSIVE_SCAN:
        focusFocusing();
        break;
      case CameraMetadata.CONTROL_AF_STATE_FOCUSED_LOCKED:
      case CameraMetadata.CONTROL_AF_STATE_PASSIVE_FOCUSED:
        focusSucceed();
        break;
      case CameraMetadata.CONTROL_AF_STATE_INACTIVE:
        focusInactive();
        break;
      case CameraMetadata.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED:
      case CameraMetadata.CONTROL_AF_STATE_PASSIVE_UNFOCUSED:
        focusFailed();
        break;
    }
  }

  private void focusFocusing() {
    //得到宽高
    int width = mFocusImage.getWidth();
    int height = mFocusImage.getHeight();
    //居中
    ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(mFocusImage.getLayoutParams());
    margin.setMargins(mRawX - width / 2, mRawY - height / 2, margin.rightMargin, margin.bottomMargin);
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
    mFocusImage.setLayoutParams(layoutParams);
    //显示
    if (mFlagShowFocusImage == false) {
      mFocusImage.startFocusing();
      mFlagShowFocusImage = true;
    }
  }

  private void focusSucceed() {
    if (mFlagShowFocusImage == true) {
      mFocusImage.focusSuccess();
      mFlagShowFocusImage = false;
    }
  }

  private void focusInactive() {
    mFocusImage.stopFocus();
    mFlagShowFocusImage = false;
  }

  private void focusFailed() {
    if (mFlagShowFocusImage == true) {
      mFocusImage.focusFailed();
      mFlagShowFocusImage = false;
    }
  }

  @Override
  public void getPosition(MotionEvent event) {
    mRawX = (int) event.getRawX();
    mRawY = (int) event.getRawY();
  }
}
