package com.zlcdgroup.libs.ocr;

import android.text.TextUtils;
import com.zlcdgroup.dao.ReadImageBeanDao;
import com.zlcdgroup.libs.db.ReadImageBean;
import com.zlcdgroup.libs.ocr.api.OcrApi;
import com.zlcdgroup.libs.utils.Base64Util;
import com.zlcdgroup.libs.utils.ImageUtils;
import com.zlcdgroup.taskManager.AbsTaskRunner;
import com.zlcdgroup.taskManager.enums.TaskStatusEnum;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/1/4 11:34
 * @ Version V1.0
 */

public class OcrReadTask  extends AbsTaskRunner {

  OcrApi  api;
  ReadImageBean  mReadImageBean;
  String  imei,macAddress;
  ReadImageBeanDao  dao ;

  public OcrReadTask(OcrApi api, ReadImageBean readImageBean, String imei, String macAddress,
      ReadImageBeanDao dao) {
    this.api = api;
    mReadImageBean = readImageBean;
    this.imei = imei;
    this.macAddress = macAddress;
    this.dao = dao;
  }



  @Override public TaskStatusEnum onBefore() {

    return TaskStatusEnum.SUCCESS;
  }

  @Override public void onToDo() {
    YunShiEntity   yunShiEntity =  checkImageMap(1,0,mReadImageBean,imei,macAddress,api);
    if(null != yunShiEntity && yunShiEntity.getCode() == 0){
      Double  value = Double.parseDouble(yunShiEntity.getValue().split(",")[0]);
      mReadImageBean.orcReading = value.longValue();
      if(null != mReadImageBean.rdReading){
        if(Math.abs(mReadImageBean.rdReading-mReadImageBean.orcReading)<=2){
          mReadImageBean.result=1;
        }else{
          mReadImageBean.result=2;
        }
      }

    }else{
      mReadImageBean.result = 3;

    }
    dao.save(mReadImageBean);
    TaskOnSuccess();
  }
  public SimpleDateFormat formatter = new SimpleDateFormat("yyyymmdd-hhmmss-SSSS");
  public YunShiEntity checkImageMap(int count,int  degree,ReadImageBean  ocrVo,String imei,String macAddress, OcrApi
      api){
    if(TextUtils.isEmpty(ocrVo.localPath)){
      return null;
    }
    if(degree>0){
      ImageUtils.rotateBitmap(degree,ocrVo.localPath);
    }
    String  image = Base64Util.FileToBase64(ocrVo.localPath);

    try {
      YunShiEntity  yunShiEntity =  api.getImageOcrMapByYushi(image,"Android","QCZLCD",imei,formatter.format(new Date()),macAddress,"重庆","重庆","[0,0]").execute().body();
      System.out.println("code="+yunShiEntity.getCode()+":"+degree);
      if(yunShiEntity.getCode() == 0 || degree == 270 || count == 5){
        if(count == 5){
          return  yunShiEntity;
        }
        if(yunShiEntity.getCode() == 0){
          Double  value = Double.parseDouble(yunShiEntity.getValue().split(",")[0]);
          if(null != ocrVo.rdReading && ocrVo.rdReading>0){
            if(value/ocrVo.rdReading>5 || value/ocrVo.rdReading<0.2){
             return checkImageMap(5,180,ocrVo,imei,macAddress,api);
            }
          }
        }
        return  yunShiEntity;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return   checkImageMap(count+1,degree+90,ocrVo,imei,macAddress,api);
  }
}
