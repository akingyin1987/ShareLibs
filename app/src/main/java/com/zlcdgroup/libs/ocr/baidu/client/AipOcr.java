package com.zlcdgroup.libs.ocr.baidu.client;

import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import okhttp3.FormBody;
import okhttp3.Request;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/8/1 11:35
 */
public class AipOcr  extends  BaseClient{

  private   static   final  String   METER_FRAM_API_HOST="https://aip.baidubce.com/rpc/2.0/ai_custom/v1/detection/meterFrame";

  public AipOcr(String appId, String apiKey, String secretKey) {
    super(appId, apiKey, secretKey);
  }

  public JSONObject basicMeterFramUrl( HashMap<String, String> options) {
    FormBody.Builder  builder = new FormBody.Builder();
    if(null != options){
      for (String s : options.keySet()) {
        builder.add(s,options.get(s));
      }
    }
    Request   request  = new Request.Builder().url(METER_FRAM_API_HOST)
        .build();

   request = preOperation(request);

   // builder.add("url",METER_FRAM_API_HOST);
    request = request.newBuilder().url(METER_FRAM_API_HOST)
        .post(builder.build()).build();


    request =postOperation(request);
    return requestServer(request);
  }

}
