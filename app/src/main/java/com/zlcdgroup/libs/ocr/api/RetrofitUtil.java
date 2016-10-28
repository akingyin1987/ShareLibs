package com.zlcdgroup.libs.ocr.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/10/24.
 */

public class RetrofitUtil {

    public   static   OkHttpClient  getOkHttp(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient   okHttpClient = new OkHttpClient.Builder()
                                     .addNetworkInterceptor(new StethoInterceptor())
                                     .addInterceptor(logging).build();
        return  okHttpClient;
    }


    private static Retrofit singleton;

    private  static  final  String   GIT_URL="https://api.github.com/";

    private  static  final  String   baseUrl="https://api.douban.com";

    public static final String BAIDU_IMAGES_URLS = "http://image.baidu.com";

    public static  final  String OCR_YUANSHI="http://120.76.220.251:4567/";

    public static  final  String OCR_BAIDU="http://apis.baidu.com/idl_baidu/baiduocrpay/idlocrpaid/";

    public static <T> T createApi(Class<T> clazz) {
        if (singleton == null) {
            synchronized (RetrofitUtil.class) {
                if (singleton == null) {
                    Retrofit.Builder builder = new Retrofit.Builder();
                    builder.baseUrl(GIT_URL);//设置远程地址
                    builder.addConverterFactory(GsonConverterFactory.create());
                    builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());

                    builder.client(getOkHttp());
                    singleton = builder.build();
                }
            }
        }
        return singleton.create(clazz);
    }


    public static <T> T createApi(Class<T> clazz,String baseUrl) {
        synchronized (RetrofitUtil.class) {
            Retrofit.Builder builder = new Retrofit.Builder();

            builder.baseUrl(baseUrl);//设置远程地址

            builder.addConverterFactory(GsonConverterFactory.create());
            builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
            builder.client(getOkHttp());
            return builder.build().create(clazz);
        }
    }


}
