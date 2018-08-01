package com.zlcdgroup.libs.ocr.api;

import com.zlcdgroup.libs.ocr.OcrVo;

import com.zlcdgroup.libs.ocr.YunShiEntity;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2016/10/24.
 */

public interface OcrApi {

    /**
     * 云识图象识别API
     * @param base64img
     * @param ClientType
     * @param UserID
     * @param DeviceID
     * @param Datetime
     * @param NumArea
     * @return
     */
    @FormUrlEncoded
    @POST("water_ssd/getDigit")
    Observable<YunShiEntity>   getImageOcrByYushi(@Field("Image") String  base64img,
                                                  @Field("ClientType")String ClientType,
                                                  @Field("UserID")String UserID,
                                                  @Field("DeviceID")String DeviceID,
                                                  @Field("Datetime")String Datetime,
                                                  @Field("NumArea")String NumArea,
                                                  @Field("MacAddress")String MacAddress,
                                                  @Field("Province")String  Province,
                                                  @Field("City")String  City
                                           );

    /**
     * 百度云识别API
     * @param apikey
     * @param fromdevice
     * @param clientip
     * @param detecttype
     * @param imagetype
     * @param image
     * @return
     */
    @FormUrlEncoded
    @POST("idl_baidu/baiduocrpay/idlocrpaid")
    Observable<ResponseBody>  getImageOcrByBaidu(@Header("apikey")String apikey,
                                          @Field("fromdevice")String fromdevice,
                                          @Field("clientip")String clientip,
                                          @Field("detecttype")String detecttype,
                                          @Field("imagetype")String imagetype,
                                          @Field("image")String image);

  /**
   * 云纠错
   * @param filename
   * @param UserID
   * @param Datetime
   * @param NumArea
   * @param ErrorNum
   * @param ErrorPos
   * @return
   */
    @FormUrlEncoded
    @POST("water/check")
    Observable<OcrVo>    checkImageOcrByCloud(@Field("filename")String filename,
                                              @Field("UserID")String UserID,
                                              @Field("Datetime")String Datetime,
                                              @Field("NumArea")String NumArea,
                                              @Field("ErrorNum")String ErrorNum,
                                              @Field("ErrorPos")String ErrorPos );

  /**
   * 全图识别
   * @param image
   * @param ClientType
   * @param UserID
   * @param DeviceID
   * @param Datetime
   * @param
   * @param MacAddress
   * @param Province
   * @param City
   * @param lastRecord
   * @return
   */
    @FormUrlEncoded
    //@POST("water/help/meter_api")
   // @POST("water/pc/getDigit")
    @POST("water/whole_pic/getDigit ")
    Call<YunShiEntity> getImageOcrMapByYushi(  @Field("Image")String  image,
                                                 @Field("ClientType")String ClientType,
                                                 @Field("UserID")String UserID,
                                                 @Field("DeviceID")String DeviceID,
                                                 @Field("Datetime")String Datetime,
                                                 @Field("MacAddress")String MacAddress,
                                                 @Field("Province")String  Province,
                                                 @Field("City")String  City,
                                                 @Field("LastRecord")String  lastRecord);





     @FormUrlEncoded
     @POST("api/v7/")
     Call<ResponseBody>  getReadJobInfoById(@Field("data") String  data, @Field("token") String  token,
         @Field("system") String system,@Field("version")String version);



     @GET
     Call<ResponseBody>  getImageForBase64(@Url String  url);
}
