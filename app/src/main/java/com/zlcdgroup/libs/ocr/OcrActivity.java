package com.zlcdgroup.libs.ocr;

import android.content.Context;
import android.media.ExifInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.zlcdgroup.camera.internal.CameraFragment;
import com.zlcdgroup.camera.util.ExifInterfaceUtil;
import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.config.AppConfig;
import com.zlcdgroup.libs.ocr.adapter.OcrAdapter;
import com.zlcdgroup.libs.ocr.api.OcrApi;
import com.zlcdgroup.libs.ocr.api.RetrofitUtil;
import com.zlcdgroup.libs.utils.Base64Util;
import com.zlcdgroup.libs.utils.RxUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zlcdgroup.libs.ocr.api.RetrofitUtil.OCR_YUANSHI;

/**
 * Created by Administrator on 2016/10/24.
 */

public class OcrActivity extends AppCompatActivity {
    @BindView(R.id.rb_yuns)
    RadioButton rbYuns;
    @BindView(R.id.rb_baidu)
    RadioButton rbBaidu;
    @BindView(R.id.rg_ocr)
    RadioGroup rgOcr;
    @BindView(R.id.lv_imgs)
    ListView lvImgs;

    OcrAdapter adapter;
    @BindView(R.id.btn_refresh)
    Button btnRefresh;
    String  imei;
    String  mac;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        imei = tm.getDeviceId();

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(null != wifi){
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress().toLowerCase();
        }

        ButterKnife.bind(this);
        adapter = new OcrAdapter(this);
        lvImgs.setAdapter(adapter);

        initData();
    }

    @OnClick(R.id.btn_refresh)
    public   void    onBtnRefresh(){
        OcrApi   api = RetrofitUtil.createApi(OcrApi.class,rbBaidu.isChecked()?RetrofitUtil.OCR_BAIDU: OCR_YUANSHI);
        for(int i=0;i<adapter.getCount();i++){
            final OcrVo  ocrVo = adapter.getItem(i);
            if(rbBaidu.isChecked()){

                api.getImageOcrByBaidu("68e7ae6a38e4ef88347d604806613b63","android","10.10.10.0","LocateRecognize","1", Base64Util.FileToBase64(ocrVo.localpath))
                        .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody responseBody) {
                        try {
                            System.out.println(responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }else{
                SimpleDateFormat  formatter = new SimpleDateFormat("yyyymmdd-hhmmss-SSSS");
                String  NumArea = ExifInterfaceUtil.getExifinterAttr(ocrVo.localpath, ExifInterface.TAG_MODEL);
                System.out.println("attr="+NumArea);
                if(!TextUtils.isEmpty(NumArea)){
                    Gson  gson = new Gson();
                    System.out.println("localpath="+ocrVo.localpath);
                  //  Base64Util.decoderBase64File(Base64Util.FileToBase64(ocrVo.localpath),AppConfig.FILE_ROOT_URL+File.separator+"copy_"+ UUID.randomUUID().toString()+".jpg");
                    CameraFragment.ZuoBiao  zuoBiao =gson.fromJson(NumArea, CameraFragment.ZuoBiao.class);
                    NumArea=(int)zuoBiao.getLeft()+","+(int)zuoBiao.getTop()+","+(int)zuoBiao.getxDes()+","+(int)zuoBiao.getyDes();

                    api.getImageOcrByYushi(Base64Util.FileToBase64(ocrVo.localpath),"Android","QCZLCD",imei,formatter.format(new Date()),NumArea,mac,"重庆","重庆")
                            .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<YunShiEntity>() {
                                @Override public void call(YunShiEntity yunShiEntity) {
                                    ocrVo.ocrtext = yunShiEntity.getValue();
                                    if(!TextUtils.isEmpty(yunShiEntity.getValue())){
                                        if(yunShiEntity.getValue().contains(",")){
                                            String[] values = yunShiEntity.getValue().split(",");
                                            ocrVo.ocrtext="读数："+values[0]+"  位数："+values[1];
                                        }
                                    }
                                    checkImage(ocrVo,yunShiEntity);
                                    adapter.notifyDataSetChanged();
                                }
                            }, new Action1<Throwable>() {
                                @Override public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
                }

            }
        }

    }

    public   void    checkImage(OcrVo   ocrVo,YunShiEntity  yunShiEntity){
       OcrApi  api = RetrofitUtil.createApi(OcrApi.class,OCR_YUANSHI);
        SimpleDateFormat  formatter = new SimpleDateFormat("yyyymmdd-hhmmss-SSSS");
        String  NumArea = ExifInterfaceUtil.getExifinterAttr(ocrVo.localpath, ExifInterface.TAG_MODEL);
        Gson  gson = new Gson();
        System.out.println("localpath="+ocrVo.localpath);
        //  Base64Util.decoderBase64File(Base64Util.FileToBase64(ocrVo.localpath),AppConfig.FILE_ROOT_URL+File.separator+"copy_"+ UUID.randomUUID().toString()+".jpg");
        CameraFragment.ZuoBiao  zuoBiao =gson.fromJson(NumArea, CameraFragment.ZuoBiao.class);
        NumArea=(int)zuoBiao.getLeft()+","+(int)zuoBiao.getTop()+","+(int)zuoBiao.getxDes()+","+(int)zuoBiao.getyDes();

        api.checkImageOcrByCloud(yunShiEntity.getFilename(),"SZBD2016",formatter.format(new Date()),NumArea,yunShiEntity.getValue(),yunShiEntity.getPos())
            .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
            .subscribe(new Action1<OcrVo>() {
                @Override public void call(OcrVo ocrVo) {

                }
            }, new Action1<Throwable>() {
                @Override public void call(Throwable throwable) {

                }
            });
    }
    public void initData() {
        adapter.clear();
        File rootfile = new File(AppConfig.FILE_ROOT_URL);
        Observable.just(rootfile).flatMap(new Func1<File, Observable<File>>() {
            @Override
            public Observable<File> call(File file) {
                return RxUtil.listFiles(file);
            }
        }).filter(new Func1<File, Boolean>() {
            @Override
            public Boolean call(File file) {

                return !file.getName().startsWith("base_");
            }
        }).map(new Func1<File, OcrVo>() {
            @Override
            public OcrVo call(File file) {
                OcrVo ocr = new OcrVo();
                ocr.localpath = file.getAbsolutePath();
                return ocr;
            }
        }).toList().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<OcrVo>>() {
                    @Override
                    public void call(List<OcrVo> ocrVos) {

                        adapter.appendToList(ocrVos);
                    }
                });
    }
}
