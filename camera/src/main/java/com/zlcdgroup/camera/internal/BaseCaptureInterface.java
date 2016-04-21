package com.zlcdgroup.camera.internal;


import com.zlcdgroup.camera.FrontLightMode;
import com.zlcdgroup.camera.VolumeMode;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface BaseCaptureInterface {

    void   onFrontLight(FrontLightMode  frontLightMode);//闪光灯

    void   onVolume(VolumeMode   volumeMode);//拍照声音




}
