package com.zlcdgroup.libs.utils;


import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by Administrator on 2016/10/25.
 */

public class Base64Util {


    /**
     * 文件转bytes
     * @param imgFile
     * @return
     */
    public static byte[] getBytesFromFile(String   imgFile) {
        File    file = new File(imgFile);
        if(!file.exists() || file.length()==0){
            return  null;
        }
        try {
            FileInputStream   fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream   outputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[2*1024];
            int  len = 0;
            while ((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
            }
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            return  outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public   static   String   FileToBase64(String  localpath){
        byte[]  data = getBytesFromFile(localpath);
        if(null == data){
            return "";
        }
        return Base64.encodeToString(data,Base64.DEFAULT);
    }



}
