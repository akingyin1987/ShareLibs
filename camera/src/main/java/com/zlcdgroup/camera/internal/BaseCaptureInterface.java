package com.zlcdgroup.camera.internal;


import android.graphics.SurfaceTexture;
import com.zlcdgroup.camera.FrontLightMode;
import com.zlcdgroup.camera.VolumeMode;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface BaseCaptureInterface {

    void   onFrontLight(FrontLightMode  frontLightMode);//闪光灯

    void   onVolume(VolumeMode   volumeMode);//拍照声音

    void   onAutoFocus();//自动对焦

    void   onCancelFocus();//取消对焦

    void   onTakePic(String  dir,String  fileName,boolean  sound,int  land);//拍照

    void   onZoom(int   zoom);






}
