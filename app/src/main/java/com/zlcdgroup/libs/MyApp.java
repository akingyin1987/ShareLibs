package com.zlcdgroup.libs;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
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
        initImageLoader(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
