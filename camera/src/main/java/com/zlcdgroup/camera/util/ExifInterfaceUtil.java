package com.zlcdgroup.camera.util;

import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by Administrator on 2016/10/25.
 */

public class ExifInterfaceUtil {

    public   static  String  getExifinterAttr(String   localPath,String  key){
        try {
            ExifInterface   exifInterface = new ExifInterface(localPath);
            return exifInterface.getAttribute(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public  static  void  saveExifinterAttr(String  localpath,String  key,String value){
        try {
            ExifInterface  exifInterface = new ExifInterface(localpath);
            exifInterface.setAttribute(key,value);
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
