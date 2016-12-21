package com.zlcdgroup.libs.ocr;

import com.zlcdgroup.dao.ReadImageBeanDao;
import com.zlcdgroup.libs.config.AppConfig;
import com.zlcdgroup.libs.db.DbCore;
import com.zlcdgroup.libs.db.ReadImageBean;
import com.zlcdgroup.libs.ocr.api.OcrApi;
import com.zlcdgroup.libs.utils.FileUtil;
import com.zlcdgroup.stickerlib.utils.FileUtils;
import com.zlcdgroup.taskManager.AbsTaskRunner;
import com.zlcdgroup.taskManager.enums.TaskStatusEnum;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

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
 * @ Date 2016/12/21 11:29
 * @ Version V1.0
 */

public class DownImageTask  extends AbsTaskRunner {

  public ReadImageBean  readImageBean;

  public OcrApi   api;

  public String   baseUrl;

  public DownImageTask(ReadImageBean readImageBean, String baseUrl, OcrApi api) {
    this.readImageBean = readImageBean;
    this.baseUrl = baseUrl;
    this.api = api;
  }

  private ReadImageBeanDao   dao;

  @Override public TaskStatusEnum onBefore() {
    dao = DbCore.getDaoSession().getReadImageBeanDao();
    return TaskStatusEnum.SUCCESS;
  }

  @Override public void onToDo() {
    Call<ResponseBody>   responseBodyCall = api.getImageForBase64(baseUrl+readImageBean.webPath);
    try {
       Response<ResponseBody> response =responseBodyCall.execute();
       if(response.isSuccessful()){
        String  fileName = FileUtil.getFileName(readImageBean.webPath);
        File  dir = new File(AppConfig.FILE_ROOT_URL);
        if(!dir.exists() || !dir.isDirectory()){
          dir.mkdirs();
        }
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        InputStream  is = null;
        try {
          File    file = new File(dir,fileName);
          if(file.exists()){

            return;
          }
          is = response.body().byteStream();

          fos = new FileOutputStream(file);
          while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);
          }
          fos.flush();
        }catch (Exception e){
          e.printStackTrace();
        }finally {
          if(null != fos){
            try {
              fos.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          if(null != is){
            try {
              is.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }

      }
    } catch (IOException e) {
      e.printStackTrace();
    }finally {
      if(null != responseBodyCall){
        responseBodyCall.cancel();
      }

    }
  }

  @Override public void onCancel() {
    super.onCancel();
  }
}
