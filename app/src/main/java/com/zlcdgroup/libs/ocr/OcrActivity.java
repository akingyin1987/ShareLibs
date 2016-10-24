package com.zlcdgroup.libs.ocr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.config.AppConfig;
import com.zlcdgroup.libs.ocr.adapter.OcrAdapter;
import com.zlcdgroup.libs.utils.FileUtil;
import com.zlcdgroup.libs.utils.RxUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
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

    OcrAdapter   adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_ocr);
        ButterKnife.bind(this);
        lvImgs.setAdapter(adapter);
        initData();
    }


    public   void   initData(){
        adapter.clear();
        File  rootfile = new File(AppConfig.FILE_ROOT_URL);
        Observable.just(rootfile).flatMap(new Func1<File, Observable<File>>() {
            @Override
            public Observable<File> call(File file) {
                return RxUtil.listFiles(file);
            }
        }).map(new Func1<File, OcrVo>() {
            @Override
            public OcrVo call(File file) {
                OcrVo   ocr = new OcrVo();
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
