package com.zlcdgroup.libs.ocr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.config.AppConfig;
import com.zlcdgroup.libs.ocr.adapter.OcrAdapter;
import com.zlcdgroup.libs.ocr.api.OcrApi;
import com.zlcdgroup.libs.ocr.api.RetrofitUtil;
import com.zlcdgroup.libs.utils.Base64Util;
import com.zlcdgroup.libs.utils.RxUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_ocr);
        ButterKnife.bind(this);
        lvImgs.setAdapter(adapter);
        initData();
    }

    @OnClick(R.id.btn_refresh)
    public   void    onBtnRefresh(){
        OcrApi   api = RetrofitUtil.createApi(OcrApi.class,rbBaidu.isChecked()?RetrofitUtil.OCR_BAIDU:RetrofitUtil.OCR_YUANSHI);
        for(int i=0;i<adapter.getCount();i++){
            OcrVo  ocrVo = adapter.getItem(i);
            if(rbBaidu.isChecked()){

                api.getImageOcrByBaidu("68e7ae6a38e4ef88347d604806613b63","android","10.10.10.0","LocateRecognize","1", Base64Util.FileToBase64(ocrVo.localpath));
            }
        }

    }

    public void initData() {
        adapter.clear();
        File rootfile = new File(AppConfig.FILE_ROOT_URL);
        Observable.just(rootfile).flatMap(new Func1<File, Observable<File>>() {
            @Override
            public Observable<File> call(File file) {
                return RxUtil.listFiles(file);
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
