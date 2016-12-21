package com.zlcdgroup.libs.ocr;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/24.
 */

public class OcrVo implements Serializable{

    public   String  localpath;

    private    String   ocrtext;

    public String getOcrtext() {
      return  ocrtext;
    }

    public void setOcrtext(String ocrtext) {
        this.ocrtext = ocrtext;

    }

    public String Value;

    public   int   Code;


}
