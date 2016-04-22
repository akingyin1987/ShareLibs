package com.zlcdgroup.libs.photovideo;

import android.os.Environment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/4/23.
 */
public class Temp {

    public  static   int  DURATION_LIMIT = 20;

    public  static   int SIZE_LIMIT = 20 *1024;


    public  static   String  getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public   static  String TEMP_ROOT= Environment.getExternalStorageDirectory().toString() + File.separator + "temp";

    public  static List<BaseImgTextItem>  tempData = new LinkedList<>();
}
