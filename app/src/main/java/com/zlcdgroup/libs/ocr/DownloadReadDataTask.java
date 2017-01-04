package com.zlcdgroup.libs.ocr;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zlcdgroup.dao.ReadImageBeanDao;
import com.zlcdgroup.libs.db.DbCore;
import com.zlcdgroup.libs.db.ReadImageBean;
import com.zlcdgroup.libs.ocr.api.OcrApi;
import com.zlcdgroup.libs.ocr.rsa.MD5;
import com.zlcdgroup.libs.ocr.rsa.RSA;
import com.zlcdgroup.libs.ocr.vo.VoReadingdata;
import com.zlcdgroup.taskManager.AbsTaskRunner;
import com.zlcdgroup.taskManager.enums.TaskStatusEnum;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;


/**
 * * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #
 *
 * @ Description:                                          #
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/12/21 11:46
 * @ Version V1.0
 */

public class DownloadReadDataTask  extends AbsTaskRunner {

  String  jobId,imei,persionId;
  VoReadingdata   voReadingdata;
  int  page;
  OcrApi   api;

  public DownloadReadDataTask(String jobId, String imei, String persionId,
      VoReadingdata voReadingdata, int page, OcrApi api) {
    this.jobId = jobId;
    this.imei = imei;
    this.persionId = persionId;
    this.voReadingdata = voReadingdata;
    this.page = page;
    this.api = api;
  }

  /**
   * 获取加密后的数据签名
   *
   * @param data
   * @return
   */
  public final static synchronized String getToken(String data) {
    // 初步想法为先用MD5获取其缩略16个字节的文本。
    // 然后执行DSA或者RSA加密，采取公钥和私钥的形式。
    try {
      String md5Txt = MD5.md5(data);
      String rsa = RSA.encrypt(md5Txt);

      if (TextUtils.isEmpty(rsa)) {
        md5Txt = MD5.md5(data);
        rsa = RSA.encrypt(md5Txt);
        return rsa;
      }
      return rsa;
    } catch (Exception e) {
      return "no token";
    }
  }

  public static String getJsonData(String method, String userid, String imei,
      Map<String, Object> dataMap) {
    JSONObject dataJson = new JSONObject(dataMap);
    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("method", method);
    hashMap.put("data", dataJson);
    hashMap.put("personId", userid);
    hashMap.put("imei", imei);
    JSONObject jsonObject = new JSONObject(hashMap);
    System.out.println("终端："+jsonObject.toJSONString());
    String data = "";
    try {
      String objStr = jsonObject.toString();
      // LogUtil.log("client", objStr);
      if (TextUtils.equals("zlcd_mrmsei_upload_img_base64", method)) {

      } else {

      }

      data = URLEncoder.encode(objStr, "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return data;
  }


  ReadImageBeanDao   dao;

  @Override public TaskStatusEnum onBefore() {
    dao = DbCore.getDaoSession().getReadImageBeanDao();
    return TaskStatusEnum.SUCCESS;
  }

  @Override public void onToDo() {
    System.out.println("onToDo---------------");
    Map<String,Object>  dataMap = new HashMap<>();
    dataMap.put("readingdata", voReadingdata);
    dataMap.put("page",page);
    dataMap.put("personId",persionId);
    String  data = getJsonData("zlcd_mrmsei_get_reading_job_details",persionId,imei,dataMap);
    Call<ResponseBody> responseBodyCall = api.getReadJobInfoById(data,getToken(data),"mrmsei","MRMSEI.1.15.3.161231");
    try {
      String   result = responseBodyCall.execute().body().string();
      result = URLDecoder.decode(result,"utf-8");
      System.out.println("服务器："+result);
      JSONObject   jsonObject = JSON.parseObject(result);
      JSONArray    jsonArray = jsonObject.getJSONArray("data");
      if(jsonArray.size() == 0){
        TaskError();
        return;
      }
      JSONObject    jsondata = jsonArray.getJSONObject(0);
      if(jsondata.containsKey("result")){
       try {
         dao.getDatabase().beginTransaction();
         List<VoReadingdata>  voReadingdatas = JSON.parseArray(jsondata.getString("result"),VoReadingdata.class);
         for(VoReadingdata  voReadingdata : voReadingdatas){
           System.out.println(voReadingdata.getRdImagename());
           if(!TextUtils.isEmpty(voReadingdata.getRdImagename())){
             if(voReadingdata.getRdImagename().contains(",")){
               String[]  imgs = voReadingdata.getRdImagename().split(",");
               ReadImageBean  readImageBean = dao.queryBuilder().where(ReadImageBeanDao.Properties.WebPath.eq(imgs[imgs.length-1])).build().unique();
               if(null == readImageBean){
                 readImageBean = new ReadImageBean();
                 readImageBean.webPath = voReadingdata.getRdImagename();
                 readImageBean.rdReading = voReadingdata.getRdReading();
                 dao.save(readImageBean);
               }
             }else{
               ReadImageBean  readImageBean = dao.queryBuilder().where(ReadImageBeanDao.Properties.WebPath.eq(voReadingdata.getRdImagename())).build().unique();
               if(null == readImageBean){
                 readImageBean = new ReadImageBean();
                 readImageBean.webPath = voReadingdata.getRdImagename();
                 readImageBean.rdReading = voReadingdata.getRdReading();
                 dao.save(readImageBean);
               }
             }
           }

         }
         dao.getDatabase().setTransactionSuccessful();
       }catch (Exception e){
         e.printStackTrace();
       }finally {
         dao.getDatabase().endTransaction();
       }

      }
    }catch (Exception e){
      e.printStackTrace();
    }
    TaskOnSuccess();
  }
}
