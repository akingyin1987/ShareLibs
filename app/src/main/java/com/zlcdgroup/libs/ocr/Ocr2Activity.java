package com.zlcdgroup.libs.ocr;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zlcdgroup.dao.ReadImageBeanDao;
import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.config.AppConfig;
import com.zlcdgroup.libs.db.DbCore;
import com.zlcdgroup.libs.db.ReadImageBean;
import com.zlcdgroup.libs.ocr.adapter.Ocr2Adapter;
import com.zlcdgroup.libs.ocr.api.OcrApi;
import com.zlcdgroup.libs.ocr.api.RetrofitUtil;
import com.zlcdgroup.libs.utils.FileUtil;
import com.zlcdgroup.taskManager.ApiTaskCallBack;
import com.zlcdgroup.taskManager.MultiTaskManager;
import com.zlcdgroup.taskManager.enums.TaskManagerStatusEnum;
import java.io.File;
import java.util.List;
import java.util.UUID;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/1/4 11:17
 * @ Version V1.0
 */

public class Ocr2Activity extends AppCompatActivity {
  @BindView(R.id.rb_yuns) RadioButton mRbYuns;
  @BindView(R.id.rb_baidu) RadioButton mRbBaidu;
  @BindView(R.id.btn_refresh) Button mBtnRefresh;
  @BindView(R.id.rg_ocr) RadioGroup mRgOcr;
  @BindView(R.id.lv_imgs) ListView mLvImgs;
  @BindView(R.id.btn_download) Button mBtnDownload;
  String imei;
  String mac;
  Ocr2Adapter adapter;

  ReadImageBeanDao dao;
  @BindView(R.id.tv_info) TextView mTvInfo;
  @BindView(R.id.btn_delect) Button mBtnDelect;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ocr);
    ButterKnife.bind(this);
    TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
    imei = tm.getDeviceId();

    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    if (null != wifi) {
      WifiInfo info = wifi.getConnectionInfo();
      mac = info.getMacAddress().toLowerCase();
    }
    dao = DbCore.getDaoSession().getReadImageBeanDao();
    adapter = new Ocr2Adapter(this);
    mLvImgs.setAdapter(adapter);
    initData();
  }
  java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00");
  public void initData() {
    List<ReadImageBean> datas = dao.queryBuilder().list();
    adapter.appendToTopList(datas);
    long  complete = dao.queryBuilder().where(ReadImageBeanDao.Properties.Result.gt(0),ReadImageBeanDao.Properties.Result.lt(3)).count();
    long  sucess = dao.queryBuilder().where(ReadImageBeanDao.Properties.Result.eq(1)).count();
    long  error = dao.queryBuilder().where(ReadImageBeanDao.Properties.Result.eq(2)).count();
    long  unRead = dao.queryBuilder().where(ReadImageBeanDao.Properties.Result.eq(3)).count();
    if(datas.size()>0 && complete>0){
      mTvInfo.setText(Html.fromHtml("总数："+datas.size()+" 已识别:"+complete+" 识别成功："+sucess+" 识别错误："+error+" 无法识别:"+unRead+"<br>"
          +"识别率"+(df.format(complete*100.0/datas.size()))+"%"+"  识别成功率:"+df.format((sucess*100/complete))+"%"));
    }


  }

  MultiTaskManager   mTaskManager;
  @OnClick(R.id.btn_refresh) public void btn_refresh() {
    if(null != mTaskManager){
      mTaskManager.cancelTasks();
    }
    mTaskManager = new MultiTaskManager(8);

    ImageLoader.getInstance().clearMemoryCache();
    ImageLoader.getInstance().clearDiskCache();
    OcrApi  api = RetrofitUtil.createApi(OcrApi.class,RetrofitUtil.OCR_YUANSHI);
    for(ReadImageBean  readImageBean : adapter.getList()){
      mTaskManager.addTask(new OcrReadTask(api,readImageBean,imei,mac,dao));
    }
    mTaskManager.setCallBack(new ApiTaskCallBack() {
      @Override public void onCallBack(int total, int progress, int error) {
          System.out.println("onCallBack="+total+":"+progress+":"+error);
      }

      @Override public void onComplete() {
        Toast.makeText(Ocr2Activity.this,"更新完毕",Toast.LENGTH_SHORT).show();
         initData();
      }

      @Override public void onError(String message, TaskManagerStatusEnum statusEnum) {

      }
    });
    mTaskManager.executeTask();
  }

  @OnClick(R.id.btn_download)
  public   void   btn_download(){
    Intent intent = new Intent(this,OcrDownloadFileActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.btn_delect)
  public   void  btn_delect(){
     dao.queryBuilder().buildDelete().executeDeleteWithoutDetachingEntities();
    Observable.just(AppConfig.FILE_ROOT_URL).map(new Func1<String, Void>() {
      @Override public Void call(String s) {
        FileUtil.deleteDirectory(s);
        return null;
      }
    }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        .subscribe(new Action1<Void>() {
          @Override public void call(Void aVoid) {

          }
        });
  }

  @OnClick(R.id.btn_abnormal)
  public   void  btn_abnormal(){
    List<ReadImageBean>   datas = dao.queryBuilder()
        .where(ReadImageBeanDao.Properties.Result.notEq(1),
            ReadImageBeanDao.Properties.Result.gt(0),
            ReadImageBeanDao.Properties.LocalPath.isNotNull()).list();
    final File   dir = new File(AppConfig.FILE_ROOT_URL+File.separator+"unread");
    if(!dir.exists()){
      dir.mkdirs();
    }
    final File  dir2 = new File(AppConfig.FILE_ROOT_URL+File.separator+"readerror");
    if(!dir2.exists()){
      dir2.mkdirs();
    }
    Observable.from(datas).filter(new Func1<ReadImageBean, Boolean>() {
      @Override public Boolean call(ReadImageBean readImageBean) {
        return !TextUtils.isEmpty(readImageBean.localPath);
      }
    }).map(new Func1<ReadImageBean, Void>() {
      @Override public Void call(ReadImageBean readImageBean) {

        File   file = new File(readImageBean.result == 2?dir2:dir, UUID.randomUUID().toString().replace("-","")+".jpg");

        FileUtil.CopySingleFileTo(readImageBean.localPath,file.getAbsolutePath());
        return null;
      }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
       .subscribe(new Subscriber<Void>() {
         @Override public void onCompleted() {

         }

         @Override public void onError(Throwable e) {

         }

         @Override public void onNext(Void aVoid) {

         }
       });
  }

}
