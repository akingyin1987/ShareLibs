package com.zlcdgroup.libs;

import android.app.Application;
import android.content.Context;

import android.content.SharedPreferences;
import android.os.Environment;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;
import com.yixia.camera.VCamera;
import com.yixia.camera.util.DeviceUtils;
import com.zlcdgroup.dao.DaoMaster;
import com.zlcdgroup.dao.DaoSession;
import com.zlcdgroup.libs.db.ImageTextBean;
import com.zlcdgroup.libs.db.UpgradeHelper;
import com.zlcdgroup.libs.db.User;
import com.zlcdgroup.libs.photovideo.vo.TempBaseVo;
import java.io.File;
import org.greenrobot.greendao.database.Database;
import org.lasque.tusdk.core.TuSdk;

/**
 * Created by Administrator on 2016/4/23.
 */
public class MyApp  extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        // 设置拍摄视频缓存路径
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/Camera/VCameraDemo/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/", "/sdcard-ext/") + "/Camera/VCameraDemo/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/Camera/VCameraDemo/");
        }
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);
        Configuration  configuration =  new  Configuration.Builder(this)
                        .addModelClass(ImageTextBean.class)
                        .addModelClass(TempBaseVo.class).create();
        ActiveAndroid.initialize(configuration,true);
        initImageLoader(this);
        CrashReport.initCrashReport(this,"900027987",false);
        CrashReport.setUserId(System.currentTimeMillis()+"test");
        Stetho.initializeWithDefaults(this);

        TuSdk.enableDebugLog(true);
        TuSdk.init(getApplicationContext(),"4387f9d67be3c238-01-k4rko1");
        initDao();
    }
    private DaoSession  daoSession;

    public static final boolean ENCRYPTED = false;
    public   void   initDao(){
        UpgradeHelper helper = new UpgradeHelper(this,ENCRYPTED ? "test-db-encrypted" : "test-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        User   user = new User();
        user.account="test";
        user.arg="111";
        daoSession.getUserDao().save(user);
        System.out.println("id="+user.getId());
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
