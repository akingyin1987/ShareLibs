package com.zlcdgroup.libs;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.zlcdgroup.libs.db.ImageTextBean;
import com.zlcdgroup.libs.photovideo.vo.TempBaseVo;

/**
 * Created by Administrator on 2016/4/23.
 */
public class MyApp  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Configuration  configuration =  new  Configuration.Builder(this)
                        .addModelClass(ImageTextBean.class)
                        .addModelClass(TempBaseVo.class).create();
        ActiveAndroid.initialize(configuration,true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
