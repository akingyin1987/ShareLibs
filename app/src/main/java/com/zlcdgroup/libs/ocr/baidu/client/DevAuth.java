package com.zlcdgroup.libs.ocr.baidu.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zlcdgroup.libs.ocr.util.AipClientConfiguration;
import com.zlcdgroup.libs.ocr.util.AipClientConst;
import com.zlcdgroup.libs.ocr.util.Util;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/8/1 10:48
 */
public class DevAuth {

  /**
   * get access_token from openapi
   * @param apiKey API key from console
   * @param secretKey Secret Key from console
   * @param config network config settings
   * @return JsonObject of response from OAuth server
   */
  public static JSONObject oauth(String apiKey, String secretKey, AipClientConfiguration config) {
    try {
      HttpLoggingInterceptor   loggingInterceptor = new HttpLoggingInterceptor();
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
      OkHttpClient   okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();
      String getAccessTokenUrl = AipClientConst.OAUTH_URL
          // 1. grant_type为固定参数
          + "?grant_type=client_credentials"
          // 2. 官网获取的 API Key
          + "&client_id=" + apiKey
          // 3. 官网获取的 Secret Key
          + "&client_secret=" + secretKey;
      System.out.println("url="+getAccessTokenUrl);
      //RequestBody  requestBody = new FormBody.Builder()
      //    .add("grant_type","client_credentials")
      //    .add("client_id",apiKey)
      //    .add("client_secret",secretKey).build();
      Request   request = new Request.Builder().url(getAccessTokenUrl).get().build();
      Response  response = okHttpClient.newCall(request).execute();
      if(response.isSuccessful()){
        String   result = response.body().string();

        System.out.println("result="+result);
         return JSON.parseObject(result);
      }else{
        return Util.getGeneralError(response.code(), "Server response code: " + response.code());
      }

    }  catch (IOException e) {
      e.printStackTrace();
    }
    return Util.getGeneralError(-1, "unknown error");
  }
}
