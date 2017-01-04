package com.zlcdgroup.libs.ocr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.zlcdgroup.dao.ReadImageBeanDao;
import com.zlcdgroup.libs.R;
import com.zlcdgroup.libs.db.DbCore;
import com.zlcdgroup.libs.db.ReadImageBean;
import com.zlcdgroup.libs.ocr.api.OcrApi;
import com.zlcdgroup.libs.ocr.api.RetrofitUtil;
import com.zlcdgroup.libs.ocr.vo.VoReadingdata;
import com.zlcdgroup.taskManager.ApiTaskCallBack;
import com.zlcdgroup.taskManager.MultiTaskManager;
import com.zlcdgroup.taskManager.enums.TaskManagerStatusEnum;
import java.util.List;

/**
 * * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #
 *
 * @ Description:                                          #
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/12/21 11:09
 * @ Version V1.0
 */

public class OcrDownloadFileActivity extends AppCompatActivity {

  @BindView(R.id.number_progress_bar) NumberProgressBar numberProgressBar;
  @BindView(R.id.btn_start) Button btnStart;
  @BindView(R.id.btn_stop) Button btnStop;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_downloadfiles);
    ButterKnife.bind(this);
  }


  public MultiTaskManager   taskManager;

  @OnClick(R.id.btn_start)
  public   void   btn_start(){
    if(null != taskManager){
      taskManager.cancelTasks();
    }
    OcrApi  api = RetrofitUtil.createApi(OcrApi.class,"http://139.129.205.241/MRMSEIZFJB/");
    taskManager = new MultiTaskManager(10);
     String  userId="15080121202741817269";
     String  imei ="868508027262370";
    String  jobid="17010303212059429985";
    VoReadingdata  readingdata = new VoReadingdata();

    int  total = 338;
    readingdata.setRdJobId(jobid);
    int  count = total/10+1;
    System.out.println("total="+total);
     for(int i=0;i<count+1;i++){
       DownloadReadDataTask  task1  = new DownloadReadDataTask(jobid,imei,userId,readingdata,i+1,api);
       taskManager.addTask(task1);
     }

     taskManager.setCallBack(new ApiTaskCallBack() {
       @Override public void onCallBack(int total, int progress, int error) {
         System.out.println(total+":"+progress+":"+error);
       }

       @Override public void onComplete() {

       }

       @Override public void onError(String message, TaskManagerStatusEnum statusEnum) {

       }
     });
    taskManager.executeTask();

  }


  MultiTaskManager   filemanager= null;
  @OnClick(R.id.btn_stop)
  public   void   btnStop(){
    if(null != filemanager){
      filemanager.cancelTasks();
    }
    OcrApi  api = RetrofitUtil.createApi(OcrApi.class,"http://139.129.205.241/MRMSEIZFJB/");
    ReadImageBeanDao  dao = DbCore.getDaoSession().getReadImageBeanDao();
    List<ReadImageBean>  readImageBeanList = dao.queryBuilder().where(ReadImageBeanDao.Properties.LocalPath.isNull()).build().list();
    filemanager = new MultiTaskManager(20);
    for(ReadImageBean  readImageBean : readImageBeanList){
      DownImageTask  task = new DownImageTask(dao,readImageBean,"http://139.129.205.241/MRMSEIZFJB/upload/",api);
      filemanager.addTask(task);
    }
    filemanager.setCallBack(new ApiTaskCallBack() {
      @Override public void onCallBack(int total, int progress, int error) {

      }

      @Override public void onComplete() {

      }

      @Override public void onError(String message, TaskManagerStatusEnum statusEnum) {

      }
    });
    filemanager.executeTask();
  }

  @Override public void onBackPressed() {
    if(null != taskManager){
      taskManager.cancelTasks();
    }
    if(null != filemanager){
      filemanager.cancelTasks();
    }
    super.onBackPressed();
  }
}
