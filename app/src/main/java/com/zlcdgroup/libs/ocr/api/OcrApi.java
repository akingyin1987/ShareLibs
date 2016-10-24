package com.zlcdgroup.libs.ocr.api;

import com.zlcdgroup.libs.ocr.OcrVo;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
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
    @POST("/water_ssd/getDigit")
    Observable<OcrVo>   getImageOcrByYushi(@Field("Image")String  base64img,
                                           @Field("ClientType")String ClientType,
                                           @Field("UserID")String UserID,
                                           @Field("DeviceID")String DeviceID,
                                           @Field("Datetime")String Datetime,
                                           @Field("NumArea")String NumArea
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
    @POST("/idl_baidu/baiduocrpay/idlocrpaid")
    Observable<OcrVo>  getImageOcrByBaidu(@Header("apikey")String apikey,
                                          @Field("fromdevice")String fromdevice,
                                          @Field("clientip")String clientip,
                                          @Field("detecttype")String detecttype,
                                          @Field("imagetype")String imagetype,
                                          @Field("image")String image);
}
