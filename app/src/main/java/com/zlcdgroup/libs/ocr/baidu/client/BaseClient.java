package com.zlcdgroup.libs.ocr.baidu.client;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zlcdgroup.libs.ocr.util.AipClientConfiguration;
import com.zlcdgroup.libs.ocr.util.AipClientConst;
import com.zlcdgroup.libs.ocr.util.SignUtil;
import com.zlcdgroup.libs.ocr.util.Util;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/8/1 10:29
 */
public  abstract class BaseClient {

  protected String appId;
  protected String aipKey;
  protected String aipToken;
  protected String accessToken;   // 不适用于使用公有云ak/sk的用户
  protected AtomicBoolean isAuthorized;
  protected AtomicBoolean isBceKey;   // 是否为公有云用户
  protected Calendar expireDate;
  protected AuthState state;
  protected AipClientConfiguration config;
  protected static final Logger LOGGER = Logger.getLogger("Baidu_Api");

  class AuthState {

    private EAuthState state;

    public AuthState() {
      state = EAuthState.STATE_UNKNOWN;
    }

    public String toString() {
      return state.name();
    }

    public EAuthState getState() {
      return state;
    }

    public void setState(EAuthState state) {
      this.state = state;
    }

    public void transfer(boolean value) {
      switch (state) {
        case STATE_UNKNOWN: {
          if (value) {
            state = EAuthState.STATE_AIP_AUTH_OK;
            isBceKey.set(false);
          }
          else {
            state = EAuthState.STATE_TRUE_CLOUD_USER;
            isBceKey.set(true);
          }
          break;
        }
        case STATE_AIP_AUTH_OK: {
          if (value) {
            state = EAuthState.STATE_TRUE_AIP_USER;
            isBceKey.set(false);
            isAuthorized.set(true);
          }
          else {
            state = EAuthState.STATE_POSSIBLE_CLOUD_USER;
            isBceKey.set(true);
          }
          break;
        }
        case STATE_TRUE_AIP_USER:
          break;
        case STATE_POSSIBLE_CLOUD_USER: {
          if (value) {
            state = EAuthState.STATE_TRUE_CLOUD_USER;
            isBceKey.set(true);
          }
          else {
            state = EAuthState.STATE_TRUE_AIP_USER;
            isBceKey.set(false);
            isAuthorized.set(true);
          }
          break;
        }
        case STATE_TRUE_CLOUD_USER:
          break;
        default:
          break;
      }
    }
  }

  /*
   * BaseClient constructor, default as AIP user
   */
  protected BaseClient(String appId, String apiKey, String secretKey) {
    this.appId = appId;
    this.aipKey = apiKey;
    this.aipToken = secretKey;
    isAuthorized = new AtomicBoolean(false);
    isBceKey = new AtomicBoolean(false);
    accessToken = null;
    expireDate = null;
    state = new AuthState();


  }

  /**
   *
   * @param timeout 服务器建立连接的超时时间（单位：毫秒）
   */
  public void setConnectionTimeoutInMillis(int timeout) {
    if (config == null) {
      config = new AipClientConfiguration();
    }
    this.config.setConnectionTimeoutMillis(timeout);
  }

  /**
   *
   * @param timeout 通过打开的连接传输数据的超时时间（单位：毫秒）
   */
  public void setSocketTimeoutInMillis(int timeout) {
    if (config == null) {
      config = new AipClientConfiguration();
    }
    this.config.setSocketTimeoutMillis(timeout);
  }

  /**
   * 设置访问网络需要的http代理
   * @param host 代理服务器地址
   * @param port 代理服务器端口
   */
  public void setHttpProxy(String host, int port) {
    if (config == null) {
      config = new AipClientConfiguration();
    }
    this.config.setProxy(host, port, Proxy.Type.HTTP);
  }

  /**
   * 设置访问网络需要的socket代理
   * @param host 代理服务器地址
   * @param port 代理服务器端口
   */
  public void setSocketProxy(String host, int port) {
    if (config == null) {
      config = new AipClientConfiguration();
    }
    this.config.setProxy(host, port, Proxy.Type.SOCKS);
  }

  /**
   * get OAuth access token, synchronized function
   * @param config 网络连接设置
   */
  protected synchronized void getAccessToken(AipClientConfiguration config) {
    if (!needAuth()) {
      Log.d("Api",String.format("app[%s] no need to auth", this.appId));
      return;
    }
    JSONObject res = DevAuth.oauth(aipKey, aipToken, config);
    if (res == null) {
      Log.w("Api","oauth get null response");
      return;
    }
    if (res.containsKey("access_token")) {
      // openAPI认证成功
      state.transfer(true);
      accessToken = res.getString("access_token");

      LOGGER.info("get access_token success. current state: " + state.toString());
      Integer expireSec = res.getInteger("expires_in");
      Calendar c = Calendar.getInstance();
      c.add(Calendar.SECOND, expireSec);
      expireDate = c;
      // isBceKey.set(false);
      // 验证接口权限
      String[] scope = res.getString("scope").split(" ");
      boolean hasRight = false;
      for (String str : scope) {
        if (AipClientConst.AI_ACCESS_RIGHT.contains(str)) {
          // 权限验证通过
          hasRight = true;
          break;
        }
      }
      state.transfer(hasRight);

    }
    else if (res.containsKey("error_code")) {
      state.transfer(false);
      Log.w("Api","oauth get error, current state: " + state.toString());
    }
  }

  /*
   *   需要重新获取access_token的条件：
   *   1. 是DEV用户，即 isBceKey为false
   *   2. isAuthorized为false，或isAuthorized为true，但当前时间晚于expireDate前一天
   */
  protected Boolean needAuth() {
    if (isBceKey.get()) {
      return false;
    }
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, 1);
    return !isAuthorized.get() || c.after(expireDate);
  }

  /*
   *  为DEV创建的用户填充body
   */
  protected Request preOperation(Request request) {
    if (needAuth()) {
      getAccessToken(config);
    }

   return    request.newBuilder().addHeader(Headers.CONTENT_TYPE, HttpContentType.FORM_URLENCODE_DATA)
       .addHeader("accept", "*/*").build();
  }

  /*
   *  为公有云用户填充http header，传入的request中body&param已经ready
   *  对于DEV用户，则将access_token放到url中
   */
  protected Request postOperation(Request request) {
    Request.Builder  builder = request.newBuilder();
    System.out.println("bceKey="+isBceKey.get());
    if (isBceKey.get()) {
      // add aipSdk param
      builder.addHeader("aipSdk", "java");
      String  bodyStr = "";
      RequestBody  requestBody = request.body();
      if(requestBody instanceof FormBody){
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 0; i < ((FormBody) requestBody).size(); i++) {
          String   key = ((FormBody) requestBody).encodedName(i);
          String   value = ((FormBody) requestBody).encodedValue(i);
          if(null == value || value.equals("")){
            arr.add(Util.uriEncode(key, true));
          }else{
            arr.add(String.format("%s=%s", Util.uriEncode(key, true),
                Util.uriEncode(value, true)));
          }
        }
        bodyStr = Util.mkString(arr.iterator(), '&');
      }
      long len = bodyStr.length();
      builder.addHeader(Headers.CONTENT_LENGTH, Long.toString(len));
      builder.addHeader(Headers.CONTENT_MD5, SignUtil.md5(bodyStr, requestBody.contentType().charset().name()));
      String timestamp = Util.getCanonicalTime();
      builder.addHeader(Headers.HOST, request.url().host());
      builder.addHeader(Headers.BCE_DATE, timestamp);
     // builder.addHeader(Headers.AUTHORIZATION, CloudAuth.sign(request, this.aipKey, this.aipToken, timestamp));
    }
    else {
      RequestBody  requestBody = request.body();

      if(requestBody  instanceof  FormBody){
        JSONObject  jsonObject = new JSONObject();
        for (int i = 0; i < ((FormBody) requestBody).size(); i++) {
          jsonObject.put(((FormBody) requestBody).encodedName(i),((FormBody) requestBody).encodedValue(i));

        }

        RequestBody requestBody1 = FormBody.create(MediaType.parse("application/json; charset=utf-8")
            , jsonObject.toJSONString());
        String   newUrl = request.url().toString();
        if(!newUrl.endsWith("?")){
          newUrl = newUrl+"?";
        }
        newUrl= newUrl+"access_token="+accessToken+"&aipSdk=java";
        return  request.newBuilder().url(newUrl).post(requestBody1).build();
      }

    }
    return   builder.build();
  }

  /**
   * send request to server
   * @param request AipRequest object
   * @return JSONObject of server response
   */
  protected JSONObject requestServer(Request request) {
    // 请求API
    HttpLoggingInterceptor   loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient   okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();


    String resData = null;
    Response  response =null;
    try {
      response =okHttpClient.newCall(request).execute();
      resData = response.body().string();
      Integer status = response.code();
      if (status.equals(200) && !resData.equals("")) {
        try {
          JSONObject res = JSON.parseObject(resData);
          if (state.getState().equals(EAuthState.STATE_POSSIBLE_CLOUD_USER)) {
            boolean cloudAuthState = res.containsKey("error_code")
                || res.getIntValue("error_code") != AipClientConst.IAM_ERROR_CODE;
            state.transfer(cloudAuthState);

            if (!cloudAuthState) {
              return Util.getGeneralError(
                  AipClientConst.OPENAPI_NO_ACCESS_ERROR_CODE,
                  AipClientConst.OPENAPI_NO_ACCESS_ERROR_MSG);
            }
          }
          return res;
        } catch (JSONException e) {
          return Util.getGeneralError(-1, resData);
        }
      }
      else {
        Log.w("Api",String.format("call failed! response status: %d, data: %s", status, resData));
        return AipError.NET_TIMEOUT_ERROR.toJsonResult();

      }
    } catch (IOException e) {
      e.printStackTrace();
      return  Util.getGeneralError(-1,e.getMessage());
    }

  }


  // getters and setters for UT
  private void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  private AtomicBoolean getIsAuthorized() {
    return isAuthorized;
  }

  private void setIsAuthorized(boolean isAuthorized) {
    this.isAuthorized.set(isAuthorized);
  }

  private AtomicBoolean getIsBceKey() {
    return isBceKey;
  }

  private void setIsBceKey(boolean isBceKey) {
    this.isBceKey.set(isBceKey);
  }

  private Calendar getExpireDate() {
    return expireDate;
  }

  private void setExpireDate(Calendar expireDate) {
    this.expireDate = expireDate;
  }

}
